package com.example.opengllk.geometry

import android.opengl.GLES20
import com.example.opengllk.Constant
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * OpenGL中的每个图形都是由这样子的顶点来完成的。
 * 顶点数据的类。
 * 1. 提供将顶点数据复制到内存中的方法
 * 2. 和OpenGL绑定数据的方法
 *
 * Created by Cry on 2018/2/3.
 */
class VertexArray(vertexData: FloatArray) {
    private val floatBuffer: FloatBuffer = ByteBuffer
            .allocateDirect(vertexData.size * Constant.BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            //创建完内存空间，需要将对应的数据放入
            .put(vertexData)

    /**
     * 从OpenGL中设置该属性
     */
    fun setVertexAttributePointer(offset: Int = 0, attributeLocation: Int, componentCount: Int, stride: Int) {
        //需要先将数据偏移到offset的位置
        floatBuffer.position(offset)

        //依次传入 1.要绑定的位置变量 2.每个变量包含的变量的count 3.变量的单位 4.暂时都穿false 5.floatBuffer中每次需要的跨距
        GLES20.glVertexAttribPointer(attributeLocation, componentCount,
                GLES20.GL_FLOAT, false, stride, floatBuffer)

        //之后要激活这个属性
        GLES20.glEnableVertexAttribArray(attributeLocation)
        //再讲floatBuffer重置回来
        floatBuffer.position(0)
    }

    fun updateBuffer(vertexData: FloatArray, start: Int, count: Int) {
        floatBuffer.position(start)
        floatBuffer.put(vertexData,start,count)
        floatBuffer.position(0)
    }
}