package com.example.opengllk.renderer

import android.content.Context
import com.example.opengllk.geometry.SimpleGeometry
import com.example.opengllk.program.SimpleShader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 这里需要注意的是，gl的初始化，都需要在SurfaceCreated之后，不能在其之前
 * Created by Cry on 2018/2/3.
 */
class SimpleRenderer(var context: Context) : BaseRenderer() {
    private var simpleGeometry: SimpleGeometry? = null
//    private val simpleShader = SimpleShader(context)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        simpleGeometry = SimpleGeometry()
        val simpleShader = SimpleShader(context)
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