package com.github.kattlo.core.backend;

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

    public static Original from(Map<String, Object> map) {

        var result = new Original();

        result.setPath((String)map.get("path"));
        result.setContentType((String)map.get("contentType"));
        result.setContent((String)map.get("content"));

        return result;
    }
}
