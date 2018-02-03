package com.example.opengllk.program

import android.content.Context
import android.opengl.GLES20
import com.example.opengllk.R

/**
 * Created by Cry on 2018/2/3.
 */
class SimpleMatrixShader(context: Context) :
        BaseShaderProgram(context,
                R.raw.simple_matrix_vertex_shader,
                R.raw.simple_matrix_fragment_shader) {
    //定义需要链接的属性
    companion object {
        val A_POSITION = "a_Position"
        val A_COLOR = "a_Color"
        val U_MATRIX = "u_Matrix"
    }

    private val aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION)
    private val aColorLocation = GLES20.glGetAttribLocation(programId, A_COLOR)
    private val uMatrixLocation = GLES20.glGetUniformLocation(programId, U_MATRIX)

    fun getPositionAttributeLocation() = aPositionLocation
    fun getColorAttributeLocation() = aColorLocation
    fun getMatrixAttributeLocation() = uMatrixLocation

}