package com.example.tictactoe;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.content.*;
import android.widget.EditText;
import java.util.Random;

public class MainActivity extends Activity {

    GameView game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        game = new GameView(this);
        setContentView(game);
    }

    public static class GameView extends View {

        Paint paint = new Paint();
        Random random = new Random();

        final int AIR = 0;
        final int GRASS = 1;
        final int DIRT = 2;
        final int STONE = 3;

        int worldWidth = 80;
        int worldHeight = 35;

        int[][] world;

        float playerX = 10;
        float playerY = 5;
        float velocityY = 0;

        boolean left = false;
        boolean right = false;
        boolean onGround = false;

        boolean wardenSpawned = false;
        float wardenX = 0;
        float wardenY = 0;
        int wardenHealth = 1000;

        int selectedBlock = DIRT;

        float cameraX = 0;
        float cameraY = 0;

        int blockSize = 48;

        public GameView(Context context) {
            super(context);

            paint.setAntiAlias(false);
            generateWorld();
            setFocusable(true);
        }

        void generateWorld() {

            world = new int[worldWidth][worldHeight];

            int ground = 18;

            for (int x = 0; x < worldWidth; x++) {

                ground += random.nextInt(3) - 1;

                if (ground < 14) ground = 14;
                if (ground > 21) ground = 21;

                for (int y = ground; y < worldHeight; y++) {

                    if (y == ground) {
                        world[x][y] = GRASS;
                    } else if (y < ground + 4) {
                        world[x][y] = DIRT;
                    } else {
                        world[x][y] = STONE;
                    }
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);

            canvas.drawColor(Color.rgb(110, 190, 235));

            updatePlayer();
            updateCamera();

            drawWorld(canvas);
            drawPlayer(canvas);

            if (wardenSpawned) {
                updateWarden();
                drawWarden(canvas);
            }

            drawControls(canvas);
            drawHotbar(canvas);
            drawCheatButton(canvas);

            invalidate();
        }

        void drawWorld(Canvas canvas) {

            int startX = Math.max(
                    0,
                    (int)(cameraX / blockSize) - 1
            );

            int endX = Math.min(
                    worldWidth,
                    (int)((cameraX + getWidth()) / blockSize) + 2
            );

            int startY = Math.max(
                    0,
                    (int)(cameraY / blockSize) - 1
            );

            int endY = Math.min(
                    worldHeight,
                    (int)((cameraY + getHeight()) / blockSize) + 2
            );

            for (int x = startX; x < endX; x++) {

                for (int y = startY; y < endY; y++) {

                    int block = world[x][y];

                    if (block == AIR) continue;

                    float screenX =
                            x * blockSize - cameraX;

                    float screenY =
                            y * blockSize - cameraY;

                    if (block == GRASS) {

                        paint.setColor(
                                Color.rgb(120, 190, 70)
                        );

                        canvas.drawRect(
                                screenX,
                                screenY,
                                screenX + blockSize,
                                screenY + blockSize,
                                paint
                        );

                        paint.setColor(
                                Color.rgb(85, 145, 55)
                        );

                        canvas.drawRect(
                                screenX,
                                screenY + blockSize * 0.75f,
                                screenX + blockSize,
                                screenY + blockSize,
                                paint
                        );

                    } else if (block == DIRT) {

                        paint.setColor(
                                Color.rgb(130, 85, 45)
                        );

                        canvas.drawRect(
                                screenX,
                                screenY,
                                screenX + blockSize,
                                screenY + blockSize,
                                paint
                        );

                    } else if (block == STONE) {

                        paint.setColor(
                                Color.rgb(105, 105, 105)
                        );

                        canvas.drawRect(
                                screenX,
                                screenY,
                                screenX + blockSize,
                                screenY + blockSize,
                                paint
                        );
                    }

                    paint.setColor(
                            Color.argb(35, 0, 0, 0)
                    );

                    canvas.drawRect(
                            screenX,
                            screenY,
                            screenX + blockSize,
                            screenY + blockSize,
                            paint
                    );
                }
            }
        }

        void drawPlayer(Canvas canvas) {

            float x =
                    playerX * blockSize - cameraX;

            float y =
                    playerY * blockSize - cameraY;

            paint.setColor(
                    Color.rgb(60, 90, 200)
            );

            canvas.drawRect(
                    x + 5,
                    y + 5,
                    x + blockSize - 5,
                    y + blockSize * 1.8f,
                    paint
            );

            paint.setColor(
                    Color.rgb(230, 190, 150)
            );

            canvas.drawRect(
                    x + 9,
                    y,
                    x + blockSize - 9,
                    y + blockSize * 0.75f,
                    paint
            );
        }

        void updatePlayer() {

            float speed = 0.10f;

            if (left) {
                playerX -= speed;
            }

            if (right) {
                playerX += speed;
            }

            velocityY += 0.018f;

            if (velocityY > 0.35f) {
                velocityY = 0.35f;
            }

            moveVertical(velocityY);

            if (playerX < 0) {
                playerX = 0;
            }

            if (playerX > worldWidth - 1) {
                playerX = worldWidth - 1;
            }
        }

        void jumpPlayer() {

            if (onGround) {
                velocityY = -0.30f;
                onGround = false;
            }
        }

        void moveVertical(float amount) {

            float newY = playerY + amount;

            if (amount > 0) {

                int blockX =
                        (int)(playerX + 0.5f);

                int blockY =
                        (int)(newY + 1.8f);

                if (isSolid(blockX, blockY)) {

                    playerY =
                            blockY - 1.8f;

                    velocityY = 0;
                    onGround = true;

                } else {

                    playerY = newY;
                    onGround = false;
                }

            } else {

                int blockX =
                        (int)(playerX + 0.5f);

                int blockY =
                        (int)newY;

                if (isSolid(blockX, blockY)) {

                    playerY =
                            blockY + 1;

                    velocityY = 0;

                } else {

                    playerY = newY;
                }
            }
        }

        boolean isSolid(int x, int y) {

            if (x < 0 || x >= worldWidth) {
                return true;
            }

            if (y < 0) {
                return true;
            }

            if (y >= worldHeight) {
                return false;
            }

            return world[x][y] != AIR;
        }

        void updateCamera() {

            cameraX =
                    playerX * blockSize -
                    getWidth() / 2f;

            cameraY =
                    playerY * blockSize -
                    getHeight() / 2f;

            if (cameraX < 0) {
                cameraX = 0;
            }

            float maxX =
                    worldWidth * blockSize -
                    getWidth();

            if (cameraX > maxX) {
                cameraX = Math.max(0, maxX);
            }

            if (cameraY < 0) {
                cameraY = 0;
            }

            float maxY =
                    worldHeight * blockSize -
                    getHeight();

            if (cameraY > maxY) {
                cameraY = Math.max(0, maxY);
            }
        }

        void drawControls(Canvas canvas) {

            int bottom = getHeight() - 100;

            paint.setColor(
                    Color.argb(150, 30, 30, 30)
            );

            canvas.drawCircle(
                    100,
                    bottom,
                    65,
                    paint
            );

            canvas.drawCircle(
                    240,
                    bottom,
                    65,
                    paint
            );

            canvas.drawCircle(
                    getWidth() - 100,
                    bottom,
                    65,
                    paint
            );

            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            paint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText(
                    "<",
                    100,
                    bottom + 14,
                    paint
            );

            canvas.drawText(
                    ">",
                    240,
                    bottom + 14,
                    paint
            );

            canvas.drawText(
                    "^",
                    getWidth() - 100,
                    bottom + 14,
                    paint
            );
        }

        void drawHotbar(Canvas canvas) {

            int size = 65;

            int startX =
                    getWidth() / 2 - 97;

            for (int i = 0; i < 3; i++) {

                int x =
                        startX + i * size;

                paint.setColor(
                        Color.argb(200, 40, 40, 40)
                );

                canvas.drawRect(
                        x,
                        getHeight() - 180,
                        x + size,
                        getHeight() - 115,
                        paint
                );

                int block = i + 1;

                if (block == GRASS) {

                    paint.setColor(
                            Color.rgb(120, 190, 70)
                    );

                } else if (block == DIRT) {

                    paint.setColor(
                            Color.rgb(130, 85, 45)
                    );

                } else {

                    paint.setColor(
                            Color.rgb(105, 105, 105)
                    );
                }

                canvas.drawRect(
                        x + 10,
                        getHeight() - 170,
                        x + 55,
                        getHeight() - 125,
                        paint
                );

                if (selectedBlock == block) {

                    paint.setStyle(
                            Paint.Style.STROKE
                    );

                    paint.setStrokeWidth(4);

                    paint.setColor(Color.WHITE);

                    canvas.drawRect(
                            x,
                            getHeight() - 180,
                            x + size,
                            getHeight() - 115,
                            paint
                    );

                    paint.setStyle(
                            Paint.Style.FILL
                    );
                }
            }
        }

        void drawCheatButton(Canvas canvas) {

            paint.setColor(
                    Color.argb(190, 30, 30, 30)
            );

            canvas.drawRect(
                    20,
                    20,
                    190,
                    80,
                    paint
            );

            paint.setColor(Color.WHITE);
            paint.setTextSize(28);
            paint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText(
                    "CHEAT",
                    105,
                    60,
                    paint
            );
        }

        void spawnWarden() {

            wardenSpawned = true;

            wardenX =
                    Math.min(
                            worldWidth - 2,
                            playerX + 5
                    );

            wardenY = playerY;

            wardenHealth = 1000;
        }

        void updateWarden() {

            if (wardenX < playerX) {
                wardenX += 0.025f;
            }

            if (wardenX > playerX) {
                wardenX -= 0.025f;
            }

            int groundY =
                    worldHeight - 1;

            for (int y = 0;
                    y < worldHeight;
                    y++) {

                if (isSolid(
                        (int)wardenX,
                        y
                )) {

                    groundY = y;
                    break;
                }
            }

            wardenY =
                    groundY - 2;
        }

        void drawWarden(Canvas canvas) {

            float x =
                    wardenX * blockSize -
                    cameraX;

            float y =
                    wardenY * blockSize -
                    cameraY;

            paint.setColor(
                    Color.rgb(25, 30, 35)
            );

            canvas.drawRect(
                    x,
                    y + 25,
                    x + blockSize * 1.5f,
                    y + blockSize * 2.2f,
                    paint
            );

            paint.setColor(
                    Color.rgb(35, 40, 45)
            );

            canvas.drawRect(
                    x + 10,
                    y,
                    x + blockSize * 1.4f,
                    y + blockSize,
                    paint
            );

            paint.setColor(
                    Color.rgb(80, 180, 210)
            );

            canvas.drawRect(
                    x + 20,
                    y + 25,
                    x + 30,
                    y + 35,
                    paint
            );

            canvas.drawRect(
                    x + 45,
                    y + 25,
                    x + 55,
                    y + 35,
                    paint
            );

            paint.setColor(Color.DKGRAY);

            canvas.drawRect(
                    x,
                    y - 18,
                    x + blockSize * 1.5f,
                    y - 6,
                    paint
            );

            paint.setColor(Color.RED);

            canvas.drawRect(
                    x,
                    y - 18,
                    x + blockSize * 1.5f *
                            (wardenHealth / 1000f),
                    y - 6,
                    paint
            );

            paint.setColor(Color.WHITE);
            paint.setTextSize(20);
            paint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText(
                    "WARDEN",
                    x + blockSize * 0.75f,
                    y - 25,
                    paint
            );
        }

        void interactWorld(
                float screenX,
                float screenY
        ) {

            int worldX =
                    (int)((screenX + cameraX) /
                            blockSize);

            int worldY =
                    (int)((screenY + cameraY) /
                            blockSize);

            if (worldX < 0 ||
                    worldX >= worldWidth ||
                    worldY < 0 ||
                    worldY >= worldHeight) {

                return;
            }

            float centerX =
                    playerX + 0.5f;

            float centerY =
                    playerY + 0.9f;

            float dx =
                    worldX + 0.5f - centerX;

            float dy =
                    worldY + 0.5f - centerY;

            float distance =
                    (float)Math.sqrt(
                            dx * dx + dy * dy
                    );

            if (distance > 4.0f) {
                return;
            }

            if (world[worldX][worldY] != AIR) {

                world[worldX][worldY] =
                        AIR;

            } else {

                if (!isPlayerInsideBlock(
                        worldX,
                        worldY
                )) {

                    world[worldX][worldY] =
                            selectedBlock;
                }
            }
        }

        boolean isPlayerInsideBlock(
                int x,
                int y
        ) {

            return playerX < x + 1 &&
                    playerX + 0.8f > x &&
                    playerY < y + 1 &&
                    playerY + 1.8f > y;
        }

        void openCheatInput() {

            final EditText input =
                    new EditText(getContext());

            input.setSingleLine(true);
            input.setHint("Enter cheat code");

            android.app.AlertDialog dialog =
                    new android.app.AlertDialog.Builder(
                            getContext()
                    )
                    .setTitle("Cheat Code")
                    .setView(input)
                    .setNegativeButton(
                            "Cancel",
                            null
                    )
                    .setPositiveButton(
                            "Enter",
                            null
                    )
                    .create();

            dialog.setOnShowListener(
                    d -> {

                        dialog.getButton(
                                android.app.AlertDialog.BUTTON_POSITIVE
                        ).setOnClickListener(
                                v -> {

                                    String code =
                                            input.getText()
                                                    .toString()
                                                    .trim()
                                                    .toUpperCase();

                                    if (code.equals("XZ")) {

                                        spawnWarden();

                                        dialog.dismiss();
                                    }
                                }
                        );

                        input.requestFocus();

                        dialog.getWindow()
                                .setSoftInputMode(
                                        WindowManager.LayoutParams
                                                .SOFT_INPUT_STATE_ALWAYS_VISIBLE
                                );
                    }
            );

            dialog.show();
        }

        @Override
        public boolean onTouchEvent(
                MotionEvent event
        ) {

            float x = event.getX();
            float y = event.getY();

            if (event.getAction() ==
                    MotionEvent.ACTION_DOWN) {

                // Cheat button
                if (x >= 20 &&
                        x <= 190 &&
                        y >= 20 &&
                        y <= 80) {

                    openCheatInput();
                    return true;
                }

                // Hotbar
                if (y >= getHeight() - 190 &&
                        y <= getHeight() - 100) {

                    int startX =
                            getWidth() / 2 - 97;

                    for (int i = 0; i < 3; i++) {

                        int bx =
                                startX + i * 65;

                        if (x >= bx &&
                                x <= bx + 65) {

                            selectedBlock = i + 1;
                            return true;
                        }
                    }
                }

                // Controls
                if (y > getHeight() - 180) {

                    if (x < 165) {

                        left = true;

                    } else if (x < 310) {

                        right = true;

                    } else if (
                            x > getWidth() - 180
                    ) {

                        jumpPlayer();
                    }

                    return true;
                }

                // World interaction
                interactWorld(x, y);

                return true;
            }

            if (event.getAction() ==
                    MotionEvent.ACTION_UP ||
                    event.getAction() ==
                    MotionEvent.ACTION_CANCEL) {

                left = false;
                right = false;

                return true;
            }

            return true;
        }
    }
}
