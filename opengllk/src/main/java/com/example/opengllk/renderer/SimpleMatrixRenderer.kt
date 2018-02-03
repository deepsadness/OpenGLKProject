package com.example.opengllk.renderer

import android.content.Context
import android.opengl.GLES20
import com.example.opengllk.geometry.SimpleColorGeometry
import com.example.opengllk.geometry.SimpleMatrixGeometry
import com.example.opengllk.program.SimpleColorShader
import com.example.opengllk.program.SimpleMatrixShader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.Matrix.orthoM


/**
 * 这里需要注意的是，gl的初始化，都需要在SurfaceCreated之后，不能在其之前
 * Created by Cry on 2018/2/3.
 */
class SimpleMatrixRenderer(var context: Context) : BaseRenderer() {
    private var simpleGeometry: SimpleMatrixGeometry? = null

    //这里开始要维护投影矩阵
    private val projectionMatrix = FloatArray(16)
    var uMatrixAttributeLocation: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        simpleGeometry = SimpleMatrixGeometry()
        val simpleShader = SimpleMatrixShader(context)
        simpleShader.useProgram()
        uMatrixAttributeLocation = simpleShader.getMatrixAttributeLocation()
        simpleGeometry!!.bindData(simpleShader)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        val wider = width > height
        val aspectRatio = if (wider)
            width * 1.0f / height * 1.0f
        else
            height * 1.0f / width * 1.0f

        if (wider) {
            //如果是宽于高的话，则为横屏，将左右进行正交
            orthoM(projectionMatrix, 0,
                    -aspectRatio, aspectRatio,
                    -1f, 1f,
                    -1f, 1f)
        } else {
            // Portrait or square
            //如果是宽于高的话，则为竖屏，将上下进行正交
            orthoM(projectionMatrix, 0,
                    -1f, 1f,
                    -aspectRatio, aspectRatio,
                    -1f, 1f)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        //这里进行关联
        GLES20.glUniformMatrix4fv(uMatrixAttributeLocation, 1, false, projectionMatrix, 0)
        simpleGeometry?.draw()
    }
}