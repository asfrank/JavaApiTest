package com.testfan.apitest.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class HttpClientUtils {
    // 代理开关
    public static boolean openProxy = true;
    static RequestConfig requestConfig;

    static XmlMapper xmlMapper = new XmlMapper();
    static {
        // 代理设置
        HttpHost proxy = new HttpHost("127.0.0.1", 8888);
        // ConnectTimeout： 链接建立的超时时间；
        // SocketTimeout：响应超时时间，超过此时间不再读取响应；
        // ConnectionRequestTimeout： http clilent中从connetcion pool中获得一个connection的超时时间；
        requestConfig = RequestConfig.custom().setProxy(proxy).setConnectTimeout(10000).setSocketTimeout(10000)
                .setConnectionRequestTimeout(3000).build();
    }

    /**
     * get 请求
     *
     * @param url
     * @param headers
     * @return
     */
    public static String doGet(String url, String headers) {
        // 创建http 默认请求池
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        HttpEntity httpEntity = null;
        String result = null;
        try {
            if (headers != null) {
                String[] header_array = headers.split(";");
                for (String header : header_array) {
                    String[] key_array = header.split("=");
                    get.addHeader(key_array[0], key_array[1]);
                }
            }
            if (openProxy) {
                get.setConfig(requestConfig);
            }
            CloseableHttpResponse closeableHttpResponse = httpclient.execute(get);
            if (closeableHttpResponse.getStatusLine().getStatusCode() == 200) {
                httpEntity = closeableHttpResponse.getEntity();
                result = EntityUtils.toString(httpEntity, "utf-8");
            } else {
                result = "接口返回错误";
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     *
     * @param url 地址
     * @param params 参数
     * @param headers 头部信息
     * @return
     */
    public static String doPost(String url, String params, String headers) {
        // 创建http 默认请求池
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        HttpEntity httpEntity = null;
        String result = null;
        try {
            if (params != null) {
                // 参数处理，核心是 UrlEncodedFormEntity 需要List<NameValuePair>
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                String[] params_array = params.split("&");
                for (String param : params_array) {
                    String[] param_array = param.split("=");
                    list.add(new BasicNameValuePair(param_array[0], param_array[1]));
                }
                if (list.size() > 0) {
                    post.setEntity(new UrlEncodedFormEntity(list, "utf-8"));
                }
            }
            // 处理header
            if (headers != null) {
                String[] header_array = headers.split(";");
                for (String header : header_array) {
                    String[] key_array = header.split("=");
                    post.addHeader(key_array[0], key_array[1]);
                }
            }

            if (openProxy) {
                post.setConfig(requestConfig);
            }
            CloseableHttpResponse closeableHttpResponse = httpclient.execute(post);
            if (closeableHttpResponse.getStatusLine().getStatusCode() == 200) {
                httpEntity = closeableHttpResponse.getEntity();
                result = EntityUtils.toString(httpEntity, "utf-8");
            } else {
                result = "接口返回错误";
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String doPostXml(String url, String params, String headers) {
        String xmlString =doGet(url, headers);
        String jsonString = null;
        try {
            Map map =xmlMapper.readValue(xmlString, HashMap.class);
            jsonString = JSON.toJSONString(map);
        } catch (IOException e) {
            jsonString = "格式不对";
            e.printStackTrace();
        }
        return jsonString;
    }
    public static String doPostJson(String url, String params, String headers) {
        // 创建http 默认请求池
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("content-type", "application/json");
        HttpEntity httpEntity = null;
        String result = null;
        try {
            if (params != null) {
                post.setEntity(new StringEntity(params, "utf-8"));
                // 参数处理，核心是 UrlEncodedFormEntity 需要List<NameValuePair>
//				List<NameValuePair> list = new ArrayList<NameValuePair>();
//				String[] params_array = params.split("&");
//				for (String param : params_array) {
//					String[] param_array = param.split("=");
//					list.add(new BasicNameValuePair(param_array[0], param_array[1]));
//				}
//				if (list.size() > 0) {
//					post.setEntity(new UrlEncodedFormEntity(list, "utf-8"));
//				}
            }
            // 处理header
            if (headers != null) {
                String[] header_array = headers.split(";");
                for (String header : header_array) {
                    String[] key_array = header.split("=");
                    if(key_array.length>1) {
                        post.addHeader(key_array[0], key_array[1]);
                    }
                }
            }

            if (openProxy) {
                post.setConfig(requestConfig);
            }
            CloseableHttpResponse closeableHttpResponse = httpclient.execute(post);
            if (closeableHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                httpEntity = closeableHttpResponse.getEntity();
                result = EntityUtils.toString(httpEntity, "utf-8");
            } else {
                result = "接口返回错误";
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
