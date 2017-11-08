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
import com.qiniu.storage.UploadManager;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;

import java.io.IOException;

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
public class UploadMain extends Basic {

    public Response simpleUpload(String fileName) throws IOException {
        return upload(
                getUploadManager(null, false),
                config.getFilepath() + fileName
        );
    }

    public Response overwriteUpload1(String fileName) throws IOException {
        return uploadWithPolicy(
                getUploadManager(null, false),
                config.getFilepath() + fileName,
                new StringMap().put("insertOnly", 1),
                config.getFirstBucketName(),
                "overwrite1.jpg"
        );
    }

    public Response overwriteUpload2(String fileName) throws IOException {
        return uploadWithPolicy(
                getUploadManager(null, false),
                config.getFilepath() + fileName,
                new StringMap().put("insertOnly", 1),
                config.getFirstBucketName(),
                "overwrite2.jpg",
                "3600"
        );
    }

    public Response callbackUpload(String fileName) throws IOException {
        return uploadWithPolicy(
                getUploadManager(null, false),
                config.getFilepath() + fileName,
                new StringMap()
//                        .put("callbackUrl", "http://oysol03xx.bkt.clouddn.com/a.jpg")
                        .put("callbackBody", "filename=$(fname)&filesize=$(fsize)"),
                config.getFirstBucketName(),
                "callback.jpg",
                "3600"
        );
    }

    public Response breakpointUpload(String fileName) throws IOException {
        return upload(
                getUploadManager(null, true),
                config.getFilepath() + fileName,
                config.getFirstBucketName(),
                "breakpoint.avi"
        );
    }

    public Response pfopsUpload(String fileName) throws IOException {
        //设置转码操作参数
        String fops = "avthumb/mp4/s/640x360/vb/1.25m";
        //设置转码的队列
        String pipeline = "testqueue";

        //可以对转码后的文件进行使用saveas参数自定义命名，当然也可以不指定文件会默认命名并保存在当前空间。
        String urlbase64 = UrlSafeBase64.encodeToString(config.getFirstBucketName() + ":" + "pfops.mp4");
        String pfops = fops + "|saveas/" + urlbase64;

        return uploadWithPolicy(
                getUploadManager(null, false),
                config.getFilepath() + fileName,
                new StringMap()
                        .putNotEmpty("persistentOps", pfops)
                        .putNotEmpty("persistentPipeline", pipeline),
                config.getFirstBucketName(),
                "pfops.avi",
                "3600",
                "true"
        );
    }

    public static void main(String[] args) {
        UploadMain uploadMain = new UploadMain();

        try {
            Response res = uploadMain.simpleUpload("1.jpg");
            System.out.println(res.bodyString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Response res = uploadMain.overwriteUpload1("2.jpg");
            System.out.println(res.bodyString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Response res = uploadMain.overwriteUpload2("3.jpg");
            System.out.println(res.bodyString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Response res = uploadMain.breakpointUpload("t.avi");
            System.out.println(res.bodyString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Response res = uploadMain.callbackUpload("4.jpg");
            System.out.println(res.bodyString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Response res = uploadMain.pfopsUpload("t.avi");
            System.out.println(res.bodyString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
