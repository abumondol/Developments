package edu.virginia.cs.mooncake.mywatchface;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.concurrent.TimeUnit;

public class MyWatchFaceService extends CanvasWatchFaceService {

    String m2fedText1 = "Invalid data1";
    String m2fedText2 = "Invalid data2";

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(new MyReceiver(),
                new IntentFilter("edu.virginia.cs.mooncake.m2feddata.SERVICE_MSG"));
    }

    @Override
    public Engine onCreateEngine() {
        return new SimpleEngine();
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            m2fedText1 = intent.getStringExtra("msg1");
            m2fedText2 = intent.getStringExtra("msg2");

            if (m2fedText1 == null)
                m2fedText1 = "Null";

            if (m2fedText2 == null)
                m2fedText2 = "Null";
        }

    }


    private class SimpleEngine extends CanvasWatchFaceService.Engine {

        private final long TICK_PERIOD_MILLIS = TimeUnit.SECONDS.toMillis(1);
        private Handler timeTick;
        private SimpleWatchFace watchFace;


        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            timeTick = new Handler(Looper.myLooper());
            startTimerIfNecessary();

            watchFace = SimpleWatchFace.newInstance(MyWatchFaceService.this);
        }

        private void startTimerIfNecessary() {
            timeTick.removeCallbacks(timeRunnable);
            if (isVisible() && !isInAmbientMode()) {
                timeTick.post(timeRunnable);
            }
        }


        private final Runnable timeRunnable = new Runnable() {
            @Override
            public void run() {
                onSecondTick();

                if (isVisible() && !isInAmbientMode()) {
                    timeTick.postDelayed(this, TICK_PERIOD_MILLIS);
                }
            }
        };

        private void onSecondTick() {
            invalidateIfNecessary();
        }

        private void invalidateIfNecessary() {
            if (isVisible() && !isInAmbientMode()) {
                invalidate();
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            startTimerIfNecessary();
        }


        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);
            watchFace.draw(canvas, bounds, m2fedText1, m2fedText2);
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            watchFace.setAntiAlias(!inAmbientMode);
            watchFace.setColor(inAmbientMode ? Color.GRAY : Color.WHITE);
            watchFace.setShowSeconds(!isInAmbientMode());
            invalidate();

            startTimerIfNecessary();
        }


        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onDestroy() {
            timeTick.removeCallbacks(timeRunnable);
            super.onDestroy();
        }


    }

}
