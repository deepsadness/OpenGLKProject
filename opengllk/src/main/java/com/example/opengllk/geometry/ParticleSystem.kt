package com.example.opengllk.geometry

import android.graphics.Color
import android.opengl.GLES20
import com.example.opengllk.Constant
import com.example.opengllk.program.ParticlesShader

/**
 * 我们会用currentParticleCount和nextParticle持续记录数组中的粒子
 *
 * Created by Cry on 2018/2/5.
 */
class ParticleSystem(private val maxParticleCount: Int) : BaseGeometry() {
    companion object {
        private val POSITION_COMPONENT_COUNT = 3
        private val COLOR_COMPONENT_COUNT = 3
        private val VECTOR_COMPONENT_COUNT = 3
        private val PARTICLE_START_TIME_COMPONENT_COUNT = 1

        private val TOTAL_COMPONENT_COUNT =
                POSITION_COMPONENT_COUNT +
                        COLOR_COMPONENT_COUNT +
                        VECTOR_COMPONENT_COUNT +
                        PARTICLE_START_TIME_COMPONENT_COUNT

        private val STRIDE = TOTAL_COMPONENT_COUNT * Constant.BYTES_PER_FLOAT
    }

    private var currentParticleCount = 0
    private var nextParticle = 0

    private val particles = FloatArray(maxParticleCount * TOTAL_COMPONENT_COUNT)

    init {
        vertexArray = VertexArray(particles)
        drawCmds.add(object : DrawCommand {
            override fun draw() {
                GLES20.glDrawArrays(GLES20.GL_POINTS, 0, currentParticleCount)
            }
        })
    }


    fun addParticle(position: Geometry.Point, color: Int, direction: Geometry.Vector, particleStartTime: Float): Unit {
        val particleOffset = nextParticle * TOTAL_COMPONENT_COUNT

        var currentOffset = particleOffset

        nextParticle++

        if (currentParticleCount < maxParticleCount) {
            currentParticleCount++
        }

        if (nextParticle == maxParticleCount) {
            //如果大了最大值，不能扩容了，就从头开始
            nextParticle = 0
        }

        particles[currentOffset++] = position.x
        particles[currentOffset++] = position.y
        particles[currentOffset++] = position.z

        particles[currentOffset++] = Color.red(color) / 255f
        particles[currentOffset++] = Color.green(color) / 255f
        particles[currentOffset++] = Color.blue(color) / 255f

        particles[currentOffset++] = direction.x
        particles[currentOffset++] = direction.y
        particles[currentOffset++] = direction.z

        particles[currentOffset] = particleStartTime

        //还需要将这些数据复制到缓冲区中。
        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT)
    }

    fun bindData(program: ParticlesShader) {
        //绑定顶点着色器
        var dataOffset = 0
        vertexArray.setVertexAttributePointer(
                dataOffset,
                program.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        )

        dataOffset += POSITION_COMPONENT_COUNT

        vertexArray.setVertexAttributePointer(
                dataOffset,
                program.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE
        )

        dataOffset += COLOR_COMPONENT_COUNT

        vertexArray.setVertexAttributePointer(
                dataOffset,
                program.getDirectionVectorAttributeLocation(),
                VECTOR_COMPONENT_COUNT,
                STRIDE
        )

        dataOffset += VECTOR_COMPONENT_COUNT

        vertexArray.setVertexAttributePointer(
                dataOffset,
                program.getParticelStartTimeAttributeLocation(),
                PARTICLE_START_TIME_COMPONENT_COUNT,
                STRIDE
        )

    }
}