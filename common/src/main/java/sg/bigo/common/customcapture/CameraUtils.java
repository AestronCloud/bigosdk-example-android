package sg.bigo.common.customcapture;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.util.Log;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@TargetApi(14)
public class CameraUtils {

    private static final String TAG = "CameraUtils";

    public static int[] adaptPreviewFps(int expectedFps, List<int[]> fpsRanges) {
        expectedFps *= 1000;
        int[] closestRange = fpsRanges.get(0);
        int measure = Math.abs(closestRange[0] - expectedFps) + Math.abs(closestRange[1] - expectedFps);
        for (int[] range : fpsRanges) {
            if (range[0] <= expectedFps && range[1] >= expectedFps) {
                int curMeasure = Math.abs(range[0] - expectedFps) + Math.abs(range[1] - expectedFps);
                if (curMeasure < measure) {
                    closestRange = range;
                    measure = curMeasure;
                }
            }
        }
        return closestRange;
    }

    public static void setOrientation(int cameraId, boolean isLandscape, Camera camera) {
        int orientation = getDisplayOrientation(cameraId);
        if(isLandscape) {
            orientation = orientation - 90;
        }
        camera.setDisplayOrientation(orientation);
    }

    public static boolean supportTouchFocus(Camera camera) {
        if(camera != null) {
            return (camera.getParameters().getMaxNumFocusAreas() != 0);
        }
        return false;
    }

    public static void setAutoFocusMode(Camera camera) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.size() > 0 && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                camera.setParameters(parameters);
            } else if (focusModes.size() > 0) {
                parameters.setFocusMode(focusModes.get(0));
                camera.setParameters(parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTouchFocusMode(Camera camera) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.size() > 0 && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                camera.setParameters(parameters);
            } else if (focusModes.size() > 0) {
                parameters.setFocusMode(focusModes.get(0));
                camera.setParameters(parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Camera.Size getOptimalPreviewSize(Camera camera, int width, int height, boolean portrait) {
        Camera.Size optimalSize = null;
        List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
        if (sizes == null) return null;

        Camera.Size[] sizesArray = new Camera.Size[sizes.size()];
        for (int i = 0; i < sizesArray.length; i++) {
            sizesArray[i] = sizes.get(i);
        }
        Arrays.sort(sizesArray, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                // 按照视频的宽排序，宽一样的按照高排序
                if (lhs.width != rhs.width) {
                    return lhs.width - rhs.width;
                } else {
                    return lhs.height - rhs.height;
                }
            }
        });

        int position = -1;
        int preferredWidth = 480;
        int preferredHeight = 640;
        if (width > preferredWidth || height > preferredHeight) {
            preferredWidth = 720;
            preferredHeight = 1280;
        }

        if (!portrait) {
            int tmpWidth = preferredWidth;
            preferredWidth = preferredHeight;
            preferredHeight = tmpWidth;
        }

        if (width <= preferredWidth && height <= preferredHeight) {
            for (int i = 0; i < sizesArray.length; i++) {
                if (sizesArray[i].width == preferredWidth && sizesArray[i].height == preferredHeight) {
                    position = i;
                    break;
                }
            }
        }

        if (!portrait) {
            int tmpWidth = width;
            width = height;
            height = tmpWidth;
        }

        if (position == -1) {
            for (position = 0; position < sizesArray.length; position++) {
                if (sizesArray[position].width % 4 != 0 || sizesArray[position].height % 4 != 0) {
                    Log.i(TAG, "ignore " + sizesArray[position].width + "x" + sizesArray[position].height);
                    continue;
                }
                if (sizesArray[position].width >= width && sizesArray[position].height >= height) {
                    break;
                }
            }
        }
        if (position != sizesArray.length) {
            optimalSize = sizesArray[position];
        } else {
            for (position = sizesArray.length - 1; position > 0; position--) {
                if (sizesArray[position].width <= 720 && sizesArray[position].height <= 1280) {
                    break;
                }
            }
            optimalSize = sizesArray[position];
        }
        return optimalSize;
    }

    public static int getDisplayOrientation(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation + 360) % 360;
        }
        return result;
    }

    public static boolean supportFlash(Camera camera){
        Camera.Parameters params = camera.getParameters();
        List<String> flashModes = params.getSupportedFlashModes();
        if(flashModes == null) {
            return false;
        }
        for(String flashMode : flashModes) {
            if(Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
                return true;
            }
        }
        return false;
    }
}
