package sg.bigo.common.customcapture;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by dulx on 2017/3/9.
 */
public abstract class FilterBase {
    public final static String TAG = "vpsdkfilter";

    public static final int FILTER_TYPE_UNKNOWN     = 100;
    public static final int FILTER_TYPE_MOON        = 101;
    public static final int FILTER_TYPE_LOMO        = 102;
    public static final int FILTER_TYPE_SUNNY       = 103;
    public static final int FILTER_TYPE_SWEETPIXIE  = 104;
    public static final int FILTER_TYPE_WARMBEACH   = 105;
    public static final int FILTER_TYPE_VINTAGE     = 106;
    public static final int FILTER_TYPE_FILM        = 107;
    public static final int FILTER_TYPE_BRANNA      = 108;
    public static final int FILTER_TYPE_INKWELL     = 109;
    public static final int FILTER_TYPE_LOMOFI      = 110;
    public static final int FILTER_TYPE_RISE        = 111;
    public static final int FILTER_TYPE_SIERRA      = 112;
    public static final int FILTER_TYPE_WALDEN      = 113;
    public static final int FILTER_TYPE_WHITEN      = 114;


    /**
     * vertex coordinate for rendering a rectangle
     */
    private static final float CUBE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    /**
     * texture coordinate for rendering a texture on a rectangle
     */
    protected static final float TEXTURE_COORD_NORMAL[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    protected static final float TEXTURE_COORD_FLIP_VERTICAL[] = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
    };

    // col major
    public static final float colorMatBt601Fullrange[] = {
       1.0f, 1.0f, 1.0f,
       0.0f, -0.344f, 1.772f,
       1.402f, -0.714f, 0.0f
    };

    public static final float colorMatBt709Fullrange[] = {
         1.0f, 1.0f, 1.0f,
         0.0f, -0.1873f, 1.8556f,
         1.5748f, -0.4681f, 0.0f,
    };

    public static final float colorMatBt601Videorange[] = {
            1.164f, 1.164f, 1.164f,
            0.0f, -0.392f, 2.017f,
            1.596f, -0.813f, 0.0f,
    };

    public static final float colorMatBt709Videorange[] = {
            1.1644f, 1.1644f, 1.1644f,
            0.0f, -0.2132f, 2.1124f,
            1.7927f, -0.5329f, 0.0f,
    };

    public static final float colorOffsetVideoRange[] = {
            -16.0f / 255.0f, -0.5f, -0.5f,
    };

    public static final float colorOffsetFullRange[] = {
            0.0f, -0.5f, -0.5f,
    };
    /**
     * buffer for vertex coordinate
     */
    protected FloatBuffer mGLCubeBuffer = null;

    /**
     * buffer for texture coordinate
     */
    protected FloatBuffer mGLTextureBuffer = null;

    protected float mTextureCoords[] = TEXTURE_COORD_NORMAL;

    /**
     * whether the filter has been initialized or not
     */
    protected boolean mIsInitialized = false;

    /**
     * type of current filter
     */
    protected int mFilterType = FILTER_TYPE_UNKNOWN;

    protected int mProgID = -1;
    protected int mAttribPosLocation;
    protected int mAttribTexCoordLocation;

    protected boolean mFlipVertical = false;

    protected float alpha = 1.0f;
    /**
     * ctor
     */
    public FilterBase() {
    }

    public FilterBase(boolean flipVertical) {
        mTextureCoords = getTextureCoords(flipVertical);
    }

    private float[] getTextureCoords(boolean flipVertical) {
        if (flipVertical) {
            return TEXTURE_COORD_FLIP_VERTICAL;
        }
        return TEXTURE_COORD_NORMAL;
    }

    /**
     * initialization interface, one should override onInit function to supply extra initialization
     */
    public final void init() {
        onInit();

        mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(mTextureCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(mTextureCoords).position(0);

        mIsInitialized = true;
        onInitialized();
    }

    /**
     * Initialize vertex and texture coordinate buffer
     *
     * @note One should override this to give extra initialization of the derived filter,
     * but one should call super() unless the two buffers are not needed
     */
    protected void onInit() {

    }

    /**
     * another placed to do some initialization that cannot be presented in OnInit function
     */
    protected void onInitialized() {
    }

    /**
     * clean the resource of the filter
     *
     * @note one should override onDestroy function to give extra clean function
     */
    public final void destroy() {
        mIsInitialized = false;
        mGLCubeBuffer = null;
        mGLTextureBuffer = null;

        if (mProgID > 0) {
            GLES20.glDeleteProgram(mProgID);
            mProgID = -1;
        }
        onDestroy();
    }

    /**
     * clear the resource used by the filter
     */
    protected void onDestroy() {

    }

    public void setFlipVertical(boolean flipVertical) {
        float[] textureCoords = getTextureCoords(flipVertical);
        if (textureCoords != mTextureCoords) {
            mTextureCoords = textureCoords;
            if (mGLTextureBuffer != null) {
                mGLTextureBuffer.rewind();
                mGLTextureBuffer.put(mTextureCoords).position(0);
            }
        }
    }

    public void setSize(int width, int height) {}

    public void setAlpha(float alpha_)
    {
        alpha = alpha_;
    }

    public void useProgram() {

        GLES20.glUseProgram(mProgID);
        mGLCubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mAttribPosLocation, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
        GLES20.glEnableVertexAttribArray(mAttribPosLocation);

        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(mAttribTexCoordLocation, 2, GLES20.GL_FLOAT, false, 0,
                mGLTextureBuffer);
        GLES20.glEnableVertexAttribArray(mAttribTexCoordLocation);
    }

    public void unUseProgram() {
        GLES20.glDisableVertexAttribArray(mAttribPosLocation);
        GLES20.glDisableVertexAttribArray(mAttribTexCoordLocation);
        GLES20.glUseProgram(0);
    }

    /**
     * drawing interface, one should override this function to supply the real drawing function
     *
     * @param textureId The array of texture ID to be used by the filter
     *
     * @return void
     */
    public void draw(final int[] textureId, float[] colorOffset, float[] colorMat, int frameBufferId)
    {
    }

    /**
     * querying whether the shader has been initialized
     *
     * @return true if the shader has been initialized, false otherwise
     */
    public boolean isInitialized() {
        return mIsInitialized;
    }

    /**
     * query the current filter type, for example, denoise, sharpen or assembly
     * @return
     */
    public int getFilterType() {
        return mFilterType;
    }

    /**
     * Create shader from the shader type and shader string
     *
     * @param iType        Shader type, it should be GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
     * @param shderSource  Source code of the corresponding shader
     * @return Handle of the shader created
     */
    protected int loadShader(final String strSource, final int iType) {
        int[] compiled = new int[1];
        int iShader = GLES20.glCreateShader(iType);
        GLES20.glShaderSource(iShader, strSource);
        GLES20.glCompileShader(iShader);
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (0 == compiled[0]) {
            Log.e(TAG, "Shader compilation failed with reason: " + GLES20.glGetShaderInfoLog(iShader));
            return -1;
        }
        return iShader;
    }

    /**
     * Create shader program give the vertex and fragment shader source strings
     *
     * @param vertexShaderSource   	Source code of the vertex shader
     * @param fragmentShaderSource	Source code of the fragment shader
     * @return Handle of the shader program created
     */
    protected int loadProgram(final String strVSource, final String strFSource) {
        int iVShader;
        int iFShader;
        int iProgId;
        int[] link = new int[1];

        iVShader = loadShader(strVSource, GLES20.GL_VERTEX_SHADER);
        if (-1 == iVShader) {
            Log.e(TAG, "Vertex Shader program failed");
            return -1;
        }

        iFShader = loadShader(strFSource, GLES20.GL_FRAGMENT_SHADER);
        if (-1 == iFShader) {
            Log.e(TAG, "Fragment Shader program failed");
            GLES20.glDeleteShader(iVShader);
            return -1;
        }

        iProgId = GLES20.glCreateProgram();
        GLES20.glAttachShader(iProgId, iVShader);
        GLES20.glAttachShader(iProgId, iFShader);
        GLES20.glLinkProgram(iProgId);

        GLES20.glGetProgramiv(iProgId, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] <= 0) {
            Log.e(TAG, "Shader program link error: " + GLES20.glGetProgramInfoLog(iProgId));
            GLES20.glDeleteShader(iVShader);
            GLES20.glDeleteShader(iFShader);
            return -1;
        }

        GLES20.glDeleteShader(iVShader);
        GLES20.glDeleteShader(iFShader);
        return iProgId;
    }
}
