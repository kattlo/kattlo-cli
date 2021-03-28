package com.github.kattlo.acl.yaml;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.*;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.*;

import java.nio.file.Path;
import java.util.Map;

import com.github.kattlo.core.yaml.MigrationLoader;
import com.jayway.jsonpath.Configuration;

import org.everit.json.schema.ValidationException;
import org.junit.jupiter.api.Test;

public class LoaderTest {

    @Test
    public void should_load_create_yaml_as_java_map() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");

        var actual = Loader.loadAsMap(yaml);

        assertThat(actual, instanceOf(Map.class));
        assertNotNull(actual.get("create"));
    }

    @Test
    public void should_load_patch_yaml_as_java_map() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_patch.yaml");

        var actual = Loader.loadAsMap(yaml);

        assertThat(actual, instanceOf(Map.class));
        assertNotNull(actual.get("patch"));
    }

    @Test
    public void should_load_remove_by_id_yaml_as_java_map() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_remove-by-access-id.yaml");

        var actual = Loader.loadAsMap(yaml);

        assertThat(actual, instanceOf(Map.class));
        assertNotNull(actual.get("remove"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_load_remove_by_declaration_yaml_as_java_map() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_remove-by-declaration.yaml");

        var actual = Loader.loadAsMap(yaml);

        assertThat(actual, instanceOf(Map.class));
        assertNotNull(actual.get("remove"));
        assertNotNull(((Map<String, Object>)actual.get("remove")).get("declaration"));
    }

    @Test
    public void should_convert_create_java_map_as_json_string() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = Configuration.defaultConfiguration().jsonProvider().parse(json);

        assertThat(actual, hasJsonPath("$.create.notes", equalTo("Notes about this ACL creation by Principal\n")));
        assertThat(actual, hasJsonPath("$.create.to.principal", equalTo("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown")));
        assertThat(actual, hasJsonPath("$.create.allow.connection.from[0]", equalTo("172.16.0.100")));
        assertThat(actual, hasJsonPath("$.create.allow.topic.name", equalTo("my-topic-name")));
        assertThat(actual, hasJsonPath("$.create.allow.group.id", equalTo("my-group.id")));
        assertThat(actual, hasJsonPath("$.create.allow.cluster"));
        assertThat(actual, hasJsonPath("$.create.allow.transactional.id", equalTo("my-transactional.id")));

        assertThat(actual, hasJsonPath("$.create.deny.connection.from[0]", equalTo("192.168.0.100")));
        assertThat(actual, hasJsonPath("$.create.deny.topic.name", equalTo("another-topic-name")));
        assertThat(actual, hasJsonPath("$.create.deny.group.id", equalTo("another-group.id")));
        assertThat(actual, hasJsonPath("$.create.deny.transactional.id", equalTo("another-transactional.id")));
    }

    @Test
    public void should_convert_patch_java_map_as_json_string() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_patch.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = Configuration.defaultConfiguration().jsonProvider().parse(json);

        assertThat(actual, hasJsonPath("$.patch.notes", equalTo("Notes about this ACL patch\n")));
        assertThat(actual, hasJsonPath("$.patch.to.principal", equalTo("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown")));

        assertThat(actual, hasJsonPath("$.patch.allow.connection.from[0]", equalTo("192.168.0.20")));
        assertThat(actual, hasJsonPath("$.patch.allow.topic.name", equalTo("my-topic-name")));
        assertThat(actual, hasJsonPath("$.patch.allow.group.id", equalTo("my-group.id")));

        assertThat(actual, hasJsonPath("$.patch.deny.connection.from[0]", equalTo("172.16.0.3")));
        assertThat(actual, hasJsonPath("$.patch.deny.cluster"));
        assertThat(actual, hasJsonPath("$.patch.deny.transactional.id", equalTo("my-transactional.id")));
    }

    @Test
    public void should_convert_remove_by_id_java_map_as_json_string() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_remove-by-access-id.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = Configuration.defaultConfiguration().jsonProvider().parse(json);

        assertThat(actual, hasJsonPath("$.remove.notes", equalTo("Remove an access using the id generated to create and patches, deleting\nexactly what was allowed and/or denied\n")));
        assertThat(actual, hasJsonPath("$.remove.accessId", equalTo("96ed5bfc-0ee6-45ca-a50c-3baa7214de57")));
    }

    @Test
    public void should_convert_remove_by_declaration_java_map_as_json_string() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_remove-by-declaration.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = Configuration.defaultConfiguration().jsonProvider().parse(json);

        assertThat(actual, hasJsonPath("$.remove.notes", equalTo("Remove accesses by declaration. The adhoc approach to remove\nwhat was allowed and/or denied.\n")));
        assertThat(actual, hasJsonPath("$.remove.declaration.to.principal", equalTo("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown")));
        assertThat(actual, hasJsonPath("$.remove.declaration.allow.origin[0]", equalTo("192.168.0.1")));
        assertThat(actual, hasJsonPath("$.remove.declaration.allow.permission[0]", equalTo("Read")));
        assertThat(actual, hasJsonPath("$.remove.declaration.deny.origin[0]", equalTo("192.168.0.20")));
        assertThat(actual, hasJsonPath("$.remove.declaration.deny.permission[0]", equalTo("Write")));

        assertThat(actual, hasJsonPath("$.remove.declaration.inthe.topic[0]", equalTo("my-topic-name-0")));

        assertThat(actual, hasJsonPath("$.remove.declaration.inthe.group[0]", equalTo("my-group.id-1")));

        assertThat(actual, hasJsonPath("$.remove.declaration.inthe.cluster"));

        assertThat(actual, hasJsonPath("$.remove.declaration.inthe.transactional[0]", equalTo("my-transactional.id-1")));
    }

    @Test
    public void should_parse_stringified_create_json_to_object() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actualJson = MigrationLoader.parseJson(json);
        var actual = actualJson.getJSONObject("create");

        assertEquals("Notes about this ACL creation by Principal\n", actual.getString("notes"));
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown", actual.getJSONObject("to").getString("principal"));

        assertEquals("172.16.0.100", actual.getJSONObject("allow").getJSONObject("connection").getJSONArray("from").getString(0));
        assertEquals("my-topic-name", actual.getJSONObject("allow").getJSONObject("topic").getString("name"));
        assertEquals("my-group.id", actual.getJSONObject("allow").getJSONObject("group").getString("id"));
        assertNotNull(actual.getJSONObject("allow").getJSONObject("cluster"));
        assertEquals("my-transactional.id", actual.getJSONObject("allow").getJSONObject("transactional").getString("id"));

        assertEquals("192.168.0.100", actual.getJSONObject("deny").getJSONObject("connection").getJSONArray("from").getString(0));
        assertEquals("another-topic-name", actual.getJSONObject("deny").getJSONObject("topic").getString("name"));
        assertEquals("another-group.id", actual.getJSONObject("deny").getJSONObject("group").getString("id"));
        assertEquals("another-transactional.id", actual.getJSONObject("deny").getJSONObject("transactional").getString("id"));
    }

    @Test
    public void should_parse_stringified_patch_json_to_object() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_patch.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actualJson = MigrationLoader.parseJson(json);
        var actual = actualJson.getJSONObject("patch");

        assertEquals("Notes about this ACL patch\n", actual.getString("notes"));

        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown", actual.getJSONObject("to").getString("principal"));

        assertEquals("192.168.0.20", actual.getJSONObject("allow").getJSONObject("connection").getJSONArray("from").getString(0));
        assertEquals("my-topic-name", actual.getJSONObject("allow").getJSONObject("topic").getString("name"));
        assertEquals("my-group.id", actual.getJSONObject("allow").getJSONObject("group").getString("id"));

        assertEquals("172.16.0.3", actual.getJSONObject("deny").getJSONObject("connection").getJSONArray("from").getString(0));
        assertNotNull(actual.getJSONObject("deny").getJSONObject("cluster"));
        assertEquals("my-transactional.id", actual.getJSONObject("deny").getJSONObject("transactional").getString("id"));

    }

    @Test
    public void should_parse_stringified_remove_by_id_json_to_object() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_remove-by-access-id.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actualJson = MigrationLoader.parseJson(json);
        var actual = actualJson.getJSONObject("remove");

        assertEquals("Remove an access using the id generated to create and patches, deleting\nexactly what was allowed and/or denied\n", actual.getString("notes"));

        assertEquals("96ed5bfc-0ee6-45ca-a50c-3baa7214de57", actual.getString("accessId"));

    }

    @Test
    public void should_create_yaml_converted_to_json_pass_through_validation() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        Loader.validade(actual);
    }

    @Test
    public void should_patch_yaml_converted_to_json_pass_through_validation() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_patch.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        Loader.validade(actual);
    }

    @Test
    public void should_remove_by_id_yaml_converted_to_json_pass_through_validation() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_remove-by-access-id.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        Loader.validade(actual);
    }

    @Test
    public void should_remove_by_declaration_yaml_converted_to_json_pass_through_validation() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_remove-by-declaration.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        Loader.validade(actual);
    }

    @Test
    public void should_create_pass_through_when_no_origin() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-without-origin.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        Loader.validade(actual);
    }

    @Test
    public void should_patch_pass_through_when_no_origin() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_patch-without-origin.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        Loader.validade(actual);
    }

    @Test
    public void should_create_fail_through_validation_when_no_required_to() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-without-to.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        assertThrows(ValidationException.class, ()->
            Loader.validade(actual));
    }

    @Test
    public void should_patch_fail_through_validation_when_no_required_to() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_patch-without-to.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        assertThrows(ValidationException.class, ()->
            Loader.validade(actual));
    }

    @Test
    public void should_remove_by_declaration_fail_through_validation_when_no_required_to() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_remove-by-declaration-without-to.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        assertThrows(ValidationException.class, ()->
            Loader.validade(actual));
    }

    @Test
    public void should_remove_by_id_fail_through_validation_when_empty_id() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_remove-by-access-id-empty-id.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        assertThrows(ValidationException.class, ()->
            Loader.validade(actual));
    }

    @Test
    public void should_create_fail_through_validation_when_additional_properties() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-additional-properties.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        assertThrows(ValidationException.class, ()->
            Loader.validade(actual));
    }

    @Test
    public void should_patch_fail_through_validation_when_additional_properties() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_patch-additional-properties.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        assertThrows(ValidationException.class, ()->
            Loader.validade(actual));
    }

    @Test
    public void should_remove_by_id_fail_through_validation_when_additional_properties() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_remove-by-access-id-additional-properties.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        assertThrows(ValidationException.class, ()->
            Loader.validade(actual));
    }

    @Test
    public void should_remove_by_declaration_fail_through_validation_when_additional_properties() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_remove-by-declaration-additional-properties.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        assertThrows(ValidationException.class, ()->
            Loader.validade(actual));
    }

    @Test
    public void should_remove_by_declaration_fail_through_validation_when_empty_inthe() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_remove-by-declaration-empty-inthe.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        assertThrows(ValidationException.class, ()->
            Loader.validade(actual));
    }

    @Test
    public void should_remove_by_declaration_fail_through_validation_when_empty_allow() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_remove-by-declaration-empty-allow.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        assertThrows(ValidationException.class, ()->
            Loader.validade(actual));
    }

    @Test
    public void should_remove_by_declaration_fail_through_validation_when_empty_deny() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_remove-by-declaration-empty-deny.yaml");
        var map = Loader.loadAsMap(yaml);
        var json = MigrationLoader.toStringifiedJSON(map);

        var actual = MigrationLoader.parseJson(json);

        assertThrows(ValidationException.class, ()->
            Loader.validade(actual));
    }
}
