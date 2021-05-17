package com.github.kattlo.util;

import java.util.ArrayList;
import java.util.List;

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

}
