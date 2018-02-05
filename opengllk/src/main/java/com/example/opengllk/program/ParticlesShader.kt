package com.example.opengllk.program

import android.content.Context
import android.opengl.GLES20
import com.example.opengllk.R

/**
 * Created by Cry on 2018/2/3.
 */
class ParticlesShader(context: Context) :
        BaseShaderProgram(
                context,
                R.raw.particle_vertex_shader,
                R.raw.particle_fragment_shader) {
    //定义需要链接的属性
    companion object {
        val U_TIME = "u_Time"
        val U_MATRIX = "u_Matrix"

        val A_COLOR = "a_Color"
        val A_POSITION = "a_Position"
        val A_DIRECION_VECTOR = "a_DirecionVector"
        val A_PARTICLE_START_TIME = "a_ParticleStartTime"
    }

    //    UniformLocation
    private val uMatrixLocation = GLES20.glGetUniformLocation(programId, U_MATRIX)
    private val uTimeLocation = GLES20.glGetUniformLocation(programId, U_TIME)
    //    AttribLocation
    private val aColorLocation = GLES20.glGetAttribLocation(programId, A_COLOR)
    private val aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION)
    private val aDirectionVectorLocation = GLES20.glGetAttribLocation(programId, A_DIRECION_VECTOR)
    private val aParticelStartTimeLocation = GLES20.glGetAttribLocation(programId, A_PARTICLE_START_TIME)

    fun getPositionAttributeLocation() = aPositionLocation
    fun getTimeAttributeLocation() = uTimeLocation
    fun getDirectionVectorAttributeLocation() = aDirectionVectorLocation
    fun getParticelStartTimeAttributeLocation() = aParticelStartTimeLocation
    fun getColorAttributeLocation() = aColorLocation
    fun getMatrixAttributeLocation() = uMatrixLocation

    fun setUniforms(matrix: FloatArray, elapsedTime: Float) {
        GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0)
        GLES20.glUniform1f(uTimeLocation,elapsedTime)
    }

}