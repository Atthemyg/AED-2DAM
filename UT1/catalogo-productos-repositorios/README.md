# Documentación del código: `catalogo-productos-repositorios`

Este documento explica, pieza por pieza, todo lo que hay en el proyecto: la configuración de Maven, los ficheros de datos, el modelo, la interfaz, las clases de repositorio y las clases de apoyo. Al final hay una sección con los **fallos que se ven al leer el código** y cómo arreglarlos.

---

## 1. Qué hace el proyecto

Es un **catálogo de productos** (`id`, `nombre`, `precio`) cuya información se guarda en un fichero. Lo interesante del ejercicio es que el mismo catálogo se puede guardar en **tres formatos distintos** (CSV, JSON y XML) sin que el resto del programa tenga que saberlo.

Para conseguirlo se usa el **patrón Repositorio**:

- Una **interfaz** (`IProductoRepository`) define *qué* se puede hacer: listar, buscar, crear, actualizar y borrar.
- Una **clase abstracta** (`AbstractProductoRepository`) implementa la lógica común (el CRUD sobre una lista en memoria).
- Tres **clases concretas** (`CsvRepository`, `JsonRepository`, `XmlRepository`) solo se encargan de *cómo* se lee y se escribe cada formato.
- Un fichero de configuración (`app.properties`) y una clase (`AppConfiguration`) deciden qué formato usar.

```
IRepository  (interfaz: el contrato)
     ▲
AbstractRepository  (lógica CRUD común, trabaja sobre List<Producto>)
     ▲
     ├── CsvRepository   (lee/escribe CSV con Commons CSV)
     ├── JsonRepository  (lee/escribe JSON con Jackson)
     └── XmlRepository   (lee/escribe XML con Jackson XML)
```

Además existe `CsvCrudDemo`, que es la **primera versión** del ejercicio (todo en una sola clase, solo CSV), antes de separarlo en capas.

---

## 2. Estructura del proyecto

```
catalogo-productos-repositorios/
├── pom.xml                      ← configuración de Maven
├── README.md                    ← descripción del enunciado
├── data/
│   ├── app.properties           ← configuración de la aplicación
│   ├── productos.csv            ← datos en CSV
│   └── productos.json           ← datos en JSON
├── src/main/java/
│   ├── CsvCrudDemo.java         ← versión inicial, todo en una clase
│   └── com/ejemplo/catalogo/
│       ├── model/
│       │   ├── Producto.java
│       │   └── ProductosXml.java
│       ├── repository/file/csv/
│       │   ├── IRepository.java
│       │   ├── AbstractRepository.java
│       │   ├── CsvRepository.java
│       │   ├── JsonRepository.java
│       │   └── XmlRepository.java
│       └── configuration/
│           └── AppConfiguration.java
├── target/                      ← lo genera Maven/IntelliJ al compilar (no es código tuyo)
└── .idea/                       ← configuración de IntelliJ (no es código tuyo)
```

`target/` contiene los `.class` compilados y `.idea/` los ajustes del IDE. Ninguna de las dos carpetas forma parte del código fuente y normalmente no se sube a Git.

---

## 3. `pom.xml` (configuración de Maven)

Maven es la herramienta que compila el proyecto y descarga las librerías. El `pom.xml` le dice cómo.

| Elemento | Qué significa |
| --- | --- |
| `groupId` = `com.ejemplo`, `artifactId` = `catalogo-productos-repositorios`, `version` = `1.0-SNAPSHOT` | La "identidad" del proyecto. `SNAPSHOT` indica que está en desarrollo. |
| `maven.compiler.release` = `21` | Se compila para Java 21. Es lo que permite usar `record`. |
| `project.build.sourceEncoding` = `UTF-8` | El código fuente se lee como UTF-8, necesario para no romper tildes como la de "mecánico". |
| `commons-csv.version`, `jackson.version` | Las versiones se guardan como propiedades para cambiarlas en un solo sitio. |

**Dependencias** (librerías externas):

1. **`commons-csv`**: leer y escribir ficheros CSV (`CSVFormat`, `CSVParser`, `CSVPrinter`). Se encarga de cosas delicadas como las comillas cuando un nombre contiene una coma.
2. **`jackson-databind`**: convertir objetos Java a JSON y al revés (`ObjectMapper`).
3. **`jackson-dataformat-xml`**: lo mismo, pero para XML (`XmlMapper` y las anotaciones `@JacksonXml...`).

El proyecto no declara plugins de ejecución ni tests, así que se ejecuta desde el IDE.

---

## 4. Ficheros de datos (`data/`)

### `productos.csv`

```
id,nombre,precio
1,Teclado mecánico,79.9
```

La primera línea es la **cabecera**. Cada línea siguiente es un producto. El fichero usa saltos de línea `\r\n` porque es el separador por defecto de `CSVFormat.DEFAULT`. Este contenido es justo el resultado final de ejecutar el `main` de `CsvCrudDemo`.

### `productos.json`

```json
[
  { "id": 1, "nombre": "Teclado", "precio": 29.99 },
  { "id": 2, "nombre": "Raton", "precio": 15.50 },
  { "id": 3, "nombre": "Monitor", "precio": 189.99 }
]
```

Una lista de tres productos escrita a mano. Cada objeto JSON tiene los mismos nombres que los campos del `record Producto`, y eso es lo que permite a Jackson convertirlos sin ayuda extra. En cuanto `JsonRepository` guarde algo, reescribirá el fichero con su propio formato.

### `app.properties`

```properties
#Configuración de la aplicación
#Wed Sep 30 16:19:30 WEST 2026
app.name=FileLab
storage.format=json
storage.path=data/productos.json
```

Fichero de configuración con formato `clave=valor`:

- `app.name`: nombre de la aplicación.
- `storage.format`: qué formato usar (`json` en este momento).
- `storage.path`: ruta del fichero de datos.

Las dos primeras líneas son comentarios. La de la fecha la añade `Properties.store(...)` automáticamente cada vez que `AppConfiguration` guarda el fichero. Que esté ahí indica que `AppConfiguration` se ha ejecutado al menos una vez.

---

## 5. Modelo (`com.ejemplo.catalogo.model`)

### `Producto.java`

```java
public record Producto(long id, String nombre, double precio) { }
```

Un **`record`** es una clase pensada para guardar datos. Con esa sola línea Java genera automáticamente:

- El constructor `new Producto(1, "Teclado", 29.99)`.
- Los métodos de lectura **`id()`**, **`nombre()`** y **`precio()`** (sin el prefijo `get`).
- `equals`, `hashCode` y `toString` basados en **los tres campos**.
- Los campos son inmutables: para "modificar" un producto se crea otro.

Este detalle de `equals` importa: dos productos con el mismo `id` pero distinto precio **no son iguales** para Java. Por eso en `AbstractRepository.update` se compara `p.id() == producto.id()` a mano en lugar de usar `indexOf`.

### `ProductosXml.java`

```java
@JacksonXmlRootElement(localName = "productos")
public class ProductosXml {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "producto")
    public List<Producto> productos;
    ...
}
```

Una clase **auxiliar solo para XML**. Un documento XML necesita un único elemento raíz, y una lista suelta de productos no lo tiene, así que se envuelve en este objeto.

- `@JacksonXmlRootElement(localName = "productos")`: el elemento raíz se llama `<productos>`.
- `@JacksonXmlProperty(localName = "producto")`: cada elemento de la lista se escribe como `<producto>`.
- `@JacksonXmlElementWrapper(useWrapping = false)`: evita un nivel extra. Sin esto, Jackson añadiría un elemento contenedor alrededor de la lista y los `<producto>` quedarían un nivel más profundo de lo necesario.
- El constructor vacío inicializa la lista (`new ArrayList<>()`). Jackson necesita un constructor sin argumentos para crear el objeto al leer.
- `getProductos` y `setProductos` son los accesores que usan los repositorios.

El XML resultante tiene esta forma:

```xml
<productos>
  <producto>
    <id>1</id>
    <nombre>Teclado</nombre>
    <precio>29.99</precio>
  </producto>
</productos>
```

Para JSON y CSV no hace falta una clase así: JSON puede ser directamente una lista `[...]` y CSV es una tabla.

---

## 6. La interfaz `IProductoRepository`

Define el **contrato** que cumplen los tres repositorios:

| Método | Qué hace | Devuelve |
| --- | --- | --- |
| `findAll()` | Obtiene todos los productos | `List<Producto>` |
| `findById(long id)` | Busca un producto por su id | `Optional<Producto>` (vacío si no existe) |
| `create(Producto)` | Añade un producto nuevo | nada (`void`) |
| `update(Producto)` | Sustituye el producto que tenga ese id | `boolean`: `true` si existía y se actualizó |
| `delete(long id)` | Borra el producto con ese id | `boolean`: `true` si se borró |

`Optional` es la forma moderna de decir "puede que haya resultado o puede que no" sin devolver `null`.

A diferencia de `CsvCrudDemo`, estos métodos **no declaran `throws IOException`**. Los errores de lectura y escritura se convierten en excepciones sin comprobar (`RuntimeException` / `UncheckedIOException`) dentro de cada implementación.

---

## 7. `AbstractProductoRepository` (la lógica común)

Es la clase que evita repetir código. Aplica el patrón **Template Method**: implementa el CRUD completo y deja dos métodos abstractos para que cada formato los rellene.

### Campos

- `private Path path`: ruta del fichero. Es privado, así que las subclases lo obtienen con `getPath()`.
- `List<Producto> productos`: la **lista en memoria** con todos los productos. Sin modificador, es visible para las clases del mismo paquete (por eso las subclases pueden usarla).

### Constructor

```java
public AbstractRepository(Path path) {
    if (path == null) throw new IllegalArgumentException();
    this.path = path;
    if (Files.notExists(path)) {
        try { Files.createFile(path); }
        catch (IOException e) { throw new RuntimeException(e); }
    }
}
```

1. Rechaza una ruta `null`.
2. Guarda la ruta.
3. Si el fichero no existe, **lo crea vacío**. Falla si la carpeta que lo contiene no existe.

### Métodos abstractos (los rellenan las subclases)

- `abstract void saveAll(List<Producto> items)`: volcar la lista al fichero.
- `abstract List<Producto> load()`: leer el fichero y devolver la lista.

### Métodos del CRUD

- **`findAll()`**: devuelve la lista en memoria tal cual.
- **`findById(id)`**: recorre la lista con un `stream`, filtra por id y devuelve el primero (`findFirst()`).
- **`create(producto)`**:
  1. Si el producto es `null` o su id es negativo, no hace nada y sale.
  2. Si ya existe ese id, lanza `IllegalArgumentException("Id duplicado: ...")`.
  3. Lo añade a la lista y llama a `saveAll`.
- **`update(producto)`**:
  1. Si es `null` o con id negativo, devuelve `false`.
  2. Recorre la lista; cuando encuentra el mismo id, lo reemplaza con `set(i, producto)`, guarda y devuelve `true`.
  3. Si no lo encuentra, devuelve `false`.
  - El bloque comentado intenta usar `indexOf`, que solo funcionaría si `Producto` tuviera un `equals` que compare únicamente el id. Como no lo tiene, se usa el bucle.
- **`delete(id)`**: `removeIf` borra los elementos que cumplan la condición y dice si borró alguno; solo si borró, guarda.

**Idea clave:** cada operación que modifica datos (`create`, `update`, `delete`) cambia la lista en memoria y **vuelve a escribir todo el fichero** con `saveAll`.

---

## 8. Los tres repositorios concretos

### 8.1 `CsvRepository`

**Formatos de Commons CSV:**

```java
inputFormat  = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get();
outputFormat = CSVFormat.DEFAULT.builder().setHeader("id", "nombre", "precio").get();
```

- `inputFormat` (para leer): `setHeader()` sin argumentos le dice que la primera línea contiene los nombres de columna, y `setSkipHeaderRecord(true)` evita que esa línea se trate como un producto.
- `outputFormat` (para escribir): `setHeader("id", "nombre", "precio")` hace que `CSVPrinter` escriba esa cabecera al principio.

**Constructor** (`CsvRepository(Path path)`): llama a `super(path)` y después `productos = load()`. Es de **ámbito de paquete** (sin `public`), por lo que no se puede crear desde otros paquetes.

**`load()`:** abre el fichero con UTF-8 y recorre cada `CSVRecord`. Para cada fila construye un `Producto` convirtiendo el texto: `Long.parseLong` para el id y `Double.parseDouble` para el precio, que espera el punto decimal (`79.9`). Si hay un `IOException`, lo ignora (el comentario `//Logger.ERROR` recuerda que ahí iría un log).

**`saveAll(items)`:** abre un `Writer` y un `CSVPrinter`, y con `printRecord(id, nombre, precio)` escribe una línea por producto. Si el nombre tiene una coma, como en `"Monitor, 27 pulgadas"`, el `CSVPrinter` lo pone entre comillas automáticamente. Cualquier excepción se convierte en `RuntimeException`.

Los `try (...)` con recursos entre paréntesis cierran el fichero solos al terminar, aunque haya error.

### 8.2 `JsonRepository`

Usa un `ObjectMapper` de Jackson.

**`saveAll(items)`** hace una **escritura segura** en cuatro pasos:

1. Calcula la ruta absoluta del destino y se asegura de que la carpeta existe (`createDirectories`).
2. Crea un **fichero temporal** en esa misma carpeta (`productos-xxxx.json.tmp`).
3. Escribe ahí el JSON con formato legible (`writerWithDefaultPrettyPrinter()`).
4. **Mueve** el temporal sobre el fichero real con `ATOMIC_MOVE` + `REPLACE_EXISTING`. Si el sistema de ficheros no admite movimiento atómico (`AtomicMoveNotSupportedException`), repite el movimiento sin `ATOMIC_MOVE`.

Con esto, si el programa se cae a mitad de escritura, el fichero original no queda a medias: o está el contenido viejo completo o el nuevo completo. El `finally` borra el temporal si aún existiera; tras un movimiento correcto ya no existe, así que `deleteIfExists` no hace nada.

**`load()`:**

```java
mapper.readValue(getPath().toFile(), new TypeReference<List<Producto>>() {});
```

`TypeReference<List<Producto>>` es necesario porque Java "olvida" el tipo genérico en tiempo de ejecución (*type erasure*). Sin él, Jackson no sabría que debe crear objetos `Producto`. Después comprueba que no sea `null` (un JSON que contenga solo `null` daría `null`), vacía la lista en memoria (`clear()`) y copia los elementos leídos (`addAll`). Los `IOException` se convierten en `UncheckedIOException`.

### 8.3 `XmlRepository`

Idéntica idea que la de JSON pero con `XmlMapper`.

- **`saveAll`**: crea un `ProductosXml`, le asigna la lista y lo escribe con `writerWithDefaultPrettyPrinter()` directamente en el fichero. **No** usa fichero temporal: ese bloque está comentado (era la copia del código de JSON). Por eso quedan variables sin uso (`temporal`), un `finally` vacío y varios `import` que ya no se usan.
- **`load`**: lee el fichero como `ProductosXml`, vacía la lista y añade los productos que contiene.

---

## 9. `AppConfiguration`

Clase con un `main` que lee la configuración y elige el repositorio.

Paso a paso:

1. Construye la ruta `data/app.properties` (relativa a la carpeta desde la que se ejecuta el programa).
2. Carga el fichero en un objeto `Properties` (un mapa clave-valor) con `props.load(reader)`.
3. Imprime el formato: `storage.format`.
4. Imprime `storage.fichero`, usando `"Valor_por_defecto"` si no existe esa clave. (Ver problema nº 4: la clave real se llama `storage.path`.)
5. Pone `app.name = FileLab` y **guarda el fichero** con `props.store(writer, "Configuración de la aplicación")`. Esto añade la línea de comentario y la fecha. Al usar un `Writer` UTF-8, las tildes se guardan tal cual en lugar de como `\u00f3`.
6. Si el formato es `json`, crea `new JsonRepository(Path.of(props.getProperty("storage.path")))` y lo guarda en el campo estático `repository`, de tipo `IProductoRepository`.

El campo se declara con el tipo de la **interfaz**, no con el de la clase concreta: así el resto del código no depende del formato.

---

## 10. `CsvCrudDemo` (la versión inicial)

Está en el paquete por defecto (sin `package`) y contiene todo junto: un `record Producto` propio, la lectura y escritura CSV y un `main` de prueba.

Diferencia importante con los repositorios: **no guarda estado en memoria**. Cada operación vuelve a leer el fichero entero:

|  | `CsvCrudDemo` | Repositorios (`AbstractProductoRepository`) |
| --- | --- | --- |
| Datos | Se leen del fichero en cada llamada | Se cargan una vez y viven en una lista |
| Formatos | Solo CSV | CSV, JSON y XML |
| Errores | `throws IOException` | Excepciones sin comprobar |
| Estructura | Una sola clase | Interfaz + clase abstracta + subclases |

Métodos:

- **`findAll()`**: si el fichero no existe devuelve lista vacía; si existe, lo lee fila a fila.
- **`findById(id)`**: usa `findAll()` y filtra con un stream.
- **`create(producto)`**: lee todo, comprueba que el id no exista (si existe lanza excepción), añade y guarda todo.
- **`update(producto)`**: lee todo, busca el id, reemplaza, guarda y devuelve `true`; si no está, `false`.
- **`delete(id)`**: `removeIf`; solo guarda si borró algo.
- **`saveAll(items)`** (privado): crea la carpeta si hace falta y escribe el fichero completo.

**`main`** ejecuta esta secuencia sobre `data/productos.csv`:

1. Crea el producto 1 (Teclado, 49.99) y el 2 (`"Monitor, 27 pulgadas"`, 219.90).
2. Imprime el producto 2.
3. Actualiza el 1 a "Teclado mecánico" por 79.90.
4. Borra el 2.
5. Imprime todos los que quedan.

Eso es exactamente lo que hay ahora en `productos.csv`. Si se ejecuta una segunda vez, lanzará `IllegalArgumentException: Id duplicado: 1`, porque el producto 1 ya existe.

---

## 11. Cómo encaja todo (ejemplo de flujo)

`repository.create(new Producto(4, "Auriculares", 39.5))` con `JsonRepository`:

1. Se ejecuta `AbstractRepository.create`.
2. Comprueba que no sea `null`, que el id no sea negativo y que no exista ya.
3. Añade el producto a la lista en memoria.
4. Llama a `saveAll(productos)`, que en este caso es la versión de `JsonRepository`.
5. `JsonRepository` escribe el temporal y lo mueve sobre `productos.json`.

Si el repositorio fuera `CsvRepository` o `XmlRepository`, los pasos 1 a 3 serían idénticos y solo cambiaría el paso 4. Esa es la ventaja de este diseño.

---

## 12. Problemas detectados en el código

> Estos puntos salen de **leer el código, sin ejecutarlo**. Los más graves hacen que los repositorios fallen al crearse, así que conviene revisarlos primero.

### Graves (el programa falla)

**1. La lista `productos` nunca se inicializa.** En `AbstractProductoRepository` está declarada (`List<Producto> productos;`) pero vale `null`. Cuando `load()` hace `productos.add(...)`, `productos.clear()` o `productos.addAll(...)` se produce un `NullPointerException`. *Arreglo:* inicializarla en la declaración.

```java
protected List<Producto> productos = new ArrayList<>();
```

**2. En `JsonRepository` y `XmlRepository`, el `mapper` se crea después de usarlo.** El constructor hace `productos = load();` y **después** `mapper = new ObjectMapper();`. Pero `load()` usa `mapper`, que todavía es `null` → `NullPointerException`. *Arreglo:* inicializarlo en la propia declaración, como indica el comentario que hay encima:

```java
private final ObjectMapper mapper = new ObjectMapper();
```

Los inicializadores de campo se ejecutan justo después de `super(...)` y antes del resto del constructor, así que así `mapper` ya existe cuando se llama a `load()`.

**3. Un fichero nuevo se crea vacío y JSON/XML no pueden leer un fichero vacío.** `AbstractProductoRepository` crea el fichero vacío si no existe, y Jackson lanza un error ("No content to map") al leerlo. Con el `productos.json` actual no ocurre porque ya tiene contenido, pero con una ruta nueva (por ejemplo, un `productos.xml` que aún no existe) sí. *Arreglo:* en `load()` comprobar `Files.size(getPath()) == 0` y devolver la lista vacía, o escribir `[]` al crear un JSON nuevo.

### Medios (comportamiento incorrecto)

**4. `AppConfiguration` busca una clave que no existe.** Lee `storage.fichero`, pero en `app.properties` la clave se llama `storage.path`. Por eso siempre imprime `Valor_por_defecto`.

**5. `AppConfiguration` solo soporta JSON.** Si `storage.format` fuera `csv` o `xml`, `repository` se queda en `null`. Además `props.getProperty("storage.format").equals("json")` falla con `NullPointerException` si falta la clave. Es más seguro `"json".equals(...)` y un `switch` con los tres formatos y un `default` que lance un error claro. Tampoco se usa el repositorio después de crearlo.

**6. `CsvRepository` tiene el constructor sin `public`.** `JsonRepository` y `XmlRepository` sí lo tienen público. Desde `AppConfiguration` (otro paquete) no se podría crear un `CsvRepository`.

**7. `CsvRepository.load()` no vacía la lista antes de cargar.** JSON y XML hacen `productos.clear()`; CSV solo añade. Si se llamara a `load()` dos veces, los productos saldrían duplicados. Además, ignora los `IOException` en silencio, así que un fallo de lectura parecería un catálogo vacío.

### Mejoras de diseño

**8. Todos los repositorios están en el paquete `...repository.file.csv`.** Incluidos JSON y XML y la interfaz, que no tienen nada que ver con CSV. El README prepara los paquetes `repository`, `repository.file` y `repository.file.csv`: lo lógico sería `IProductoRepository` en `repository`, `AbstractProductoRepository` en `repository.file`, y cada implementación en su paquete de formato.

**9. `findAll()` devuelve la lista interna.** Quien la reciba puede modificarla y cambiar el catálogo sin pasar por `create/update/delete`, y sin que se guarde. Es más seguro devolver `List.copyOf(productos)`.

**10. `create` ignora en silencio un producto nulo o con id negativo.** Pero lanza excepción si el id está duplicado. Es incoherente: lo normal sería lanzar `IllegalArgumentException` en ambos casos.

**11. Código sobrante.** `XmlRepository` tiene imports sin usar (`TypeReference`, `ObjectMapper`, `Files`, `StandardCopyOption`, `AtomicMoveNotSupportedException`), una variable `temporal` sin usar, un `finally` vacío y un bloque grande comentado. `JsonRepository.saveAll` escribe el campo `productos` en lugar del parámetro `items`; funciona porque son la misma lista, pero confunde.

**12. `CsvCrudDemo` duplica el modelo.** Tiene su propio `Producto` y vive en el paquete por defecto. Una vez que el diseño por capas funcione, se puede borrar o mover a una carpeta de ejemplos.

---

## 13. Glosario rápido de lo que se usa

| Concepto | Dónde aparece | Qué es |
| --- | --- | --- |
| `record` | `Producto` | Clase de datos inmutable con constructor, accesores, `equals`, `hashCode` y `toString` automáticos |
| `Optional<T>` | `findById` | Contenedor que puede tener un valor o estar vacío |
| Interfaz | `IProductoRepository` | Contrato: define métodos sin implementarlos |
| Clase abstracta | `AbstractProductoRepository` | Clase parcialmente implementada que no se puede instanciar |
| Stream | `findById`, `create` | Forma de recorrer y filtrar colecciones (`filter`, `anyMatch`, `findFirst`) |
| `try` con recursos | CSV, JSON, config | Cierra ficheros automáticamente al terminar |
| `Path` / `Files` | Todas | API moderna de Java para rutas y ficheros |
| Excepción sin comprobar | Repositorios | `RuntimeException` / `UncheckedIOException`: no obligan a `throws` ni `try/catch` |
| `Properties` | `AppConfiguration` | Mapa clave-valor para ficheros de configuración |
| Anotaciones Jackson | `ProductosXml` | Marcas que controlan el nombre y la forma de los elementos XML |