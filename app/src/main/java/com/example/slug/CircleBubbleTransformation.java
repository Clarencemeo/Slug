package com.example.slug;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;

public class CircleBubbleTransformation implements com.squareup.picasso.Transformation {
    private static final int photoMargin = 30;
    private static final int margin = 20;
    private static final int triangleMargin = 10;

    @Override
    public Bitmap transform(final Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        float r = size/2f;

        Bitmap output = Bitmap.createBitmap(size+triangleMargin, size+triangleMargin, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paintBorder = new Paint();
        paintBorder.setAntiAlias(true);
        paintBorder.setColor(Color.parseColor("#333030"));
        paintBorder.setStrokeWidth(margin);
        canvas.drawCircle(r, r, r-margin, paintBorder);

        Paint trianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trianglePaint.setStrokeWidth(2);
        trianglePaint.setColor(Color.parseColor("#333030"));
        trianglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        trianglePaint.setAntiAlias(true);
        Path triangle = new Path();
        triangle.setFillType(Path.FillType.EVEN_ODD);
        triangle.moveTo(size-margin, size / 2);
        triangle.lineTo(size/2, size+triangleMargin);
        triangle.lineTo(margin, size/2);
        triangle.close();
        canvas.drawPath(triangle, trianglePaint);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawCircle(r, r, r-photoMargin, paint);

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return "circlebubble";
    }
}