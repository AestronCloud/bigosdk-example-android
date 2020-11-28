//
// Created by bigo on 2020/10/19.
//

#ifndef MEDIASDK_PARENT_BIGOENGINE_RAWDATA_MEDIAPREPROCESSING_H
#define MEDIASDK_PARENT_BIGOENGINE_RAWDATA_MEDIAPREPROCESSING_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif


JNIEXPORT jlong JNICALL Java_sg_bigo_common_rawdata_MediaPreProcessing_createNativeAudioFrameObserver
(JNIEnv *, jclass);


#ifdef __cplusplus
}
#endif

#endif //MEDIASDK_PARENT_BIGOENGINE_RAWDATA_MEDIAPREPROCESSING_H
