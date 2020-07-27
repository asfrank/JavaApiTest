package com.testfan.apitest.api;

import com.github.crab2died.annotation.ExcelField;
import lombok.Data;

@Data
public class TestCase {

    @ExcelField(title = "是否开启")
    private String run;

    @ExcelField(title = "用例名称")
    private String caseName;

    @ExcelField(title = "类型")
    private String type;

    @ExcelField(title = "地址")
    private String url;

    @ExcelField(title = "参数")
    private String params;

    @ExcelField(title = "头部")
    private String header;

    @ExcelField(title = "检查点")
    private String checkpoint;

    @ExcelField(title = "关联")
    private String correlation;
}
