package com.example.opengllk.renderer

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Render的实现类。会回调三种方法。分别是在创建，改变和绘画时调用
 * Created by Cry on 2018/2/3.
 */
abstract class BaseRenderer : GLSurfaceView.Renderer {
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //这这里创建的时候。先简单的清楚颜色
        GLES20.glClearColor(0f, 0f, 0f, 0f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //在这里设置视口
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //这里开始绘画。清除缓存
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}