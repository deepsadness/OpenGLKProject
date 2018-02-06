uniform mat4 u_Matrix;
attribute vec3 a_Position;
varying vec3 v_Position;

void main() {
    //首先将顶点的位置传递给片段着色器
    v_Position=a_Position;
    //反转z方向的分量.
    //这样就是世界坐标系中的右手坐标系，转换成天空盒所期望的左手坐标系
    v_Position.z=-v_Position.z;

    gl_Position = u_Matrix*vec4(a_Position,1.0);
    //我们把z分量设置为其w分量相等的值。
    //这是一种技巧。确保天空盒的每一部分都将位于归一化设备坐标点的远平面上以及场景中的其他一切的后面！
    //因为透视除法把一切都除以w,并且w除以自己。结果等于1.
    //透视除法之后，z最终的值就为1，的远平面上了。
    gl_Position = gl_Position.xyww;
}
