package com.mobileframe.tools;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.DrawableRes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The type Image utils.
 */
public class ImageUtils {

    /**
     * 计算图片的压缩比率
     *
     * @param options   the options
     * @param reqWidth  the req width
     * @param reqHeight the req height
     * @return the int
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 获取图片的角度
     *
     * @param imagePath the image path
     * @return the picture degree
     */
    public static int getPictureDegree(String imagePath) {
        int i = 0;
        try {
            ExifInterface localExifInterface = new ExifInterface(imagePath);
            int j = localExifInterface.getAttributeInt("Orientation", 1);
            switch (j) {
                case 6:
                    i = 90;
                    break;
                case 3:
                    i = 180;
                    break;
                case 8:
                    i = 270;
                case 4:
                case 5:
                case 7:
                default:
                    break;
            }
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
        return i;
    }

    /**
     * 旋转图片
     *
     * @param paramInt    the param int
     * @param paramBitmap the param bitmap
     * @return the bitmap
     */
    public static Bitmap rotaingImageView(int paramInt, Bitmap paramBitmap) {
        Matrix localMatrix = new Matrix();
        localMatrix.postRotate(paramInt);
        return Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), localMatrix, true);
    }

    /**
     * 获取圆角图片
     *
     * @param bitmap  the bitmap
     * @param roundPx the round px
     * @return the rounded corner bitmap
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 将bitmap保存为文件
     *
     * @param bitmap    the bitmap
     * @param imageFile the image file
     * @return the boolean
     */
    public static boolean bitmap2File(Bitmap bitmap, File imageFile) {
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            boolean isOK = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            return isOK;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Bitmap 2 gallery boolean.
     *
     * @param context  the context
     * @param bitmap   the bitmap
     * @param filename the filename
     * @return the boolean
     */
    public static boolean bitmap2gallery(Context context, Bitmap bitmap, String filename) {
        boolean saveSuccess;
        String extraPath = FileUtils.getExtraPath("19code");
        File file = new File(extraPath, filename);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            boolean compress = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getPath(), filename, null);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));
            saveSuccess = compress;
        } catch (FileNotFoundException e) {
            saveSuccess = false;
            e.printStackTrace();
        } catch (IOException e) {
            saveSuccess = false;
            e.printStackTrace();
        }
        return saveSuccess;
    }

    /**
     * 质量压缩
     *
     * @param image the image
     * @return the bitmap
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            options -= 10;
            if (options > 0) {
                baos.reset();
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    /**
     * save bitmap
     *
     * @param bitmap   bitmap
     * @param filePath filePath
     * @param quality  quality图片压缩质量
     * @param format   format图片保存格式
     * @see Bitmap#compress(Bitmap.CompressFormat, int, java.io.OutputStream)
     */
    public static void saveBitmap(Bitmap bitmap, String filePath, int quality, Bitmap.CompressFormat format) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.compress(format, quality, fOut);
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * save bitmap default ( quality = 100,format = JPEG )
     *
     * @param bitmap   bitmap
     * @param filePath filePath
     */
    public static void saveBitmapDefault(Bitmap bitmap, String filePath) {
        saveBitmap(bitmap, filePath, 100, Bitmap.CompressFormat.JPEG);
    }

    /**
     * 加载图片并压缩
     *
     * @param imagePath the image path
     * @param outWidth  the out width
     * @param outHeight the out height
     * @return the bitmap
     */
    public static Bitmap decodeScaleImage(String imagePath, int outWidth, int outHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        options.inDither = false;    /*不进行图片抖动处理*/
        options.inPreferredConfig = null;  /*设置让解码器以最佳方式解码*/
        /* 下面两个字段需要组合使用 */
        options.inPurgeable = true;
        options.inInputShareable = true;

        int i = calculateInSampleSize(options, outWidth, outHeight);
        options.inSampleSize = i;

        options.inJustDecodeBounds = false;

        Bitmap localBitmap1 = BitmapFactory.decodeFile(imagePath, options);
        int j = getPictureDegree(imagePath);
        Bitmap localBitmap2 = null;
        if ((localBitmap1 != null) && (j != 0)) {
            localBitmap2 = rotaingImageView(j, localBitmap1);
            localBitmap1.recycle();
            localBitmap1 = null;
            return localBitmap2;
        }
        return localBitmap1;
    }

    public static Bitmap decodeStreamImage(Context context, @DrawableRes int resId) {
        int width = DensityUtil.getScreenW(context);
        return decodeStream2Resource(context, resId, width, width);
    }

    /**
     * 根据资源id获得图片
     *
     * @param context context
     * @param resId   图片资源id
     * @param targetW targetW
     * @param targetH targetH
     * @return bitmap
     */
    public static Bitmap decodeStream2Resource(Context context, int resId, int targetW, int targetH) {
        {
            if (context == null || context.getResources() == null) {
                return null;
            }
            InputStream inputStream = context.getResources().openRawResource(resId);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inPreferredConfig = null;
            BitmapFactory.decodeStream(inputStream, null, options);
            options.inSampleSize = calculateInSampleSize(options, targetW, targetH);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    public static Bitmap getByteBitmap(InputStream inStream, Resources resources, int resId) {
        try {
            byte[] data = readStream(inStream);
            if (data != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeResource(resources, resId);
    }

    /*
     * 得到图片字节流 数组大小
     * */
    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    /**
     * 按照比例缩放将图片的宽度缩放到新宽度
     *
     * @param bitmap
     * @param w      width
     * @return bitmap
     */
    public static Bitmap scaleBitmapW(Bitmap bitmap, float w) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        float scale = w / (float) width;
        return scaleBitmap(bitmap, scale);
    }

    /**
     * 按照比例缩放将图片的高度缩放到新高度
     *
     * @param bitmap bitmap
     * @param h      height
     * @return bitmap
     */
    public static Bitmap scaleBitmapH(Bitmap bitmap, float h) {
        if (bitmap == null) {
            return null;
        }
        int height = bitmap.getHeight();
        float scale = h / (float) height;
        return scaleBitmap(bitmap, scale);
    }

    /**
     * 按比例缩放图片
     *
     * @param bitmap bitmap
     * @param scale  scale
     * @return bitmap
     */
    public static synchronized Bitmap scaleBitmap(Bitmap bitmap, float scale) {
        if (bitmap == null || bitmap.getHeight() < 0 || bitmap.getWidth() < 0) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return bitmap;
    }
}
