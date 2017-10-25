package com.sensorcontrol.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.sensorcontrol.util.HexStrUtils.bytesToHexString;

/**
 * Created by lizhe on 2017/10/23 0023.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class BmpUtils {

    public static byte[] getPicturePixel(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        List<Byte> list = new ArrayList<Byte>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int color = bitmap.getPixel(j, i);
                byte r = (byte) Color.red(color);
                byte g = (byte) Color.green(color);
                byte b = (byte) Color.blue(color);
                list.add(r);
                list.add(g);
                list.add(b);
            }
        }
//        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
//
//        List<Byte> list = new ArrayList<Byte>();
//        for (int i = 0; i < pixels.length; i++) {
//            byte clr = (byte) pixels[i];
//            byte redl = (byte) ((clr & 0xff0000) >> 16); // 取高两位
//            byte greenl = (byte) ((clr & 0xff00) >> 8); // 取中两位
//            byte bluel = (byte) (clr & 0xff);
//
//            list.add(redl);
//            list.add(greenl);
//            list.add(bluel);
//        }
        byte[] shorts = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            shorts[i] = list.get(i);
        }
        return shorts;
    }

    /**
     * 通过uri获取图片并进行压缩
     *
     * @param uri
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x800为标准
        float hh = 160f;//这里设置高度为800f
        float ww = 376f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }
    public static Bitmap resizeImage2(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return resizeImage2(bitmap,376,160);
    }

    /**
     * 获取文件类型
     * @param uri
     * @return
     */
    public static String getImageType(Activity activity,Uri uri) {
        InputStream is = null;
        String value = null;
        try {
            is = activity.getContentResolver().openInputStream(uri);
            byte[] b = new byte[3];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if("FFD8FF".equals(value)){
            return "jpg";
        } else if("FFD8FF".equals(value)){
            return "jpg";
        } else if("47494638".equals(value)){
            return "gif";
        } else if("424D".equals(value)){
            return "bmp";
        }else if ("424D36".equals(value)){
            return "bmp";
        } else {
            return null;
        }
    }
}
