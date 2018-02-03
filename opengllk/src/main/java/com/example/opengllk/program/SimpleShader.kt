package com.example.opengllk.program

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.glGetAttribLocation
import com.example.opengllk.R

/**
 * Created by Cry on 2018/2/3.
 */
class SimpleShader(context: Context) :
        BaseShaderProgram(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader) {
    //定义需要链接的属性
    companion object {
        val A_POSITION = "a_Position"
        val U_COLOR = "u_Color"
    }

    private val aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION)
    private val uColorLocation = GLES20.glGetUniformLocation(programId, U_COLOR)

    fun getPositionAttributeLocation() = aPositionLocation
    fun getColorAttributeLocation() = uColorLocation


}