package com.ch.luobin.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * @Author hui cao
 * @ClassName: TuWeiQingHuaUtils
 * @Description:
 * @Date: 2023/6/4 21:20
 * @Version: v1.0
 */
@Slf4j
@Component
public class TuWeiQingHuaUtils {

    /**
     * 彩虹屁id
     */
    private static String chpKet;

    @Value("${com.chpKet}")
    public void setChpKet(String chpKet) {TuWeiQingHuaUtils.chpKet= chpKet;}

    /**
     * 土味情话接口
     * @return
     */
    public static String getTuWeiQingHua() {
        String httpUrl = "https://apis.tianapi.com/saylove/index?key=" + chpKet;
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        String content = jsonObject.getJSONObject("result").get("content").toString();
        return content;
    }



}