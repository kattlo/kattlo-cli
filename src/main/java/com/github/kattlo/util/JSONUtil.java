package com.github.kattlo.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import lombok.experimental.UtilityClass;

/**
 * @author fabiojose
 */
@UtilityClass
public class JSONUtil {

    public static List<String> asString(JSONArray array) {

        var result = new ArrayList<String>();
        for(int i = 0; i < array.length(); i++){
            result.add(array.getString(i));
        }

        return result;
    }
}
