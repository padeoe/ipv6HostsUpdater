# google ipv6 hosts updater

## Requirement

- Java 8+
- maven

## How to Build and Run
```bash
mvn package
```

Prepare your old hosts file, if in the same directory(`./hosts`), 
use command like this to generate new file:

```bash
java -jar taget/ipv6-hosts-updater-0.1-jar-with-dependencies.jar ./hosts
```