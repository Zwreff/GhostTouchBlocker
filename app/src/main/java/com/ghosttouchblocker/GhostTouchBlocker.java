package com.ghosttouchblocker;

import android.view.MotionEvent;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class GhostTouchBlocker implements IXposedHookLoadPackage {

    private static final int GHOST_X = 810;
    private static final int GHOST_Y = 693;
    private static final int RADIUS = 40;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("android")) return;

        XposedHelpers.findAndHookMethod(
            "com.android.server.input.InputManagerService",
            lpparam.classLoader,
            "injectInputEvent",
            android.view.InputEvent.class,
            int.class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    android.view.InputEvent event = (android.view.InputEvent) param.args[0];
                    if (event instanceof MotionEvent) {
                        MotionEvent motionEvent = (MotionEvent) event;
                        float x = motionEvent.getX();
                        float y = motionEvent.getY();

                        float dx = x - GHOST_X;
                        float dy = y - GHOST_Y;
                        if ((dx * dx + dy * dy) <= RADIUS * RADIUS) {
                            param.setResult(true); // Block input
                        }
                    }
                }
            }
        );
    }
}
