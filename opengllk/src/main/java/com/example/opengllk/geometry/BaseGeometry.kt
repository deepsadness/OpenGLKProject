package com.example.opengllk.geometry

/**
 * 基础的图形类。图形类中包含有图形的顶点。和图像绘制的命令
 * Created by Cry on 2018/2/3.
 */
abstract class BaseGeometry {
    //代表图形的订单
    protected lateinit var vertexArray: VertexArray
    //绘制的命令
    protected val drawCmds = ArrayList<DrawCommand>()


    fun draw() {
        drawCmds.forEach {
            it.draw()
        }
    }
}