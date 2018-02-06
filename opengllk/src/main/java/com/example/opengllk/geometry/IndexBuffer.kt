package com.example.opengllk.geometry

import android.opengl.GLES20
import com.example.opengllk.Constant
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * 索引缓冲区代表的类
 */
class IndexBuffer(indexData: ShortArray) {
    private var bufferId: Int = 0
    companion object {
        //索引缓冲区应该设置的值
        val INDEX_BUFFER_TARGET = GLES20.GL_ELEMENT_ARRAY_BUFFER
    }


    init {
        // Allocate a buffer.
        val buffers = IntArray(1)
        GLES20.glGenBuffers(buffers.size, buffers, 0)

        if (buffers[0] == 0) {
            throw RuntimeException("Could not create a new index buffer object.")
        }

        bufferId = buffers[0]

        // Bind to the buffer.
        GLES20.glBindBuffer(INDEX_BUFFER_TARGET, buffers[0])

        // Transfer data to native memory.
        val indexArray = ByteBuffer
                .allocateDirect(indexData.size * Constant.BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(indexData)
        indexArray.position(0)

        // Transfer data from native memory to the GPU buffer.
        GLES20.glBufferData(
                INDEX_BUFFER_TARGET,
                indexArray.capacity() * Constant.BYTES_PER_SHORT,
                indexArray,
                GLES20.GL_STATIC_DRAW)

        // IMPORTANT: Unbind from the buffer when we're done with it.
        GLES20.glBindBuffer(INDEX_BUFFER_TARGET, 0)

        // We let the native buffer go out of scope, but it won't be released
        // until the next time the garbage collector is run.
    }

    fun getBufferId(): Int {
        return bufferId
    }
}