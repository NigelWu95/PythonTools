/**
 * Project Name: com.qiniu.sdkdemo
 * File Name: Basic.java
 * Package Name: com.qiniu.sdkdemo
 * Date Time: 06/11/2017  6:14 PM
 * Copyright (c) 2017, xxx_xxx  All Rights Reserved.
 */
package com.qiniu.sdkdemo;

import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Recorder;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.persistent.FileRecorder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

import java.io.IOException;
import java.nio.file.Paths;

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
public abstract class Basic {

    public static final Config config = Config.getInstance();

    public Auth getAuth() {
        return Auth.create(config.getAccesskey(), config.getSecretKey());
    }

    public UploadManager getUploadManager(Zone z, boolean allowBreakpoint) throws IOException {
        if (z == null) {
            z = Zone.autoZone();
        }

        Configuration cfg = new Configuration(z);

        if (allowBreakpoint) {
            //设置断点记录文件保存在指定文件夹或的File对象
            String tempPath = Paths.get(System.getenv("java.io.tmpdir"), "qiniu").toString();
            //实例化recorder对象
            Recorder recorder = new FileRecorder(tempPath);
            //实例化上传对象，并且传入一个recorder对象
            return new UploadManager(cfg, recorder);
        } else {
            return new UploadManager(cfg);
        }
    }

    public String getAccessToken(String... params) {
        if (params == null || params.equals("") || params.clone().length == 0) {
            return getAuth().uploadToken(config.getFirstBucketName());
        } else if (params.clone().length == 1) {
            return getAuth().uploadToken(params.clone()[0]);
        } else {
            return getAuth().uploadToken(params.clone()[0], params.clone()[1]);
        }
    }

    public String getAccessTokenWithPolicy(StringMap policy, String... params) {
        if (params == null || params.equals("") || params.clone().length < 3) {
            return getAccessToken(params);
        } else if (params.clone().length == 3) {
            return getAuth().uploadToken(
                    params.clone()[0],
                    params.clone()[1],
                    Long.valueOf(params.clone()[2].toString()),
                    policy);
        } else {
            return getAuth().uploadToken(
                    params.clone()[0],
                    params.clone()[1],
                    Long.valueOf(params.clone()[2].toString()),
                    policy,
                    Boolean.valueOf(params.clone()[3]));
        }
    }

    public Response upload(UploadManager uploadManager, String filePath, String... params) throws IOException {
        if (params == null || params.equals("") || params.clone().length < 2) {
            return uploadManager.put(filePath, null, getAccessToken(params));
        } else {
            return uploadManager.put(filePath, params.clone()[1], getAccessToken(params));
        }
    }

    public Response uploadWithPolicy(UploadManager uploadManager, String filePath, StringMap policy, String... params) throws IOException {
        if (params == null || params.equals("") || params.clone().length < 2) {
            return uploadManager.put(filePath, null, getAccessToken(params));
        } else if (params.clone().length == 2) {
            return uploadManager.put(filePath, params.clone()[1], getAccessTokenWithPolicy(policy, params.clone()[0], params.clone()[1], "3600"));
        } else {
            return uploadManager.put(filePath, params.clone()[1], getAccessTokenWithPolicy(policy, params));
        }
    }
}
