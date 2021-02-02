#example-android

## 编译运行说明
1. 将您的appId和cert填入到strings_config.xml
2. 根据需求选中不同的target,然后点击run运行即可
> 不同的target可以编出不同的demo
> 1. all：包含所有feature的demo
> 2. mutli-live：多人互动直播
> 3. mutli-live：多人互动直播
> 4. one-to-one-video：1v1视频通话
> 5. one-to-one-voice：1v1语音通话
> 6. six-seat-video-live：6人房

## 工程结构说明
1. common目录：在多个demo间复用的代码与资源
2. common-beauty目录：仅包含美颜素材
3. all/multi-live/multi-voice/one-to-one-video/one-to-one-voice/six-seat-video-live：对应不同feature的包

