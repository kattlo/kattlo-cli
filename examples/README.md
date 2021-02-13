# Kattlo Examples

1. Install Kattlo
2. Create the `kafka.properties` file
3. Run the examples
   - make sure your current folder is `examples`

## Topics

- Create a topic

```bash
kattlo \
  --config-file='.kattlo.yaml' \
  --kafka-config-file='kafka.properties' \
  topic \
  --directory='topic/01_create_with_config'
```

- Create a topic and patch the number of partitions

```bash
kattlo \
  --config-file='.kattlo.yaml' \
  --kafka-config-file='kafka.properties' \
  topic \
  --directory='topic/02_create_patch_partitions'
```

- Create a topic and patch to increase the replication factor

> you need a cluster with two or more brokers to run the following example

```bash
kattlo \
  --config-file='.kattlo.yaml' \
  --kafka-config-file='kafka.properties' \
  topic \
  --directory='topic/03_create_patch_replication_factor'
```

- Create a topic and patch to reduce the replication factor

> you need a cluster with two or more brokers to run the following example

```bash
kattlo \
  --config-file='.kattlo.yaml' \
  --kafka-config-file='kafka.properties' \
  topic \
  --directory='topic/04_create_patch_reduce_replication_factor'
```

- Create a topic and remove it

```bash
kattlo \
  --config-file='.kattlo.yaml' \
  --kafka-config-file='kafka.properties' \
  topic \
  --directory='topic/05_create_and_remove'
```

- Create a topic and patch config to cluster default

```bash
kattlo \
  --config-file='.kattlo.yaml' \
  --kafka-config-file='kafka.properties' \
  topic \
  --directory='topic/06_create_patch_config_to_default'
```

- Create a topic and patch to add new config

```bash
kattlo \
  --config-file='.kattlo.yaml' \
  --kafka-config-file='kafka.properties' \
  topic \
  --directory='topic/07_create_patch_add_new_config'
```

- Many topics migrations in the same directory

 > Do not use this approach, otherwise your migration management will become a
 mess

 ```bash
kattlo \
  --config-file='.kattlo.yaml' \
  --kafka-config-file='kafka.properties' \
  topic \
  --directory='topic/08_many_topics_same_dir'
 ```

### Rules Enforcement

- [Human readable examples](./topic/rules/human)
- [Machine readable examples](./topic/rules/machine)

- Topic name does not follow de rule
```bash
kattlo \
  --config-file='topic/rules/human/.kattlo.yaml' \
  --kafka-config-file='kafka.properties' \
  topic \
  --directory='topic/rules/01_incorrect_topic_name'
```

- Topic name follows de rule
```bash
kattlo \
  --config-file='topic/rules/human/.kattlo.yaml' \
  --kafka-config-file='kafka.properties' \
  topic \
  --directory='topic/rules/02_correct_topic_name'
```

- Topic config does not follow the rules
```bash
kattlo \
  --config-file='topic/rules/human/.kattlo.yaml' \
  --kafka-config-file='kafka.properties' \
  topic \
  --directory='topic/rules/03_incorrect_config'

# The output will be:

 Topic Rule Violation:

  - partitions: expected '>=3', but was '2'
  - replicationFactor: expected '==2', but was '3'
  - compression.type: expected 'in [lz4, snappy]', but was 'gzip'
  - min.cleanable.dirty.ratio: expected '>=1%', but was '0.001'
  - max.message.bytes: expected '<=900KiB', but was '1048576'
```
