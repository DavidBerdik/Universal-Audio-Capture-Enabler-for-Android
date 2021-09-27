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
        if (loadPackageParam.packageName.equals("android") &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            // Bypass "allowAudioPlaybackCapture" in AndroidManifest.xml
            /* https://cs.android.com/android/platform/superproject/+/master:frameworks/
                base/core/java/android/content/pm/parsing/ParsingPackageImpl.java;l=2539 */
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

            // Bypass AudioManager's "setAllowedCapturePolicy()"
            /* https://cs.android.com/android/platform/superproject/+/master:frameworks/base/media/
                java/android/media/AudioManager.java;l=1554?q=setAllowedCapturePolicy&start=11 */
            try {
                Class<?> audioManager = XposedHelpers.findClass(
                        "android.media.AudioManager", loadPackageParam.classLoader);

                XposedHelpers.findAndHookMethod(
                        audioManager,
                        "setAllowedCapturePolicy",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.args[0] = AudioAttributes.ALLOW_CAPTURE_BY_ALL;
                            }
                        });
            } catch (Throwable t) {
                XposedBridge.log(t);
            }

            // Bypass AudioAttributes.Builder's "setAllowedCapturePolicy()"
            /* https://cs.android.com/android/platform/superproject/+/master:frameworks/base/media/
                java/android/media/AudioAttributes.java;l=879 */
            try {
                Class<?> audioAttribBuilder = XposedHelpers.findClass(
                        "android.media.AudioAttributes.Builder",
                        loadPackageParam.classLoader);

                XposedHelpers.findAndHookMethod(
                        audioAttribBuilder,
                        "setAllowedCapturePolicy",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.args[0] = AudioAttributes.ALLOW_CAPTURE_BY_ALL;
                            }
                        });
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
        }
    }


}