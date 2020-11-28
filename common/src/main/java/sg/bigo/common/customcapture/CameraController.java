package sg.bigo.common.customcapture;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import android.view.TextureView;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import sg.bigo.common.LiveApplication;
import sg.bigo.common.customcapture.ve_gl.EglBase;
import sg.bigo.common.customcapture.ve_gl.GlRectDrawer;

public class CameraController implements SurfaceTexture.OnFrameAvailableListener{

    private final String TAG = "CameraController";

    private int mDisplayOrientation;//旋转角度
    private int mRotation;
    private CameraPosion mPosion;//前置摄像头和后置摄像头
    private int mPreViewWidth;//预览宽度
    private int mPreViewHeight;//预览高度
    private int mPreViewFps;//帧率
    private Camera.Size mPreviewSize;

    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;

    private GLSurfaceView mGlsurfaceView;
    private SurfaceTexture mSurfaceTexture;
    private int mSurfaceTextureId;
    private boolean updateSurface;
    private final float[] mTexMtx = GlUtil.createIdentityMtx();
    private RenderScreen mRenderScreen = null;

    //frameBufferId
    private RgbaRenderFilter mRenderFilter = null;
    private int mFrameBufferId = 0;
    private int mPreviewTextureId = 0;
    private EglBase previewEglBase;

    private static CameraController instance;
    private Bitmap mBitmap;
    private TextureView mTextureView;
    private HandlerThread mThread;
    private Handler mHandler;


    public enum CameraPosion {
        FRONT,
        BACK
    }

    private CameraController() {
        mDisplayOrientation = 0;
        mRotation = 0;
        mPosion = CameraPosion.FRONT;
        mPreViewWidth = 720;
        mPreViewHeight = 1280;
        mPreViewFps = 30;
        mThread = new HandlerThread("CameraController" + hashCode());
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
    }

    public static synchronized CameraController getInstance() {
        if (instance == null) {
            instance = new CameraController();
        }
        return instance;
    }

    private int openCommonCamera() {
        int cameraId = mPosion == CameraPosion.FRONT ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;

        int numberOfCameras = Camera.getNumberOfCameras();
        mCameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, mCameraInfo);
            if (mCameraInfo.facing == cameraId) {
                mCamera = Camera.open(i);
            }
        }
        return cameraId;
    }

    private Activity mActivity;

    public boolean openCamera(Activity activity, GLSurfaceView glSurfaceView, TextureView aux_view) {
        mActivity = activity;
        mBitmap = createBitmapFromAsset();
        boolean b = true;
        mGlsurfaceView = glSurfaceView;
        mTextureView = aux_view;
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.e(TAG, "onSurfaceTextureAvailable: "  + Thread.currentThread().getName() + "" + Thread.currentThread().getId());

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                Log.e(TAG, "onSurfaceTextureSizeChanged: " + Thread.currentThread().getName() + "" + Thread.currentThread().getId());
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                Log.e(TAG, "onSurfaceTextureDestroyed: " + Thread.currentThread().getName() + "" + Thread.currentThread().getId());

                closeCamera();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                Log.e(TAG, "onSurfaceTextureUpdated: ===== " + Thread.currentThread().getName() + "" + Thread.currentThread().getId());
            }
        });

        initSurfaceTexture();


        return b;
    }

    public void closeCamera() {
        try {

            if (mRenderFilter != null) {
                mRenderFilter.destroy();
                mRenderFilter = null;
            }

            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "closeCamera: " + e);
        } finally {

            final CountDownLatch barrier = new CountDownLatch(1);
            mHandler.post(() -> {
                previewEglBase.makeCurrent();
                if(mFrameBufferId != 0) {
                    int[] fbos = new int[] { mFrameBufferId };
                    GLES20.glDeleteFramebuffers(1,fbos,0);
                    mFrameBufferId = 0;
                }

                if(mPreviewTextureId != 0) {
                    int[] textureIds = new int[] { mPreviewTextureId };
                    GLES20.glDeleteTextures(1,textureIds, 0);
                    mPreviewTextureId = 0;
                }

                if(mSurfaceTextureId != 0) {
                    int[] textureIds = new int[] { mSurfaceTextureId };
                    GLES20.glDeleteTextures(1,textureIds, 0);
                    mSurfaceTextureId = 0;
                }

                mRenderScreen = null;
                previewEglBase.detachCurrent();
                if(previewEglBase != null) {
                    previewEglBase.release();
                    previewEglBase = null;
                }
                barrier.countDown();
            });
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Camera.Size getmPreviewSize() {
        return mPreviewSize;
    }

    private Bitmap createBitmapFromAsset() {
        Bitmap bitmap = null;
        try {
            AssetManager assetManager = LiveApplication.Companion.getAppContext().getAssets();
            InputStream is = assetManager.open("output.png");
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null) {
                System.out.println("测试一:width=" + bitmap.getWidth() + " ,height=" + bitmap.getHeight());
            } else {
                System.out.println("bitmap == null");
            }
        } catch (Exception e) {
            System.out.println("异常信息:" + e.toString());
        }
        return bitmap;
    }

    float[] transformationMatrix = new float[]{1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f};
    /**
     * 是否横屏
     *
     * @return
     */
    public boolean isLandscape() {
        return false;
    }

    private void setPameras(int cameraId) {
        mDisplayOrientation = cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT ? (mCameraInfo.orientation + mRotation) % 360
                : (mCameraInfo.orientation - mRotation + 360) % 360;

        boolean portrait = false;
        boolean upsideDown = false;
        if (mDisplayOrientation == 0) {
            portrait = true;
            upsideDown = false;
        } else if (mDisplayOrientation == 90) {
            portrait = false;
            upsideDown = false;
        } else if (mDisplayOrientation == 180) {
            portrait = true;
            upsideDown = true;
        } else if (mDisplayOrientation == 270) {
            portrait = false;
            upsideDown = true;
        }

        Camera.Size preViewSize = CameraUtils.getOptimalPreviewSize(mCamera, mPreViewWidth, mPreViewHeight, portrait);
        Log.i(TAG, "setPameras: preViewSize width " + preViewSize.width + " height " + preViewSize.height + " ,mDisplayOrientation " + mDisplayOrientation);

        Camera.Parameters parameters = mCamera.getParameters();
        //parameters.setRotation(mRotation);
        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.set("orientation", "portrait");
        parameters.setPreviewSize(preViewSize.width, preViewSize.height);

        if (upsideDown) {
            mCamera.setDisplayOrientation(180);
        }

        int[] range = CameraUtils.adaptPreviewFps(mPreViewFps, parameters.getSupportedPreviewFpsRange());
        parameters.setPreviewFpsRange(range[0], range[1]);

        mCamera.setParameters(parameters);
        mPreviewSize = preViewSize;
    }

    private GlRectDrawer previewDrawer;

    private void initSurfaceTexture() {
        final CountDownLatch barrier = new CountDownLatch(1);
        mHandler.post(() -> {
            previewEglBase = EglBase.create(null, EglBase.CONFIG_RGBA);
            if (!mTextureView.isAvailable()) {
                return;
            }

            try {
                // 创建用于预览的EGLSurface
                previewEglBase.createSurface(mTextureView.getSurfaceTexture());
            } catch (RuntimeException e) {
                previewEglBase.releaseSurface();
            }

            previewDrawer = new GlRectDrawer();

            barrier.countDown();
        });

        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        mHandler.post(() -> {
            previewEglBase.makeCurrent();

            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            //摄像头纹理
            mSurfaceTextureId = textures[0];
            mSurfaceTexture = new SurfaceTexture(mSurfaceTextureId);
            mSurfaceTexture.setOnFrameAvailableListener(CameraController.this);
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            GLES20.glDisable(GLES20.GL_BLEND);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mSurfaceTextureId);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);


            startCamera();

            previewEglBase.detachCurrent();
        });
    }

    public void startCamera() {
        int cameraId = openCommonCamera();
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setPameras(cameraId);

        Log.i(TAG, "onSurfaceChanged");
        try {

        } catch (Exception e) {
            Log.e(TAG, "onSurfaceChanged: " + e);
        }
        mCamera.startPreview();
    }

    public void stopCamera() {
        mCamera.stopPreview();
    }

    private  int mBitmapTextureId = 0;

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

        Log.i(TAG, "onFrameAvailable");
        synchronized (this) {
            updateSurface = true;
        }

        mHandler.post(() -> {
            if (previewEglBase == null) return;

            previewEglBase.makeCurrent();
            synchronized (CameraController.this) {
                if (updateSurface) {
                    //把数据给了mSurfaceTextureId
                    mSurfaceTexture.updateTexImage();
                    mSurfaceTexture.getTransformMatrix(mTexMtx);
                    updateSurface = false;
                }
            }

            if (mRenderScreen == null) {
                mRenderScreen = new RenderScreen(mSurfaceTextureId, true);
                mRenderScreen.setSreenSize(mPreViewWidth, mPreViewHeight);
            }

            if (mPreviewTextureId == 0) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
                mPreviewTextureId = GlUtil.generateTexture(GLES20.GL_TEXTURE_2D);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mPreViewWidth, mPreViewHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
                mFrameBufferId = GlUtil.generateFrameBuffer(mPreviewTextureId);
            } else {
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferId);
            }

            GlUtil.rotateTextureMatrix(mTexMtx, mDisplayOrientation);
            mRenderScreen.draw(mTexMtx);
//            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

            LiveApplication.Companion.avEngine().sendCustomVideoCaptureTextureData(mPreviewTextureId, mPreViewWidth, mPreViewHeight,
                    LiveApplication.Companion.getNeedCustomUpsideDown(), LiveApplication.Companion.getNeedCustomMirror(), SystemClock.elapsedRealtime());

//            sendStaticImage();

            previewEglBase.detachCurrent();
        });
    }

    private void sendStaticImage() {
        if (mBitmapTextureId == 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            mBitmapTextureId = GlUtil.generateTexture(GLES20.GL_TEXTURE_2D);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
        }

        if (mPreviewTextureId == 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            mPreviewTextureId = GlUtil.generateTexture(GLES20.GL_TEXTURE_2D);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mPreviewSize.width, mPreviewSize.height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            mFrameBufferId = GlUtil.generateFrameBuffer(mPreviewTextureId);
        } else {
            // 绑定帧缓冲区fbo
            // Bind frame buffer
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferId);
        }

        if (previewDrawer == null) {
            previewDrawer = new GlRectDrawer();
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        previewDrawer.drawRgb(mBitmapTextureId, transformationMatrix,
                720, 1280,
                0, 0,
                720, 1280);

        LiveApplication.Companion.avEngine().sendCustomVideoCaptureTextureData(mPreviewTextureId, 720, 1280, 0, true, SystemClock.elapsedRealtime());
    }
}
