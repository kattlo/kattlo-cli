package com.github.kattlo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.experimental.UtilityClass;

/**
 * @author fabiojose
 */
@UtilityClass
public class JSONUtil {

    private static JsonProvider JSON_PROVIDER;

    private JsonProvider getJsonProvider() {
        if(null== JSON_PROVIDER){
            JSON_PROVIDER = new JsonOrgJsonProvider();
        }

        return JSON_PROVIDER;
    }

    public static List<String> asString(JSONArray array) {

        var result = new ArrayList<String>();
        for(int i = 0; i < array.length(); i++){
            result.add(array.getString(i));
        }

        return result;
    }

    public static ReadContext path(JSONObject o) {

        return JsonPath.parse(o,
                Configuration.defaultConfiguration()
                    .jsonProvider(getJsonProvider()));
    }

    /* thanks: https://stackoverflow.com/questions/41243880/how-to-convert-jsonobject-to-new-map-for-all-its-keys-using-iterator-java
     */
    public static Map<String, Object> toMap(JSONObject o) {
        var map = new HashMap<String, Object>();

        var keysItr = o.keys();
        while(keysItr.hasNext()) {
            var key = keysItr.next();
            var value = o.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);

            }else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }

            map.put(key, value);
        }

        return map;
    }

    /* thanks: https://stackoverflow.com/questions/41243880/how-to-convert-jsonobject-to-new-map-for-all-its-keys-using-iterator-java
     */
    public static List<Object> toList(JSONArray a) {

        var list = new ArrayList<Object>();
        for(int i = 0; i < a.length(); i++) {
            var value = a.get(i);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }

        return list;
    }
}
