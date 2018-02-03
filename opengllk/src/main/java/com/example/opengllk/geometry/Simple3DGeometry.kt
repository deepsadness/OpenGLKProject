package com.example.opengllk.geometry

import android.opengl.GLES20
import com.example.opengllk.Constant
import com.example.opengllk.program.Simple3DShader
import com.example.opengllk.program.SimpleMatrixShader

/**
 * 添加w分量. w分量就是添加近大远小的功能。靠近屏幕的给1，接近屏幕顶部的给2
 * 就是进行所谓的透视除法
 * Created by Cry on 2018/2/3.
 */
class Simple3DGeometry : BaseGeometry() {

    companion object {
        val POSITION_COMPONENT_COUNT = 4
        val COLOR_COMPONENT_COUNT = 3
        val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * Constant.BYTES_PER_FLOAT
    }

    init {
        vertexArray = VertexArray(
                floatArrayOf(
                        // Order of coordinates: X, Y, R, G, B

                        // Triangle Fan
                        0f, 0f, 0f, 1.5f, 1f, 1f, 1f,
                        -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
                        0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
                        0.5f, 0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
                        -0.5f, 0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
                        -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,

                        // Line 1
                        -0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,
                        0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,

                        // Mallets
                        0f, -0.4f, 0f, 1.25f, 0f, 0f, 1f,
                        0f, 0.4f, 0f, 1.75f, 1f, 0f, 0f
                )
        )

        drawCmds.add(object : DrawCommand {
            override fun draw() {
                //绘制桌子
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)
            }
        })

        drawCmds.add(object : DrawCommand {
            override fun draw() {
                //绘制分割线
                GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2)
            }
        })

        drawCmds.add(object : DrawCommand {
            override fun draw() {
                //绘制木锥
                GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1)

                GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1)
            }
        })
    }

    fun bindData(programShader: Simple3DShader) {
        //顶点着色器
        vertexArray.setVertexAttributePointer(
                0,
                programShader.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        )
        //片段着色器
        vertexArray.setVertexAttributePointer(
                POSITION_COMPONENT_COUNT,
                programShader.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE
        )

    }
}