package sg.bigo.common.utils;


import android.content.SharedPreferences;

import sg.bigo.common.LiveApplication;

public class BigoEngineConfig {
    // private static final int DEFAULT_UID = 0;
    // private int mUid = DEFAULT_UID;

    private String mChannelName;
    private boolean mShowVideoStats;
    private int mDimenIndex = -1;
    private int mFpsIndex = -1;
    private int mMirrorLocalIndex;
    private int mMirrorRemoteIndex;
    private int mMirrorEncodeIndex;
    private int mBitrate = 4096;


    private int mProfile = 0;
    private int mScenario = 0;

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

    public int getVideoDimenIndex() {
        return mDimenIndex;
    }

    public void setVideoDimenIndex(int index) {
        mDimenIndex = index;
    }

    public int getFpsIndex() {
        return mFpsIndex;
    }

    public void setFpsIndex(int fpsIndex) {
        mFpsIndex = fpsIndex;
    }

    public String getChannelName() {
        return mChannelName;
    }

    public int getBitrate() {
        return mBitrate;
    }

    public void setChannelName(String mChannel) {
        this.mChannelName = mChannel;
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

    public void setMirrorEncodeIndex(int index) {
        mMirrorEncodeIndex = index;
    }

    public void setBitrate(int bitrate) {


        mBitrate = bitrate;
    }


    public static void loadAudioConfigs() {
        SharedPreferences pref = PrefManager.getPreferences(LiveApplication.Companion.getAppContext());
        LiveApplication.Companion.getConfig().setProfile(pref.getInt(Constants.PREF_AUDIO_PROFILE,0));
        LiveApplication.Companion.getConfig().setProfile(pref.getInt(Constants.PREF_AUDIO_SCENARIO,0));
    }
}
