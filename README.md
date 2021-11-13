# Ktor Plugin
Provides Ktor Server libs for building awesome Kotlin plugins which needs to provide builtin HTTP servers.

Requires: https://github.com/PowerNukkit/KotlinPlugin

## Included Libraries

### Kotlin
* [ktor-server-core](https://ktor.io/docs/creating-http-apis.html)
* [ktor-server-cio](https://ktor.io/docs/engines.html)
* [ktor-server-cio](https://ktor.io/docs/engines.html)
* [ktor-serialization](https://ktor.io/docs/serialization.html)
* [ktor-auth](https://ktor.io/docs/authentication.html)
* [ktor-metrics-micrometer](https://ktor.io/docs/authentication.html)
* [ktor-metrics-micrometer](https://ktor.io/docs/micrometer-metrics.html)
* [micrometer-registry-prometheus](https://ktor.io/docs/micrometer-metrics.html)

## How to use in your plugin

Add this dependency to your `plugin.yml` file (required):
```yaml
depend:
  - KtorLib 
```

Add a library dependency to your project using the examples bellow (recommended).

Make your plugin class extend `KotlinPluginBase` (optional).

Note: We haven't published to the maven central yet, but the snapshots are available at Sonatype OSS snapshots repository.

### Gradle (Kotlin DSL)
```kotlin
repositories {
    maven(url="https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("org.powernukkit:powernukkit:1.5.1.0-PN")
    implementation("org.powernukkit.plugins:ktor-plugin-lib:1.6.5+0.1.0+2021.11.13-SNAPSHOT")
}
```

### Gradle (Groovy DSL)
```groovy
repositories {
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
}

dependencies {
    implementation 'org.powernukkit:powernukkit:1.5.1.0-PN'
    implementation 'org.powernukkit.plugins:ktor-plugin-lib:1.6.5+0.1.0+2021.11.13-SNAPSHOT'
}
```

### Maven
```xml
<repositories>
    <repository>
        <id>sonatype-oss-snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.powernukkit</groupId>
        <artifactId>powernukkit</artifactId>
        <version>1.5.1.0-PN</version>
    </dependency>
    <dependency>
        <groupId>org.powernukkit.plugins</groupId>
        <artifactId>ktor-plugin-lib</artifactId>
        <version>1.6.5+0.1.0+2021.11.13-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Cloning and importing
1. Just do a normal `git clone https://github.com/PowerNukkit/KotlinPlugin.git` (or the URL of your own git repository)
2. Import the `build.gradle.kts` file with your IDE, it should do the rest by itself

## Running
1. Just do a normal `git clone https://github.com/PowerNukkit/KotlinPlugin.git` (or the URL of your own git repository)
2. `cd KotlinPlugin` (or the name of your project)
3. `./gradlew run`

## Debugging
1. Import the project into your IDE
2. Make your IDE run the `debug` gradle task in debug mode

### Debuging using IntelliJ IDEA
Import the project and do this:  
![](https://i.imgur.com/eJxjEX0.png)
