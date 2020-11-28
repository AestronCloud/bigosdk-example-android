#ifndef _BIGO_MEDIA_ENGINE_H_
#define _BIGO_MEDIA_ENGINE_H_

#define BIGO_API_VISIBL __attribute__((visibility("default")))

namespace bigo {
    // 音频原始数据监测对象，需要外部实现
    class BIGO_API_VISIBL IAudioFrameObserver {
    public:
        /**
         * 录音端经过3A处理后音频数据回调
         * @param data， samples data；
         * @param len，  samples的长度：
         * @param bytesPerSample 每个sample的byte数：
         * @param channel 声道数。
         * @param samplesPerSec  采样率即1秒钟的样本点数。
        */
        virtual void onRecordFrame (char* data, int len, int bytesPerSample, int channel, int samplesPerSec) = 0;
        /**
         * 播放端即将播放的音频数据回调
         * @param data， samples data；
         * @param len，  samples的长度：
         * @param bytesPerSample， 每个sample的byte数：
         * @param channel， 声道数。
         * @param samplesPerSec  采样率即1秒钟的样本点数。
        */
        virtual void onPlaybackFrame (char* data, int len, int bytesPerSample, int channel, int samplesPerSec) = 0;
        /**
         * audioMixing播放的音乐文件数据回调
         * @param data， samples data；
         * @param len，  samples的长度：
         * @param bytesPerSample 每个sample的byte数：
         * @param channel 声道数。
         * @param samplesPerSec  采样率即1秒钟的样本点数。
        */
        virtual void onEffectFileFrame (char* data, int len, int bytesPerSample, int channel, int samplesPerSec) = 0;
        virtual ~IAudioFrameObserver() {}
    };

}

#endif
