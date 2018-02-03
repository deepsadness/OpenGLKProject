package com.example.opengllk.geometry

import android.opengl.GLES20
import com.example.opengllk.program.BaseShaderProgram
import com.example.opengllk.program.SimpleShader

/**
 * Created by Cry on 2018/2/3.
 */
class SimpleGeometry : BaseGeometry() {
    private var colorAttributeLocation: Int = 0

    companion object {
        val POSITION_COMPONENT_COUNT = 2
    }

    init {
        vertexArray = VertexArray(
                floatArrayOf(
                        // Triangle 1
                        -0.5f, -0.5f,
                        0.5f, 0.5f,
                        -0.5f, 0.5f,

                        // Triangle 2
                        -0.5f, -0.5f,
                        0.5f, -0.5f,
                        0.5f, 0.5f,

                        // Line 1
                        -0.5f, 0f,
                        0.5f, 0f,

                        // Mallets
                        0f, -0.25f,
                        0f, 0.25f
                )
        )

        drawCmds.add(object : DrawCommand {
            override fun draw() {
                //绘制桌子
                GLES20.glUniform4f(colorAttributeLocation, 1.0f, 1.0f, 1.0f, 1.0f)
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)
            }
        })

        drawCmds.add(object : DrawCommand {
            override fun draw() {
                //绘制分割线
                GLES20.glUniform4f(colorAttributeLocation, 1.0f, 0.0f, 0.0f, 1.0f)
                GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2)
            }
        })

        drawCmds.add(object : DrawCommand {
            override fun draw() {
                //绘制木锥
                GLES20.glUniform4f(colorAttributeLocation, 0.0f, 0.0f, 1.0f, 1.0f)
                GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1)

                GLES20.glUniform4f(colorAttributeLocation, 1.0f, 0.0f, 0.0f, 1.0f)
                GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1)
            }
        })
    }


    fun bindData(shaderProgram: SimpleShader){
        //顶点着色器
        vertexArray.setVertexAttributePointer(
                0,
                shaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                0
        )
        //片段着色器
        colorAttributeLocation = shaderProgram.getColorAttributeLocation()
    }
}