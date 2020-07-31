package com.testfan.apitest.api;

import com.github.crab2died.annotation.ExcelField;
import lombok.Data;

@Data
public class ParameterBean {

	@ExcelField(title ="loginname")
	private String loginname;

	@ExcelField(title ="loginpass")
	private String loginpass;

}
