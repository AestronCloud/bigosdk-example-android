package sg.bigo.common.customcapture;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by dulx on 2017/7/18.
 */
public class RgbaRenderFilter extends FilterBase {
    private static final String vertexShaderCode =
            "attribute vec3 attPosition;"
            +"attribute vec2 attTexCoord;"
            +"varying vec2 texCoord;"
            +"void main() {"
            +"  gl_Position = vec4(attPosition, 1.0);"
            +"  texCoord =  attTexCoord;"
            +"}";
    private static final String fragmentShaderCode =
            "#extension GL_OES_EGL_image_external : require \n"
           + "precision highp float;"
           +"varying vec2 texCoord;"
           +"uniform samplerExternalOES SamplerRGBA;"
           +"void main() {"
           +"                                      "
           +"    gl_FragColor = vec4(texture2D(SamplerRGBA, texCoord).rgb, 1.0);"
           +"}";

    protected int mUniformTextureLoc;

    public RgbaRenderFilter() {
        super();
        mFilterType = FILTER_TYPE_UNKNOWN;
    }

    public RgbaRenderFilter(boolean flipVertical) {
        super(flipVertical);
        mFilterType = FILTER_TYPE_UNKNOWN;
    }

    @Override
    protected void onInit() {
        mProgID = loadProgram(vertexShaderCode, fragmentShaderCode);

        if (mProgID <= 0)
        {
            Log.e(TAG, "Cannot build directDraw filter");
            return;
        }

        GLES20.glUseProgram(mProgID);
        mAttribPosLocation = GLES20.glGetAttribLocation(mProgID, "attPosition");
        mAttribTexCoordLocation = GLES20.glGetAttribLocation(mProgID, "attTexCoord");
        mUniformTextureLoc = GLES20.glGetUniformLocation(mProgID, "SamplerRGBA");
        GLES20.glUseProgram(0);
    }

    @Override
    protected void onInitialized() {
        super.onInitialized();

        if (    mProgID <= 0                ||
                mAttribPosLocation < 0      ||
                mAttribTexCoordLocation < 0 ||
                mUniformTextureLoc < 0 )
        {
            Log.e(TAG, "RgbaRenderFilter: "+ mProgID + ", " + mAttribPosLocation + ", "+ mAttribTexCoordLocation + "," + mUniformTextureLoc);
            mIsInitialized = false;
        }
    }

    @Override
    public void draw(final int[] textureId, float[] colorOffset, float[] colorMat, int frameBufferId)
    {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        GLES20.glUniform1i(mUniformTextureLoc, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }


    public void draw(final int textureId)
    {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(mUniformTextureLoc, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}