package com.mobileframe.data.db;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 功能描述：本地数据库实体类，存放到数据库中的类需要继承该类
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public abstract class Entry implements Serializable {
    private static final long serialVersionUID = -3487370318362864922L;

    public static final String[] ID_PROJECTION = {"_id"};

    public static interface Columns {
        public static final String ID = "_id";
    }

    // The primary key of the entry.
    @Column("_id")
    public long id = 0;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Table {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Column {
        String value();

        boolean indexed() default false;

        boolean fullText() default false;

        String defaultValue() default "";

        boolean unique() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Transient {

    }

    public void clear() {
        id = 0;
    }
}
