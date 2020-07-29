package com.testfan.apitest.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.testfan.apitest.api.TestCase;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CorrelationUtils {

    private static Map<String, Object> correlationMap = new LinkedHashMap<>();

    private static Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");

    public static void addCorrelation(String result, TestCase testCase) {
        if (!JSON.isValid(result)) {
            return;
        }

        Map<String, Object> map = MapUtils.convertStringToMap(testCase.getCorrelation());
        if (map!=null) {
            for (String key: map.keySet()) {
                Object value = JSONPath.read(result, String.valueOf(map.get(key)));
                // 如果提取到的是多个数据
                if (value instanceof List) {
                    int i=0;
                    List<Object> list = (List<Object>) value;
                    for (Object item :list) {
                        correlationMap.put(key+"_g"+(++i), item);
                    }

                }else {
                    correlationMap.put(key, JSONPath.read(result, String.valueOf(map.get(key))));
                }
            }
            map.clear();
        }
    }

    private static String getPatternValue(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        Matcher matcher = pattern.matcher(str);
        String value;
        if (correlationMap.containsKey(matcher.group(1))) {
            value = correlationMap.get(matcher.group(1)).toString();
        }else {
            value = "";
        }
        while (matcher.find() && correlationMap.containsKey(matcher.group(1))) {
            str = str.replace(matcher.group(0), value);
        }
        return str;
    }

    public static void clear() {
        if(correlationMap!=null) {
            correlationMap.clear();
        }
    }

    public static void replace(TestCase testcase) {
        //检查url 关联
        testcase.setUrl(getPatternValue(testcase.getUrl()));

        //检查替换头部 关联
        testcase.setHeader(getPatternValue(testcase.getHeader()));

        //检查替换body 关联
        testcase.setParams(getPatternValue(testcase.getParams()));
    }


}
