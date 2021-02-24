![Kattlo](./artwork/kattlo.png)

# Apache Kafka® Configuration Made Easy

:tada: Checkout the brand new Kattlo Documentation site :tada:

- [https://kattlo.github.io](https://kattlo.github.io)

Use an approach like Database Migrations to manage your evolutionary
configurations for:

- Topics
- Schemas
- ACLs
- ksqlDB
- Connect
- Cluster
- Quotas
- Users

:bulb: Check the [examples directory](./examples) :bulb:

## Kattlo is good for ...

- enterprises that needs a stable way to change Apache Kafka® configurations
- maintaining the configuration and avoid drifts
- knowing when a topic was removed (when its managed by Kattlo)
- accessing the history of migrations
- your DevOps toolset to properly manages the topic within the clusters

## :construction_worker: Made With

- [Java 11](https://adoptopenjdk.net/)
- [Picocli](https://picocli.info/)
- [Quarkus](https://quarkus.io/)
- [GraalVM](https://www.graalvm.org/)
- [and a lot of coffee ☕](https://www.buymeacoffee.com/fabiojose)

## Install

- [See installation instructions for MacOS and Windows](https://kattlo.github.io/docs/installation/)

### Linux Binary

```bash
curl 'https://github.com/kattlo/kattlo-cli/releases/download/v0.2.1/kattlo-v0.2.1-linux' \
  -o 'kattlo'

sudo chmod +x kattlo
sudo mv kattlo /usr/local/sbin/kattlo
```

### Linux Packages

The are `.deb` and `.rpm` packages available. Do a check in
[the latest release](https://github.com/kattlo/kattlo-cli/releases/latest).

## Released Features

- [x] Topic migrations
  - [x] apply migrations
  - [x] import existing topics
  - [x] show info and history
  - [x] generate migration example
  - [x] rules enforcement :sparkles:
  - [x] human readable values for configurations :sparkles:
- [ ] ACL migrations
- [ ] Schema migrations
- [ ] Quota migrations
- [ ] User migrations
- [ ] Connect migrations
- [ ] ksqlDB migrations
- [ ] Cluster migrations
- [x] Utilities
  - [x] init project
  - [ ] new config for consumers
  - [ ] new config for producers

## Usage

### Common Options

- `--config-file` (optional): Path to Kattlo configuration file for migrations
- `--kafka-config-file` (required): Path to properties file to be used for Kafka Admin Client

In the `.kattlo.yaml` configuration file you may define the following
properties:

```yaml
rules:
  topic:
    namePattern: 'your pattern'
    # more rules constraints...
```

In the `--kafka-config-file` you may put the properties described at
[official documentation](https://kafka.apache.org/documentation/#adminclientconfigs).

Example of `kafka.properties`:

```properties
bootstrap.servers=localhost:19092,localhost:29092
client.id=kattlo-cli
```

__Command__:

```bash
kattlo --config-file='.kattlo.yaml' \
       --kafka-config-file='kafka.properties' \
       <command>
       [command arguments]
```

You can call for help for any command:

```bash
kattlo -h

# topic
kattlo topic -h

# init
kattlo init -h
```

### Gen

To make easy the process to write down the migrations, you may use
then gen command to genereate migration files:

```bash
kattlo gen migration \
  --resource=TOPIC \
  --diretory='/path/to/gen/migration'
```

> If you suppress the `--directory` option, the migration example will
be gerenated in the current directory.

## Migrations

Kattlo provide a way to declare what we want using yaml notation. Based
on that files, Kattlo runs the necessary Admin commands to create, patch or
remove resources.

> [See examples](./examples) to see the variations of usage.

Resources can be:

- topics
- schemas
- ACLs

[See Kattlo's docs about Topics Migrations](https://kattlo.github.io/docs/topics/migrations/)

## Internals

Kattlo needs to have all permissions to manage topics, ACLs and Schemas
configurations, outherwise you will be not able to perform the migrations.

In order to manage the migrations we use special topics:

- `__kattlo-topics-state`: the topics' migrations state
- `__kattlo-topics-history`: the topics' migrations history

### `__kattlo-topics-state`

> To persist the current state per topic.

This topic has the following configurations:

- partitions: `50`
- desired replication-factor: `2`

### `__kattlo-topics-history`

> To persist the histories for topics.

This topic has the following configurations:

- partitions: `50`
- desired replication-factor: `2`

## Build and Run

### Native

```bash
./gradlew clean build -Dquarkus.package.type=native \
 -Dquarkus.native.container-build=true \
 -Dquarkus.native.additional-build-args=--report-unsupported-elements-at-runtime,--allow-incomplete-classpath,-H:IncludeResources='.*yaml$',-H:Log=registerResource:
```

You can then execute your native executable with: `./build/kattlo-1.0-SNAPSHOT-runner`

> Configure `-Dquarkus.native.container-build` to `false` if you want o use your graalvm
installation instead of Docker image.

### Running in verbose mode

If you are experiencing issues, run Kattlo with logging set to DEBUG.

```bash
kattlo -Dquarkus.log.level=DEBUG ...
```

### Packaging and running the application

The application can be packaged using `./gradlew quarkusBuild`.
It produces the `kattlo-1.0-SNAPSHOT-runner.jar` file in the `build` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/lib` directory.

The application is now runnable using `java -jar build/kattlo-1.0-SNAPSHOT-runner.jar`.

If you want to build an _über-jar_, just add the `--uber-jar` option to the command line:

```
./gradlew quarkusBuild --uber-jar
```

## Notes

- Kattlo icon by <a href="http://www.freepik.com/" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
