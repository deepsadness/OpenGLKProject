package com.example.opengllk.program

import android.content.Context
import android.opengl.GLES20
import android.util.FloatMath
import com.example.opengllk.R

/**
 * Created by Cry on 2018/2/5.
 */
class ColorShaderProgram(context: Context) :
        BaseShaderProgram(context,
                R.raw.color_vertex_shader,
                R.raw.color_fragment_shader) {

    //定义需要链接的属性
    companion object {
        val A_POSITION = "a_Position"
        val U_MATRIX = "u_Matrix"
        val U_COLOR = "u_Color"
    }

    private val aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION)

    private val uMatrixLocation = GLES20.glGetUniformLocation(programId, U_MATRIX)
    private val uColorLocation = GLES20.glGetUniformLocation(programId, U_COLOR)

    fun getPositionAttributeLocation() = aPositionLocation
    fun getColorAttributeLocation() = uColorLocation
    fun getMatrixAttributeLocation() = uMatrixLocation


    fun setUniforms(matrix: FloatArray, r: Float, g: Float, b: Float) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        GLES20.glUniform4f(uColorLocation, r, g, b, 1f)

    }

}