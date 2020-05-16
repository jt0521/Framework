package com.net.netretrofit.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

    /**
     * 从一个输入流里写文件
     *
     * @param destFilePath
     *            要创建的文件的路径
     * @param in
     *            要读取的输入流
     * @return 写入成功返回true,写入失败返回false
     */
    public static boolean writeFile(String destFilePath, InputStream in) {
        try {
            if (!createFile(destFilePath)) {
                return false;
            }
            FileOutputStream fos = new FileOutputStream(destFilePath);
            int readCount = 0;
            int len = 1024;
            byte[] buffer = new byte[len];
            while ((readCount = in.read(buffer)) != -1) {
                fos.write(buffer, 0, readCount);
            }
            fos.flush();
            if (null != fos) {
                fos.close();
                fos = null;
            }
            if (null != in) {
                in.close();
                in = null;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 创建一个文件，创建成功返回true
     *
     * @param filePath
     * @return
     */
    public static boolean createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                return file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String getDiskDir(Context context, String dirName) {
        String path = null;
        File file;
        if (isExternalMediaMounted()) {
            file = context.getExternalFilesDir(dirName);
            if (file == null) {
                file = context.getFilesDir();
                if (file != null) {
                    path = file.getAbsolutePath();
                }
            } else {
                path = file.getAbsolutePath();
            }
        } else {
            file = context.getFilesDir();
            if (file != null) {
                path = file.getAbsolutePath();
            }
        }

        return path;
    }

    @SuppressLint({"NewApi"})
    public static boolean isExternalMediaMounted() {
        return "mounted".equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable();
    }

    public static String getFileNameByUrl(String url) {
        int separatorIndex = url.lastIndexOf("/");
        return separatorIndex < 0 ? url : url.substring(separatorIndex + 1, url.length());
    }
}
