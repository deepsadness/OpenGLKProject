package com.example.opengllk.geometry

import android.opengl.Matrix.multiplyMV
import android.opengl.Matrix.setRotateEulerM



/**
 * Created by Cry on 2018/2/5.
 */
class ParticleShooter(
        private val position: Geometry.Point,
        private val direction: Geometry.Vector,
        private val color: Int) {

    init {

    }

    fun addParticles(particleSystem: ParticleSystem, currentTime: Float, count: Int) {
        for (i in 0 until count) {
//            setRotateEulerM(rotationMatrix, 0,
//                    (random.nextFloat() - 0.5f) * angleVariance,
//                    (random.nextFloat() - 0.5f) * angleVariance,
//                    (random.nextFloat() - 0.5f) * angleVariance)
//
//            multiplyMV(
//                    resultVector, 0,
//                    rotationMatrix, 0,
//                    directionVector, 0)
//
//            val speedAdjustment = 1f + random.nextFloat() * speedVariance
//
//            val thisDirection = Geometry.Vector(
//                    resultVector[0] * speedAdjustment,
//                    resultVector[1] * speedAdjustment,
//                    resultVector[2] * speedAdjustment)

            /*
            particleSystem.addParticle(position, color, direction, currentTime);
             */
            particleSystem.addParticle(position, color, direction, currentTime)
        }
    }
}