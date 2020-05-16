package com.mobileframe.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 代码创建数据库，根据系统数据库的版本更新本地数据库
 */
public abstract class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DB_NAME = "sqLite.db";
    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public DBHelper(Context context, String dbName) {
        this(context, dbName, 1);
    }

    public DBHelper(Context context, String dbName, int version) {
        this(context, dbName, (SQLiteDatabase.CursorFactory)null, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    public void onCreate(SQLiteDatabase db) {
        onCreateDb();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < newVersion) {
            this.onUpgradeDB();
        }
    }

    public void openDB() {
        this.db = this.getWritableDatabase();
    }

    public void closeDB() {
        if(this.db != null) {
            this.db.close();
        }
    }

    /** 获得数据库 */
    public SQLiteDatabase getDB() {
        if (db != null) {
            db.close();
        }
        openDB();
        return db;
    }

    /**
     * 获取版本号
     * @return
     */
    public int getVersion(){
        int version = getDB().getVersion();
        getDB().close();
        return version;
    }

    public abstract void onUpgradeDB();
    public abstract void onCreateDb();
}
