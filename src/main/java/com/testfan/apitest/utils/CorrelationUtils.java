package com.testfan.apitest.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.testfan.apitest.api.TestCase;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CorrelationUtils {

    private static Map<String, Object> correlationMap = new LinkedHashMap<>();

    private static Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");

    // 对象属性和值反射到全局map中
    public static void addCorrelation(Object o) {
        Class clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field: fields) {
            String name = field.getName();
            try {
                String value = BeanUtils.getProperty(o, name);
                correlationMap.put(name, value);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

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

    private static String getPatternValueInSql(String str) {
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
            str = str.replace(matcher.group(0), "'" + value + "'");
        }
        return str;
    }

    public static void clear() {
        if(correlationMap!=null) {
            correlationMap.clear();
        }
    }

    // 前置关联替换
    public static void doBefore_replace(TestCase testcase) {
        //检查url 关联
        testcase.setUrl(getPatternValue(testcase.getUrl()));

        //检查替换头部 关联
        testcase.setHeader(getPatternValue(testcase.getHeader()));

        //检查替换body 关联
        testcase.setParams(getPatternValue(testcase.getParams()));
    }

    // 后置关联替换
    public static void doAfter_replace(TestCase testCase) {
        testCase.setDbchecksql(getPatternValueInSql(testCase.getDbchecksql()));

        testCase.setDbcheckpoint(getPatternValue(testCase.getDbcheckpoint()));
    }


}
