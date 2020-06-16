package com.deep.netdeep.util;

import android.view.MotionEvent;
import android.view.View;

public class TouchExt {
    public static boolean alpTouch(View v, MotionEvent event, TouchExtListener touchExtListener) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setAlpha(0.5f);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                v.setAlpha(1.0f);
                touchExtListener.doThing();
                break;
        }
        return true;
    }
    public interface TouchExtListener {
        void doThing();
    }

    public static boolean alpTouchEx(View v, MotionEvent event, TouchExExtListener touchExtListener) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setAlpha(0.5f);
                touchExtListener.doThingDown();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                v.setAlpha(1.0f);
                touchExtListener.doThingUp();
                break;
        }
        return true;
    }
    public interface TouchExExtListener {
        void doThingDown();
        void doThingUp();
    }
}
