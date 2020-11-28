package sg.bigo.common.rawdata;

public class MediaPreProcessing {

    static {
        System.loadLibrary("register-raw-data");
    }

    public static native long createNativeAudioFrameObserver();

}
