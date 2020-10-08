package com.github.kattlo.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author fabiojose
 */
public class StringUtil {

    public static String requireNonBlank(String s){
        if(StringUtils.isNotBlank(s)){
            return s;
        }

        throw new IllegalArgumentException("blank string");
    }
}
