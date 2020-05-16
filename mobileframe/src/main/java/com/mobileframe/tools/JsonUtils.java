package com.mobileframe.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Administrator on 2016/6/21.
 * 解析普通或较小数据
 */
public class JsonUtils {

    private static final String TAG = "json resolve";
//
//    /**
//     * 把JSON文本parse为JavaBean
//     *
//     * @param json
//     * @param type
//     * @param <T>
//     * @return
//     */
//    public static <T> T parseObject(String json, JsonType type) {
//        try {
//            return new Gson().fromJson(json, type.getType());
//        } catch (Exception e) {
//            System.out.println("json format error");
//        }
//        return null;
//    }
//
//    /**
//     * 把JSON文本parse为JavaBean
//     *
//     * @param json
//     * @param type
//     * @param <T>
//     * @return
//     */
//    public static <T> T parseObject(String json, Type type) {
//        try {
//            return new Gson().fromJson(json, type);
//        } catch (Exception e) {
//            System.out.println("json format error");
//        }
//        return null;
//    }
//
//    /**
//     * 将json解析成java对象
//     *
//     * @param json
//     * @param cls
//     * @return
//     */
//    public static <T> T parseObject(String json, Class<T> cls) {
//        T t = null;
//        try {
//            Gson gson = new Gson();
//            t = gson.fromJson(json, cls);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            Log.e(TAG, ex.getMessage());
//        }
//        return t;
//    }
//
//    /**
//     * 将java对象解析为json
//     *
//     * @param cls
//     * @return
//     */
//    public static <T> String parseObject(Class<T> cls) {
//        String json = "";
//        try {
//            Gson gson = new Gson();
//            json = gson.toJson(cls);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            Log.e(TAG, "json or class error");
//        }
//        return json;
//    }
//
//    /**
//     * 使用Gson进行解析 List<person>
//     *
//     * @param <T>
//     * @param jsonString
//     * @param type       new TypeToken<List<T>>() {}
//     * @return
//     */
//    public static <T> List<T> parseList(String jsonString, JsonType type) {
//        List<T> list = new ArrayList<T>();
//        try {
//            Gson gson = new Gson();
//            list = gson.fromJson(jsonString, type.getType());
//        } catch (Exception e) {
//        }
//        return list;
//    }
//
//
//    /**
//     * 获取单个字段
//     *
//     * @param jO
//     * @param key
//     * @return
//     */
//    private static String getString(JSONObject jO, String key) {
//        String value = "";
//        if (TextUtils.isEmpty(key))
//            return value;
//        try {
//            if (jO.has(key))
//                value = jO.getString(key);
//        } catch (JSONException e) {
//            Log.e(TAG, e.toString());
//        }
//        return value;
//    }
//
//    /**
//     * 将json 数组转换为st 对象
//     *
//     * @param jsonString
//     * @return 例：{ "abc","def","ghi" }，或者 { 0:{a:"123"  },1:{b:456},3:{c:"789"} },皆长度不定
//     */
//    public static List<Object> resolveListFromJson(String jsonString) {
//        JSONObject jsonObject;
//        try {
//            jsonObject = new JSONObject(jsonString);
//            Iterator<String> keys = jsonObject.keys();
//            Object value;
//            List<Object> valueList = new ArrayList<>();
//            while (keys.hasNext()) {
//                value = jsonObject.get(keys.next());
//                valueList.add(value);
//            }
//            return valueList;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


//    ------------------------------------------fastJson utils---------------------------------------------------------

    private static final SerializeConfig config;

    static {
        config = new SerializeConfig();
        config.put(java.util.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格式
        config.put(java.sql.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格式
    }

    private static final SerializerFeature[] features = {
            SerializerFeature.WriteMapNullValue, // 输出空置字段
            SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，而不是null
            SerializerFeature.WriteNullNumberAsZero, // 数值字段如果为null，输出为0，而不是null
            SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，而不是null
            SerializerFeature.WriteNullStringAsEmpty // 字符类型字段如果为null，输出为""，而不是null
    };

    public static String toJSONString(Object object) {
        return JSON.toJSONString(object, config, features);
    }

    public static String toJSONNoFeatures(Object object) {
        return JSON.toJSONString(object, config,features);
    }


    /**
     * java Bean转为String json字符串
     *
     * @param object     javaBean
     * @param dateFormat 需要输入的时间格式
     * @return
     */
    public static String toJSONString(Object object, String dateFormat) {
        return JSON.toJSONStringWithDateFormat(object, dateFormat, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 将JavaBean转换为JSONObject或者JSONArray
     *
     * @param object
     * @return
     */
    public static Object toJSON(Object object) {
        return JSON.toJSON(object);
    }

    /**
     * 把JSON文本parse成JSONObject
     *
     * @param jsonStr
     * @return
     */
    public static JSONObject parseObject(String jsonStr) {
        return JSON.parseObject(jsonStr);
    }

    /**
     * 把JSON文本parse为JavaBean
     *
     * @param jsonStr
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String jsonStr, Class<T> clazz) {
        return JSON.parseObject(jsonStr, clazz);
    }

    /**
     * 把JSON文本parse为JavaBean
     *
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String json, Type type) {
        try {
            return JSON.parseObject(json, type);
        } catch (Exception e) {
            System.out.println("json format error");
        }
        return null;
    }

    /**
     * 把JSON文本parse成JSONArray
     *
     * @param jsonStr
     * @return
     */
    public static JSONArray parseArray(String jsonStr) {
        return JSON.parseArray(jsonStr);
    }

    // 转换为List
    public static <T> List<T> parseList(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz);
    }

}
