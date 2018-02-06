/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.example.opengllk.refer;


import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.opengllk.R;
import com.example.opengllk.refer.objects.ParticleShooter;
import com.example.opengllk.refer.objects.ParticleSkyBoxShooter;
import com.example.opengllk.refer.objects.ParticleSkyBoxSystem;
import com.example.opengllk.refer.objects.ParticleSystem;
import com.example.opengllk.refer.objects.Skybox;
import com.example.opengllk.refer.programs.ParticleShaderProgram;
import com.example.opengllk.refer.programs.SkyboxShaderProgram;
import com.example.opengllk.refer.util.Geometry.*;
import com.example.opengllk.refer.util.MatrixHelper;
import com.example.opengllk.refer.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

public class ParticlesSkyBoxRenderer implements GLSurfaceView.Renderer {
    private final Context context;

    private final float[] projectionMatrix = new float[16];    
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    
    private SkyboxShaderProgram skyboxProgram;
    private Skybox skybox;
    
    private ParticleShaderProgram particleProgram;
    private ParticleSkyBoxSystem particleSystem;
    private ParticleSkyBoxShooter redParticleShooter;
    private ParticleSkyBoxShooter greenParticleShooter;
    private ParticleSkyBoxShooter blueParticleShooter;

    private long globalStartTime;    
    private int particleTexture;
    private int skyboxTexture;
    
    private float xRotation, yRotation;
    

    public ParticlesSkyBoxRenderer(Context context) {
        this.context = context;
    }

    public void handleTouchDrag(float deltaX, float deltaY) {
        xRotation += deltaX / 16f;
        yRotation += deltaY / 16f;
        
        if (yRotation < -90) {
            yRotation = -90;
        } else if (yRotation > 90) {
            yRotation = 90;
        } 
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        skyboxProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox();
        
        particleProgram = new ParticleShaderProgram(context);        
        particleSystem = new ParticleSkyBoxSystem(10000);
        globalStartTime = System.nanoTime();
        
        final Vector particleDirection = new Vector(0f, 0.5f, 0f);
        final float angleVarianceInDegrees = 5f; 
        final float speedVariance = 1f;
            
        redParticleShooter = new ParticleSkyBoxShooter(
            new Point(-1f, 0f, 0f), 
            particleDirection,                
            Color.rgb(255, 50, 5),
            angleVarianceInDegrees, 
            speedVariance);
        
        greenParticleShooter = new ParticleSkyBoxShooter(
            new Point(0f, 0f, 0f), 
            particleDirection,
            Color.rgb(25, 255, 25),            
            angleVarianceInDegrees, 
            speedVariance);
        
        blueParticleShooter = new ParticleSkyBoxShooter(
            new Point(1f, 0f, 0f), 
            particleDirection,
            Color.rgb(5, 50, 255),            
            angleVarianceInDegrees, 
            speedVariance); 
                
        particleTexture = TextureHelper.loadTexture(context, R.drawable.particle_texture);

        skyboxTexture = TextureHelper.loadCubeMap(context, 
            new int[] { R.drawable.left, R.drawable.right,
                        R.drawable.bottom, R.drawable.top, 
                        R.drawable.front, R.drawable.back});
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
            / (float) height, 1f, 10f);                
    }

    @Override    
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear( GLES20.GL_COLOR_BUFFER_BIT);
        drawSkybox();
        drawParticles();
    }
    
    private void drawSkybox() {
        setIdentityM(viewMatrix, 0);
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);        
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        skyboxProgram.useProgram();
        skyboxProgram.setUniforms(viewProjectionMatrix, skyboxTexture);
        skybox.bindData(skyboxProgram);
        skybox.draw();
    }

    private void drawParticles() {
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;
        
        redParticleShooter.addParticles(particleSystem, currentTime, 1);
        greenParticleShooter.addParticles(particleSystem, currentTime, 1);              
        blueParticleShooter.addParticles(particleSystem, currentTime, 1);

        setIdentityM(viewMatrix, 0);
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        translateM(viewMatrix, 0, 0f, -1.5f, -5f);   
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        GLES20.glEnable( GLES20.GL_BLEND);
        GLES20.glBlendFunc( GLES20.GL_ONE,  GLES20.GL_ONE);
        
        particleProgram.useProgram();
        particleProgram.setUniforms(viewProjectionMatrix, currentTime, particleTexture);
        particleSystem.bindData(particleProgram);
        particleSystem.draw();

        GLES20.glDisable( GLES20.GL_BLEND);
    }    
}