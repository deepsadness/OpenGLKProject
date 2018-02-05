package com.example.cry.openglkproject

import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.cry.openglkproject.utils.shortToast
import com.example.opengllk.refer.AirHockeyRenderer
import com.example.opengllk.refer.ParticlesRenderer
import com.example.opengllk.renderer.SimpleTextureRenderer
import com.example.opengllk.supportOpenGLES2

class MainActivity : AppCompatActivity() {
    private lateinit var glSurfaceView: GLSurfaceView

    private var rendererSet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //第一步，先判断是否支持
        var isSupport = this.supportOpenGLES2()
        if (!isSupport) {
            "This device does not support OpenGL ES 2.0!!".shortToast(this)
        } else {
            //2.如果支持的话，则开始创建glSurfaceView
            glSurfaceView = GLSurfaceView(this)
            //将其版本设置为2
            glSurfaceView.setEGLContextClientVersion(2)

//            glSurfaceView.setRenderer(SimpleRenderer(this))
//            glSurfaceView.setRenderer(SimpleColorRenderer(this))
//            glSurfaceView.setRenderer(SimpleMatrixRenderer(this))
//            glSurfaceView.setRenderer(Simple3DRenderer(this))
            glSurfaceView.setRenderer(ParticlesRenderer(this))
//            glSurfaceView.setRenderer(AirHockeyRenderer(this))
            //设置标识位
            rendererSet = true

            setContentView(glSurfaceView)
        }
    }

    override fun onPause() {
        super.onPause()
        if (rendererSet) {
            glSurfaceView.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (rendererSet) {
            glSurfaceView.onResume()
        }
    }
}

