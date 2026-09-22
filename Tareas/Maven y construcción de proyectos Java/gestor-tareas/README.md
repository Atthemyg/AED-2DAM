# Maven y construcción de proyectos Java
## 1. Crear el primer proyecto Maven

### **Creación de la estructura del proyecto**
<br>

![](.../../resources/1.png)

<br>
Añado el siguiente código en el pom.xml

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.codelearn</groupId>
  <artifactId>gestor-tareas</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <properties>
    <maven.compiler.release>21</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.13.0</version>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.5.2</version>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-jar-plugin</artifactId>
        <version>3.4.2</version>
      </plugin>
    </plugins>
  </build>
</project>
````
<br>

En el Main.java añado un código de prueba


![](.../../resources/2.png)


### **Ejercicio**

Si cambiamos el ``artifactId`` en el pom.xml Maven identificará el proyecto con las nuevas coordenadas. Para Maven, pasa a ser un artefacto distinto al original. 
Al ejecutar ``mvn validate`` se verifica únicamente que el proyecto esté correcto y que toda la información necesaria esté disponible.
Como el cambio de ``artifactId`` es sintácticamente válido, el comando terminará con un ``BUILD SUCCESS``.

![](.../../resources/3.png)

## 2. Compilar y entender los archivos generados

Si ejecuto:

```
mvn compile
java -cp target/classes com.codelearn.tareas.Main
```

Devuelve:

![](.../../resources/4.png)


### **Ejercicio**

Si ejecuto ``mvn clean`` la carpeta ``target/`` desaparece.

![](.../../resources/6.png)

Si vuelvo a ejecutar ``mvn compile`` debe de reconstruirse de nuevo.

![](.../../resources/7.png)


## 3. Ciclos de vida, fases y goals

Al ejecutar ``mvn clean package``, ``clean`` que pertenece a su propio ciclo, elimina resultados anteriores. ``package`` pertenece al ciclo ``default`` y alcanza todas las fases anteriores de ese ciclo.

Para un proyecto JAR, los enlaces habituales incluyen ``compiler:compile, surefire:test y jar:jar``.

### **Práctica integradora: preparar Linux y construir un proyecto Maven**

#### **Fase 1. Identificar el usuario y el entorno**

Primero utilizo los siguientes comandos:

```
whoami
pwd
java -version
javac -version
mvn -version
echo "$JAVA_HOME"
echo "$PATH"
```

![](.../../resources/8.png)


* `whoami` — Muestra el **usuario actual** del sistema.
* `pwd` — Muestra la **ruta de la carpeta** donde estás parado.
* `java -version` — Muestra la versión instalada del **entorno de ejecución (JRE)**.
* `javac -version` — Muestra la versión instalada del **compilador de Java (JDK)**.
* `mvn -version` — Muestra la versión instalada de **Apache Maven**.
* `echo "$JAVA_HOME"` — Muestra la **ruta de instalación** del JDK.
* `echo "$PATH"` — Muestra las **rutas del sistema** donde se buscan los comandos.

<br>

#### **Fase 2. Instalar una segunda versión del JDK**

A continuación voy a instalar la versión 17 Java con los siguientes comandos:

```
sudo apt update
sudo apt install openjdk-17-jdk
```

y compruebo que ambas versiones aparecen registradas:

```
update-alternatives --list java
update-alternatives --list javac
```

![](.../../resources/9.png)

<br>

#### **Fase 3. Seleccionar el JDK activo**

Ahora voy a utilizar temporalmente JDK 17 para java y javac:

```
sudo update-alternatives --config java
sudo update-alternatives --config javac
```

![](.../../resources/11.png)

y configuro las variables de entorno de la sesión, sustituyendo la ruta por la que existe en mi máquina:

```
export JAVA_HOME=/home/alumno/.jdks/ms-17.0.20.1
export PATH="$JAVA_HOME/bin:$PATH"
```

Para comprobar el cambio:

```
java -version
javac -version
mvn -version
echo "$JAVA_HOME"
```

![](.../../resources/12.png)

Por último, vuelvo a seleccionar JDK 21 y configuro sus variables

```
sudo update-alternatives --config java
sudo update-alternatives --config javac
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"
```

![](.../../resources/13.png)

![](.../../resources/14.png)

#### **Fase 4. Construir el proyecto**

Ejecuto un ``mvn clean verify`` que termina en un ``BUILD SUCCESS``, y compruebo que también Maven ha generado el resultado de construcción con ``find target -maxdepth 2 -type f | sort``

![](.../../resources/15.png)


El archivo JAR generado es:

`target/gestor-tareas-1.0.0-SNAPSHOT.jar`

Al ejecutar `mvn verify`, Maven ejecuta todas las fases anteriores del ciclo de vida hasta llegar a `verify`:

- **validate:** comprueba que el proyecto es correcto.
- **compile:** compila el código fuente.
- **test:** ejecuta los tests.
- **package:** empaqueta la aplicación y genera el archivo JAR.
- **verify:** realiza las comprobaciones finales para verificar que el paquete es válido.

<br>

#### **Fase 5. Comprobar la relación entre JDK y Maven**

Para comprobar la relación entre JDK y Maven, selecciono de nuevo JDK 17 y ejecuto ``mvn clean verify``. 


![](.../../resources/16.png)

El proyecto falla porque el pom.xml solicita Java 21 mediante ``maven.compiler.release``

Si vuelvo  a seleccionar JDK 21 otra vez y ejecuto ``mvn clean verify``, la ejecución termina correctamente.

![](.../../resources/17.png)

<br>

### **Añadir y utilizar una dependencia**

Añado en el pom.xml dentro de <project>, antes de <build>:

```
<dependencies>
  <dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.11.0</version>
  </dependency>
</dependencies>
```

y sustituyo el contenido de Main.java:

![](.../../resources/18.png)

Luego ejecuto:

```
mvn compile
mvn dependency:tree
```

![](.../../resources/19.png)


Maven descarga la biblioteca cuando la necesita. El árbol incluye ``com.google.code.gson:gson:jar:2.11.0:compile``. El scope por defecto es ``compile``.

### **Ejercicio**

Si cambio el título y recompilo: 

![](.../../resources/20.png)

Si retiro temporalmente la dependencia: 

![](.../../resources/21.png)

Gson es una biblioteca externa, por lo que Maven necesita tenerla indicada como dependencia en el archivo pom.xml.

<br>

### **Maven Central y el repositorio local**

Maven Central está disponible por defecto. La descarga termina en el repositorio local, normalmente en ``~/.m2/repository``

El pom.xml de una biblioteca permite conocer sus dependencias. La ruta refleja sus coordenadas.

Si hago ``mvn install``, Maven compila, prueba y empaqueta el proyecto, y además lo copia al repositorio local ``.m2``.



### **Ejercicio**

El pom.xml de Gson se encuentra en el repositorio local de Maven, dentro de:

`~/.m2/repository/com/google/code/gson/gson/2.11.0/gson-2.11.0.pom`

![](.../../resources/22.png)

El pom.xml de nuestra aplicación se encuentra después de ejecutar `mvn install` en:

`~/.m2/repository/com/codelearn/gestor-tareas/1.0.0-SNAPSHOT/gestor-tareas-1.0.0-SNAPSHOT.pom`

![](.../../resources/23.png)

El comando `mvn install` no publica el proyecto para otras personas porque solamente instala el proyecto en el repositorio local de Maven del ordenador donde se ejecuta.

Por tanto, el proyecto queda disponible para otros proyectos Maven de ese mismo ordenador, pero no se sube a Internet ni a un repositorio remoto.

Para que otras personas puedan utilizarlo, habría que publicarlo en un repositorio remoto, como Maven Central o un repositorio privado de una empresa.

<br>

### **Repositorios externos y settings.xml**

El pom.xml expresa las necesidades del proyecto. ``settings.xml`` contiene configuración del entorno, como servidores, proxy, mirrors y perfiles. No se crea automáticamente en todas las instalaciones.

Creo una carpeta config en el proyecto y guardo sin credenciales ``config/settings-publico.xml`` con:

```
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd">
  <profiles>
    <profile>
      <id>repositorio-publico</id>
      <repositories>
        <repository>
          <id>central-explicito</id>
          <url>https://repo.maven.apache.org/maven2</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>false</enabled></snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>
</settings>
```

Si ejecuto:

```
mvn -s config/settings-publico.xml -Prepositorio-publico help:active-profiles
mvn -s config/settings-publico.xml -Prepositorio-publico compile
```

![](.../../resources/24.png)

aparece ``repositorio-publico`` entre los perfiles activos. 

``-s``selecciona el archivo de configuración del usuario; no reemplaza el pom.xml ni elimina necesariamente los settings globales.


### **Ejercicio**

Con el comando:

`mvn -s config/settings-publico.xml -Prepositorio-publico help:active-profiles`

se activa el perfil `repositorio-publico`, por lo que aparece entre los perfiles activos.

Al ejecutar:

`mvn -s config/settings-publico.xml -Prepositorio-publico compile`

![](.../../resources/25.png)

el proyecto se compila correctamente utilizando la configuración del archivo `settings-publico.xml`.

Después se ejecutó la misma comprobación sin `-Prepositorio-publico`. En este caso, el perfil `repositorio-publico` no se activa, porque no se ha indicado mediante la opción `-P`.

Sin embargo, el proyecto puede seguir descargando dependencias y compilándose correctamente porque Maven utiliza Central como repositorio por defecto. Por tanto, el perfil creado sirve para añadir explícitamente ese repositorio, pero no es necesario para utilizar Central en un proyecto Maven normal.