package com.example.opengllk.geometry

import android.opengl.GLES20
import com.example.opengllk.Constant
import com.example.opengllk.Constant.Companion.BYTES_PER_SHORT
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 顶点缓冲区
 * Created by Cry on 2018/2/6.
 */
class VertexBuffer(vertexData: FloatArray) {
    private var bufferId = 0
    //顶点缓冲区应该设置的值
    companion object {
        private val VERTEX_BUFFER_TARGET = GLES20.GL_ARRAY_BUFFER
    }

    init {
        //创建一个buffer
        val buffers = IntArray(1)

        GLES20.glGenBuffers(buffers.size, buffers, 0)

        if (buffers[0] == 0) {
            throw RuntimeException("Could not create a new vertex buffer object")
        }

        bufferId = buffers[0]

        //绑定bufferId
        GLES20.glBindBuffer(VERTEX_BUFFER_TARGET, buffers[0])

        //复制数据到native内存中
        val vertexArray: FloatBuffer = ByteBuffer
                .allocateDirect(vertexData.size * Constant.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                //创建完内存空间，需要将对应的数据放入
                .put(vertexData)
        vertexArray.position(0)

        //从native内存中，复制数据到gpu buffer中
        GLES20.glBufferData(VERTEX_BUFFER_TARGET,
                vertexArray.capacity() * Constant.BYTES_PER_FLOAT,
                vertexArray,
                GLES20.GL_STATIC_DRAW
        )

        //重要的：清除绑定的bufferid.如果不解除绑定。glVertexAttribPoint这样，需要绑定的函数将无法使用!!!
        GLES20.glBindBuffer(VERTEX_BUFFER_TARGET, 0)
    }

    /**
     * 从OpenGL中设置该属性
     */
    fun setVertexAttributePointer(dataOffset: Int = 0, attributeLocation: Int, componentCount: Int, stride: Int) {
        //重新绑定上bufferId
        GLES20.glBindBuffer(VERTEX_BUFFER_TARGET, bufferId)

        //依次传入 1.要绑定的位置变量 2.每个变量包含的变量的count 3.变量的单位 4.暂时都穿false 5.floatBuffer中每次需要的跨距
        GLES20.glVertexAttribPointer(
                attributeLocation,
                componentCount,
                GLES20.GL_FLOAT,
                false,
                stride,
                dataOffset)

        //之后要激活这个属性
        GLES20.glEnableVertexAttribArray(attributeLocation)
        //解除绑定
        GLES20.glBindBuffer(VERTEX_BUFFER_TARGET, 0)
    }
}