# Building FastMouseLogger from Source

## Prerequisites

- JDK 17+
- Windows 10/11 (for native Win32 Raw Input capture)
- Maven 3.9+

## Build

```bash
mvn clean package
```

## Run Tests

```bash
mvn test
```

## Installation via JitPack

```xml
<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastMouseLogger</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```
