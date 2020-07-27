package com.testfan.apitest.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {

    private static final String regex1 = ";";
    private static final String regex2 = "&";

    public static Map<String, Object> convertStringToMap(String str, String regex) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        String[] strings = str.split(regex);
        for (String string : strings) {
            String[] split = string.split("=");
            map.put(split[0], split[1]);
        }
        return map;
    }

    public static Map<String, Object> convertStringToMap(String str) {
        return convertStringToMap(str, regex1);
    }

    public static Map<String, Object> convertStringToMap1(String str) {
        return convertStringToMap(str, regex2);
    }
}
