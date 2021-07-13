package com.mobileframe.tools;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * 功能描述： 数字工具类
 * 添加日期：2017.10.6
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class NumberUtil {

    public static String parseToString(double doubleMoney) {
        return parseToString(String.valueOf(doubleMoney));
    }

    public static String parseToString(String doubleMoney) {
        if (doubleMoney.contains(".")) {
            int num = doubleMoney.length() - (doubleMoney.indexOf(".") + 1);
            if (num > 2) {
                doubleMoney = doubleMoney.substring(0,
                        doubleMoney.indexOf(".") + 3);
            } else if (num < 2) {
                doubleMoney += "0";
            }
        } else {
            doubleMoney += ".00";
        }
        return doubleMoney;
    }

    /**
     * 四舍五入
     *
     * @param val
     * @param precision 保留几位小数
     * @return
     */
    public static Double roundDouble(double val, int precision) {
        Double ret = null;
        try {
            double factor = Math.pow(10, precision);
            ret = Math.floor(val * factor + 0.5) / factor;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * double转换成string 可以防止double显示成4.99958333E7
     *
     * @param value     待转换值
     * @param precision 需要保留的位数
     * @return
     */
    public static String doubleToString(double value, int precision) {
        return doubleToString(value, precision, true);
    }

    /**
     * double转换成string 可以防止double显示成4.99958333E7 不四舍五入 不四舍五入
     *
     * @param value     待转换值
     * @param precision 需要保留的位数
     * @param isUpDown  是否需要四舍五入 false 直接截取
     * @return
     */
    public static String doubleToString(double value, int precision,
                                        boolean isUpDown) {
        NumberFormat df = NumberFormat.getInstance();
        df.setMaximumFractionDigits(precision);
        df.setMinimumFractionDigits(precision);
        if (!isUpDown) {
            df.setRoundingMode(RoundingMode.DOWN);
        }
        return df.format(value).replace(",", "");
    }

    /**
     * long转换成string 可以防止long显示成4.99958333E7
     *
     * @param value
     * @return
     */
    public static String longToString(long value) {
        NumberFormat df = NumberFormat.getInstance();
        df.setMaximumFractionDigits(0);
        df.setMinimumFractionDigits(0);
        return df.format(value).replace(",", "");
    }

    // 从字符串中获取数字
    public static String getNumFromString(String str) {
        String str2 = "";
        str = str.trim();
        if (str != null && !"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    str2 += str.charAt(i);
                }
            }
        }
        return str2;
    }

    // 根据传入指定起始点隐藏(用*代替) 起点从0开始计算
    public static String hideString(String str, int start, int end) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        if (end > str.length() || start > str.length() || end <= start) {
            return str;
        }
        String hideReplace = "";
        int count = end - start;
        for (int i = 0; i < count; i++) {
            hideReplace += "*";
        }
        return str.substring(0, start) + hideReplace
                + str.substring(end);
    }

    /**
     * 1转成01
     *
     * @param value
     * @return
     */
    public static String intAddToString(int value) {
        if (value < 9) {
            return "0" + value;
        } else {
            return "" + value;
        }
    }

    /**
     * 判断number参数是否是整型数或者浮点数表示方式
     *
     * @param number
     * @return
     */
    public static boolean isIntegerOrFloatNumber(String number) {
        return isIntegerNumber(number) || isFloatPointNumber(number);
    }

    /**
     * 判断number参数是否是整型数表示方式
     *
     * @param number
     * @return
     */
    public static boolean isIntegerNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        number = number.trim();
        String intNumRegex = "\\-{0,1}\\d+";//整数的正则表达式
        return number.matches(intNumRegex);
    }

    /**
     * 判断number参数是否是浮点数表示方式
     *
     * @param number
     * @return
     */
    public static boolean isFloatPointNumber(String number) {
        number = number.trim();
        String pointPrefix = "(\\-|\\+){0,1}\\d*\\.\\d+";//浮点数的正则表达式-小数点在中间与前面
        String pointSuffix = "(\\-|\\+){0,1}\\d+\\.";//浮点数的正则表达式-小数点在后面
        return number.matches(pointPrefix) || number.matches(pointSuffix);
    }

    /**
     * 修剪浮点类型 @param value @param rules 规则(如:0.00保留2位小数)
     */
    public static String getTrim(String value, String rules) {
        if (value == null || value.length() == 0 || rules == null || rules.length() == 0) {
            return "";
        }
        try {
            return getTrim(Double.parseDouble(value), rules);
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * 修剪浮点类型 @param value @param rules 规则(如:0.00保留2位小数)
     */
    public static String getTrim(double value, String rules) {
        DecimalFormat df = new DecimalFormat(rules);
        return df.format(value);
    }

    /**
     * @param number
     * @return : boolean
     * @Method :isOdd
     * @Function:（判断奇、偶数）
     */
    public static boolean isOdd(int number) {
        return (number & 1) == 0;
    }

    /**
     * 是否是float类型
     *
     * @param str
     * @return
     */
    public static synchronized Boolean isDouble(String str) {
        if (issNull(str)) {
            return false;
        } else return str.contains(".") || isNumeric(str);
    }

    /**
     * 是否为空
     *
     * @param obj
     * @return
     */
    private static Boolean issNull(Object obj) {
        if (obj == null || obj.equals("") || obj.equals("null") || obj.equals("-")) {
            return true;
        }
        if (obj instanceof String) {
            String objValue = (String) obj;
            return objValue.trim().equals("");
        }
        return false;
    }

    /**
     * 字符串中是否纯数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (issNull(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 四舍五入
     *
     * @param value
     * @return
     */
    public String getRoundingNum(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        try {
            DecimalFormat df = new DecimalFormat("0");
            return df.format(Double.parseDouble(value));
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * 四舍五入
     *
     * @param value
     * @param rules
     * @return
     */
    public String getRoundingNum(String value, String rules) {
        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(rules)) {
            return "";
        }
        try {
            DecimalFormat df = new DecimalFormat(rules);
            return df.format(Double.parseDouble(value));
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * 字符串转int
     *
     * @param numStr
     * @param defaultValue
     * @return
     */
    public static int getInt(String numStr, int defaultValue) {
        if (isFloatPointNumber(numStr)) {
            float tmp = Float.valueOf(numStr);
            return (int) tmp;
        }
        return defaultValue;
    }

    /**
     * 字符串转Integer
     *
     * @param numStr 字符串数字
     * @return int.若为null表示字符串为空或者转换错误
     */
    public static Integer str2Integer(String numStr) {
        if (TextUtils.isEmpty(numStr)) {
            return null;
        } else {
            try {
                return Integer.parseInt(numStr);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static Integer str2Int(String numStr) {
        if (TextUtils.isEmpty(numStr)) {
            return 0;
        } else {
            try {
                return Integer.parseInt(numStr);
            } catch (Exception e) {
                return 0;
            }
        }
    }

    public static Double str2Double(String numStr) {
        if (TextUtils.isEmpty(numStr)) {
            return 0.0;
        } else {
            try {
                return Double.parseDouble(numStr);
            } catch (Exception e) {
                return 0.0;
            }
        }
    }

    /**
     * 判断数值是否相等
     *
     * @param num    数值
     * @param numStr 字符串数值
     * @return boolean:true相等，false否
     */
    public static boolean numEqualsNumstr(int num, String numStr) {
        Integer num2 = str2Integer(numStr);
        return null != num2 && num == num2;
    }


    /**
     * 获取四舍五入的小数
     *
     * @param num   数字字符串
     * @param scale 保留小数位
     */
    public static String getScaleData(String num, int scale) {
        if (TextUtils.isEmpty(num)) {
            num = "0";
        }
        BigDecimal mData = new BigDecimal(num)
                .setScale(scale, BigDecimal.ROUND_HALF_UP); //四舍五入，保留两位小数

        return "" + mData;
    }
}
