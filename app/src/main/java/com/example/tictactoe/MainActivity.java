package com.example.tictactoe;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import android.content.*;
import java.util.*;

public class MainActivity extends Activity {

    SurvivalView game;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        game = new SurvivalView(this);
        setContentView(game);
    }

    public static class SurvivalView extends View {

        Paint p = new Paint();
        Random random = new Random();

        float px = 300, py = 300;
        float vx = 0, vy = 0;

        boolean left, right, jump, mine, craft, use;
        boolean grounded = false;

        int health = 100;
        int hunger = 100;
        int wood = 0;
        int stone = 0;
        int food = 2;

        float cameraX = 0;
        float cameraY = 0;

        long startTime;

        ArrayList<Tree> trees = new ArrayList<>();
        ArrayList<Rock> rocks = new ArrayList<>();
        ArrayList<Enemy> enemies = new ArrayList<>();

        public SurvivalView(Context c) {
            super(c);
            p.setAntiAlias(false);
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 35; i++) {
                trees.add(new Tree(
                        150 + random.nextInt(5000),
                        300 + random.nextInt(80)
                ));
            }

            for (int i = 0; i < 30; i++) {
                rocks.add(new Rock(
                        200 + random.nextInt(5000),
                        350
                ));
            }

            for (int i = 0; i < 10; i++) {
                enemies.add(new Enemy(
                        500 + random.nextInt(4000),
                        330
                ));
            }
        }

        @Override
        protected void onDraw(Canvas c) {
            super.onDraw(c);

            int w = getWidth();
            int h = getHeight();

            drawSky(c, w, h);

            cameraX = px - w / 2f;
            if (cameraX < 0) cameraX = 0;

            c.save();
            c.translate(-cameraX, 0);

            drawWorld(c, h);
            drawPlayer(c);
            drawEnemies(c);

            c.restore();

            drawUI(c, w, h);

            updateGame();

            postInvalidateDelayed(16);
        }

        void drawSky(Canvas c, int w, int h) {
            long seconds = (System.currentTimeMillis() - startTime) / 1000;
            int cycle = (int)(seconds % 120);

            if (cycle < 80)
                c.drawColor(Color.rgb(100, 190, 245));
            else
                c.drawColor(Color.rgb(25, 35, 70));

            p.setColor(Color.WHITE);

            if (cycle >= 80) {
                c.drawCircle(80, 80, 30, p);
            } else {
                p.setColor(Color.YELLOW);
                c.drawCircle(w - 80, 80, 35, p);
            }
        }

        void drawWorld(Canvas c, int h) {

            // ground
            p.setColor(Color.rgb(110, 75, 45));
            c.drawRect(0, 370, 6000, 1000, p);

            // grass
            p.setColor(Color.rgb(55, 170, 65));
            c.drawRect(0, 350, 6000, 370, p);

            // water
            p.setColor(Color.rgb(45, 150, 210));
            c.drawRect(1200, 350, 1900, 450, p);

            // trees
            for (Tree t : trees) {
                p.setColor(Color.rgb(100, 55, 25));
                c.drawRect(t.x - 10, 275, t.x + 10, 355, p);

                p.setColor(Color.rgb(35, 130, 45));
                c.drawCircle(t.x, 255, 38, p);
                c.drawCircle(t.x - 28, 270, 30, p);
                c.drawCircle(t.x + 28, 270, 30, p);
            }

            // rocks
            for (Rock r : rocks) {
                p.setColor(Color.GRAY);
                c.drawCircle(r.x, r.y, 20, p);
            }

            // ground blocks
            p.setColor(Color.rgb(80, 50, 30));

            for (int x = 0; x < 6000; x += 40) {
                c.drawRect(x, 370, x + 38, 408, p);
            }
        }

        void drawPlayer(Canvas c) {

            // body
            p.setColor(Color.rgb(40, 100, 220));
            c.drawRect(px - 16, py - 45, px + 16, py, p);

            // head
            p.setColor(Color.rgb(245, 190, 140));
            c.drawCircle(px, py - 62, 18, p);

            // legs
            p.setColor(Color.DKGRAY);
            c.drawRect(px - 14, py, px - 3, py + 28, p);
            c.drawRect(px + 3, py, px + 14, py + 28, p);
        }

        void drawEnemies(Canvas c) {

            for (Enemy e : enemies) {

                p.setColor(Color.rgb(80, 200, 80));

                c.drawCircle(e.x, e.y, 22, p);

                p.setColor(Color.BLACK);

                c.drawCircle(e.x - 7, e.y - 5, 3, p);
                c.drawCircle(e.x + 7, e.y - 5, 3, p);
            }
        }

        void updateGame() {

            // movement
            if (left)
                vx = -4;
            else if (right)
                vx = 4;
            else
                vx *= 0.8f;

            px += vx;

            // gravity
            vy += 0.7f;
            py += vy;

            if (py >= 350) {
                py = 350;
                vy = 0;
                grounded = true;
            }

            if (jump && grounded) {
                vy = -12;
                grounded = false;
            }

            if (px < 20)
                px = 20;

            // mining
            if (mine) {
                mineNearby();
                mine = false;
            }

            // crafting
            if (craft) {
                if (wood >= 3 && stone >= 2) {
                    wood -= 3;
                    stone -= 2;
                    food++;
                }
                craft = false;
            }

            // eat
            if (use) {
                if (food > 0 && hunger < 100) {
                    food--;
                    hunger += 30;

                    if (hunger > 100)
                        hunger = 100;
                }

                use = false;
            }

            // hunger
            if (System.currentTimeMillis() % 4000 < 20) {
                hunger--;

                if (hunger < 0)
                    hunger = 0;
            }

            if (hunger == 0 && System.currentTimeMillis() % 1000 < 20) {
                health--;

                if (health <= 0) {
                    health = 100;
                    hunger = 100;
                    px = 300;
                    wood = 0;
                    stone = 0;
                }
            }

            // enemies follow player
            for (Enemy e : enemies) {

                if (Math.abs(e.x - px) < 500) {

                    if (e.x < px)
                        e.x += 1.1f;
                    else
                        e.x -= 1.1f;

                    if (Math.abs(e.x - px) < 35) {
                        if (System.currentTimeMillis() % 1000 < 20)
                            health -= 5;
                    }
                }
            }
        }

        void mineNearby() {

            for (Tree t : trees) {

                if (Math.abs(t.x - px) < 70) {
                    wood += 2;
                    t.x = -1000;
                    return;
                }
            }

            for (Rock r : rocks) {

                if (Math.abs(r.x - px) < 70) {
                    stone += 2;
                    r.x = -1000;
                    return;
                }
            }
        }

        void drawUI(Canvas c, int w, int h) {

            p.setColor(Color.argb(190, 0, 0, 0));
            c.drawRect(10, 10, 250, 105, p);

            p.setColor(Color.WHITE);
            p.setTextSize(18);

            c.drawText("HP: " + health, 20, 35, p);
            c.drawText("Hunger: " + hunger, 20, 58, p);
            c.drawText("Wood: " + wood + "  Stone: " + stone, 20, 81, p);
            c.drawText("Food: " + food, 20, 101, p);

            // controls

            button(c, 25, h - 100, 85, h - 30, "LEFT");
            button(c, 95, h - 100, 155, h - 30, "RIGHT");

            button(c, w - 160, h - 150, w - 80, h - 80, "JUMP");
            button(c, w - 160, h - 75, w - 80, h - 5, "MINE");

            button(c, w - 75, h - 150, w - 5, h - 80, "USE");
            button(c, w - 75, h - 75, w - 5, h - 5, "CRAFT");
        }

        void button(Canvas c, float x1, float y1,
                    float x2, float y2, String text) {

            p.setColor(Color.argb(170, 40, 40, 40));
            c.drawRoundRect(x1, y1, x2, y2, 15, 15, p);

            p.setColor(Color.WHITE);
            p.setTextSize(13);

            float tw = p.measureText(text);

            c.drawText(
                    text,
                    (x1 + x2 - tw) / 2,
                    (y1 + y2) / 2 + 5,
                    p
            );
        }

        @Override
        public boolean onTouchEvent(android.view.MotionEvent e) {

            float x = e.getX();
            float y = e.getY();

            int action = e.getActionMasked();

            if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_MOVE) {

                left = false;
                right = false;
                jump = false;

                int h = getHeight();
                int w = getWidth();

                if (x >= 25 && x <= 85 && y >= h - 100)
                    left = true;

                if (x >= 95 && x <= 155 && y >= h - 100)
                    right = true;

                if (x >= w - 160 && x <= w - 80 &&
                    y >= h - 150 && y <= h - 80)
                    jump = true;

                if (x >= w - 160 && x <= w - 80 &&
                    y >= h - 75)
                    mine = true;

                if (x >= w - 75 && x <= w - 5 &&
                    y >= h - 150 && y <= h - 80)
                    use = true;

                if (x >= w - 75 && x <= w - 5 &&
                    y >= h - 75)
                    craft = true;
            }

            if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL) {

                left = false;
                right = false;
                jump = false;
            }

            return true;
        }

        static class Tree {
            float x, y;

            Tree(float x, float y) {
                this.x = x;
                this.y = y;
            }
        }

        static class Rock {
            float x, y;

            Rock(float x, float y) {
                this.x = x;
                this.y = y;
            }
        }

        static class Enemy {
            float x, y;

            Enemy(float x, float y) {
                this.x = x;
                this.y = y;
            }
        }
    }
}
