package com.testfan.apitest.utils;

import com.alibaba.fastjson.JSON;
import com.github.checkpoint.CheckPointUtils;
import com.github.checkpoint.JsonCheckResult;
import com.testfan.apitest.api.TestCase;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.Map;

public class DbCheck {

    public static String check(TestCase testCase) {
        if (StringUtils.isEmpty(testCase.getDbchecksql()) || StringUtils.isEmpty(testCase.getDbcheckpoint())) {
            return "没有设置数据库检查";
        }

        String sql = testCase.getDbchecksql();
        QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());
        try {
            Map<String, Object> map = runner.query(sql, new MapHandler());
            String json = JSON.toJSONString(map);
            JsonCheckResult checkResult = CheckPointUtils.check(json, testCase.getDbcheckpoint());
            if (checkResult.isResult()) {
                return "数据库检查成功";
            }else {
                return "数据库检查失败";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "检查点异常";
    }
}
