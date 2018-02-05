package com.example.opengllk.renderer

import android.content.Context
import android.opengl.Matrix
import android.opengl.Matrix.setLookAtM
import com.example.opengllk.geometry.SimpleTextureGeometry
import com.example.opengllk.helper.MatrixHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by Cry on 2018/2/5.
 */
class GeometryRender(var context: Context) : BaseRenderer() {
    private var simpleGeometry: SimpleTextureGeometry? = null

    //视图矩阵
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    //模型矩阵。复制物体的平移旋转等动作的矩阵
    private val modelMatrix = FloatArray(16)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        simpleGeometry = SimpleTextureGeometry(context)


    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        /*
           创建一个从45度角度的透视投影。从z值为-1开始。在z值为-10结束
       */
        val aspectRatio =
                width * 1.0f / height * 1.0f
        MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f)
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f,
                0f, 0f, 0f, 0f,1f, 0f)
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)

        simpleGeometry!!.draw(projectionMatrix)
    }
}