/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package com.example.opengllk.geometry;

import android.R.attr.radius
import android.R.attr.x
import android.R.attr.y
import com.example.opengllk.geometry.Geometry.Cylinder
import com.example.opengllk.geometry.Geometry.Circle
import android.R.attr.translateY
import android.opengl.GLES20
import android.util.FloatMath
import com.example.opengllk.program.Simple3DShader


class Geometry {
    val puck=Puck(0.06f,0.02f,32)
    val mallet:Mallet=Mallet(0.08f,0.15f,32)




    data class Point(val x: Float, val y: Float, val z: Float) {

        fun translateY(distance: Float): Point {
            return Point(x, y + distance, z);
        }
    }

    data class Circle(val center: Point, val radius: Float) {

        fun scale(scale: Float): Circle {
            return Circle(center, radius * scale);
        }
    }

    data class Cylinder(val center: Point, val radius: Float, val height: Float)

    class GeneratedData(val vertexData: FloatArray, val drawList: List<DrawCommand>)


    class Mallet(val radius: Float, val height: Float, numPointsAroundMallet: Int) : BaseGeometry() {
        companion object {
            val POSITION_COMPONENT_COUNT = 3
        }

        init {
            val generatedData = ObjectBuilder.createMallet(
                    Point(0f, 0f, 0f),
                    radius,
                    height,
                    numPointsAroundMallet
            )

            vertexArray = VertexArray(generatedData.vertexData)
            drawCmds.addAll(generatedData.drawList)
        }

        fun bindData(program: Simple3DShader) {
            vertexArray.setVertexAttributePointer(
                    0,
                    program.getPositionAttributeLocation(),
                    POSITION_COMPONENT_COUNT, 0
            )
        }
    }

    class Puck(val radius: Float, val height: Float, numPointsAroundPuck: Int) : BaseGeometry() {
        companion object {
            val POSITION_COMPONENT_COUNT = 3
        }

        init {
            val generatedData = ObjectBuilder.createPuck(
                    Cylinder(Point(0f, 0f, 0f), radius, height),
                    numPointsAroundPuck
            )

            vertexArray = VertexArray(generatedData.vertexData)
            drawCmds.addAll(generatedData.drawList)
        }

        fun bindData(program: Simple3DShader) {
            vertexArray.setVertexAttributePointer(
                    0,
                    program.getPositionAttributeLocation(),
                    POSITION_COMPONENT_COUNT, 0
            )
        }

    }

    class ObjectBuilder(sizeInVertices: Int) {
        companion object {
            val FLOATS_PER_VERTEX = 3

            fun createPuck(puck: Cylinder, numPoints: Int): GeneratedData {
                val size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints)

                val builder = ObjectBuilder(size)

                val puckTop = Circle(
                        puck.center.translateY(puck.height / 2f),
                        puck.radius)

                builder.appendCircle(puckTop, numPoints)
                builder.appendOpenCylinder(puck, numPoints)

                return builder.build()
            }

            fun createMallet(
                    center: Point, radius: Float, height: Float, numPoints: Int): GeneratedData {
                val size = sizeOfCircleInVertices(numPoints) * 2 + sizeOfOpenCylinderInVertices(numPoints) * 2

                val builder = ObjectBuilder(size)

                // First, generate the mallet base.
                val baseHeight = height * 0.25f

                val baseCircle = Circle(
                        center.translateY(-baseHeight),
                        radius)
                val baseCylinder = Cylinder(
                        baseCircle.center.translateY(-baseHeight / 2f),
                        radius, baseHeight)

                builder.appendCircle(baseCircle, numPoints)
                builder.appendOpenCylinder(baseCylinder, numPoints)

                // Now generate the mallet handle.
                val handleHeight = height * 0.75f
                val handleRadius = radius / 3f

                val handleCircle = Circle(
                        center.translateY(height * 0.5f),
                        handleRadius)
                val handleCylinder = Cylinder(
                        handleCircle.center.translateY(-handleHeight / 2f),
                        handleRadius, handleHeight)

                builder.appendCircle(handleCircle, numPoints)
                builder.appendOpenCylinder(handleCylinder, numPoints)

                return builder.build()
            }


            private fun sizeOfCircleInVertices(numPoints: Int): Int {
                return 1 + (numPoints + 1)
            }

            private fun sizeOfOpenCylinderInVertices(numPoints: Int): Int {
                return (numPoints + 1) * 2
            }
        }

        private val drawList = ArrayList<DrawCommand>()
        private var offset = 0
        private var vertexData: FloatArray = FloatArray(sizeInVertices * FLOATS_PER_VERTEX)

        private fun appendCircle(circle: Circle, numPoints: Int) {
            val startVertex = offset / FLOATS_PER_VERTEX
            val numVertices = sizeOfCircleInVertices(numPoints)

            // Center point of fan
            vertexData[offset++] = circle.center.x
            vertexData[offset++] = circle.center.y
            vertexData[offset++] = circle.center.z

            // Fan around center point. <= is used because we want to generate
            // the point at the starting angle twice to complete the fan.
            for (i in 0..numPoints) {
                val angleInRadians = i.toFloat() / numPoints.toFloat() * (Math.PI.toFloat() * 2f)

                vertexData[offset++] = circle.center.x + circle.radius * Math.cos(angleInRadians.toDouble()).toFloat()
                vertexData[offset++] = circle.center.y
                vertexData[offset++] = circle.center.z + circle.radius * Math.sin(angleInRadians.toDouble()).toFloat()
            }
            drawList.add(object : DrawCommand {
                override fun draw() {
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices)
                }
            })
        }

        private fun appendOpenCylinder(cylinder: Cylinder, numPoints: Int) {
            val startVertex = offset / FLOATS_PER_VERTEX
            val numVertices = sizeOfOpenCylinderInVertices(numPoints)
            val yStart = cylinder.center.y - cylinder.height / 2f
            val yEnd = cylinder.center.y + cylinder.height / 2f

            // Generate strip around center point. <= is used because we want to
            // generate the points at the starting angle twice, to complete the
            // strip.
            for (i in 0..numPoints) {
                val angleInRadians = i.toFloat() / numPoints.toFloat() * (Math.PI.toFloat() * 2f)

                val xPosition = cylinder.center.x + cylinder.radius * Math.cos(angleInRadians.toDouble()).toFloat()

                val zPosition = cylinder.center.z + cylinder.radius * Math.sin(angleInRadians.toDouble()).toFloat()

                vertexData[offset++] = xPosition
                vertexData[offset++] = yStart
                vertexData[offset++] = zPosition

                vertexData[offset++] = xPosition
                vertexData[offset++] = yEnd
                vertexData[offset++] = zPosition
            }
            drawList.add(object : DrawCommand {
                override fun draw() {
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices)
                }
            })
        }


        private fun build(): GeneratedData {
            return GeneratedData(vertexData, drawList)
        }
    }


}
