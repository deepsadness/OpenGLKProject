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
                R.raw.particle_vertex_shader0,
                R.raw.particle_fragment_shader0) {
    //定义需要链接的属性
    companion object {
        val U_TIME = "u_Time"
        val U_MATRIX = "u_Matrix"
        val U_TEXTURE_UNIT = "u_TextureUnit"

        val A_COLOR = "a_Color"
        val A_POSITION = "a_Position"
        val A_DIRECTION_VECTOR = "a_DirectionVector"
        val A_PARTICLE_START_TIME = "a_ParticleStartTime"
    }

    //    UniformLocation
    private val uMatrixLocation = GLES20.glGetUniformLocation(programId, U_MATRIX)
    private val uTimeLocation = GLES20.glGetUniformLocation(programId, U_TIME)
    private val uTextureUnitLocation = GLES20.glGetUniformLocation(programId, U_TEXTURE_UNIT)
    //    AttribLocation
    private val aColorLocation = GLES20.glGetAttribLocation(programId, A_COLOR)
    private val aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION)
    private val aDirectionVectorLocation = GLES20.glGetAttribLocation(programId, A_DIRECTION_VECTOR)
    private val aParticleStartTimeLocation = GLES20.glGetAttribLocation(programId, A_PARTICLE_START_TIME)

    fun getPositionAttributeLocation() = aPositionLocation
    fun getTimeAttributeLocation() = uTimeLocation
    fun getDirectionVectorAttributeLocation() = aDirectionVectorLocation
    fun getParticelStartTimeAttributeLocation() = aParticleStartTimeLocation
    fun getColorAttributeLocation() = aColorLocation
    fun getMatrixAttributeLocation() = uMatrixLocation

    fun setUniforms(matrix: FloatArray, elapsedTime: Float) {
        //将uniforms的值进行绑定
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        GLES20.glUniform1f(uTimeLocation, elapsedTime)
    }

    fun setUniforms(matrix: FloatArray, elapsedTime: Float, textureId: Int) {
        //将uniforms的值进行绑定
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        GLES20.glUniform1f(uTimeLocation, elapsedTime)
        //绑定纹理到unit上
        //1.激活
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        //绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        //绑定到着色器
        GLES20.glUniform1i(uTextureUnitLocation, 0)
    }

}