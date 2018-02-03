package com.example.opengllk.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import android.opengl.Matrix.*
import com.example.opengllk.geometry.Simple3DGeometry
import com.example.opengllk.geometry.SimpleMatrixGeometry
import com.example.opengllk.helper.MatrixHelper
import com.example.opengllk.program.Simple3DShader
import com.example.opengllk.program.SimpleMatrixShader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 这里需要注意的是，gl的初始化，都需要在SurfaceCreated之后，不能在其之前
 * Created by Cry on 2018/2/3.
 */
class Simple3DRenderer(var context: Context) : BaseRenderer() {
    private var simpleGeometry: Simple3DGeometry? = null

    //这里开始要维护投影矩阵
    private val projectionMatrix = FloatArray(16)
    //模型矩阵。复制物体的平移旋转等动作的矩阵
    private val modelMatrix = FloatArray(16)
    var uMatrixAttributeLocation: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        simpleGeometry = Simple3DGeometry()
        val simpleShader = Simple3DShader(context)
        simpleShader.useProgram()
        uMatrixAttributeLocation = simpleShader.getMatrixAttributeLocation()
        simpleGeometry!!.bindData(simpleShader)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        /*
           创建一个从45度角度的透视投影。从z值为-1开始。在z值为-10结束
       */
        val aspectRatio =
                width * 1.0f / height * 1.0f
        MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f)

        //设置为单位矩阵
        setIdentityM(modelMatrix, 0)
        //会将我们的模型沿着z轴移动-2.因为我们的视角是从-1到-10,所以就能看到东西了
        translateM(modelMatrix, 0, 0f, 0f, -2.5f)
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f)

        //更新我们的投影矩阵。后乘modelMatrix
        val temp = FloatArray(16)
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.size)
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        //这里进行关联
        GLES20.glUniformMatrix4fv(uMatrixAttributeLocation, 1, false, projectionMatrix, 0)
        simpleGeometry?.draw()
    }
}