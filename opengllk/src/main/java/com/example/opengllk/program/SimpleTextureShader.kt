package com.example.opengllk.program

import android.content.Context
import android.opengl.GLES20
import com.example.opengllk.R

/**
 * Created by Cry on 2018/2/3.
 */
class SimpleTextureShader(context: Context) :
        BaseShaderProgram(context,
                R.raw.simple_texture_vertex_shader,
                R.raw.simple_texture_fragment_shader) {
    //定义需要链接的属性
    companion object {
        val A_POSITION = "a_Position"
        val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
        val U_MATRIX = "u_Matrix"
        val U_TEXTURE_UNIT = "u_TextureUnit"
    }

    private val aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION)
    private val aTextureCoordinatesLocation = GLES20.glGetAttribLocation(programId, A_TEXTURE_COORDINATES)

    private val uMatrixLocation = GLES20.glGetUniformLocation(programId, U_MATRIX)
    private val uTextureUnitLocation = GLES20.glGetUniformLocation(programId, U_TEXTURE_UNIT)

    fun getPositionAttributeLocation() = aPositionLocation
    fun getTextureCoordinatesAttributeLocation() = aTextureCoordinatesLocation

    fun setUniforms(matrix: FloatArray, textureId: Int): Unit {

        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        /*
        当我们再OpenGL使用纹理进行绘制时，我们不需要直接给着色器传递纹理。
        相反，我们使用纹理单元(texture unit)保存这个纹理
            1. 把活动的纹理单元设置为纹理单元0
            2. 通过glBindTexture绑定纹理到这个单元
            3. glUniform1i 把被选定的纹理单元传递给片段着色器中的u_TextureUnit
         */


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glUniform1i(uTextureUnitLocation, 0)

    }
}