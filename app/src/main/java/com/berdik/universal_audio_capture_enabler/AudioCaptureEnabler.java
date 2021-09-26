package com.berdik.universal_audio_capture_enabler;

import android.os.Build;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AudioCaptureEnabler implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        // Filter by package names with "android" in them and limit hooking to Android 11.
        if (loadPackageParam.packageName.equals("android") &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            // Bypass "allowAudioPlaybackCapture" in AndroidManifest.xml
            // https://cs.android.com/android/platform/superproject/+/master:frameworks/
            //  base/core/java/android/content/pm/parsing/ParsingPackageImpl.java;l=2539?
            //  q=setAllowAudioPlaybackCapture
            try {
                Class<?> parsingPkgImpl = XposedHelpers.findClass(
                        "android.content.pm.parsing.ParsingPackageImpl",
                        loadPackageParam.classLoader);

                XposedHelpers.findAndHookMethod(
                        parsingPkgImpl,
                        "setAllowAudioPlaybackCapture",
                        XC_MethodReplacement.returnConstant(true));
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
        }
    }


}