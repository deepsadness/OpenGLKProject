package com.example.opengllk.geometry

import android.content.Context
import android.opengl.GLES20
import com.example.opengllk.Constant
import com.example.opengllk.R
import com.example.opengllk.loadTexture
import com.example.opengllk.program.Simple3DShader
import com.example.opengllk.program.SimpleTextureShader

/**
 * 添加w分量. w分量就是添加近大远小的功能。靠近屏幕的给1，接近屏幕顶部的给2
 * 就是进行所谓的透视除法
 * Created by Cry on 2018/2/3.
 */
class SimpleTextureGeometry(context: Context) {
    private val table = Table()
    private val mallet = Mallet()
    private val textureShader = SimpleTextureShader(context)
    private val colorShader = Simple3DShader(context)
    private var textureId = R.drawable.air_hockey_surface.loadTexture(context)

    fun draw(matrix: FloatArray) {
        textureShader.useProgram()
        textureShader.setUniforms(matrix, textureId)
        table.bindData(textureShader)
        table.draw()

        colorShader.useProgram()
        colorShader.setUniforms(matrix)
        mallet.bindData(colorShader)
        mallet.draw()
    }

    //木锥
    class Mallet : BaseGeometry() {

        companion object {
            val POSITION_COMPONENT_COUNT = 2
            val COLOR_COMPONENT_COUNT = 3
            val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * Constant.BYTES_PER_FLOAT
        }

        init {
            vertexArray = VertexArray(
                    floatArrayOf(
                            // Order of coordinates: X, Y, R, G, B

                            // Mallets
                            0f, -0.4f, 0f, 0f, 1f,
                            0f,  0.4f, 1f, 0f, 0f
                    )
            )

            drawCmds.add(object : DrawCommand {
                override fun draw() {
                    //绘制桌子
                    GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 2)
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

    //桌子
    class Table : BaseGeometry() {
        companion object {
            private val POSITION_COMPONENT_COUNT = 2
            private val TEXTURE_COORDINATES_COMPONENT_COUNT = 2
            private val STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constant.BYTES_PER_FLOAT
        }

        init {
            vertexArray = VertexArray(
                    floatArrayOf(
                            //2D贴图。的坐标为S,T。横向为s，竖向为t
                            // Order of coordinates: X, Y, S, T

                            // Triangle Fan
                            0f, 0f, 0.5f, 0.5f,
                            -0.5f, -0.8f, 0f, 0.9f,
                            0.5f, -0.8f, 1f, 0.9f,
                            0.5f, 0.8f, 1f, 0.1f,
                            -0.5f, 0.8f, 0f, 0.1f,
                            -0.5f, -0.8f, 0f, 0.9f
                    )
            )

            drawCmds.add(object : DrawCommand {
                override fun draw() {
                    //绘制桌子
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)
                }
            })
        }

        fun bindData(programShader: SimpleTextureShader) {
//            //顶点着色器
            vertexArray.setVertexAttributePointer(
                    0,
                    programShader.getPositionAttributeLocation(),
                    POSITION_COMPONENT_COUNT,
                    STRIDE
            )
//            //片段着色器
            vertexArray.setVertexAttributePointer(
                    POSITION_COMPONENT_COUNT,
                    programShader.getTextureCoordinatesAttributeLocation(),
                    TEXTURE_COORDINATES_COMPONENT_COUNT,
                    STRIDE
            )
        }

    }
}