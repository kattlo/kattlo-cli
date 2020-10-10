package com.github.kattlo.topic.yaml;

import java.util.regex.Pattern;

/**
 * @author fabiojose
 */
public class Loader {

    public static final Pattern FILE_NAME_PATTERN =
        Pattern.compile("v[0-9]{4}_[\\w\\-]{0,246}\\.ya?ml");
}
