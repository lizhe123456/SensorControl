package com.sensorcontrol.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Created by lizhe on 2017/11/18 0018.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class ImageHelper {
    /**
     *
     * @param bm 图像 （不可修改）
     * @param contrast 对比度
     * @param saturation 饱和度
     * @param lum 亮度
     * @return
     */
    public static Bitmap handleImageEffect(Bitmap bm, float contrast, float saturation, float lum) {

        Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bmp);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

//        ColorMatrix hueMatrix = new ColorMatrix();
//        hueMatrix.setRotate(0, hue); // R
//        hueMatrix.setRotate(1, hue); // G
//        hueMatrix.setRotate(2, hue); // B
        // int brightness = progress - 127;

        ColorMatrix cMatrix = new ColorMatrix();

        float regulateBright = 0.0F;
        regulateBright = (1.0F - contrast) * 128.0F;
        cMatrix.set(new float[]{contrast, 0.0F, 0.0F, 0.0F, regulateBright,
                0.0F, contrast, 0.0F, 0.0F, regulateBright,
                0.0F, 0.0F, contrast, 0.0F, regulateBright,
                0.0F, 0.0F, 0.0F, 1.0F, 0.0F});

        ColorMatrix saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(saturation);

        ColorMatrix lumMatrix = new ColorMatrix();
//        lumMatrix.setScale(lum, lum, lum, 1);
        lumMatrix.set(new float[]{1.0F, 0.0F, 0.0F, 0.0F, lum,
                0.0F, 1.0F, 0.0F, 0.0F, lum,
                0.0F, 0.0F, 1.0F, 0.0F, lum,
                0.0F, 0.0F, 0.0F, 1.0F, 0.0F});

        //融合
        ColorMatrix imageMatrix = new ColorMatrix();
        imageMatrix.postConcat(cMatrix);
//        imageMatrix.postConcat(hueMatrix);
        imageMatrix.postConcat(saturationMatrix);
        imageMatrix.postConcat(lumMatrix);

        paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));
        canvas.drawBitmap(bm, 0, 0, paint);

        return bmp;
    }
}
