package com.example.opengllk.geometry

import android.opengl.GLES20
import com.example.opengllk.program.SkyboxShaderProgram
import java.nio.ByteBuffer

/**
 * 创建立方体对象，需要创建我们的第一个索引数组。
 * 因为一个立方体只有8个相互独立的顶点。每个点点有三个位置分量。因此需要24个浮点数
 *
 * 假设我们每个面用2个三角形来绘制这个立方体，我们就总共需要12个三角形
 * 每个三角形有3个顶点。 所以就需要36个顶点或者108个浮点数。其中很多数据是重复的。
 * 通过这个索引数组，就不用重复所有的顶点数据了。相反，只需要重复那些索引值。
 *
 * Created by Cry on 2018/2/6.
 */
class SkyBox : BaseGeometry() {
    companion object {
        val POSITION_COMPONENT_COUNT = 3
    }

    val indexArray: ByteBuffer

    init {
        vertexArray = VertexArray(
                floatArrayOf(
                        -1f, 1f, 1f,      //0 上左 近
                        1f, 1f, 1f,      //0 上右 近
                        -1f, -1f, 1f,      //0 下左 近
                        1f, -1f, 1f,      //0 下右 近

                        -1f, 1f, -1f,     //0 上左 远
                        1f, 1f, -1f,     //0 上右 远
                        -1f, -1f, -1f,     //0 下左 远
                        1f, -1f, -1f      //0 下右 远
                ))

        //这里是一个面，由两个三角形构成。每个三角形的顶点，是通过索引值来确定。
        indexArray = ByteBuffer.allocateDirect(6 * 6)
                .put(byteArrayOf(
                        //前
                        1, 3, 0,
                        0, 3, 2,
                        //后
                        4, 6, 5,
                        5, 6, 7,
                        //左
                        0, 2, 4,
                        4, 2, 6,
                        //右
                        5, 7, 1,
                        1, 7, 3,
                        //上
                        5, 1, 4,
                        4, 1, 0,
                        //下
                        6, 2, 7,
                        7, 2, 3

                ))
        indexArray.position(0)

        drawCmds.add(object : DrawCommand {
            override fun draw() {
                //因为我们使用了indices所定义的索引数组，所以把这个数组解释成无符号字节数。
                //OpenGL es2 中 indices需要的是无符号字节数 或者无符号的短整形类型
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_BYTE, indexArray)
            }
        })
    }

    fun bindData(skyboxShaderProgram: SkyboxShaderProgram) {
        vertexArray.setVertexAttributePointer(
                0,
                skyboxShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                0
        )
    }
}