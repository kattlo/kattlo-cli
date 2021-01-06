# Apache Kafka® Configuration Made Easy

Use an approach like Database Migrations to manage your evolutionary
configurations for:

- Topics
- Schemas
- ACLs
- ksqlDB
- Connect
- Cluster
- and more soon . . .

:bulb: Check the [examples directory](./examples) :bulb:

## Kattlo helps with

- maintain the configuration and avoid drifts
- helps to known when a topic was removed
- access the history of migrations
- and more . . .

## Install

```bash
curl ...

sudo chmod +x kattlo
sudo mv kattlo /usr/sbin/kattlo
```

## Released Features

- [x] Topic migrations
- [ ] ACL migrations
- [ ] Schema migrations
- [ ] ksqlDB migrations
- [ ] Cluster migrations

## Usage

### Common Options

- `--config-file`: Path to Kattlo configuration file for migrations
- `--kafka-config-file`: Path to properties file to be used for Kafka Admin Client

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
       --kafka-config-file='kafka.properties' \
       <command>
       [command arguments]
```

## Examples

Set the directory with migrations using the `--directory` option:
```bash
kattlo \
  --config-file='examples/.kattlo.yaml' \
  --kafka-config-file='examples/kafka.properties' \
  topic \
  --directory='examples/topics/01_create_with_config'
```

Directory with migrations will be default to current, when `--directory` is
suppressed:
```bash
kattlo \
  --config-file='examples/.kattlo.yaml' \
  --kafka-config-file='examples/kafka.properties' \
  topic
```

The current directory contains `kafka.properties` and `.kattlo.yaml`:
```bash
kattlo \
  topic \
  --directory='examples/topics/01_create_with_config'
```

Want to use the `kafka.properties`, but in another cluster:
```bash
kattlo \
  --config-file='examples/.kattlo.yaml' \
  --kafka-config-file='examples/kafka.properties' \
  --bootstrap-servers='my.kafka:9092' \
  topic \
  --directory='examples/topics/01_create_with_config'
```

The option `--bootstrap-servers` overrides the config [`bootstrap.servers`](https://kafka.apache.org/documentation/#adminclientconfigs_bootstrap.servers).

### Import

To import existing topics to Kattlo.

```bash
kattlo \
  --config-file='examples/.kattlo.yaml' \
  --kafka-config-file='examples/kafka.properties' \
  --bootstrap-servers='my.kafka:9092' \
  topic \
  --directory='/path/to/migrations/for/my/existing/topic' \
  import \
  --topic='my-existing-topic'
```

The operation above will import the existing topic, create the very first
migration with create operation and the necessary stuff to enable that
topic as a managed resource.

- file automatically create within `--directory`: `v0001_create-topic.yaml`

## Main Concepts

TODO

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

> See [examples](./examples) to see the variations of usage.

Resources can be:

- topics
- schemas
- ACLs

### Topics

This is the way to manage topics resources within Apache Kafka®.

To __create__ a topic:
```yaml
operation: create # The operation over the resource
notes: |
  Write down whatever you want to describe this.
  This can be a multiline text . . .
topic: topic_name
partitions: 1 #partitions is optional
replicationFactor: 1 #replicationFactor is optional
config: #config is optional
  compression.type: gzip
  cleanup.policy  : compact
  # Any configuration available here:
  #   https://kafka.apache.org/documentation/#topicconfigs
```

Notes about `create`:
- if you want all cluster default values for `partitions`, `replicationFactor`
and `config`, just suppress them

To __patch__ a topic:

```yaml
operation: patch # The operation over the resource
notes: Patch partitions to 3 # Describe your patch . . .
topic: 02_create_patch_partitions # Topic that was created before
partitions: 3 #partitions is optional
replicationFactor: 2 #replicationFactor is optional
config: #config is optional
  retention.ms: -1
  # Any configuration available here:
  #   https://kafka.apache.org/documentation/#topicconfigs

  compresstion.type: $default # Patch to cluster default value
```

Notes about `patch`:
- `partitions` can not be reduced
- at least one of `partitions`, `replicationFactor` or `config` must be present
in the migration file
- use the keyword `$default` to patch config to cluster default value

To __remove__ a topic:

```yaml
operation: remove
notes: Descrive your motivation to remove this topic
topic: 05_create_and_remove # Topic to remove
```

Notes about `remove`:
- remove will delete the entire topic data
- all migrations history about the topic will be maintained

## Internals

Kattlo needs to have all permissions to manage topics, ACLs and Schemas
configurations, outherwise you will be not able to perform the migrations.

In order to manage the migrations, we use four special topics:

- `__kattlo-topics-state`: the topics' migrations state
- `__kattlo-topics-history`: the topics' migrations history

### `__kattlo-topics-state`

> To persist the current state per topic.

This topic has the following configurations:

- partitions: `50`
- replication-factor: `2`

Kafka CLI to create the state topic:

```bash
kafka-topics.sh --create \
  --bootstrap-server 'localhost:9092' \
  --replication-factor 1 \
  --partitions 50 \
  --topic '__kattlo-topics-state' \
  --config 'cleanup.policy=compact' \
  --config 'segment.ms=3000' \
  --config 'segment.bytes=104857600' \
  --config 'compression.type=producer' \
  --config 'message.timestamp.type=CreateTime' \
  --config 'delete.retention.ms=0' \
  --config 'min.cleanable.dirty.ratio=0.0001'
```

### `__kattlo-topics-history`

> To persist the histories for topics.

This topic has the following configurations:

- partitions: `50`
- replication-factor: `2`

Kafka CLI to create the state topic:

```bash
kafka-topics.sh --create \
  --bootstrap-server 'localhost:9092' \
  --replication-factor 1 \
  --partitions 50 \
  --topic '__kattlo-topics-history' \
  --config 'cleanup.policy=delete' \
  --config 'retention.ms=-1' \
  --config 'segment.ms=3000' \
  --config 'segment.bytes=104857600' \
  --config 'compression.type=producer' \
  --config 'message.timestamp.type=CreateTime' \
  --config 'delete.retention.ms=0' \
  --config 'min.cleanable.dirty.ratio=0.0001'
```

## Build and Run

TODO

### Native

```bash
./gradlew build -Dquarkus.package.type=native \
  -Dquarkus.native.container-build=true \
  -Dquarkus.native.additional-build-args=--report-unsupported-elements-at-runtime,--allow-incomplete-classpathe
```

You can then execute your native executable with: `./build/kattlo-1.0-SNAPSHOT-runner`

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

## Made with :purple_heart: by

- fabiojose
