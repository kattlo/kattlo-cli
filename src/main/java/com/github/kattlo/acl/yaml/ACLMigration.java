package com.github.kattlo.acl.yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;

import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.core.backend.Original;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.yaml.MigrationLoader;
import com.github.kattlo.topic.TopicCommandException;
import com.github.kattlo.util.JSONUtil;
import com.github.kattlo.util.VersionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author fabiojose
 */
@Getter
@RequiredArgsConstructor
@ToString
public class ACLMigration {

    private static final JSONObject NOT_FOUND = null;

    private final JSONObject json;
    private final Path file;

    private String version;

    public String getPrincipal() {
        return JSONUtil.path(json).read("$..to.principal", JSONArray.class)
            .getString(0);
    }

    public String getVersion() {
        if(null== version){
            version = MigrationLoader.versionOf(file).get();
        }

        return version;
    }

    public String getOperation() {

        return "create";
    }

    public Optional<String> getNotes() {
        var found = JSONUtil.path(json).read("$..notes", JSONArray.class);

        return Optional.ofNullable(found.isEmpty() ? null : found.getString(0));
    }

    Optional<JSONObject> getAllow() {

        var found = JSONUtil.path(json).read("$..allow", JSONArray.class);

        return Optional.ofNullable(found.isEmpty() ? NOT_FOUND : found.getJSONObject(0));
    }

    Optional<JSONObject> getDeny() {

        var found = JSONUtil.path(json).read("$..deny", JSONArray.class);

        return Optional.ofNullable(found.isEmpty() ? NOT_FOUND : found.getJSONObject(0));
    }

    public Migration toBackend() {

        var migration = new Migration();

        migration.setVersion(getVersion());
        migration.setOperation(OperationType.valueOf(getOperation().toUpperCase()));
        getNotes().ifPresent(migration::setNotes);
        migration.setResourceType(ResourceType.ACL);
        migration.setResourceName(getPrincipal());
        migration.setTimestamp(LocalDateTime.now());
        migration.setKattlo(VersionUtil.appVersion());

        var attributes = new HashMap<String, Object>();

        getAllow().ifPresent(allow -> attributes.put("allow", JSONUtil.toMap(allow)));
        getDeny().ifPresent(deny -> attributes.put("deny", JSONUtil.toMap(deny)));

        migration.setAttributes(attributes);

        var original = new Original();
        original.setPath(getFile().toString());
        original.setContentType(MigrationLoader.DEFAULT_CONTENT_TYPE);

        try {
            var contentBytes = Files.readAllBytes(getFile());
            var contentBase64 = Base64.getEncoder().encodeToString(contentBytes);
            original.setContent(contentBase64);

            migration.setOriginal(original);

        }catch(IOException e) {
            throw new TopicCommandException(e.getMessage(), e);
        }

        return migration;
    }
}
