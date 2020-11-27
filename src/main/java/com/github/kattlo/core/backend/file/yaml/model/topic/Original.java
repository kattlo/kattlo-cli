package com.github.kattlo.core.backend.file.yaml.model.topic;

import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

/**
 * @author fabiojose
 */
@Data
@RegisterForReflection
public class Original {

    private String path;
    private String contentType;

    //base64
    private String content;

    public Map<String, Object> asMap() {
        return Map.of(
            "path", path,
            "contentType", contentType,
            "content", content
        );
    }

}
