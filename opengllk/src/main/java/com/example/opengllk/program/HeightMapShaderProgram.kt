package com.example.opengllk.program

import android.content.Context
import android.opengl.GLES20
import com.example.opengllk.R

/**
 * Created by Cry on 2018/2/6.
 */
class HeightMapShaderProgram(context: Context)
    : BaseShaderProgram(
        context,
        R.raw.heightmap_vertex_shader,
        R.raw.heightmap_fragment_shader
) {

    //定义需要链接的属性
    companion object {
        val A_POSITION = "a_Position"
        val U_MATRIX = "u_Matrix"
    }

    private val aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION)

    private val uMatrixLocation = GLES20.glGetUniformLocation(programId, U_MATRIX)
    fun getPositionAttributeLocation() = aPositionLocation

    fun setUniforms(matrix: FloatArray) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }

}