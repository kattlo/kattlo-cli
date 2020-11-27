# Apache Kafka® Configuration Made Easy

Use an approach like Database Migrations to manage your evolutionary
configurations with:

- Topics
- Schemas
- ACLs
- Clusters
- and more soon . . .

## Released Features

- [x] Topic migrations
- [ ] Schema migrations
- [ ] ACL migrations
- [ ] Cluster migrations

## Usage

### Common Options

- `--config-file`: Path to Kattlo configuration file for migrations
- `--kafka-cfg`: Path to properties file to be used for Kafka Admin Client

In the `.kattlo.yaml` configuration file you may define the following
properties:

```yaml
TODO
```

In the Kafka Admin client file you may put the properties described at [official documentation](https://kafka.apache.org/documentation/#adminclientconfigs).

__Example__: `kafka.properties`

```properties
bootstrap.servers=localhost:19092,localhost:29092
client.id=kattlo-cli
```

__Command__:

```bash
kattlo --config-file='.kattlo.yaml' \
      --kafka-cfg='kafka.properties'
```

## Examples

```bash
build/ottla-1.0-SNAPSHOT-runner \
  --config-file=src/test/resources/.kattlo.yaml \
  --kafka-cfg=src/test/resources/kafka.properties \
  topic \
  --directory=src/test/resources/topics/
```

## Internals

Kattlo needs to have all permissions to manage topics, ACLs, schemas and clusters
configurations, outherwise you will be not able to perform the migrations.

In order to manage the migrations, we use four special topics:

- `__kattlo_topic_migrations`:
- `__kattlo_schema_migrations`:
- `__kattlo_acl_migrations`:
- `__kattlo_cluster_migrations`:

### `__kattlo_topic_migrations`

> To persist migrations per topic.

This topic has the following configurations:

- partitions: `50`
- replication-factor: `2`

```properties

```

## Build and Run

TODO

### Native

```bash
./gradlew build \
  -Dquarkus.package.type=native \
  -Dquarkus.native.container-build=true
```

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./gradlew quarkusDev
```

### Packaging and running the application

The application can be packaged using `./gradlew quarkusBuild`.
It produces the `ottla-1.0-SNAPSHOT-runner.jar` file in the `build` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/lib` directory.

The application is now runnable using `java -jar build/ottla-1.0-SNAPSHOT-runner.jar`.

If you want to build an _über-jar_, just add the `--uber-jar` option to the command line:
```
./gradlew quarkusBuild --uber-jar
```

### Creating a native executable

You can create a native executable using:

```bash
./gradlew build -Dquarkus.package.type=native \
 -Dquarkus.native.container-build=true \
 -Dquarkus.native.additional-build-args=--report-unsupported-elements-at-runtime,--allow-incomplete-classpath
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./build/ottla-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling#building-a-native-executable.

## Made with :purple_heart: by

- fabiojose
