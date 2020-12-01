package com.mobileframe.tools;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.MediaDataSource;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import com.mobileframe.tools.service.DownloadService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import okhttp3.MediaType;

/**
 * The type File utils.
 */
public class FileUtils {

    public static final String DOWNLOAD_FOLDER = "download_folder";

    /**
     * 文件下载目录
     *
     * @param context
     * @return
     */
    public static String getDownloadFolder(Context context) {
        return getDownloadFolder(context, DOWNLOAD_FOLDER);
    }

    public static String getDownloadFolder(Context context, String folder) {
        return getDiskDir(context, folder);
    }

    /**
     * 检查是否存在SD卡
     *
     * @return true:有 or false：无
     */
    public static boolean hasSdCardOK() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 获取有效的SD卡存储位置列表
     *
     * @param context
     * @return
     */
    @SuppressWarnings("all")
    public static List<String> getValidSDCardPathList(Context context) {
        List<String> pathList = new ArrayList<String>();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            try {
                StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
                Class<?> cl = sm.getClass();
                String[] paths = (String[]) cl.getMethod("getVolumePaths", new Class[0]).invoke(sm, new Object[]{});
                for (int i = 0; i < paths.length; i++) {
                    String status = (String) cl.getMethod("getVolumeState", String.class).invoke(sm, paths[i]);
                    if (status.equals(android.os.Environment.MEDIA_MOUNTED)) {
                        pathList.add(paths[i]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pathList;
    }

    /**
     * 获取缓存目录 存放临时缓存数据 卸载后会删除 需要访问外部存储卡权限
     *
     * @param context
     * @return
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;

        if (hasSdCardOK()) {// isExternalStorageRemovable外部存储可移动

            // 获取外部缓存
            // /sdcard/Android/data/<application package>/cache
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir != null) {
                cachePath = context.getExternalCacheDir().getAbsolutePath();
            } else {
                cachePath = context.getCacheDir().getAbsolutePath();
            }

        } else {

            // 获取内部缓存
            // /data/data/<application package>/cache
            cachePath = context.getCacheDir().getAbsolutePath();
        }
        return cachePath;
    }

    /**
     * 获取存储目录 长时间保存的数据 卸载后会删除 需要访问外部存储卡权限
     * 路径(外部存储卡可用)1：/mnt/sdcard/Android/data/com.my.app/files/dirName
     * 路径(外部存储卡不可用)2：/data/data/com.my.app/files/dirName
     *
     * @param context
     * @return
     */
    public static String getDiskDir(Context context, String dirName) {
        String path = null;

        if (hasSdCardOK()) {
            // /mnt/sdcard/Android/data/com.my.app/files/dirName
            File file = context.getExternalFilesDir(dirName);
            if (file == null) {
                file = context.getFilesDir();
                if (file != null) {
                    path = file.getAbsolutePath();
                }
            } else {
                path = file.getAbsolutePath();
            }
        } else {
            // /data/data/com.my.app/files/dirName
            File file = context.getFilesDir();
            if (file != null) {
                path = file.getAbsolutePath();
            }
        }

        return path;
    }


    /**
     * 获取应用在SDCard上的工作路径
     *
     * @param context the context
     * @return the app external path
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static String getAppExternalPath(Context context) {
/*        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        sb.append("Android/data/");
        sb.append(packageName);
        return sb.toString();*/
        return context.getObbDir().getAbsolutePath();
    }

    /**
     * 获取SDCard上目录的路径
     *
     * @param folder the folder
     * @return the extra path
     */
    public static String getExtraPath(String folder) {
        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folder;
        File file = new File(storagePath);
        if (!file.exists()) {
            file.mkdir();
        }
        return storagePath;
    }

    /**
     * 关闭IO流
     *
     * @param closeables the closeables
     */
    public static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 删除目录下的所有文件
     *
     * @param directory the directory
     */
    public static void deleteFileByDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                file.delete();
            }
        }
    }

    /**
     * 文件是否存在
     *
     * @param filePath the file path
     * @return the boolean
     */
    public static boolean isFileExist(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * 将字符串写入到文件
     *
     * @param filename the filename
     * @param content  the content
     * @param append   the append
     * @return the boolean
     */
    public static boolean writeFile(String filename, String content, boolean append) {
        boolean isSuccess = false;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(filename, append));
            bufferedWriter.write(content);
            isSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(bufferedWriter);
        }
        return isSuccess;
    }

    /**
     * 写入文件
     *
     * @param inputStream
     * @param filePathDir   文件路径
     * @param fileName      文件名
     * @param isRewriteFile 是否重写
     * @return
     */
    public static boolean writeFile(InputStream inputStream, String filePathDir, String fileName, boolean isRewriteFile) {
        if (inputStream != null && filePathDir != null && fileName != null) {
            try {
                File e = new File(filePathDir);
                if (!e.exists()) {
                    e.mkdirs();
                }

                String filePath = filePathDir + "/" + fileName;
                File file = new File(filePath);
                FileOutputStream fileOutputStream;
                byte[] buffer;
                int count;
                if (file.exists() && file.isFile()) {
                    if (!isRewriteFile) {
                        inputStream.close();
                        return false;
                    } else {
                        file.delete();
                        fileOutputStream = new FileOutputStream(filePath);
                        buffer = new byte[1024];

                        while ((count = inputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, count);
                        }

                        fileOutputStream.close();
                        inputStream.close();
                        return true;
                    }
                } else {
                    fileOutputStream = new FileOutputStream(filePath);
                    buffer = new byte[1024];

                    while ((count = inputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }

                    fileOutputStream.close();
                    inputStream.close();
                    return true;
                }
            } catch (Exception var11) {
                var11.printStackTrace();
                return false;
            }
        } else {
            throw new NullPointerException();
        }
    }

    /**
     * 从流读取文件
     *
     * @param ins
     * @param file
     * @return
     * @throws IOException
     */
    public File inputStreamToFile(InputStream ins, File file) throws IOException {
        OutputStream os = new FileOutputStream(file);
        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        while ((bytesRead = ins.read(buffer, 0, 1024)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
        return file;
    }

    /**
     * 从文件中读取字符串
     *
     * @param filename the filename
     * @return the string
     */
    public static String readFile(String filename) {
        File file = new File(filename);
        BufferedReader bufferedReader = null;
        String str = null;
        try {
            if (file.exists()) {
                bufferedReader = new BufferedReader(new FileReader(filename));
                str = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(bufferedReader);
        }
        return str;
    }

    /**
     * 从文件中读取字符串(可设置编码)
     *
     * @param file        the file
     * @param charsetName the charset name
     * @return the string builder
     */
    public static StringBuilder readFile(File file, String charsetName) {
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            return null;
        }
        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            closeIO(reader);
        }
    }

    /**
     * 复制文件
     *
     * @param in  the in
     * @param out the out
     */
    public static void copyFile(InputStream in, OutputStream out) {
        try {
            byte[] b = new byte[2 * 1024 * 1024]; //2M memory
            int len = -1;
            while ((len = in.read(b)) > 0) {
                out.write(b, 0, len);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(in, out);
        }
    }

    /**
     * 快速复制
     *
     * @param in  the in
     * @param out the out
     */
    public static void copyFileFast(File in, File out) {
        FileChannel filein = null;
        FileChannel fileout = null;
        try {
            filein = new FileInputStream(in).getChannel();
            fileout = new FileOutputStream(out).getChannel();
            filein.transferTo(0, filein.size(), fileout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(filein, fileout);
        }
    }

    /**
     * 分享文件
     *
     * @param context  the context
     * @param title    the title
     * @param filePath the file path
     */
    public static void shareFile(Context context, String title, String filePath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.parse("file://" + filePath);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, title));
    }

    /**
     * zip压缩
     *
     * @param is the is
     * @param os the os
     */
    public static void zip(InputStream is, OutputStream os) {
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(os);
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) != -1) {
                gzip.write(buf, 0, len);
                gzip.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(is, gzip);
        }
    }

    /**
     * zip解压
     *
     * @param is the is
     * @param os the os
     */
    public static void unzip(InputStream is, OutputStream os) {
        GZIPInputStream gzip = null;
        try {
            gzip = new GZIPInputStream(is);
            byte[] buf = new byte[1024];
            int len;
            while ((len = gzip.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(gzip, os);
        }
    }

    /**
     * 格式化文件大小
     *
     * @param context the context
     * @param size    the size
     * @return the string
     */
    public static String formatFileSize(Context context, long size) {
        return Formatter.formatFileSize(context, size);
    }

    /**
     * 将输入流写入到文件
     *
     * @param is   the is
     * @param file the file
     */
    public static void Stream2File(InputStream is, File file) {
        byte[] b = new byte[1024];
        int len;
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(is, os);
        }
    }

    /**
     * 创建文件夹
     *
     * @param filePath the file path
     * @return the boolean
     */
    public static boolean createFolder(String filePath) {
        return createFolder(filePath, false);
    }

    /**
     * 创建文件夹(支持覆盖已存在的同名文件夹)
     *
     * @param filePath the file path
     * @param recreate the recreate
     * @return the boolean
     */
    public static boolean createFolder(String filePath, boolean recreate) {
        String folderName = getFolderName(filePath);
        if (folderName == null || folderName.length() == 0 || folderName.trim().length() == 0) {
            return false;
        }
        File folder = new File(folderName);
        if (folder.exists()) {
            if (recreate) {
                deleteFile(null, folderName);
                return folder.mkdirs();
            } else {
                return true;
            }
        } else {
            return folder.mkdirs();
        }
    }

    /**
     * 获取文件名
     *
     * @param filePath the file path
     * @return the file name
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    /**
     * 获取文件大小
     *
     * @param filepath the filepath
     * @return the file size
     */
    public static long getFileSize(String filepath) {
        if (TextUtils.isEmpty(filepath)) {
            return -1;
        }
        File file = new File(filepath);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }

    /**
     * 重名名文件\文件夹
     *
     * @param filepath the filepath
     * @param newName  the new name
     * @return the boolean
     */
    public static boolean rename(String filepath, String newName) {
        File file = new File(filepath);
        return file.exists() && file.renameTo(new File(newName));
    }

    /**
     * 获取文件夹名称
     *
     * @param filePath the file path
     * @return the folder name
     */
    public static String getFolderName(String filePath) {
        if (filePath == null || filePath.length() == 0 || filePath.trim().length() == 0) {
            return filePath;
        }
        int filePos = filePath.lastIndexOf(File.separator);
        return (filePos == -1) ? "" : filePath.substring(0, filePos);
    }

    /**
     * 获取文件夹下所有文件
     *
     * @param path the path
     * @return the files array
     */
    public static ArrayList<File> getFilesArray(String path) {
        File file = new File(path);
        File files[] = file.listFiles();
        ArrayList<File> listFile = new ArrayList<File>();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    listFile.add(files[i]);
                }
                if (files[i].isDirectory()) {
                    listFile.addAll(getFilesArray(files[i].toString()));
                }
            }
        }
        return listFile;
    }

    /**
     * 删除文件
     *
     * @param folder the folder
     * @return the boolean
     */
    public static boolean deleteFiles(String folder) {
        if (folder == null || folder.length() == 0 || folder.trim().length() == 0) {
            return true;
        }
        File file = new File(folder);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(null, f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    /**
     * 打开图片
     *
     * @param mContext  the m context
     * @param imagePath the image path
     */
    public static void openImage(Context mContext, String imagePath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(imagePath));
        intent.setDataAndType(uri, "image/*");
        mContext.startActivity(intent);
    }

    /**
     * 打开视频
     *
     * @param mContext  the m context
     * @param videoPath the video path
     */
    public static void openVideo(Context mContext, String videoPath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(videoPath));
        intent.setDataAndType(uri, "video/*");
        mContext.startActivity(intent);
    }

    /**
     * 打开URL
     *
     * @param mContext the m context
     * @param url      the url
     */
    public static void openURL(Context mContext, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mContext.startActivity(intent);
    }

    /**
     * 下载文件
     *
     * @param context the context
     * @param fileUrl the fileUrl
     * @return 任务id
     */
    public static long downloadFile(Context context, String fileUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setDestinationInExternalPublicDir("/Download/", fileUrl.substring(fileUrl.lastIndexOf("/") + 1));
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        return downloadManager.enqueue(request);
    }

    /**
     * 选择一个文件
     * android 10 及以上必须使用
     *
     * @param context
     * @param type        文件类型
     * @param requestCode
     */
    public static void selectSingleFile(Context context, String type, int requestCode) {
        //通过系统的文件浏览器选择一个文件
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //筛选，只显示可以“打开”的结果，如文件(而不是联系人或时区列表)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //过滤只显示图像类型文件
        intent.setType(type);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 创建一个文件
     * android 10 及以上必须使用
     *
     * @param context
     * @param type        文件类型
     * @param fileName    文件名称+后缀
     * @param requestCode
     */
    public static void createFile(Context context, String type, String fileName, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 读取文件字符串
     * android 10 及以上必须使用
     *
     * @param context
     * @param uri
     */
    public static String readFileString(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.fillInStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 修改文件
     * android 10 及以上必须使用
     *
     * @param context
     * @param targetUri
     * @param content
     * @return
     */
    public static boolean modifyFile(Context context, Uri targetUri, String content) {
        if (targetUri == null) {
            return false;
        }
        OutputStream outputStream = null;
        try {
            // 获取 OutputStream
            outputStream = context.getContentResolver().openOutputStream(targetUri);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                outputStream.write(content.getBytes(StandardCharsets.UTF_8));
            } else {
                outputStream.write(content.getBytes(Charset.forName("UTF_8")));
            }
        } catch (IOException e) {
            return false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.fillInStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 删除文件
     * android 10 及以上必须使用
     *
     * @param filename the filename
     * @return the boolean
     */
    public static boolean deleteFile(Context context, String filename) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return new File(filename).delete();
        }
        return deleteFile(context, Uri.fromFile(new File(filename)));
    }

    /**
     * 删除文件夹
     * android 10 及以上必须使用
     *
     * @param context
     * @param targetUri
     * @return
     */
    public static boolean deleteFile(Context context, Uri targetUri) {
        if (targetUri == null || context == null) {
            return false;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                DocumentsContract.deleteDocument(context.getContentResolver(), targetUri);
            }
            targetUri = null;
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }
}
