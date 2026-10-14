# Expresiones regulares en Java

## 1. Introducción

Las expresiones regulares (*Regular Expressions* o *Regex*) son patrones que permiten buscar, validar, extraer y reemplazar texto. En Java se utilizan, por ejemplo, para comprobar formatos, localizar números o sustituir partes de una cadena.

Las clases principales se encuentran en el paquete `java.util.regex`: `Pattern` y `Matcher`.

## 2. ¿Para qué sirven?

- Validar datos, como correos electrónicos o números.
- Buscar palabras y caracteres en un texto.
- Reemplazar partes de una cadena.
- Dividir texto usando separadores.
- Extraer información que coincida con un patrón.

## 3. Pattern y Matcher

`Pattern` representa una expresión regular compilada y `Matcher` permite buscar coincidencias dentro de un texto.

```java
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Ejemplo {
    public static void main(String[] args) {
        String texto = "Tengo 20 años y 3 mascotas";
        Pattern patron = Pattern.compile("\\\\d+");
        Matcher matcher = patron.matcher(texto);

        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}
```

Resultado:

```text
20
3
```

`find()` busca la siguiente coincidencia y `group()` devuelve el texto encontrado. En cadenas Java, una barra invertida de la expresión regular suele escribirse duplicada: `\\d`.

## 4. Caracteres habituales

| Patrón | Significado | Ejemplo |
|---|---|---|
| `.` | Cualquier carácter, salvo normalmente terminadores de línea | `a.c` |
| `\\d` | Un dígito | `\\d+` |
| `\\D` | Un carácter que no sea dígito | `\\D+` |
| `\\w` | Carácter de palabra: letras ASCII, dígitos o `_` | `\\w+` |
| `\\s` | Espacio en blanco | `\\s+` |
| `[abc]` | Un carácter entre a, b o c | `[abc]` |
| `[a-z]` | Una letra minúscula inglesa | `[a-z]+` |
| `[0-9]` | Un dígito | `[0-9]+` |
| `[^0-9]` | Un carácter que no sea dígito | `[^0-9]+` |

## 5. Cuantificadores

| Cuantificador | Significado |
|---|---|
| `*` | Cero o más repeticiones |
| `+` | Una o más repeticiones |
| `?` | Cero o una repetición |
| `{3}` | Exactamente tres repeticiones |
| `{2,5}` | Entre dos y cinco repeticiones |
| `{2,}` | Dos o más repeticiones |

Ejemplos:

```java
String unoOMasDigitos = "\\\\d+";
String tresMayusculas = "[A-Z]{3}";
String entreDosYCuatroDigitos = "\\\\d{2,4}";
```

## 6. Anclas y límites

- `^`: inicio de la entrada o línea, según las opciones.
- `$`: final de la entrada o línea, según las opciones.
- `\\b`: límite de palabra.

El patrón `^Hola$` comprueba que la entrada sea exactamente `Hola` cuando se utiliza `matches()`.

## 7. Métodos útiles

### `matches()`

Comprueba si toda la cadena coincide con el patrón.

```java
System.out.println("12345".matches("\\\\d+")); // true
```

### `find()`

Busca coincidencias dentro de un texto.

```java
import java.util.regex.*;

public class BuscarNumeros {
    public static void main(String[] args) {
        Matcher m = Pattern.compile("\\\\d+")
                           .matcher("Pedido 125 y pedido 300");
        while (m.find()) {
            System.out.println(m.group());
        }
    }
}
```

### `replaceAll()`

Sustituye todas las coincidencias.

```java
String texto = "Tengo 3 manzanas y 5 peras";
System.out.println(texto.replaceAll("\\\\d+", "X"));
```

Resultado: `Tengo X manzanas y X peras`.

### `split()`

Divide una cadena usando un patrón como separador.

```java
String[] colores = "rojo,verde;azul".split("[,;]");
```

## 8. Ejemplos prácticos

### Validar un número entero positivo

```java
String numero = "245";
System.out.println(numero.matches("[0-9]+")); // true
```

### Comprobar el formato habitual de un correo

```java
String correo = "usuario@ejemplo.com";
String patron = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,}$";
System.out.println(correo.matches(patron));
```

Este patrón comprueba un formato habitual, pero no garantiza que la dirección exista.

### Encontrar palabras que comienzan por mayúscula

```java
import java.util.regex.*;

public class Mayusculas {
    public static void main(String[] args) {
        String texto = "Java es un lenguaje y Madrid es una ciudad";
        Matcher m = Pattern.compile("\\\\b[A-Z][a-z]*\\\\b")
                           .matcher(texto);
        while (m.find()) {
            System.out.println(m.group());
        }
    }
}
```

## 9. Ventajas y limitaciones

**Ventajas**
- Facilitan la validación y búsqueda de texto.
- Permiten extraer y reemplazar información con poco código.
- Forman parte de la biblioteca estándar de Java.

**Limitaciones**
- Los patrones complejos pueden ser difíciles de leer.
- Un patrón incorrecto puede dar resultados inesperados.
- Validar el formato no garantiza que el dato sea real.
- Los patrones excesivamente complejos pueden afectar al rendimiento.

## 10. Ejercicios

1. Comprobar si una cadena contiene únicamente letras minúsculas inglesas.
2. Encontrar todos los números de `Hay 12 alumnos, 3 profesores y 2 aulas`.
3. Sustituir los espacios de una frase por guiones.
4. Comprobar si una cadena contiene exactamente cinco dígitos.
5. Dividir una cadena separada por comas o puntos y coma.

## 11. Conclusión

Las expresiones regulares permiten buscar, validar y manipular texto de forma flexible. Con `Pattern`, `Matcher` y los métodos de `String`, es posible resolver muchas tareas habituales de procesamiento de cadenas.

## 12. Bibliografía

- [Tutorial de expresiones regulares de Oracle](https://docs.oracle.com/javase/tutorial/essential/regex/)
- [Documentación oficial de Java](https://docs.oracle.com/en/java/javase/)
