//
// Created by bigo on 2020/10/19.
//  用于音频原始数据回调
//

#include "bigoengine_rawdata_MediaPreProcessing.h"
#include "include/BigoMediaEngine.h"
#include <android/log.h>
#include <string.h>

namespace bigo {
    class BigoAudioFrameObserver : public bigo::IAudioFrameObserver {
    public:
        BigoAudioFrameObserver() {
        }

        ~BigoAudioFrameObserver() {
        }

    public:
        virtual void
        onRecordFrame(char *data, int len, int bytesPerSample, int channel, int samplesPerSec) {
            // doing
        }

        virtual void
        onPlaybackFrame(char *data, int len, int bytesPerSample, int channel, int samplesPerSec) {
            // doing
        }

        virtual void
        onEffectFileFrame(char *data, int len, int bytesPerSample, int channel, int samplesPerSec) {
            // doing
        }
    };

}

static bigo::BigoAudioFrameObserver s_audioFrameObserver;


#ifdef __cplusplus
extern "C" {
#endif



JNIEXPORT jlong JNICALL Java_sg_bigo_common_rawdata_MediaPreProcessing_createNativeAudioFrameObserver
(JNIEnv *env, jclass obj){
    //  强转为long，方便通过java设置到native层
    return  (jlong)&s_audioFrameObserver;
}

#ifdef __cplusplus
}
#endif
