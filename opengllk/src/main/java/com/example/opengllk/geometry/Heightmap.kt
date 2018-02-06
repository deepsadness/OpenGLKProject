package com.example.opengllk.geometry

import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES20
import com.example.opengllk.program.HeightMapShaderProgram

/**
 * Created by Cry on 2018/2/6.
 */
class Heightmap(bitmap: Bitmap) {
    companion object {
        val POSITION_COMPONENT_COUNT = 3

    }

    private val width = bitmap.width/2
    private val height = bitmap.height/2

    private val numElements = calculateNumElements()
    private val vertexBuffer = VertexBuffer(loadBitmapData(bitmap))
    private val indexBuffer = IndexBuffer(createIndexData())

    init {
        if (width * height > 65536) {
            throw RuntimeException("Heightmap is too large for the index buffer.")
        }
    }

    /**
     * 工作原理： 针对高度图中每4个顶点构成的组，会生成2个三角形。而且每个三角形有3个索引。共需要6个。
     * 通过 (width - 1) * (height - 1) ，计算出我们需要多少组。
     */
    fun calculateNumElements(): Int {
        return (width - 1) * (height - 1) * 2 * 3
    }

    /**
     * 1.为了有效的读入所有位图数据。先提取所有的像素。然后回收这个bitmap
     * 2.既然每个像素都对应一个顶点，就用这个位图的宽高创建一个新的数组
     *
     */
    private fun loadBitmapData(bitmap: Bitmap): FloatArray {
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        bitmap.recycle()

        val heightmapVertices = FloatArray(width * height * POSITION_COMPONENT_COUNT)
        var offset = 0
        /*
        要生成高度图的每一个顶点。首先要计算顶点的位置；
        高度图在每个方向上都是1个单位宽，而且其以x-z屏幕上的位置(0,0)为中心。因此通过这个循环。
        位图的左上角将被映射到(-0.5,-0.5),右下角会被映射到(0.5,0.5)

        我们假定这个图像是灰像图。因此，读入其对应像素的红色分量。并把它除以255，得到高度。
        一个像素值0对应高度0，而一个像素值255对应高度1

        一行行读取的原因在于其在内存中的布局方式就是这样的。当CPU按照顺序移动缓存和移动数据时，更有效率
         */
        for (row in 0 until height) {
            for (col in 0 until width) {
                // The heightmap will lie flat on the XZ plane and centered
                // around (0, 0), with the bitmap width mapped to X and the
                // bitmap height mapped to Z, and Y representing the height. We
                // assume the heightmap is grayscale, and use the value of the
                // red color to determine the height.
                val xPosition = (col.toFloat() / (width - 1)) - 0.5f
                val yPosition = (Color.red(pixels[row * height + col])).toFloat() / 255f
                val zPosition = (row.toFloat() / (height - 1)) - 0.5f

                heightmapVertices[offset++] = xPosition
                heightmapVertices[offset++] = yPosition
                heightmapVertices[offset++] = zPosition
            }
        }
        return heightmapVertices
    }

    private fun createIndexData(): ShortArray {
        val indexData = ShortArray(numElements)
        var offset = 0

        for (row in 0 until height - 1) {
            for (col in 0 until width - 1) {
                // Note: The (short) cast will end up underflowing the number
                // into the negative range if it doesn't fit, which gives us the
                // right unsigned number for OpenGL due to two's complement.
                // This will work so long as the heightmap contains 65536 pixels
                // or less.
                val topLeftIndexNum = (row * width + col).toShort()
                val topRightIndexNum = (row * width + col + 1).toShort()
                val bottomLeftIndexNum = ((row + 1) * width + col).toShort()
                val bottomRightIndexNum = ((row + 1) * width + col + 1).toShort()

                // Write out two triangles.
                indexData[offset++] = topLeftIndexNum
                indexData[offset++] = bottomLeftIndexNum
                indexData[offset++] = topRightIndexNum

                indexData[offset++] = topRightIndexNum
                indexData[offset++] = bottomLeftIndexNum
                indexData[offset++] = bottomRightIndexNum
            }
        }

        return indexData
    }

    fun bindData(heightMapShaderProgram: HeightMapShaderProgram) {
        vertexBuffer.setVertexAttributePointer(
                0,
                heightMapShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                0
        )
    }

    fun draw() {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.indexBufferTarget)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numElements, GLES20.GL_UNSIGNED_SHORT, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }
}