package com.testfan.apitest.api;

import com.github.checkpoint.CheckPointUtils;
import com.github.checkpoint.JsonCheckResult;
import com.github.crab2died.ExcelUtils;
import com.testfan.apitest.utils.CorrelationUtils;
import com.testfan.apitest.utils.DbCheck;
import com.testfan.apitest.utils.HttpClientUtils;
import org.apache.commons.beanutils.BeanUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class ApiTest {

    public static void main(String[] args) {
        HttpClientUtils.openProxy=false; //是否走代理开关
        if (args.length == 0) {
            args = new String[3];
            args[0] = "0";
            args[1] = "12";
            args[2] = "-h";
        }
        //System.out.println("当前启动时间"+getStartTime(args[0],args[2]));
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        Future f=scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                testcase();
            }
        }, getStartTime(args[0]), getPeriod(args[1],args[2]), TimeUnit.SECONDS);

        try {
            //线程池异常问题
            f.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 任何时间秒转换
     * @param time
     * @param unit
     * @return
     */
    private static long getPeriod(String time,String unit) {
        if ("-h".equalsIgnoreCase(unit)) {
            return TimeUnit.HOURS.toSeconds(Long.parseLong(time));
        } else if ("-m".equalsIgnoreCase(unit)) {
            return TimeUnit.MINUTES.toSeconds(Long.parseLong(time));
        } else if ("-d".equalsIgnoreCase(unit)) {
            return TimeUnit.DAYS.toSeconds(Long.parseLong(time));
        }
        return Long.parseLong(time);
    }

    private static String getCurrentTime() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        String time = format.format(date);
        return time;
    }

    /**
     * 返回时间差 单位：秒
     * @param startTime
     * @return
     */
    private static long getStartTime(String startTime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        try {
            Date date =df.parse(startTime);
            long dif=date.getTime()-new Date().getTime();
            if(dif<=0) {
                return 0;
            }else {
                return dif/1000;
            }
        } catch (ParseException e) {
            //e.printStackTrace();
        }
        return 0;
    }

//    // 毫秒转化
//    private static long formatDifTime(long ms, String time) {
//        long ss = 1000;
//        long mi = ss * 60;
//        long hh = mi * 60;
//        long dd = hh * 24;
//        long day = ms / dd;
//        long hour = (ms - day * dd) / hh;
//        long minute = (ms - day * dd - hour * hh) / mi;
//        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
//        if ("-h".equalsIgnoreCase(time)) {
//            return hour;
//        } else if ("-m".equalsIgnoreCase(time)) {
//            return minute;
//        } else if ("-d".equalsIgnoreCase(time)) {
//            return day;
//        }
//        return second;
//    }

//    private static TimeUnit getTimeUnit(String time) {
//        if ("-h".equalsIgnoreCase(time)) {
//            return TimeUnit.HOURS;
//        } else if ("-m".equalsIgnoreCase(time)) {
//            return TimeUnit.MINUTES;
//        } else if ("-d".equalsIgnoreCase(time)) {
//            return TimeUnit.DAYS;
//        }
//        return TimeUnit.SECONDS;
//    }

    private static void testcase() {
        String path = System.getProperty("user.dir") + File.separator + "data" + File.separator + "apitest.xlsx";
        String path2 = System.getProperty("user.dir") + File.separator + "data" + File.separator + "result"
                + getCurrentTime() + ".xlsx";

        try {
            List<TestCase> testCases = ExcelUtils.getInstance().readExcel2Objects(path, TestCase.class);
            List<TestCaseResult> results = new ArrayList<TestCaseResult>();

            for (TestCase testCase: testCases) {
                if ("是".equals(testCase.getRun())) {
                    // 关联替换
                    CorrelationUtils.doBefore_replace(testCase);
                    System.out.println(testCase);
                    String result = null;
                    if ("get".equalsIgnoreCase(testCase.getType())) {
                        result = HttpClientUtils.doGet(testCase.getUrl(), testCase.getHeader());
                    }else if ("post".equalsIgnoreCase(testCase.getType())) {
                        // post 请求
                        result = HttpClientUtils.doPost(testCase.getUrl(), testCase.getParams(), testCase.getHeader());
                    } else if ("postjson".equalsIgnoreCase(testCase.getType())) {
                        result = HttpClientUtils.doPostJson(testCase.getUrl(), testCase.getParams(),
                                testCase.getHeader());
                    } else if ("postxml".equalsIgnoreCase(testCase.getType())) {
                        result = HttpClientUtils.doPostXml(testCase.getUrl(), testCase.getParams(),
                                testCase.getHeader());
                    }
                    System.out.println(result);
                    JsonCheckResult checkResult = CheckPointUtils.check(result, testCase.getCheckpoint());
                    //if (checkResult.isResult()) {
                        CorrelationUtils.addCorrelation(result, testCase);
                    //}

                    // 后置关联处理
                    CorrelationUtils.doAfter_replace(testCase);

                    String dbCheckResult = DbCheck.check(testCase);
                    System.out.println(testCase);

                    TestCaseResult testCaseResult = new TestCaseResult();
                    BeanUtils.copyProperties(testCaseResult, testCase);
                    testCaseResult.setResult(checkResult.getMsg());
                    testCaseResult.setDbCheckResult(dbCheckResult);
                    results.add(testCaseResult);

                }else {
                    System.out.println("用例未开启");
                }
            }
            CorrelationUtils.clear();
            ExcelUtils.getInstance().exportObjects2Excel(results, TestCaseResult.class, path2);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
