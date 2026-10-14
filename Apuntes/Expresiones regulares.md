# Las expresiones regulares en Java

## 1. Introducción

Las expresiones regulares, también conocidas como *Regular Expressions* o *Regex*, son patrones que permiten buscar, identificar, comprobar y manipular cadenas de texto. En Java, se utilizan para validar datos, encontrar palabras, comprobar formatos y sustituir fragmentos de texto.

Por ejemplo, una expresión regular puede servir para comprobar si una dirección de correo electrónico tiene un formato válido, si una cadena contiene únicamente números o si un texto comienza con una palabra determinada.

Java incluye herramientas para trabajar con expresiones regulares en el paquete `java.util.regex`, principalmente las clases `Pattern` y `Matcher`.

## 2. ¿Para qué sirven las expresiones regulares?

Las expresiones regulares se utilizan habitualmente para:

- **Validar datos:** comprobar si un correo electrónico, un número de teléfono o una contraseña cumple determinadas condiciones.
- **Buscar texto:** localizar palabras o patrones dentro de una cadena.
- **Reemplazar caracteres:** sustituir espacios, símbolos o palabras por otros elementos.
- **Dividir cadenas:** separar un texto utilizando comas, espacios u otros delimitadores.
- **Extraer información:** obtener números, fechas o palabras que cumplan un patrón.

## 3. Clases principales de Java

Java proporciona dos clases fundamentales para trabajar con expresiones regulares.

### 3.1. Clase Pattern

La clase `Pattern` representa una expresión regular compilada. Se utiliza para definir el patrón que se desea buscar.

Ejemplo:

```java
import java.util.regex.Pattern;

public class EjemploPattern {
    public static void main(String[] args) {
        Pattern patron = Pattern.compile("\\d+");

        System.out.println(patron.pattern());
    }
}
```

En este ejemplo, `\\d+` representa uno o más dígitos. Como las barras invertidas deben escaparse dentro de una cadena de Java, se escribe `\\d` en lugar de `\d`.

### 3.2. Clase Matcher

La clase `Matcher` permite aplicar un patrón a una cadena de texto y comprobar si existen coincidencias.

Ejemplo:

```java
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class EjemploMatcher {
    public static void main(String[] args) {
        String texto = "Mi edad es 20 años";

        Pattern patron = Pattern.compile("\\d+");
        Matcher coincidencia = patron.matcher(texto);

        while (coincidencia.find()) {
            System.out.println(coincidencia.group());
        }
    }
}
```

**Resultado:**

```text
20
```

El método `find()` busca la siguiente coincidencia, mientras que `group()` devuelve el texto encontrado.

## 4. Elementos básicos de las expresiones regulares

Las expresiones regulares utilizan símbolos especiales para representar distintos tipos de caracteres.

| Símbolo | Significado | Ejemplo |
|---|---|---|
| `.` | Cualquier carácter, salvo normalmente los terminadores de línea | `a.c` |
| `\\d` | Un dígito | `\\d+` |
| `\\D` | Un carácter que no sea un dígito | `\\D+` |
| `\\w` | Un carácter de palabra: letras, dígitos o guion bajo | `\\w+` |
| `\\s` | Un espacio en blanco | `\\s+` |
| `[abc]` | Una letra entre las indicadas | `[abc]` |
| `[a-z]` | Una letra minúscula inglesa | `[a-z]+` |
| `[0-9]` | Un dígito entre 0 y 9 | `[0-9]+` |
| `[^0-9]` | Un carácter que no sea un dígito | `[^0-9]+` |

Es importante recordar que, en Java, algunos patrones necesitan dos barras invertidas dentro de las cadenas de texto. Por ejemplo, para utilizar `\d`, se escribe `"\\d"`.

## 5. Cuantificadores

Los cuantificadores indican cuántas veces puede aparecer un carácter o grupo de caracteres.

| Cuantificador | Significado |
|---|---|
| `*` | Cero o más repeticiones |
| `+` | Una o más repeticiones |
| `?` | Cero o una repetición |
| `{3}` | Exactamente tres repeticiones |
| `{2,5}` | Entre dos y cinco repeticiones |
| `{2,}` | Dos o más repeticiones |

Ejemplo:

```java
String patron1 = "\\d+";
String patron2 = "[A-Z]{3}";
String patron3 = "\\d{2,4}";
```

- `\\d+` busca uno o más dígitos.
- `[A-Z]{3}` busca exactamente tres letras mayúsculas inglesas consecutivas.
- `\\d{2,4}` busca entre dos y cuatro dígitos consecutivos.

## 6. Anclas y límites

Las anclas permiten indicar la posición en la que debe aparecer una coincidencia.

| Símbolo | Significado |
|---|---|
| `^` | Inicio de la entrada o línea, según las opciones utilizadas |
| `$` | Final de la entrada o línea, según las opciones utilizadas |
| `\\b` | Límite de palabra |

Por ejemplo, el patrón `^Hola$` permite comprobar que el texto completo sea exactamente `Hola` cuando se utiliza `matches()`.

```java
import java.util.regex.Pattern;

public class ValidarTexto {
    public static void main(String[] args) {
        String texto = "Hola";

        boolean valido =
            Pattern.matches("^Hola$", texto);

        System.out.println(valido);
    }
}
```

**Resultado:**

```text
true
```

## 7. Métodos importantes para trabajar con Regex

Java dispone de varios métodos útiles para trabajar con expresiones regulares.

### 7.1. matches()

Comprueba si toda la cadena coincide con el patrón.

```java
String numero = "12345";

System.out.println(numero.matches("\\d+"));
```

Resultado: `true`.

### 7.2. find()

Busca la siguiente coincidencia dentro del texto.

```java
import java.util.regex.*;

public class BuscarNumero {
    public static void main(String[] args) {
        String texto = "Pedido 125 y pedido 300";

        Matcher m = Pattern.compile("\\d+")
                           .matcher(texto);

        while (m.find()) {
            System.out.println(m.group());
        }
    }
}
```

Resultado:

```text
125
300
```

### 7.3. replaceAll()

Sustituye todas las coincidencias del patrón.

```java
String texto = "Tengo 3 manzanas y 5 peras";
String resultado = texto.replaceAll("\\d+", "X");

System.out.println(resultado);
```

Resultado:

```text
Tengo X manzanas y X peras
```

### 7.4. split()

Divide una cadena utilizando una expresión regular como separador.

```java
String texto = "rojo,verde;azul";
String[] colores = texto.split("[,;]");

for (String color : colores) {
    System.out.println(color);
}
```

Resultado:

```text
rojo
verde
azul
```

## 8. Ejemplos prácticos

### 8.1. Validar un número entero positivo

```java
public class ValidarNumero {
    public static void main(String[] args) {
        String numero = "245";

        if (numero.matches("[0-9]+")) {
            System.out.println("Número válido");
        } else {
            System.out.println("Número no válido");
        }
    }
}
```

Esta expresión comprueba que la cadena contiene uno o más dígitos del 0 al 9, sin signos ni espacios.

### 8.2. Comprobar un correo electrónico

```java
public class ValidarCorreo {
    public static void main(String[] args) {
        String correo = "usuario@ejemplo.com";

        String patron =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (correo.matches(patron)) {
            System.out.println("Formato correcto");
        } else {
            System.out.println("Formato incorrecto");
        }
    }
}
```

Este patrón comprueba un formato habitual de correo electrónico. No garantiza que la dirección exista ni que pueda recibir mensajes.

### 8.3. Buscar palabras que comiencen por mayúscula

```java
import java.util.regex.*;

public class BuscarMayusculas {
    public static void main(String[] args) {
        String texto = "Java es un lenguaje y Madrid es una ciudad";

        Matcher m = Pattern.compile("\\b[A-Z][a-z]*\\b")
                           .matcher(texto);

        while (m.find()) {
            System.out.println(m.group());
        }
    }
}
```

Resultado:

```text
Java
Madrid
```

Este ejemplo reconoce palabras con una primera letra mayúscula seguida de letras minúsculas inglesas. No contempla todas las letras acentuadas ni todos los sistemas de escritura.

## 9. Ventajas y limitaciones

### Ventajas

- Permiten comprobar formatos de manera rápida.
- Reducen la cantidad de código necesario para buscar patrones.
- Facilitan la extracción y sustitución de información.
- Pueden reutilizarse en diferentes partes de un programa.
- Están integradas en Java mediante su biblioteca estándar.

### Limitaciones

- Las expresiones complejas pueden resultar difíciles de leer.
- Un patrón incorrecto puede producir resultados inesperados.
- Las expresiones regulares no sustituyen todas las técnicas de análisis de texto.
- Los patrones excesivamente complejos pueden afectar al rendimiento.
- Una validación sintáctica no garantiza que un dato sea real o correcto en todos los sentidos.


## 10. Conclusión

Las expresiones regulares son una herramienta importante en Java porque permiten trabajar con texto de forma flexible y eficiente. Gracias a las clases `Pattern` y `Matcher`, es posible buscar coincidencias, validar cadenas y extraer información mediante patrones.

Comprender los metacaracteres, los cuantificadores, las anclas y los métodos principales facilita la creación de programas más seguros y organizados. La práctica con ejemplos sencillos es la mejor manera de aprender a construir expresiones regulares cada vez más complejas.
