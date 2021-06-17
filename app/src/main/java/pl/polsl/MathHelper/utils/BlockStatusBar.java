package pl.polsl.MathHelper.utils;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BlockStatusBar {
    Context context;
    // To keep track of activity's window focus
    boolean currentFocus;
    // To keep track of activity's foreground/background status
    boolean isPaused;

    public Handler collapseNotificationHandler;
    Method collapseStatusBar = null;

    public BlockStatusBar(Context context, boolean isPaused) {
        this.context = context;
        this.isPaused = isPaused;
        collapseNow();
    }

    public void collapseNow() {


        // Initialize 'collapseNotificationHandler'
        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = new Handler();

        }

        // If window focus has been lost && activity is not in a paused state
        // Its a valid check because showing of notification panel
        // steals the focus from current activity's window, but does not
        // 'pause' the activity
        if (!currentFocus && !isPaused) {

            Runnable myRunnable = new Runnable() {
                public void run() {
                    // do something
                    try {

                        // Use reflection to trigger a method from 'StatusBarManager'
                        Object statusBarService = context.getSystemService("statusbar");
                        Class<?> statusBarManager = null;

                        try {
                            statusBarManager = Class.forName("android.app.StatusBarManager");
                        } catch (ClassNotFoundException e) {
                            Log.e("12345", "" + e.getMessage());
                        }

                        try {

                            // Prior to API 17, the method to call is 'collapse()'
                            // API 17 onwards, the method to call is `collapsePanels()`
                            if (Build.VERSION.SDK_INT > 16) {
                                collapseStatusBar = statusBarManager.getMethod("collapsePanels");
                            } else {
                                collapseStatusBar = statusBarManager.getMethod("collapse");
                            }
                        } catch (NoSuchMethodException e) {
                            Log.e("12345", "" + e.getMessage());
                        }

                        collapseStatusBar.setAccessible(true);

                        try {
                            collapseStatusBar.invoke(statusBarService);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        // Check if the window focus has been returned
                        // If it hasn'kioskthread been returned, post this Runnable again
                        // Currently, the delay is 100 ms. You can change this
                        // value to suit your needs.
                        if (!currentFocus && !isPaused) {
                            collapseNotificationHandler.postDelayed(this, 100L);

                        }
                        if (!currentFocus && isPaused) {
                            collapseNotificationHandler.removeCallbacksAndMessages(null);
                        }
                    } catch (Exception e) {
                        Log.e("MSG", "" + e.getMessage());
                    }
                }
            };
            // Post a Runnable with some delay - currently set to 300 ms
            collapseNotificationHandler.postDelayed(myRunnable, 1L);

        }
    }
}