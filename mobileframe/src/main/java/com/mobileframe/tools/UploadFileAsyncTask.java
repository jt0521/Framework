package com.mobileframe.tools;

import android.os.AsyncTask;

import com.mobileframe.common.DeBug;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * 上传文件和字符串
 */
public abstract class UploadFileAsyncTask extends AsyncTask {

    public abstract void onPostExecute(String result);
    private final static String LINE_END = "\r\n";
    private final static String LINE = "--";
    private final static String Charset = "utf-8";
    private String url;
    private String fileKey;
    private Map<String,String> paramsStringMap;
    private File [] files;

    public UploadFileAsyncTask(String url, Map<String, String> paramsStringMap,File[] files,String fileKey){
        this.url = url;
        this.fileKey = fileKey;
        this.paramsStringMap = paramsStringMap;
        this.files = files;
    }

    public UploadFileAsyncTask(String url, Map<String, String> paramsStringMap,String[] pathS,String fileKey){
        this.url = url;
        this.fileKey = fileKey;
        this.paramsStringMap = paramsStringMap;
        this.files = files;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        return uploadFile(url,paramsStringMap,files,fileKey);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        onPostExecute(o+"");
    }

    /**
     * 上传文件和文本，没有就传null
     * @param url 上传地址
     * @param paramsStringMap 文本信息 key - value
     * @param files 文件内容
     * @param fileKey 文件 key
     * @return
     */
    private String uploadFile(String url, Map<String, String> paramsStringMap,File[] files,String fileKey) {
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        FileInputStream fileInput = null;
        DataOutputStream requestStream = null;
        try {
            // open connection
            URL mURL = new URL(url);
            urlConnection = (HttpURLConnection) mURL.openConnection();
            // create random boundary
            String boundary = UUID.randomUUID().toString();

            /* for POST request */
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("POST");
            // 构建数据表单（Entity form）
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            urlConnection.setRequestProperty("Cache-Control", "no-cache");
            urlConnection.setRequestProperty("Charset", Charset);//设置编码
//-----------------------------------------------------------------------------------------------------------------------------------
//          写字符串内容
            if (paramsStringMap != null) {
                requestStream = new DataOutputStream(urlConnection.getOutputStream());
                StringBuffer params = new StringBuffer();
                Iterator it = paramsStringMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry element = (Map.Entry) it.next();
                    params.append(LINE);
                    params.append(boundary);
                    params.append(LINE_END);
                    params.append("Content-Disposition: form-data; name=");
                    params.append(element.getKey());
                    params.append(LINE_END);
                    params.append(LINE_END);
                    params.append(element.getValue());
                    params.append(LINE_END);
                }
                //先转成bytes[]再写入，若调用writeBytes（String params）可能会乱码
                requestStream.write(params.toString().getBytes());
                requestStream.flush();
            }
//-----------------------------------------------------------------------------------------------------------------------------------
            //文件内容
            if (files==null){

            }else {
                for (int i = 0; i < files.length; i++) {
                    fileInput = new FileInputStream(files[i]);
                    requestStream = new DataOutputStream(urlConnection.getOutputStream());
                    requestStream = new DataOutputStream(urlConnection.getOutputStream());
                    requestStream.writeBytes("--" + boundary + LINE_END);
                    requestStream.writeBytes("Content-Disposition: form-data; name=\"" + fileKey + "\"; filename=\"" + files[i].getName() + "\"" + LINE_END);
                    requestStream.writeBytes("Content-Type: " + getMIMEType(files[i]) + LINE_END);
                    requestStream.writeBytes(LINE_END);
                    // 写图像字节内容
                    int bytesRead;
                    byte[] buffer = new byte[8192];
                    while ((bytesRead = fileInput.read(buffer)) != -1) {
                        requestStream.write(buffer, 0, bytesRead);
                    }
                }
                requestStream.flush();
            }
            requestStream.writeBytes(LINE_END);
            requestStream.flush();
            requestStream.writeBytes(LINE + boundary + LINE + LINE_END);
            requestStream.flush();
            fileInput.close();

            // try to get response
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                return inputStream2String(inputStream);
            }
            return statusCode+"";
        } catch (Exception e) {
            e.printStackTrace();
            DeBug.e("可能是无sdCard权限导致");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (requestStream != null) {
                try {
                    requestStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInput != null) {
                try {
                    fileInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return "-1";
    }

    private static String getMIMEType(File file) {
        String fileName = file.getName();
        if (fileName.endsWith("png") || fileName.endsWith("PNG")) {
            return "image/png";
        } else {
            return "image/jpg";
        }
    }

    /**
     * stream流转为String
     *
     * @param is
     * @return
     */
    private static String inputStream2String(InputStream is) {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        try {
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            return "-1";
        }
        return buffer.toString();
    }
}
