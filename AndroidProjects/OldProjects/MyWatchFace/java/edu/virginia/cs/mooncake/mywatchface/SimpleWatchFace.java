package edu.virginia.cs.mooncake.mywatchface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.format.Time;

public class SimpleWatchFace {

    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ":%02d";
    private static final String DATE_FORMAT = "%02d.%02d.%d";

    private final Paint timePaint;
    private final Paint datePaint;
    private final Paint m2fedPaint1;
    private final Paint m2fedPaint2;
    private final Time time;



    private boolean shouldShowSeconds = false;

    public static SimpleWatchFace newInstance(Context context) {
        Paint timePaint = new Paint();
        timePaint.setColor(Color.WHITE);
        //timePaint.setTextSize(context.getResources().getDimension(R.dimen.time_size));
        timePaint.setTextSize(55);
        timePaint.setAntiAlias(true);

        Paint datePaint = new Paint();
        datePaint.setColor(Color.WHITE);
        //datePaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
        datePaint.setTextSize(30);
        datePaint.setAntiAlias(true);

        Paint m2fedPaint1 = new Paint();
        m2fedPaint1.setColor(Color.WHITE);
        //datePaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
        m2fedPaint1.setTextSize(35);
        m2fedPaint1.setAntiAlias(true);

        Paint m2fedPaint2 = new Paint();
        m2fedPaint2.setColor(Color.WHITE);
        //datePaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
        m2fedPaint2.setTextSize(30);
        m2fedPaint2.setAntiAlias(true);

        return new SimpleWatchFace(timePaint, datePaint, m2fedPaint1, m2fedPaint2, new Time());
    }

    SimpleWatchFace(Paint timePaint, Paint datePaint, Paint m2fedPaint1, Paint m2fedPaint2, Time time) {
        this.timePaint = timePaint;
        this.datePaint = datePaint;
        this.m2fedPaint1 = m2fedPaint1;
        this.m2fedPaint2 = m2fedPaint2;
        this.time = time;
    }

    public void draw(Canvas canvas, Rect bounds, String m2fedText1, String m2fedText2) {
        time.setToNow();
        canvas.drawColor(Color.BLACK);

        String timeText = String.format(shouldShowSeconds ? TIME_FORMAT_WITH_SECONDS : TIME_FORMAT_WITHOUT_SECONDS, time.hour, time.minute, time.second);
        float timeXOffset = computeXOffset(timeText, timePaint, bounds);
        float timeYOffset = computeTimeYOffset(timeText, timePaint, bounds);
        canvas.drawText(timeText, timeXOffset, timeYOffset, timePaint);

        String dateText = String.format(DATE_FORMAT, (time.month + 1), time.monthDay, time.year);
        float dateXOffset = computeXOffset(dateText, datePaint, bounds);
        float dateYOffset = computeDateYOffset(dateText, datePaint);
        canvas.drawText(dateText, dateXOffset, timeYOffset - dateYOffset, datePaint);

        float m2fedXOffset1 = computeXOffset(m2fedText1, m2fedPaint1, bounds);
        float m2fedXOffset2 = computeXOffset(m2fedText2, m2fedPaint2, bounds);
        canvas.drawText(m2fedText1, m2fedXOffset1, timeYOffset + 60, m2fedPaint1);
        canvas.drawText(m2fedText2, m2fedXOffset2, timeYOffset + 110, m2fedPaint2);
    }

    private float computeXOffset(String text, Paint paint, Rect watchBounds) {
        float centerX = watchBounds.exactCenterX();
        float timeLength = paint.measureText(text);
        return centerX - (timeLength / 2.0f);
    }

    private float computeTimeYOffset(String timeText, Paint timePaint, Rect watchBounds) {
//        float centerY = watchBounds.exactCenterY();
//        Rect textBounds = new Rect();
//        timePaint.getTextBounds(timeText, 0, timeText.length(), textBounds);
//        int textHeight = textBounds.height();
//        return centerY + (textHeight / 2.0f);
        return 150;
    }

    private float computeDateYOffset(String dateText, Paint datePaint) {
        Rect textBounds = new Rect();
        datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
        return textBounds.height() + 40.0f;
    }

    private float computeM2fedYOffset(String m2fedText, Paint m2fedPaint1) {
//        Rect textBounds = new Rect();
//        m2fedPaint1.getTextBounds(m2fedText, 0, m2fedText.length(), textBounds);
//        return textBounds.height() + 20.0f;
        return 50;
    }

    public void setAntiAlias(boolean antiAlias) {
        timePaint.setAntiAlias(antiAlias);
        datePaint.setAntiAlias(antiAlias);
    }

    public void setColor(int color) {
        timePaint.setColor(color);
        datePaint.setColor(color);
    }

    public void setShowSeconds(boolean showSeconds) {
        shouldShowSeconds = showSeconds;
    }
}