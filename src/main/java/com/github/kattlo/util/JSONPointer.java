package com.github.kattlo.util;

import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.experimental.UtilityClass;

/**
 * @author fabiojose
 */
@UtilityClass
public class JSONPointer {

    public static Optional<JSONObject> asObject(JSONObject object, String query) {
        return Optional.ofNullable((JSONObject)object.optQuery(query));
    }

    public static Optional<JSONArray> asArray(JSONObject object, String query) {
        return Optional.ofNullable((JSONArray)object.optQuery(query));
    }

    public static Optional<String> asString(JSONObject object, String query) {
        return Optional.ofNullable((String)object.optQuery(query));
    }
}
