# Kattlo Examples

1. Install Kattlo
2. Create the `kafka.properties` file
3. Run the examples

## Topics

- Create a topic

```bash
kattlo \
  --config-file='.kattlo.yaml' \
  --kafka-cfg='kafka.properties' \
  topic \
  --directory='topic/01_create_with_config'
```

- Create a topic and patch the partitions

```bash
kattlo \
  --config-file='.kattlo.yaml' \
  --kafka-cfg='kafka.properties' \
  topic \
  --directory='topic/02_create_patch_partitions'
```

- Create a topics and patch to increase the replication factor

```bash
kattlo \
  --config-file='.kattlo.yaml' \
  --kafka-cfg='kafka.properties' \
  topic \
  --directory='topic/03_create_patch_replication_factor'
```
