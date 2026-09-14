package com.example.tictactoe;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import android.content.*;
import java.util.*;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(new SurvivalGame(this));
    }

    public static class SurvivalGame extends View {

        Paint p = new Paint();
        Random random = new Random();

        float playerX = 500;
        float playerY = 300;
        float velocityX = 0;
        float velocityY = 0;

        boolean left, right, jumping;
        boolean attacking;

        int health = 100;
        int hunger = 100;

        int wood = 0;
        int stone = 0;
        int coal = 0;
        int iron = 0;
        int food = 3;

        boolean grounded = false;

        float cameraX = 0;

        long gameTime = 0;

        ArrayList<Tree> trees = new ArrayList<>();
        ArrayList<Rock> rocks = new ArrayList<>();
        ArrayList<Enemy> enemies = new ArrayList<>();
        ArrayList<Block> blocks = new ArrayList<>();

        public SurvivalGame(Context context) {
            super(context);

            p.setAntiAlias(false);

            generateWorld();
        }

        void generateWorld() {

            // Trees
            for (int i = 0; i < 80; i++) {

                float x = 200 + random.nextInt(12000);

                trees.add(
                        new Tree(
                                x,
                                330
                        )
                );
            }

            // Rocks
            for (int i = 0; i < 70; i++) {

                float x = 300 + random.nextInt(12000);

                rocks.add(
                        new Rock(
                                x,
                                350
                        )
                );
            }

            // Enemies
            for (int i = 0; i < 30; i++) {

                float x = 800 + random.nextInt(11000);

                enemies.add(
                        new Enemy(
                                x,
                                330
                        )
                );
            }

            // underground blocks
            for (int x = 0; x < 13000; x += 40) {

                for (int y = 390; y < 800; y += 40) {

                    blocks.add(
                            new Block(
                                    x,
                                    y,
                                    random.nextInt(100)
                            )
                    );
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {

            int width = getWidth();
            int height = getHeight();

            gameTime++;

            drawSky(canvas, width, height);

            cameraX = playerX - width * 0.5f;

            if (cameraX < 0)
                cameraX = 0;

            canvas.save();

            canvas.translate(-cameraX, 0);

            drawTerrain(canvas);
            drawBlocks(canvas);
            drawTrees(canvas);
            drawRocks(canvas);
            drawEnemies(canvas);
            drawPlayer(canvas);

            canvas.restore();

            drawInterface(canvas, width, height);

            updateGame();

            postInvalidateDelayed(16);
        }

        void drawSky(Canvas c, int w, int h) {

            int cycle = (int)((gameTime / 5) % 240);

            if (cycle < 120) {

                p.setColor(Color.rgb(
                        90,
                        180,
                        240
                ));

            } else {

                p.setColor(Color.rgb(
                        20,
                        30,
                        65
                ));
            }

            c.drawRect(0, 0, w, h, p);

            // sun/moon

            if (cycle < 120) {

                p.setColor(Color.YELLOW);

                c.drawCircle(
                        w - 100,
                        90,
                        40,
                        p
                );

            } else {

                p.setColor(Color.WHITE);

                c.drawCircle(
                        w - 100,
                        90,
                        30,
                        p
                );
            }

            // clouds

            p.setColor(Color.WHITE);

            for (int i = 0; i < 8; i++) {

                float x =
                        (i * 250 + gameTime * 0.2f)
                                % (w + 300);

                c.drawCircle(
                        x,
                        130 + i % 3 * 35,
                        25,
                        p
                );

                c.drawCircle(
                        x + 25,
                        130 + i % 3 * 35,
                        30,
                        p
                );
            }
        }

        void drawTerrain(Canvas c) {

            // grass

            p.setColor(Color.rgb(
                    55,
                    170,
                    60
            ));

            c.drawRect(
                    0,
                    350,
                    13000,
                    390,
                    p
            );

            // dirt

            p.setColor(Color.rgb(
                    120,
                    80,
                    45
            ));

            c.drawRect(
                    0,
                    390,
                    13000,
                    900,
                    p
            );

            // water

            p.setColor(Color.rgb(
                    40,
                    150,
                    220
            ));

            c.drawRect(
                    1800,
                    350,
                    2600,
                    450,
                    p
            );

            // grass details

            p.setColor(Color.rgb(
                    30,
                    120,
                    40
            ));

            for (int x = 0; x < 13000; x += 35) {

                c.drawRect(
                        x,
                        345,
                        x + 5,
                        355,
                        p
                );
            }
        }

        void drawBlocks(Canvas c) {

            for (Block b : blocks) {

                if (b.x < cameraX - 100 ||
                        b.x > cameraX + getWidth() + 100)
                    continue;

                if (b.type < 65) {

                    p.setColor(Color.rgb(
                            105,
                            70,
                            40
                    ));

                } else if (b.type < 85) {

                    p.setColor(Color.GRAY);

                } else {

                    p.setColor(Color.DKGRAY);
                }

                c.drawRect(
                        b.x,
                        b.y,
                        b.x + 38,
                        b.y + 38,
                        p
                );
            }
        }

        void drawTrees(Canvas c) {

            for (Tree t : trees) {

                if (t.x < -500)
                    continue;

                p.setColor(Color.rgb(
                        100,
                        60,
                        30
                ));

                c.drawRect(
                        t.x - 12,
                        275,
                        t.x + 12,
                        350,
                        p
                );

                p.setColor(Color.rgb(
                        30,
                        135,
                        45
                ));

                c.drawCircle(
                        t.x,
                        245,
                        42,
                        p
                );

                c.drawCircle(
                        t.x - 30,
                        265,
                        30,
                        p
                );

                c.drawCircle(
                        t.x + 30,
                        265,
                        30,
                        p
                );
            }
        }

        void drawRocks(Canvas c) {

            for (Rock r : rocks) {

                if (r.x < -500)
                    continue;

                p.setColor(Color.GRAY);

                c.drawCircle(
                        r.x,
                        r.y,
                        22,
                        p
                );

                p.setColor(Color.LTGRAY);

                c.drawCircle(
                        r.x - 7,
                        r.y - 7,
                        5,
                        p
                );
            }
        }

        void drawEnemies(Canvas c) {

            for (Enemy e : enemies) {

                if (Math.abs(e.x - playerX) > 700)
                    continue;

                p.setColor(Color.rgb(
                        80,
                        210,
                        80
                ));

                c.drawCircle(
                        e.x,
                        e.y,
                        24,
                        p
                );

                p.setColor(Color.BLACK);

                c.drawCircle(
                        e.x - 8,
                        e.y - 6,
                        4,
                        p
                );

                c.drawCircle(
                        e.x + 8,
                        e.y - 6,
                        4,
                        p
                );
            }
        }

        void drawPlayer(Canvas c) {

            // legs

            p.setColor(Color.DKGRAY);

            c.drawRect(
                    playerX - 15,
                    playerY,
                    playerX - 3,
                    playerY + 30,
                    p
            );

            c.drawRect(
                    playerX + 3,
                    playerY,
                    playerX + 15,
                    playerY + 30,
                    p
            );

            // body

            p.setColor(Color.rgb(
                    40,
                    100,
                    220
            ));

            c.drawRect(
                    playerX - 18,
                    playerY - 45,
                    playerX + 18,
                    playerY,
                    p
            );

            // head

            p.setColor(Color.rgb(
                    245,
                    190,
                    140
            ));

            c.drawCircle(
                    playerX,
                    playerY - 62,
                    20,
                    p
            );

            // weapon

            if (attacking) {

                p.setColor(Color.LTGRAY);

                c.drawRect(
                        playerX + 15,
                        playerY - 35,
                        playerX + 65,
                        playerY - 28,
                        p
                );
            }
        }

        void updateGame() {

            // movement

            if (left)
                velocityX = -5;

            else if (right)
                velocityX = 5;

            else
                velocityX *= 0.75f;

            playerX += velocityX;

            // gravity

            velocityY += 0.65f;

            playerY += velocityY;

            if (playerY >= 350) {

                playerY = 350;
                velocityY = 0;

                grounded = true;
            }

            if (jumping && grounded) {

                velocityY = -13;

                grounded = false;
            }

            // attack

            if (attacking) {

                attackEnemies();

                attacking = false;
            }

            // enemies

            for (Enemy e : enemies) {

                if (e.x < -500)
                    continue;

                if (Math.abs(e.x - playerX) < 600) {

                    if (e.x < playerX)
                        e.x += 1.2f;
                    else
                        e.x -= 1.2f;

                    if (Math.abs(e.x - playerX) < 45) {

                        if (gameTime % 60 == 0)
                            health -= 5;
                    }
                }
            }

            // hunger

            if (gameTime % 300 == 0) {

                hunger--;

                if (hunger < 0)
                    hunger = 0;
            }

            if (hunger == 0 &&
                    gameTime % 60 == 0) {

                health--;
            }

            // respawn

            if (health <= 0) {

                health = 100;
                hunger = 100;

                playerX = 500;
                playerY = 300;
            }
        }

        void attackEnemies() {

            for (Enemy e : enemies) {

                if (Math.abs(e.x - playerX) < 100) {

                    e.x = -1000;
                }
            }
        }

        void mine() {

            for (Tree t : trees) {

                if (Math.abs(t.x - playerX) < 80) {

                    wood += 3;
                    t.x = -1000;

                    return;
                }
            }

            for (Rock r : rocks) {

                if (Math.abs(r.x - playerX) < 80) {

                    stone += 2;

                    r.x = -1000;

                    return;
                }
            }
        }

        void eat() {

            if (food > 0 &&
                    hunger < 100) {

                food--;

                hunger += 30;

                if (hunger > 100)
                    hunger = 100;
            }
        }

        void craft() {

            if (wood >= 4 &&
                    stone >= 2) {

                wood -= 4;
                stone -= 2;

                food++;
            }
        }

        void drawInterface(
                Canvas c,
                int w,
                int h) {

            // stats panel

            p.setColor(Color.argb(
                    190,
                    0,
                    0,
                    0
            ));

            c.drawRoundRect(
                    15,
                    15,
                    330,
                    135,
                    15,
                    15,
                    p
            );

            p.setColor(Color.WHITE);
            p.setTextSize(20);

            c.drawText(
                    "HP: " + health,
                    30,
                    42,
                    p
            );

            c.drawText(
                    "Hunger: " + hunger,
                    30,
                    68,
                    p
            );

            c.drawText(
                    "Wood: " + wood,
                    30,
                    94,
                    p
            );

            c.drawText(
                    "Stone: " + stone +
                            "  Food: " + food,
                    30,
                    120,
                    p
            );

            // BIG TABLET CONTROLS

            float size = Math.min(
                    110,
                    Math.max(80, w * 0.10f)
            );

            float gap = 18;

            float bottom = h - 25;

            // left

            drawButton(
                    c,
                    25,
                    bottom - size,
                    size,
                    "◀"
            );

            // right

            drawButton(
                    c,
                    25 + size + gap,
                    bottom - size,
                    size,
                    "▶"
            );

            // jump

            drawButton(
                    c,
                    w - size - 25,
                    bottom - size * 2 - gap,
                    size,
                    "JUMP"
            );

            // attack/mine

            drawButton(
                    c,
                    w - size - 25,
                    bottom - size,
                    size,
                    "MINE"
            );

            // use

            drawButton(
                    c,
                    w - size * 2 - gap - 25,
                    bottom - size,
                    size,
                    "USE"
            );

            // craft

            drawButton(
                    c,
                    w - size * 2 - gap - 25,
                    bottom - size * 2 - gap,
                    size,
                    "CRAFT"
            );
        }

        void drawButton(
                Canvas c,
                float x,
                float y,
                float size,
                String text) {

            p.setColor(Color.argb(
                    185,
                    35,
                    35,
                    35
            ));

            c.drawRoundRect(
                    x,
                    y,
                    x + size,
                    y + size,
                    25,
                    25,
                    p
            );

            p.setColor(Color.WHITE);

            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(3);

            c.drawRoundRect(
                    x,
                    y,
                    x + size,
                    y + size,
                    25,
                    25,
                    p
            );

            p.setStyle(Paint.Style.FILL);

            p.setTextSize(
                    text.length() <= 2
                            ? size * 0.42f
                            : size * 0.18f
            );

            float tw = p.measureText(text);

            c.drawText(
                    text,
                    x + (size - tw) / 2,
                    y + size * 0.60f,
                    p
            );
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            float x = event.getX();
            float y = event.getY();

            int action = event.getActionMasked();

            int w = getWidth();
            int h = getHeight();

            float size = Math.min(
                    110,
                    Math.max(80, w * 0.10f)
            );

            float bottom = h - 25;
            float gap = 18;

            if (action == MotionEvent.ACTION_DOWN ||
                    action == MotionEvent.ACTION_MOVE) {

                left = false;
                right = false;
                jumping = false;

                // LEFT

                if (x >= 25 &&
                        x <= 25 + size &&
                        y >= bottom - size)
                    left = true;

                // RIGHT

                if (x >= 25 + size + gap &&
                        x <= 25 + size * 2 + gap &&
                        y >= bottom - size)
                    right = true;

                // JUMP

                if (x >= w - size - 25 &&
                        x <= w - 25 &&
                        y >= bottom - size * 2 - gap &&
                        y <= bottom - size - gap)
                    jumping = true;

                // MINE / ATTACK

                if (x >= w - size - 25 &&
                        x <= w - 25 &&
                        y >= bottom - size) {

                    mine();
                    attacking = true;
                }

                // USE FOOD

                if (x >= w - size * 2 - gap - 25 &&
                        x <= w - size - gap - 25 &&
                        y >= bottom - size) {

                    eat();
                }

                // CRAFT

                if (x >= w - size * 2 - gap - 25 &&
                        x <= w - size - gap - 25 &&
                        y >= bottom - size * 2 - gap &&
                        y <= bottom - size - gap) {

                    craft();
                }
            }

            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_CANCEL) {

                left = false;
                right = false;
                jumping = false;
            }

            return true;
        }

        static class Tree {

            float x;
            float y;

            Tree(float x, float y) {
                this.x = x;
                this.y = y;
            }
        }

        static class Rock {

            float x;
            float y;

            Rock(float x, float y) {
                this.x = x;
                this.y = y;
            }
        }

        static class Enemy {

            float x;
            float y;

            Enemy(float x, float y) {
                this.x = x;
                this.y = y;
            }
        }

        static class Block {

            float x;
            float y;
            int type;

            Block(
                    float x,
                    float y,
                    int type) {

                this.x = x;
                this.y = y;
                this.type = type;
            }
        }
    }
}
