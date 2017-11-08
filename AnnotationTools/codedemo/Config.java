/**
 * Project Name: com.qiniu.sdkdemo
 * File Name: Basic.java
 * Package Name: com.qiniu.sdkdemo
 * Date Time: 06/11/2017  6:14 PM
 * Copyright (c) 2017, xxx_xxx  All Rights Reserved.
 */
package com.qiniu.sdkdemo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ClassName: Basic
 * Description: TODO
 * Date Time: 06/11/2017  6:14 PM
 * @author Nigel Wu  wubinghengajw@outlook.com
 * @version V1.0
 * @since V1.0
 * @jdk 1.8
 * @see
 */
public class Config {

    private static Config config;

    private String accesskey;
    private String secretKey;
    private String firstBucketName;
    private String filepath;

    public Config() {
        Properties p = new Properties();
        InputStream inStream = this.getClass().getResourceAsStream("/config.properties");

        try {
            p.load(inStream);
            this.accesskey = p.getProperty("qiniu.access_key");
            this.secretKey = p.getProperty("qiniu.secret_key");
            this.firstBucketName = p.getProperty("qiniu.bucket_name.first");
            this.filepath = p.getProperty("qiniu.filedir");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Config getInstance() {
        if (config == null) {
            config = new Config();
        }

        return config;
    }

    public String getAccesskey() {
        return this.accesskey;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public String getFirstBucketName() {
        return firstBucketName;
    }

    public String getFilepath() { return filepath; }
}