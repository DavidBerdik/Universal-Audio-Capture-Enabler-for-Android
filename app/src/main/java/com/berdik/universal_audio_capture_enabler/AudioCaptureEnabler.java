package com.berdik.universal_audio_capture_enabler;

import android.media.AudioAttributes;
import android.os.Build;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AudioCaptureEnabler implements IXposedHookLoadPackage {
    // Useful Reference: https://developer.android.com/guide/topics/media/av-capture

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        // Filter by package names with "android" in them and limit hooking to Android 11.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Bypass "allowAudioPlaybackCapture" in AndroidManifest.xml
            /* https://cs.android.com/android/platform/superproject/+/master:frameworks/
                base/core/java/android/content/pm/parsing/ParsingPackageImpl.java;l=2539 */
            try {
                XposedHelpers.findAndHookMethod(
                        "android.content.pm.parsing.ParsingPackageImpl",
                        loadPackageParam.classLoader,
                        "setAllowAudioPlaybackCapture",
                        boolean.class,
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.args[0] = true;
                                XposedBridge.log("Hooked android.content.pm.parsing." +
                                        "ParsingPackageImpl#setAllowAudioPlaybackCapture()");
                            }
                        });
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
        }
    }


}