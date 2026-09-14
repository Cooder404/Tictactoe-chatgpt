package com.example.tictactoe;

import android.app.Activity;
import android.os.Bundle;
import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.MotionEvent;
import android.graphics.Color;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {

    GameView game;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        game = new GameView();
        setContentView(game);
    }

    class GameView extends GLSurfaceView {

        Renderer3D renderer;

        float joyX = 0;
        float joyY = 0;
        float lookX = 0;
        float lookY = 0;

        float playerX = 0;
        float playerY = 2;
        float playerZ = 5;

        float yaw = 0;
        float pitch = 0;

        boolean jumping = false;

        long lastTime;

        ArrayList<Mob> mobs = new ArrayList<>();
        Random random = new Random();

        GameView() {
            super(MainActivity.this);

            setEGLContextClientVersion(2);

            renderer = new Renderer3D();
            setRenderer(renderer);

            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

            for (int i = 0; i < 8; i++) {
                mobs.add(new Mob(
                        random.nextFloat() * 20 - 10,
                        1,
                        random.nextFloat() * 20 - 10
                ));
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {

            int action = e.getActionMasked();

            if (action == MotionEvent.ACTION_DOWN ||
                    action == MotionEvent.ACTION_POINTER_DOWN) {

                int index = e.getActionIndex();

                float x = e.getX(index);
                float y = e.getY(index);

                // LEFT SIDE = movement joystick
                if (x < getWidth() * 0.45f) {

                    float cx = getWidth() * 0.20f;
                    float cy = getHeight() * 0.78f;

                    float dx = x - cx;
                    float dy = y - cy;

                    float max = getWidth() * 0.14f;

                    float length = (float)Math.sqrt(dx * dx + dy * dy);

                    if (length > max) {
                        dx = dx / length * max;
                        dy = dy / length * max;
                    }

                    joyX = dx / max;
                    joyY = dy / max;
                }

                // RIGHT SIDE = camera
                else {
                    lookX = x;
                    lookY = y;
                }

                return true;
            }

            if (action == MotionEvent.ACTION_MOVE) {

                for (int i = 0; i < e.getPointerCount(); i++) {

                    float x = e.getX(i);
                    float y = e.getY(i);

                    // Whole joystick circle works
                    if (x < getWidth() * 0.45f) {

                        float cx = getWidth() * 0.20f;
                        float cy = getHeight() * 0.78f;

                        float dx = x - cx;
                        float dy = y - cy;

                        float max = getWidth() * 0.14f;

                        float length =
                                (float)Math.sqrt(dx * dx + dy * dy);

                        if (length > max) {
                            dx = dx / length * max;
                            dy = dy / length * max;
                        }

                        joyX = dx / max;
                        joyY = dy / max;
                    }
                }

                return true;
            }

            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_POINTER_UP ||
                    action == MotionEvent.ACTION_CANCEL) {

                joyX = 0;
                joyY = 0;

                return true;
            }

            return true;
        }

        void jump() {
            if (!jumping) {
                jumping = true;
                playerY += 1.2f;
            }
        }

        void update() {

            float speed = 0.08f;

            float forwardX =
                    (float)Math.sin(Math.toRadians(yaw));

            float forwardZ =
                    (float)Math.cos(Math.toRadians(yaw));

            playerX += (-joyY * forwardX + joyX * forwardZ) * speed;
            playerZ += (-joyY * forwardZ - joyX * forwardX) * speed;

            if (jumping) {
                playerY -= 0.025f;

                if (playerY <= 2) {
                    playerY = 2;
                    jumping = false;
                }
            }

            // Mobs flee when hit / flee state
            for (Mob m : mobs) {

                if (m.fleeTimer > 0) {

                    float dx = m.x - playerX;
                    float dz = m.z - playerZ;

                    float d =
                            (float)Math.sqrt(dx * dx + dz * dz);

                    if (d > 0.01f) {
                        m.x += dx / d * 0.03f;
                        m.z += dz / d * 0.03f;
                    }

                    m.fleeTimer--;
                }
            }
        }

        class Mob {

            float x, y, z;
            int hp = 20;
            int fleeTimer = 0;

            Mob(float x, float y, float z) {
                this.x = x;
                this.y = y;
                this.z = z;
            }

            void hit() {
                hp -= 5;
                fleeTimer = 180;
            }
        }

        class Renderer3D implements GLSurfaceView.Renderer {

            Cube cube;

            float[] projection = new float[16];
            float[] view = new float[16];
            float[] model = new float[16];
            float[] mvp = new float[16];

            float time = 0;

            @Override
            public void onSurfaceCreated(
                    javax.microedition.khronos.egl.EGLConfig config) {

                GLES20.glEnable(GLES20.GL_DEPTH_TEST);
                GLES20.glClearColor(
                        0.45f,
                        0.70f,
                        1.0f,
                        1.0f
                );

                cube = new Cube();
                lastTime = System.currentTimeMillis();
            }

            @Override
            public void onSurfaceChanged(
                    javax.microedition.khronos.opengles.GL10 gl,
                    int width,
                    int height) {

                GLES20.glViewport(0, 0, width, height);

                float ratio = (float)width / height;

                Matrix.perspectiveM(
                        projection,
                        0,
                        70,
                        ratio,
                        0.1f,
                        100
                );
            }

            @Override
            public void onDrawFrame(
                    javax.microedition.khronos.opengles.GL10 gl) {

                long now = System.currentTimeMillis();

                float dt =
                        (now - lastTime) / 1000.0f;

                lastTime = now;

                time += dt;

                update();

                // 24-hour day/night cycle
                float day =
                        (float)((Math.sin(
                                time * Math.PI * 2 / 120.0
                        ) + 1) * 0.5);

                float sky = 0.20f + day * 0.55f;

                GLES20.glClearColor(
                        sky * 0.5f,
                        sky * 0.75f,
                        sky,
                        1
                );

                GLES20.glClear(
                        GLES20.GL_COLOR_BUFFER_BIT |
                        GLES20.GL_DEPTH_BUFFER_BIT
                );

                Matrix.setLookAtM(
                        view,
                        0,
                        playerX,
                        playerY,
                        playerZ,
                        playerX,
                        playerY,
                        playerZ - 5,
                        0,
                        1,
                        0
                );

                // Ground
                drawCube(
                        0,
                        0,
                        0,
                        20,
                        0.5f,
                        20,
                        0.25f,
                        0.70f,
                        0.20f
                );

                // Dirt/stone world blocks
                for (int x = -10; x <= 10; x++) {
                    for (int z = -10; z <= 10; z++) {

                        if ((x + z) % 5 == 0) {
                            drawCube(
                                    x,
                                    0.7f,
                                    z,
                                    1,
                                    1,
                                    1,
                                    0.35f,
                                    0.22f,
                                    0.10f
                            );
                        }
                    }
                }

                // Mobs
                for (Mob m : mobs) {

                    drawCube(
                            m.x,
                            m.y,
                            m.z,
                            0.9f,
                            1.8f,
                            0.9f,
                            0.25f,
                            0.55f,
                            0.25f
                    );
                }

                // Warden can be spawned with XZ
                // handled below
            }

            void drawCube(
                    float x,
                    float y,
                    float z,
                    float sx,
                    float sy,
                    float sz,
                    float r,
                    float g,
                    float b) {

                Matrix.setIdentityM(model, 0);

                Matrix.translateM(
                        model,
                        0,
                        x,
                        y,
                        z
                );

                Matrix.scaleM(
                        model,
                        0,
                        sx,
                        sy,
                        sz
                );

                float[] temp = new float[16];

                Matrix.multiplyMM(
                        temp,
                        0,
                        view,
                        0,
                        model,
                        0
                );

                Matrix.multiplyMM(
                        mvp,
                        0,
                        projection,
                        0,
                        temp,
                        0
                );

                cube.draw(mvp, r, g, b);
            }
        }

        class Cube {

            FloatBuffer vertices;
            int program;

            final float[] data = {

                    -0.5f,-0.5f,-0.5f,
                     0.5f,-0.5f,-0.5f,
                     0.5f, 0.5f,-0.5f,
                    -0.5f, 0.5f,-0.5f,

                    -0.5f,-0.5f, 0.5f,
                     0.5f,-0.5f, 0.5f,
                     0.5f, 0.5f, 0.5f,
                    -0.5f, 0.5f, 0.5f
            };

            Cube() {

                ByteBuffer bb =
                        ByteBuffer.allocateDirect(
                                data.length * 4
                        );

                bb.order(
                        ByteOrder.nativeOrder()
                );

                vertices = bb.asFloatBuffer();
                vertices.put(data);
                vertices.position(0);

                String vertexShader =
                        "attribute vec4 v;" +
                        "uniform mat4 m;" +
                        "void main(){gl_Position=m*v;}";

                String fragmentShader =
                        "precision mediump float;" +
                        "uniform vec4 c;" +
                        "void main(){gl_FragColor=c;}";

                int vs = loadShader(
                        GLES20.GL_VERTEX_SHADER,
                        vertexShader
                );

                int fs = loadShader(
                        GLES20.GL_FRAGMENT_SHADER,
                        fragmentShader
                );

                program =
                        GLES20.glCreateProgram();

                GLES20.glAttachShader(
                        program,
                        vs
                );

                GLES20.glAttachShader(
                        program,
                        fs
                );

                GLES20.glLinkProgram(program);
            }

            void draw(
                    float[] matrix,
                    float r,
                    float g,
                    float b) {

                GLES20.glUseProgram(program);

                int pos =
                        GLES20.glGetAttribLocation(
                                program,
                                "v"
                        );

                int mat =
                        GLES20.glGetUniformLocation(
                                program,
                                "m"
                        );

                int col =
                        GLES20.glGetUniformLocation(
                                program,
                                "c"
                        );

                GLES20.glEnableVertexAttribArray(pos);

                GLES20.glVertexAttribPointer(
                        pos,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        12,
                        vertices
                );

                GLES20.glUniformMatrix4fv(
                        mat,
                        1,
                        false,
                        matrix,
                        0
                );

                GLES20.glUniform4f(
                        col,
                        r,
                        g,
                        b,
                        1
                );

                GLES20.glDrawArrays(
                        GLES20.GL_TRIANGLE_FAN,
                        0,
                        4
                );

                GLES20.glDisableVertexAttribArray(pos);
            }

            int loadShader(
                    int type,
                    String source) {

                int shader =
                        GLES20.glCreateShader(type);

                GLES20.glShaderSource(
                        shader,
                        source
                );

                GLES20.glCompileShader(shader);

                return shader;
            }
        }
    }
}
