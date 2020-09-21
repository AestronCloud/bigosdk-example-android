package sg.bigo.common.utils;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import sg.bigo.common.LiveApplication;

public class DialogUtils {
    public static void fitFullScreen(@NonNull Dialog dialog) {
        if (isAllScreenDevice()) {
            Window window = dialog.getWindow();
            if (window != null) {
                final View decorView = window.getDecorView();
                if (decorView != null) {
                    decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                        public void onSystemUiVisibilityChange(int visibility) {
                            int uiOptionsx = 1794;
                            int uiOptions;
                            if (Build.VERSION.SDK_INT >= 19) {
                                uiOptions = uiOptionsx | 4096;
                            } else {
                                uiOptions = uiOptionsx | 1;
                            }

                            decorView.setSystemUiVisibility(uiOptions);
                        }
                    });
                }
            }
        }
    }

    private static volatile boolean mHasCheckAllScreen;
    private static volatile boolean mIsAllScreenDevice;

    public static boolean isAllScreenDevice() {
        if (mHasCheckAllScreen) {
            return mIsAllScreenDevice;
        } else {
            mHasCheckAllScreen = true;
            mIsAllScreenDevice = false;
            if (Build.VERSION.SDK_INT < 21) {
                return false;
            } else {
                WindowManager windowManager = (WindowManager) LiveApplication.Companion.getAppContext().getSystemService("window");
                if (windowManager != null) {
                    Display display = windowManager.getDefaultDisplay();
                    Point point = new Point();
                    display.getRealSize(point);
                    float width;
                    float height;
                    if (point.x < point.y) {
                        width = (float)point.x;
                        height = (float)point.y;
                    } else {
                        width = (float)point.y;
                        height = (float)point.x;
                    }

                    if (height / width >= 1.97F) {
                        mIsAllScreenDevice = true;
                    }
                }

                return mIsAllScreenDevice;
            }
        }
    }
}
