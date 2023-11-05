package com.badnewsbots.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

// New class I made to help store Android Graphics helper functions that replicate those in OpenCV,
// with the goal of easing the transition from EasyOpenCV to VisionPortal API-based pipelines.
// This is crucial because we can no longer draw image annotations in OpenCV itself, and instead must
// use the Android Graphics classes like Canvas, Paint, and Bitmap to annotate on pipeline images
public final class AndroidGraphicsHelper {
    @Deprecated
    // Obsolete. Use built-in scale method in Android graphics API.
    public static Bitmap scaleBitmapByFactor(Bitmap bitmap, float scaleFactor) {
        if (scaleFactor <= 0) {
            throw new RuntimeException();
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Calculate the scaled width and height
        int scaledWidth = (int) (width * scaleFactor);
        int scaledHeight = (int) (height * scaleFactor);

        // Create a new bitmap to hold the scaled image
        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false);
    }

    // Terribly slow version of this function, only remains here for demonstration purposes to show how individual get and set methods causes such
    // a slowdown versus only one bulk get and one bulk set. (Millions of JNI calls are made in this slower method as they are not automatically
    // converted into one by the compiler. )
    @Deprecated
    public static void slowApplyMaskToCanvas(Canvas sourceCanvas, Bitmap maskBitmap, float scaleBmpPxToCanvasPx, int desiredColor) {
        //int width = sourceCanvas.getWidth();
        //int height = sourceCanvas.getHeight();
        //System.out.println(width + " " + height);
        //System.out.println(maskBitmap.getWidth() + " " + maskBitmap.getHeight());

        Paint paint = new Paint();
        paint.setColor(desiredColor);
        // Iterate through the pixels of the maskBitmap and apply the mask
        for (int x = 0; x < maskBitmap.getWidth(); x++) {
            for (int y = 0; y < maskBitmap.getHeight(); y++) {
                int maskPixel = maskBitmap.getPixel(x, y);
                // Check if the mask pixel is not black (0,0,0)
                if (Color.red(maskPixel) > 0) {
                    // Set the corresponding pixel in the sourceBitmap to the desired color
                    sourceCanvas.drawPoint(x*scaleBmpPxToCanvasPx, y*scaleBmpPxToCanvasPx, paint);
                }
            }
        }
    }

    public static void applyMaskToCanvas(Canvas sourceCanvas, Bitmap maskBitmap, float scaleBmpPxToCanvasPx, int desiredColor) {
        // Step 1: Convert maskBitmap (RGB565) to ARGB
        Bitmap maskBitmapARGB = Bitmap.createBitmap(maskBitmap.getWidth(), maskBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas toARGBCanvas = new Canvas(maskBitmapARGB);
        toARGBCanvas.drawBitmap(maskBitmap, 0, 0, new Paint());

        // Step 2: Make all black pixels transparent
        int[] pixels = new int[maskBitmapARGB.getHeight()*maskBitmapARGB.getWidth()];
        maskBitmapARGB.getPixels(pixels, 0, maskBitmapARGB.getWidth(), 0, 0, maskBitmapARGB.getWidth(), maskBitmapARGB.getHeight());
        for (int i=0; i<maskBitmapARGB.getWidth()*maskBitmapARGB.getHeight(); i++) {
            int value = pixels[i];
            if (Color.red(value)==0) {
                pixels[i] = Color.TRANSPARENT;
            }
        }
        maskBitmapARGB.setPixels(pixels, 0, maskBitmapARGB.getWidth(), 0, 0, maskBitmapARGB.getWidth(), maskBitmapARGB.getHeight());

        //Bitmap maskBlackRemoved = Bitmap.createBitmap(maskBitmap.getWidth(), maskBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //Canvas blackRemovedCanvas = new Canvas(maskBlackRemoved);
        //Paint blackRemovedPaint = new Paint();
        //blackRemovedPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
        //blackRemovedCanvas.drawBitmap(maskBitmap, 0 ,0, blackRemovedPaint);
        // Create a matrix to map the RGB565 bitmap to the ARGB8888 bitmap

        // Step 3: Apply mask with desired color
        Matrix matrix = new Matrix();
        matrix.setScale(scaleBmpPxToCanvasPx, scaleBmpPxToCanvasPx);
        ColorFilter colorFilter = new LightingColorFilter(desiredColor, 0);
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);
        sourceCanvas.drawBitmap(maskBitmapARGB, matrix, paint);
    }
}
