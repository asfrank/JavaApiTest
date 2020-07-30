package com.testfan.apitest.api;

import com.github.crab2died.annotation.ExcelField;
import lombok.Data;

/**
 * 结果
 * @author pc
 *
 */
@Data
public class TestCaseResult extends TestCase{
	@ExcelField(title = "测试结果", order=1)
	private String result;

	@ExcelField(title = "数据库验证结果", order=2)
	private String dbCheckResult;

}
