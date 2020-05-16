package com.mobileframe.tools;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.math.BigDecimal;

/**
 * 本应用数据清除管理器
 **/
public class DataCleanManager {
	/** * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context */
	public static void cleanInternalCache(Context context) {
		deleteFilesByDirectory(context.getCacheDir());
	}

	/** * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context */
	public static void cleanDatabases(Context context) {
		deleteFilesByDirectory(new File(context.getFilesDir().getParent()
				+ "/databases"));
	}

	/**
	 * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
	 * context
	 */
	public static void cleanSharedPreference(Context context) {
		deleteFilesByDirectory(new File(context.getFilesDir().getParent()
				+ "/shared_prefs"));
	}

	/** * 按名字清除本应用数据库 * * @param context * @param dbName */
	public static void cleanDatabaseByName(Context context, String dbName) {
		context.deleteDatabase(dbName);
	}

	/** * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context */
	public static void cleanFiles(Context context) {
		deleteFilesByDirectory(context.getFilesDir());
	}

	/**
	 * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
	 * context
	 */
	public static void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(context.getExternalCacheDir()
					.getParentFile());
		}
	}

	/**
	 * 清除本应用所有的数据
	 * @param context
     */
	public static void cleanApplicationData(Context context){
		cleanApplicationData(context,context.getFilesDir().getParent());
	}

	/** * 清除本应用指定文件夹的数据 * * @param context * @param filepath */
	public static void cleanApplicationData(Context context, String... filepath) {
		 cleanInternalCache(context);
		 cleanExternalCache(context);
//		 cleanDatabases(context);
//		 cleanSharedPreference(context);
		 cleanFiles(context);
		for (String filePath : filepath) {
			cleanCustomCache(filePath);
		}
	}

	/** * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath */
	public static void cleanCustomCache(String filePath) {
		deleteFilesByDirectory(new File(filePath));
	}

	/** * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory */
	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists()) {
			if (directory.isFile()) {
				directory.delete();
			}
			if (directory.isDirectory()) {
				for (File item : directory.listFiles()) {
					deleteFilesByDirectory(item);
				}
				directory.delete();
			}
		}
	}

	/**
	 * 获取缓存大小
	 */
	private static long cacheSize = 0;

	public static String getCacheDataSize(Context mContext) {
		cacheSize = 0;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File f = mContext.getExternalCacheDir().getParentFile();
			cacheSize = getFileSize(f);
		}

		return getCacheDataSize(mContext,mContext.getExternalCacheDir().getParent());
	}

	public static String getCacheDataSize(Context mContext,String...targetPath) {
		cacheSize = 0;
		for (String path:targetPath){
			File f = new File(path);
			long size = getFileSize(f);
			cacheSize+=size;
		}
//		cacheSize = getFileSize(f);
		return getFormatSize(cacheSize);
	}

	private static long getFileSize(File f) {
		if (f != null && f.exists()) {
			if (f.isFile()) {
				cacheSize += f.length();
				return cacheSize;
			} else if (f.isDirectory()) {
				for (File item : f.listFiles()) {
					getFileSize(item);
				}
			}
		}
		return cacheSize;
	}

	/**
	 * 格式化单位
	 * 
	 * @param size
	 * @return
	 */
	private static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			return null;
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "KB";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "MB";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
				+ "TB";
	}
}