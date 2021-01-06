package sg.bigo.common.utils;


import android.content.SharedPreferences;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import sg.bigo.common.LiveApplication;
import sg.bigo.common.R;
import sg.bigo.opensdk.api.AVEngineConstant;

public class BigoEngineConfig {
    // private static final int DEFAULT_UID = 0;
    // private int mUid = DEFAULT_UID;

    private String mChannelName;
    private boolean mShowVideoStats;

    public boolean isCustomCaptureEnabled() {
        return Pref().getBoolean(key_debug_enable_custom_capture,false);
    }

    private boolean mCustomCaptureEnabled;
    private int mMaxResolutionIdx = 4;
    private int mFpsIndex = 3;
    private int mMirrorLocalIndex;
    private int mMirrorRemoteIndex;
    private int mMirrorEncodeIndex;
    private int mBitrate = 4096;


    private int mProfile = 0;
    private int mScenario = 0;

    private static long generateRandomNumber(int n){

        String randomNumString ="";
        Random r = new Random();
        //Generate the first digit from 1-9
        randomNumString += (r.nextInt(9) + 1);

        //Generate the remaining digits between 0-9
        for(int x = 1; x < n; x++){
            randomNumString += r.nextInt(9);
        }

        //Parse and return
        return Long.parseLong(randomNumString);

    }

    public BigoEngineConfig() {
        genCustomUid();
    }

    public static final String key_debug_cfg_extra_live_info = "key_debug_cfg_extra_live_info";
    public static final String key_debug_cfg_video_frame_rate = "key_debug_cfg_video_frame_rate";
    public static final String key_debug_cfg_lbs_ip = "key_debug_cfg_lbs_ip";
    public static final String key_debug_cfg_lbs_port = "key_debug_cfg_lbs_port";
    public static final String key_debug_cfg_appid = "key_debug_cfg_appid";
    public static final String key_debug_cfg_cert = "key_debug_cfg_cert";
    public static final String key_debug_cfg_env_enabled = "key_debug_cfg_env_enabled";
    public static final String key_debug_cfg_appid_cert_enabled = "key_debug_cfg_appid_cert_enabled";
    public static final String key_debug_cfg_is_test_env = "key_debug_cfg_is_test_env";
    public static final String key_debug_cfg_custom_temp_token = "key_debug_cfg_custom_temp_token";
    public static final String key_debug_enable_temp_token = "key_debug_enable_temp_token";
    public static final String key_debug_enable_custom_capture = "key_debug_enable_custom_capture";
    public static final String key_debug_cfg_custom_uid = "key_debug_cfg_custom_uid";
    public static final String key_debug_cfg_enable_custom_uid = "key_debug_cfg_enable_custom_uid";
    public static final String key_debug_cfg_enable_multi_view = "key_debug_cfg_enable_multi_view";
    public static final String key_debug_cfg_enable_mirror = "key_debug_cfg_enable_mirror";
    public static final String key_debug_cfg_enable_updown = "key_debug_cfg_enable_updown";
    public static final String key_debug_cfg_auto_publish_and_livetrancoding = "key_debug_cfg_auto_publish_and_livetrancoding";
    public static final String key_debug_cfg_user_account = "key_debug_cfg_user_account";
    public static final String key_debug_cfg_channel_name = "key_debug_cfg_channel_name";
    public static final String key_debug_cfg_channel_cc = "key_debug_cfg_channel_cc";

    private SharedPreferences Pref() {
        return PreferenceManager.getDefaultSharedPreferences(LiveApplication.Companion.getAppContext());
    }

    public String getAutoPublishAndLiveTranscodingUrl() {
        return Pref().getString(key_debug_cfg_auto_publish_and_livetrancoding,"");
    }

//    public final boolean isEnableMultiView;

    public boolean isEnableMultiView() {
        return Pref().getBoolean(key_debug_cfg_enable_multi_view,false);
    }

    public boolean isEnableCustomUid() {
        return Pref().getBoolean(key_debug_cfg_enable_custom_uid,false);
    }

    public boolean isEnableCustomCaptureMirror() {
        return Pref().getBoolean(key_debug_cfg_enable_mirror,false);
    }

    public int isEnableCustomCaptureUpsideDown() {
        int rotation = 0;
        if (Pref().getBoolean(key_debug_cfg_enable_updown, false)) {
            rotation = 180;
        }
        return rotation;
    }

    public void genCustomUid() {
        Pref().edit().putString(key_debug_cfg_custom_uid,"" + generateRandomNumber(15)).apply();
    }

    public long getCustomUid() {
        return Long.valueOf(Pref().getString(key_debug_cfg_custom_uid,"123456789"));
    }

    public String getUserAccount() {
        return Pref().getString(key_debug_cfg_user_account,"张三_" + generateRandomNumber(5));
    }

    public void setUserAccount(String userAccount) {
        Pref().edit().putString(key_debug_cfg_user_account,userAccount).apply();
    }

    public String getChannelName() {
        return Pref().getString(key_debug_cfg_channel_name,"频道名_" + generateRandomNumber(5));
    }

    public void setChannelName(String channelName) {
        Pref().edit().putString(key_debug_cfg_channel_name,channelName).apply();
    }

    public String getLiveExtraInfo() {
        return Pref().getString(key_debug_cfg_extra_live_info,"");
    }

    //====================自定义环境相关==============================
    public String getLbsIp() {
        return Pref().getString(key_debug_cfg_lbs_ip,"xxx.xxx.xxx.xxx");
    }

    public int getLbsPort() {
        return Integer.valueOf(Pref().getString(key_debug_cfg_lbs_port,"65537"));
    }


    public boolean isTestEnv() {
        return Pref().getBoolean(key_debug_cfg_is_test_env,false);
    }

    public boolean isCustomEnvEnabled() {
        return Pref().getBoolean(key_debug_cfg_env_enabled,false);
    }

    public boolean isEnableCustomAppidCert() {
        return Pref().getBoolean(key_debug_cfg_appid_cert_enabled,false);
    }
    //===========================================================================

    //=====临时token相关=====
    public boolean isEnableTempToken() {
        return Pref().getBoolean(key_debug_enable_temp_token,false);
    }

    public String getCustomTempToken() {
        return Pref().getString(key_debug_cfg_custom_temp_token, "0016f08222zln1c8sf41weg9hcn0sx1qvp2ABS2dfkorLJRUcG9JlIboOpnnh4a8RInOmwf9z0+a09LP1+XjlAAAVGA");
    }


    public String getFinalToken(@NotNull String finalToken) {
        if(isEnableTempToken()) {
            return getCustomTempToken();
        }
        return finalToken;
    }
    //=====临时token相关=====


    public String getAppId() {
        String appId = LiveApplication.Companion.getAppContext().getString(R.string.bigo_app_id);
        if (isEnableCustomAppidCert()) {
            appId = Pref().getString(key_debug_cfg_appid,appId);
        }
        return appId;
    }

    public String getCert() {
        String cert = LiveApplication.Companion.getAppContext().getString(R.string.bigo_cert);
        if(isEnableCustomAppidCert()) {
            cert = Pref().getString(key_debug_cfg_cert,cert);
        }
        return cert;
    }

    public int getProfile() {
        return mProfile;
    }

    public void setProfile(int mProfile) {
        this.mProfile = mProfile;
    }

    public int getScenario() {
        return mScenario;
    }

    public void setScenario(int mScenario) {
        this.mScenario = mScenario;
    }

    public int getMaxResolutionIdx() {
        return mMaxResolutionIdx;
    }

    public void setMaxResolutionIdx(int index) {
        mMaxResolutionIdx = index;
    }

    public int getFpsIndex() {
        return mFpsIndex;
    }

    public void setFpsIndex(int fpsIndex) {
        mFpsIndex = fpsIndex;
    }


    public int getBitrate() {
        return mBitrate;
    }

    public boolean ifShowVideoStats() {
        return mShowVideoStats;
    }

    public void setIfShowVideoStats(boolean show) {
        mShowVideoStats = show;
    }

    public int getMirrorLocalIndex() {
        return mMirrorLocalIndex;
    }

    public void setMirrorLocalIndex(int index) {
        mMirrorLocalIndex = index;
    }

    public int getMirrorRemoteIndex() {
        return mMirrorRemoteIndex;
    }

    public void setMirrorRemoteIndex(int index) {
        mMirrorRemoteIndex = index;
    }

    public int getMirrorEncodeIndex() {
        return mMirrorEncodeIndex;
    }

    public String getChannelCC() {
        return Pref().getString(key_debug_cfg_channel_cc,"CN");
    }

    public void setMirrorEncodeIndex(int index) {
        mMirrorEncodeIndex = index;
    }

    public void setBitrate(int bitrate) {

        mBitrate = bitrate;
    }


    public int getMaxResolution() {
        try {
            return maxResolutions.get(mMaxResolutionIdx);
        } catch (Throwable e) {
            mMaxResolutionIdx = 4;
            return maxResolutions.get(mMaxResolutionIdx);
        }
    }

    public int getFrameRate() {
        return Integer.valueOf(Pref().getString(key_debug_cfg_video_frame_rate,"24"));
    }


    Map<Integer,Integer> maxResolutions = new HashMap<Integer,Integer>() {
        {
            put(0,AVEngineConstant.MaxResolutionTypes.MR_480x270);
            put(1,AVEngineConstant.MaxResolutionTypes.MR_640x360);
            put(2,AVEngineConstant.MaxResolutionTypes.MR_854x480);
            put(3,AVEngineConstant.MaxResolutionTypes.MR_960x540);
            put(4,AVEngineConstant.MaxResolutionTypes.MR_1280x720);
        }
    };

    Map<Integer,Integer> frameRates = new HashMap<Integer,Integer>() {
        {
            put(0,AVEngineConstant.FrameRate.FPS_1);
            put(1,AVEngineConstant.FrameRate.FPS_7);
            put(2,AVEngineConstant.FrameRate.FPS_10);
            put(3,AVEngineConstant.FrameRate.FPS_15);
            put(4,AVEngineConstant.FrameRate.FPS_24);
            put(5,AVEngineConstant.FrameRate.FPS_30);
        }
    };

    public static void loadAudioConfigs() {
        SharedPreferences pref = PrefManager.getPreferences(LiveApplication.Companion.getAppContext());
        LiveApplication.Companion.getConfig().setProfile(pref.getInt(Constants.PREF_AUDIO_PROFILE,0));
        LiveApplication.Companion.getConfig().setScenario(pref.getInt(Constants.PREF_AUDIO_SCENARIO,0));
        LiveApplication.Companion.getConfig().setMaxResolutionIdx(pref.getInt(Constants.PREF_RESOLUTION_IDX, 4));
        LiveApplication.Companion.getConfig().setFpsIndex(pref.getInt(Constants.PREF_FPS_IDX, 4));
    }

    public void setCustomCaptureEnabled(boolean activated) {
        mCustomCaptureEnabled = activated;
    }

}
