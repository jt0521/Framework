package com.mobileframe.tools;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Locale;
import java.util.UUID;

/**
 * 设备信息工具
 */
public class DeviceUtils {


    /**
     * 获取AndroidID
     *
     * @param ctx the ctx
     * @return the android id
     */
    public static String getAndroidID(Context ctx) {
        return Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取设备IMEI码
     *
     * @param ctx the ctx
     * @return the imei
     */
    public static String getIMEI(Context ctx) {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        return ((TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    /**
     * @param slotId slotId为卡槽Id，它的值为 0、1；
     * @return
     */
    public static String getIMEI(Context context, int slotId) {
        try {
            TelephonyManager manager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method method = manager.getClass().getMethod("getImei", int.class);
            return (String) method.invoke(manager, slotId);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取设备IMSI码
     *
     * @param ctx the ctx
     * @return the imsi
     */
    public static String getIMSI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSubscriberId() != null ? tm.getSubscriberId() : null;
    }

    /**
     * 获取MAC地址
     *
     * @param ctx the ctx
     * @return the wifi mac addr
     */
    @SuppressWarnings("MissingPermission")
    public static String getWifiMacAddr(Context ctx) {
        String macAddr = "";
        try {
            WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            macAddr = wifi.getConnectionInfo().getMacAddress();
            if (macAddr == null) {
                macAddr = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return macAddr;
    }

    /**
     * 获取网络IP地址(优先获取wifi地址)
     *
     * @param ctx the ctx
     * @return the ip
     */
    public static String getIP(Context ctx) {
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled() ? getWifiIP(wifiManager) : getGPRSIP();
    }

    /**
     * 获取WIFI连接下的ip地址
     *
     * @param wifiManager the wifi manager
     * @return the wifi ip
     */
    public static String getWifiIP(WifiManager wifiManager) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ip = intToIp(wifiInfo.getIpAddress());
        return ip != null ? ip : "";
    }

    /**
     * 获取GPRS连接下的ip地址
     *
     * @return the gprsip
     */
    public static String getGPRSIP() {
        String ip = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                for (Enumeration<InetAddress> enumIpAddr = en.nextElement().getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip = null;
        }
        return ip;
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }


    /**
     * 获取设备序列号
     *
     * @return the serial
     */
    public static String getSerial() {
        return Build.SERIAL;
    }

    /**
     * 获取SIM序列号
     *
     * @param ctx the ctx
     * @return the sim serial
     */
    public static String getSIMSerial(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimSerialNumber();
    }

    /**
     * 获取网络运营商 46000,46002,46007 中国移动,46001 中国联通,46003 中国电信
     *
     * @param ctx the ctx
     * @return the mnc
     */
    public static String getMNC(Context ctx) {
        String providersName = "";
        TelephonyManager telephonyManager =
                (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
            providersName = telephonyManager.getSimOperator();
            providersName = providersName == null ? "" : providersName;
        }
        return providersName;
    }

    /**
     * 获取网络运营商：中国电信,中国移动,中国联通
     *
     * @param ctx the ctx
     * @return the carrier
     */
    public static String getCarrier(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkOperatorName().toLowerCase(Locale.getDefault());
    }


    /**
     * 获取硬件型号
     *
     * @return the model
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取编译厂商
     *
     * @return the build brand
     */
    public static String getBuildBrand() {
        return Build.BRAND;
    }

    /**
     * 获取编译服务器主机
     *
     * @return the build host
     */
    public static String getBuildHost() {
        return Build.HOST;
    }

    /**
     * 获取描述Build的标签
     *
     * @return the build tags
     */
    public static String getBuildTags() {
        return Build.TAGS;
    }

    /**
     * 获取系统编译时间
     *
     * @return the build time
     */
    public static long getBuildTime() {
        return Build.TIME;
    }

    /**
     * 获取系统编译作者
     *
     * @return the build user
     */
    public static String getBuildUser() {
        return Build.USER;
    }

    /**
     * 获取编译系统版本(5.1)
     *
     * @return the build version release
     */
    public static String getBuildVersionRelease() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取开发代号
     *
     * @return the build version codename
     */
    public static String getBuildVersionCodename() {
        return Build.VERSION.CODENAME;
    }

    /**
     * 获取源码控制版本号
     *
     * @return the build version incremental
     */
    public static String getBuildVersionIncremental() {
        return Build.VERSION.INCREMENTAL;
    }

    /**
     * 获取编译的SDK
     *
     * @return the build version sdk
     */
    public static int getBuildVersionSDK() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取修订版本列表(LMY47D)
     *
     * @return the build id
     */
    public static String getBuildID() {
        return Build.ID;
    }

    /**
     * CPU指令集
     *
     * @return the string [ ]
     */
    public static String[] getSupportedABIS() {
        String[] result = new String[]{"-"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            result = Build.SUPPORTED_ABIS;
        }
        if (result == null || result.length == 0) {
            result = new String[]{"-"};
        }
        return result;
    }

    /**
     * 获取硬件制造厂商
     *
     * @return the manufacturer
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }


    /**
     * 获取系统启动程序版本号
     *
     * @return the bootloader
     */
    public static String getBootloader() {
        return Build.BOOTLOADER;
    }


    /**
     * Gets screen display id.
     *
     * @param ctx the ctx
     * @return the screen display id
     */
    public static String getScreenDisplayID(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        return String.valueOf(wm.getDefaultDisplay().getDisplayId());
    }

    /**
     * 获取系统版本Level
     *
     * @return
     */
    public static int getSystemVersionLevel() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取系统版本号
     *
     * @return the display version
     */
    public static String getDisplayVersion() {
        return Build.DISPLAY;
    }


    /**
     * 获取语言
     *
     * @return the language
     */
    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取国家
     *
     * @param ctx the ctx
     * @return the country
     */
    public static String getCountry(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        Locale locale = Locale.getDefault();
        return tm.getSimState() == TelephonyManager.SIM_STATE_READY ?
                tm.getSimCountryIso().toLowerCase(Locale.getDefault()) :
                locale.getCountry().toLowerCase(locale);
    }

    /**
     * 获取系统版本:5.1.1
     *
     * @return the os version
     */
    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取GSF序列号
     *
     * @param context the context
     * @return the gsfid
     */
//<uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES"/>
    public static String getGSFID(Context context) {
        String result;
        final Uri URI = Uri.parse("content://com.google.android.gsf.gservices");
        final String ID_KEY = "android_id";
        String[] params = {ID_KEY};
        Cursor c = context.getContentResolver().query(URI, null, null, params, null);
        if (c == null || !c.moveToFirst() || c.getColumnCount() < 2) {
            return null;
        } else {
            result = Long.toHexString(Long.parseLong(c.getString(1)));
        }
        c.close();
        return result;
    }

    /**
     * 获取蓝牙地址
     *
     * @param context the context
     * @return the bluetooth mac
     */
//<uses-permission android:name="android.permission.BLUETOOTH"/>
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @SuppressWarnings("MissingPermission")
    public static String getBluetoothMAC(Context context) {
        String result = null;
        try {
            if (context.checkCallingOrSelfPermission(Manifest.permission.BLUETOOTH)
                    == PackageManager.PERMISSION_GRANTED) {
                BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
                result = bta.getAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Android设备物理唯一标识符
     *
     * @return the psuedo unique id
     */
    public static String getPsuedoUniqueID() {
        String devIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            devIDShort += (Build.SUPPORTED_ABIS[0].length() % 10);
        } else {
            devIDShort += (Build.CPU_ABI.length() % 10);
        }
        devIDShort += (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
        String serial;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            return new UUID(devIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception e) {
            serial = "ESYDV000";
        }
        return new UUID(devIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * 构建标识,包括brand,name,device,version.release,id,version.incremental,type,tags这些信息
     *
     * @return the fingerprint
     */
    public static String getFingerprint() {
        return Build.FINGERPRINT;
    }

    /**
     * 获取硬件信息
     *
     * @return the hardware
     */
    public static String getHardware() {
        return Build.HARDWARE;
    }

    /**
     * 获取产品信息
     *
     * @return the product
     */
    public static String getProduct() {
        return Build.PRODUCT;
    }

    /**
     * 获取设备信息
     *
     * @return the device
     */
    public static String getDevice() {
        return Build.DEVICE;
    }

    /**
     * 获取主板信息
     *
     * @return the board
     */
    public static String getBoard() {
        return Build.BOARD;
    }

    /**
     * 获取基带版本(无线电固件版本 Api14以上)
     *
     * @return the radio version
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static String getRadioVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ?
                Build.getRadioVersion() : "";
    }

    /**
     * 获取的浏览器指纹(User-Agent)
     *
     * @param ctx the ctx
     * @return the ua
     */
    public static String getUA(Context ctx) {
        final String system_ua = System.getProperty("http.agent");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return new WebView(ctx).getSettings().getDefaultUserAgent(ctx) + "__" + system_ua;
        } else {
            return new WebView(ctx).getSettings().getUserAgentString() + "__" + system_ua;
        }
    }

    /**
     * 获取得屏幕密度
     *
     * @param ctx the ctx
     * @return the density
     */
    public static String getDensity(Context ctx) {
        String densityStr = null;
        final int density = ctx.getResources().getDisplayMetrics().densityDpi;
        switch (density) {
            case DisplayMetrics.DENSITY_LOW:
                densityStr = "LDPI";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                densityStr = "MDPI";
                break;
            case DisplayMetrics.DENSITY_TV:
                densityStr = "TVDPI";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                densityStr = "HDPI";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                densityStr = "XHDPI";
                break;
            case DisplayMetrics.DENSITY_400:
                densityStr = "XMHDPI";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                densityStr = "XXHDPI";
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                densityStr = "XXXHDPI";
                break;
        }
        return densityStr;
    }

    /**
     * 获取google账号
     *
     * @param ctx the ctx
     * @return the string [ ]
     */
//<uses-permission android:name="android.permission.GET_ACCOUNTS"/>
    @SuppressWarnings("MissingPermission")
    public static String[] getGoogleAccounts(Context ctx) {
        if (ctx.checkCallingOrSelfPermission(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            Account[] accounts = AccountManager.get(ctx).getAccountsByType("com.google");
            String[] result = new String[accounts.length];
            for (int i = 0; i < accounts.length; i++) {
                result[i] = accounts[i].name;
            }
            return result;
        }
        return null;
    }

    /**
     * if null, use the default format (Mozilla/5.0 (Linux; U;
     * Android %s) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0
     * %sSafari/534.30).
     *
     * @return
     */
    public static String getUserAgent() {
        String webUserAgent = "Android_%s_%s";
        String version = Build.VERSION.RELEASE;
        String name = Build.BRAND + " " + Build.MODEL;
        String userAgent = String.format(webUserAgent, version, name);
        try {
            return URLEncoder.encode(userAgent, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;

        // if (context != null) {
        // try {
        // Class sysResCls = Class.forName("com.android.internal.R$string");
        // Field webUserAgentField =
        // sysResCls.getDeclaredField("web_user_agent");
        // Integer resId = (Integer) webUserAgentField.get(null);
        // webUserAgent = context.getString(resId);
        // } catch (Throwable ignored) {
        // }
        // }
        // if (TextUtils.isEmpty(webUserAgent)) {
        // webUserAgent =
        // "Mozilla/5.0 (Linux; U; Android %s) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0
        // %sSafari/533.1";
        // }
        //
        // Locale locale = Locale.getDefault();
        // StringBuffer buffer = new StringBuffer();
        // // Add version
        // final String version = Build.VERSION.RELEASE;
        // if (version.length() > 0) {
        // buffer.append(version);
        // } else {
        // // default to "1.0"
        // buffer.append("1.0");
        // }
        // buffer.append("; ");
        // final String language = locale.getLanguage();
        // if (language != null) {
        // buffer.append(language.toLowerCase());
        // final String country = locale.getCountry();
        // if (country != null) {
        // buffer.append("-");
        // buffer.append(country.toLowerCase());
        // }
        // } else {
        // // default to "en"
        // buffer.append("en");
        // }
        // // add the model for the release build
        // if ("REL".equals(Build.VERSION.CODENAME)) {
        // final String model = Build.MODEL;
        // if (model.length() > 0) {
        // buffer.append("; ");
        // buffer.append(model);
        // }
        // }
        // final String id = Build.ID;
        // if (id.length() > 0) {
        // buffer.append(" Build/");
        // buffer.append(id);
        // }
        // return String.format(webUserAgent, buffer, "Mobile ");
    }

    /**
     * 获取网络类型
     *
     * @param content
     * @return
     */
    public static final int NETWORD_UNKNOW = 200;
    public static final int NETWORK_WIFI = 201;
    public static final int NETWORK_2G = 202;
    public static final int NETWORK_3G = 203;
    public static final int NETWORK_4G = 204;

    public static int getNetType(Context content) {
        ConnectivityManager cManager = (ConnectivityManager) content
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cManager.getActiveNetworkInfo();
        int type = NETWORD_UNKNOW;
        if (netInfo != null) {
            switch (netInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    type = NETWORK_WIFI;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    TelephonyManager telMgr = (TelephonyManager) content
                            .getSystemService(Context.TELEPHONY_SERVICE);
                    switch (telMgr.getNetworkType()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            type = NETWORK_2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case 14:// TelephonyManager.NETWORK_TYPE_EHRPD
                        case 15:// TelephonyManager.NETWORK_TYPE_HSPAP
                            type = NETWORK_3G;
                            break;
                        case 13:// TelephonyManager.NETWORK_TYPE_LTE
                            type = NETWORK_4G;
                        default:
                            type = NETWORD_UNKNOW;
                            break;
                    }
                    break;
                default:
                    type = NETWORD_UNKNOW;
                    break;
            }
        }
        return type;
    }

    /**
     * 获取网络类型名称
     *
     * @param content
     * @return
     */
    public static String getNetTypeName(Context content) {
        String type = "";
        switch (getNetType(content)) {
            case NETWORD_UNKNOW:
                type = "unknow";
                break;
            case NETWORK_WIFI:
                type = "wifi";
                break;
            case NETWORK_2G:
                type = "2G";
                break;
            case NETWORK_3G:
                type = "3G";
                break;
            case NETWORK_4G:
                type = "4G";
                break;
            default:
                break;
        }
        return type;
    }
}