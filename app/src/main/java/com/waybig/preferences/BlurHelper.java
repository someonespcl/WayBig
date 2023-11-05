package com.waybig.preferences;

import android.content.Context;
import android.view.View;
import android.graphics.Bitmap;
import android.renderscript.RenderScript;
import android.renderscript.Allocation;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Element;
import android.graphics.drawable.BitmapDrawable;

public class BlurHelper {

    public static Bitmap applyBlur(Context context, View view, int radius) {
        RenderScript rs = RenderScript.create(context);

        // Create a bitmap from the view
        Bitmap sourceBitmap = createBitmapFromView(view);

        // Apply the blur effect
        Bitmap blurredBitmap = blurBitmap(rs, sourceBitmap, radius);

        rs.destroy();

        return blurredBitmap;
    }

    private static Bitmap createBitmapFromView(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private static Bitmap blurBitmap(RenderScript rs, Bitmap bitmap, int radius) {
        Allocation input =
                Allocation.createFromBitmap(
                        rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setInput(input);
        script.setRadius(radius);
        script.forEach(output);
        output.copyTo(bitmap);
        return bitmap;
    }

    public static void applyBlurToView(Context context, View view, int radius) {
        Bitmap blurredBitmap = applyBlur(context, view, radius);
        view.setBackground(new BitmapDrawable(context.getResources(), blurredBitmap));
    }
}
