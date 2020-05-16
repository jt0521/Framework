package com.mobileframe.tools;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * 数据库查询
 */
public class SQLHelper {
    /**
     * 将查询数据以列表返回
     *
     * @param dbHelper  SQL助手
     * @param tableName 表名称
     * @param sqlStr
     * @return
     */
    public static ArrayList<String[]> queryList(DBHelper dbHelper,
                                                String tableName, String[] filedArr, String sqlStr) {
        synchronized (dbHelper) {
            ArrayList<String[]> dataList = new ArrayList<String[]>();
            if (TextUtils.isEmpty(sqlStr)) {
                return dataList;
            }
            Cursor cursor = dbHelper.getDB().rawQuery(sqlStr, null);
            int len = filedArr.length;
            while (cursor.moveToNext()) {
                String fieldDataArr[] = new String[len];
                for (int i = 0; i < len; i++) {
                    fieldDataArr[i] = cursor.getString(cursor
                            .getColumnIndex(filedArr[i]));
                }
                dataList.add(fieldDataArr);
            }
            cursor.close();
            return dataList;
        }
    }

    /**
     * 插入单条数据
     */
    public static boolean insertOneData(DBHelper dbHelper, String table,
                                        ContentValues values) {
        synchronized (dbHelper) {
            long i = dbHelper.getDB().insert(table, null, values);
            if (i == -1) {
                return false;
            }
            return true;
        }
    }

    /**
     * 插入单条数据
     */
    public static boolean insertOneData(DBHelper dbHelper, String table,
                                        String[] fieldArr, String[] values) {
        //数据更新
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < fieldArr.length; i++) {
            contentValues.put(fieldArr[i], values[i]);
        }
        synchronized (dbHelper) {
            long i = dbHelper.getDB().insert(table, null, contentValues);
            if (i == -1) {
                return false;
            }
            return true;
        }
    }

    /**
     * 删除单条数据
     */
    public static void deleteOneData(DBHelper dbHelper, String table,
                                     String whereClause, String whereArgs[]) {
        synchronized (dbHelper) {
            dbHelper.getDB().delete(table, whereClause, whereArgs);
        }
    }

    /**
     * 更改单条数据
     */
    public static boolean updateOneData(DBHelper dbHelper, String table,
                                        ContentValues values, String whereClause, String whereArgs[]) {
        synchronized (dbHelper) {
            return dbHelper.getDB().update(table, values, whereClause,
                    whereArgs) > 0;
        }
    }

    /**
     * 查询数据是否存在
     *
     * @param dbHelper
     * @param sql
     * @return
     */
    public static boolean queryIsExitData(DBHelper dbHelper, String sql) {
        synchronized (dbHelper) {
            Cursor cursor = dbHelper.getDB().rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                return true;
            }
            return false;
        }
    }

    /**
     * 查询数据是否存在
     *
     * @param dbHelper
     * @param tableName 表名
     * @param whereArgs 查询条件
     * @return
     */
    public static boolean hasData(DBHelper dbHelper, String tableName, String whereArgs) {
        String sql = "select * from main." + tableName + " where id='" +
                whereArgs + "'";
        synchronized (dbHelper) {
            Cursor cursor = dbHelper.getDB().rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                return true;
            }
            return false;
        }
    }

//    /**
//     * 多条件查询数据是否存在
//     *
//     * @param dbHelper
//     * @param tableName 表名
//     * @param whereArgs 查询条件
//     * @return
//     */
//    public static boolean hasData(DBHelper dbHelper,String tableName,String whereClause, String[] whereArgs) {
//        String sql = "select * from main." + tableName + " where id='" +
//                whereArgs + "'";
//        synchronized (dbHelper) {
//            Cursor cursor = dbHelper.getDB().rawQuery(sql, null);
//            if (cursor.getCount() > 0) {
//                return true;
//            }
//            return false;
//        }
//    }

    /**
     * 保存城市天气数据和添加城市
     * @param dbHelper
     * @param tableName
     * @param fieldArr 更新什么字段
     * @param values 更新的值
     * @param whereClause 根据什么字段更新 ：“id=? and name=?”
     * @param whereArgs 根据什么字段的什么值更新 new String []{ "111","project" }
     * @return 更新成功返回true，失败返回false
     */
    public static boolean updateSaveCityData(DBHelper dbHelper,
                                             String tableName,String []fieldArr,String []values,
                                             String whereClause, String whereArgs[]) {
        if (true) {//已添加城市，做数据更新
            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < fieldArr.length; i++) {
                contentValues.put(fieldArr[i], values[i]);
            }
            return SQLHelper.updateOneData(dbHelper,tableName,contentValues,whereClause,whereArgs);
        } else {//未添加做添加
            return SQLHelper.insertOneData(dbHelper,tableName,fieldArr,values);
        }
    }

    /**
     * 更新单条数据
     *
     * @param dbHelper
     * @param tableName   表名
     * @param fieldArr    要更新的字段
     * @param values      更新为什么值
     * @param whereClause 根据什么字段更新
     * @param whereArgs   根据什么字段的什么值更新数据
     */
    public static void updateData(DBHelper dbHelper, String tableName,
                                  String[] fieldArr, String[] values, String whereClause, String whereArgs) {
        //数据更新
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < fieldArr.length; i++) {
            contentValues.put(fieldArr[i], values[i]);
        }
        SQLHelper.updateOneData(dbHelper, tableName, contentValues, "", new String[]{whereArgs});
    }
}
