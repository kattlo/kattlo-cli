![Kattlo](./artwork/kattlo.png)

# Apache Kafka® Configuration Made Easy

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
- maintain the configuration and avoid drifts
- helps to known when a topic was removed (when its managed by Kattlo)
- access the history of migrations
- your DevOps toolset to properly manages the topic within clusters

## Install

### Linux Binary

```bash
curl 'https://github.com/kattlo/kattlo-cli/releases/download/v0.1.1/kattlo-v0.1.1-linux' \
  -o 'kattlo'

sudo chmod +x kattlo
sudo mv kattlo /usr/local/sbin/kattlo
```

### Linux Packages

The are `.deb` and `.rpm` packages available. Do a check in
[the latest release](https://github.com/kattlo/kattlo-cli/releases/latest).

### MacOS

```bash
curl 'https://github.com/kattlo/kattlo-cli/releases/download/v0.1.1/kattlo-v0.1.1-mac' \
  -o 'kattlo'

sudo chmod +x kattlo
sudo mv kattlo /usr/local/bin/kattlo
```

### Windows

- download the [latest release](https://github.com/kattlo/kattlo-cli/releases/latest) package for windows
- unzip it
- copy `VCRUNTIME140.dll` to `C:\Windows\System32\`
- get the absolute path to that unzipped directory
- add the absolute path of Kattlo to your `PATH` environment variable
- open the prompt and type: `kattlo -V`

## Released Features

- [x] Topic migrations
  - [x] apply migrations
  - [x] import existing topics
  - [x] show info and history
  - [x] generate migration example
  - [x] rules enforcement
  - [x] human readable values for configurations
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

### Init

To init new Kattlo project you just run the following command:

```bash
kattlo init --directory='/path/to/initialize'
```

Use the `--bootstrap-servers` to generate the Kattlo config
with right Kafka addresses:

```bash
kattlo --bootstrap-servers='my-kafka-b1:9092,my-kafka-b2:9092' \
  init --directory='/path/to/initialize'
```

> If you suppress the `--directory` option, the current folder will be
initialized.

### Gen

To make easy the process to write down the migrations, you may use
then gen command to genereate migration files:

```bash
kattlo gen migration --resource=TOPIC --diretory='/path/to/gen/migration'
```

> If you suppress the `--directory` option, the migration example will
be gerenated in the current directory.

## Best Practices

- Always create a new migration file and never change an applied one
- Create a directory for each resource that you want to manage
  - Kattlo is able to process many distinct resources migrations within same directory,
  but you get better organization following that practice

### File Naming

All migrations are defined using physical files, and they must follow
this naming pattern:

- `v[0-9]{4}_[\\w\\-]{0,246}\\.ya?ml`

Simplifing:

- `v0000_the-name-of-my-migration.yaml`
- where `v0000` will be the version of resource migration, from `1` to `n`
- when a new migration is created, increase the version

### File Content

Every migration file must have exatcly one resource migration.

Never mix `create`, `patch` or `remove` in the same file or same operations
for distinct resources.

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
