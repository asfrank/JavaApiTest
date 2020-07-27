package com.testfan.apitest.utils;

import com.github.crab2died.ExcelUtils;
import com.github.crab2died.exceptions.Excel4JException;
import com.testfan.apitest.api.TestCase;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.List;

public class HttpRequest {


    public static void main(String[] args) {
        String path = HttpRequest.class.getResource("/apitest.xlsx").getPath();

        try {
            List<TestCase> list = ExcelUtils.getInstance().readExcel2Objects(path, TestCase.class);
            System.out.println(list.size());

            for (TestCase testCase : list) {
                if ("是".equals(testCase.getRun())) {
                    if ("get".equalsIgnoreCase(testCase.getType())) {

                    }else if ("post".equalsIgnoreCase(testCase.getType())) {

                    }
                }else {
                    System.out.println(testCase + "用例当前关闭");
                }
            }
        } catch (Excel4JException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }

    }
}
