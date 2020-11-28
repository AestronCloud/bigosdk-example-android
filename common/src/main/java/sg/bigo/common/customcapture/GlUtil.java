/*
 *  Copyright 2015 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package sg.bigo.common.customcapture;

import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.Matrix;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Some OpenGL static utility functions.
 */
public class GlUtil {
  private GlUtil() {}

  private static final String TAG = "GlUtil";

  public static FloatBuffer createSquareVtx() {
    final float vtx[] = {
            // XYZ, UV
            -1f,  1f, 0f, 0f, 1f,
            -1f, -1f, 0f, 0f, 0f,
            1f,   1f, 0f, 1f, 1f,
            1f,  -1f, 0f, 1f, 0f,
    };
    ByteBuffer bb = ByteBuffer.allocateDirect(4 * vtx.length);
    bb.order(ByteOrder.nativeOrder());
    FloatBuffer fb = bb.asFloatBuffer();
    fb.put(vtx);
    fb.position(0);
    return fb;
  }
  
  
  public static FloatBuffer createVertexBufferImage() {  //顶点镜像
	    final float vtx[] = {
	    		// XYZ
	    		1f,   1f, 0f,
	    	    1f,  -1f, 0f,
	    	    -1f,  1f, 0f,
	    	    -1f, -1f, 0f,    
	    };
	    
	    ByteBuffer bb = ByteBuffer.allocateDirect(4 * vtx.length);
	    bb.order(ByteOrder.nativeOrder());
	    FloatBuffer fb = bb.asFloatBuffer();
	    fb.put(vtx);
	    fb.position(0);
	    return fb;
	  }

  public static FloatBuffer createVertexBuffer() {
    final float vtx[] = {
        // XYZ
        -1f,  1f, 0f,
        -1f, -1f, 0f,
        1f,   1f, 0f,
        1f,  -1f, 0f,
    };
    
    ByteBuffer bb = ByteBuffer.allocateDirect(4 * vtx.length);
    bb.order(ByteOrder.nativeOrder());
    FloatBuffer fb = bb.asFloatBuffer();
    fb.put(vtx);
    fb.position(0);
    return fb;
  }

  public static FloatBuffer createTexCoordBuffer() {
    final float vtx[] = {
            // UV
            0f, 1f,
            0f, 0f,
            1f, 1f,
            1f, 0f,
    };
    ByteBuffer bb = ByteBuffer.allocateDirect(4 * vtx.length);
    bb.order(ByteOrder.nativeOrder());
    FloatBuffer fb = bb.asFloatBuffer();
    fb.put(vtx);
    fb.position(0);
    return fb;
  }

  public static float[] createIdentityMtx() {
    float[] m = new float[16];
    Matrix.setIdentityM(m, 0);
    return m;
  }

  private static void adjustOrigin(float[] matrix) {
    // Note that OpenGL is using column-major order.
    // Pre translate with -0.5 to move coordinates to range [-0.5, 0.5].
    matrix[12] -= 0.5f * (matrix[0] + matrix[4]);
    matrix[13] -= 0.5f * (matrix[1] + matrix[5]);
    // Post translate with 0.5 to move coordinates to range [0, 1].
    matrix[12] += 0.5f;
    matrix[13] += 0.5f;
  }

  public static float[] multiplyMatrices(float[] a, float[] b) {
    final float[] resultMatrix = new float[16];
    Matrix.multiplyMM(resultMatrix, 0, a, 0, b, 0);
    return resultMatrix;
  }

  public static float[] rotateTextureMatrix(float[] textureMatrix, float rotationDegree) {
    final float[] rotationMatrix = new float[16];
    Matrix.setRotateM(rotationMatrix, 0, rotationDegree, 0, 0, 1);
    adjustOrigin(rotationMatrix);
    return multiplyMatrices(textureMatrix, rotationMatrix);
  }

  public static int createProgram(String vertexSource, String fragmentSource) {
    int vs = loadShader(GLES20.GL_VERTEX_SHADER,   vertexSource);
    int fs = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
    int program = GLES20.glCreateProgram();
    GLES20.glAttachShader(program, vs);
    GLES20.glAttachShader(program, fs);
    GLES20.glLinkProgram(program);
    int[] linkStatus = new int[1];
    GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
    if (linkStatus[0] != GLES20.GL_TRUE) {
      GLES20.glDeleteProgram(program);
      program = 0;
    }
    return program;
  }

  public static int loadShader(int shaderType, String source) {
    int shader = GLES20.glCreateShader(shaderType);
    GLES20.glShaderSource(shader, source);
    GLES20.glCompileShader(shader);
    //
    int[] compiled = new int[1];
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
    if (compiled[0] == 0) {
      GLES20.glDeleteShader(shader);
      shader = 0;
    }
    //
    return shader;
  }

  public static void checkGlError(String op) {
    int error;
    while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
      throw new RuntimeException("glGetError encountered (see log)");
    }
  }

  public static void checkEglError(String op) {
    int error;
    while ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
      throw new RuntimeException("eglGetError encountered (see log)");
    }
  }


  // Assert that no OpenGL ES 2.0 error has been raised.
  public static void checkNoGLES2Error(String msg) {
    int error = GLES20.glGetError();
    if (error != GLES20.GL_NO_ERROR) {
      throw new RuntimeException(msg + ": GLES20 error: " + error);
    }
  }

  public static FloatBuffer createFloatBuffer(float[] coords) {
    // Allocate a direct ByteBuffer, using 4 bytes per float, and copy coords into it.
    ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
    bb.order(ByteOrder.nativeOrder());
    FloatBuffer fb = bb.asFloatBuffer();
    fb.put(coords);
    fb.position(0);
    return fb;
  }

  /**
   * Generate texture with standard parameters.
   */
  public static int generateTexture(int target) {
    final int textureArray[] = new int[1];
    GLES20.glGenTextures(1, textureArray, 0);
    final int textureId = textureArray[0];
    GLES20.glBindTexture(target, textureId);
    GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
    GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
    GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    checkNoGLES2Error("generateTexture");
    return textureId;
  }

  public static int generateFrameBuffer(int textureId) {
    final int frameBufferArray[] = new int[1];
    GLES20.glGenFramebuffers(1, frameBufferArray, 0);
    final int frameBufferId = frameBufferArray[0];
    // 绑定帧缓冲区
    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId);
    // 将2D纹理附着到帧缓冲对象
    GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId, 0);
    // 检查帧缓冲区是否完整
    int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);

    if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
      return -1;
    }

    checkNoGLES2Error("generateFrameBuffer");
    return frameBufferId;
  }

}
