package com.example.opengllk.renderer

import android.content.Context
import com.example.opengllk.geometry.SimpleColorGeometry
import com.example.opengllk.program.SimpleColorShader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 这里需要注意的是，gl的初始化，都需要在SurfaceCreated之后，不能在其之前
 * Created by Cry on 2018/2/3.
 */
class SimpleColorRenderer(var context: Context) : BaseRenderer() {
    private var simpleGeometry: SimpleColorGeometry? = null
//    private val simpleShader = SimpleShader(context)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        simpleGeometry = SimpleColorGeometry()
        val simpleShader = SimpleColorShader(context)
        simpleShader.useProgram()
        simpleGeometry!!.bindData(simpleShader)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)

    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        simpleGeometry?.draw()
    }
}