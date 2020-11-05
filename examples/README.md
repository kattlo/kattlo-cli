# Kattlo Examples

Install Kattlo and run the examples.

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
