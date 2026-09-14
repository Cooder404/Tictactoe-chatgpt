package com.example.doomai;

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
        setContentView(new DoomGame(this));
    }

    public static class DoomGame extends View {

        Paint p = new Paint();
        Random random = new Random();

        float px = 3.5f;
        float py = 3.5f;
        float angle = 0;

        int hp = 100;
        int ammo = 50;
        int kills = 0;

        boolean gameOver = false;
        boolean victory = false;

        long lastShot = 0;

        int[][] map = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
            {1,0,1,1,1,0,1,0,1,1,1,0,1,0,1,1,1,1,0,1},
            {1,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,1},
            {1,0,1,0,1,1,1,1,1,0,1,1,1,0,1,0,1,1,0,1},
            {1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,1},
            {1,1,1,1,1,1,1,0,1,1,1,0,1,1,1,0,1,0,1,1},
            {1,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,1},
            {1,0,1,1,1,1,1,1,1,0,1,1,1,0,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,0,1},
            {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,1},
            {1,0,1,1,1,0,1,1,1,1,1,1,1,1,0,1,0,1,0,1},
            {1,0,0,0,1,0,0,0,0,0,0,0,0,1,0,1,0,0,0,1},
            {1,1,1,0,1,1,1,1,1,1,1,1,0,1,0,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };

        ArrayList<Enemy> enemies = new ArrayList<>();

        // Finger states
        HashSet<Integer> moveFingers = new HashSet<>();
        HashSet<Integer> fireFingers = new HashSet<>();
        HashSet<Integer> turnLFingers = new HashSet<>();
        HashSet<Integer> turnRFingers = new HashSet<>();
        HashSet<Integer> jumpFingers = new HashSet<>();

        public DoomGame(Context c) {
            super(c);

            p.setAntiAlias(false);

            spawn(5.5f, 3.5f, 0);
            spawn(10.5f, 3.5f, 1);
            spawn(16.5f, 3.5f, 0);
            spawn(4.5f, 7.5f, 1);
            spawn(11.5f, 7.5f, 0);
            spawn(17.5f, 9.5f, 1);
            spawn(5.5f, 13.5f, 0);
            spawn(15.5f, 15.5f, 1);
        }

        void spawn(float x, float y, int type) {
            Enemy e = new Enemy();
            e.x = x;
            e.y = y;
            e.type = type;
            e.hp = type == 0 ? 45 : 75;
            enemies.add(e);
        }

        @Override
        protected void onDraw(Canvas c) {

            int w = getWidth();
            int h = getHeight();

            if (gameOver) {
                drawEnd(c, w, h, false);
                postInvalidateDelayed(16);
                return;
            }

            if (victory) {
                drawEnd(c, w, h, true);
                postInvalidateDelayed(16);
                return;
            }

            drawWorld(c, w, h);
            drawWeapon(c, w, h);
            drawHUD(c, w, h);
            drawControls(c, w, h);

            update();

            postInvalidateDelayed(16);
        }

        // ---------------- WORLD ----------------

        void drawWorld(Canvas c, int w, int h) {

            p.setColor(Color.rgb(35, 40, 60));
            c.drawRect(0, 0, w, h / 2, p);

            p.setColor(Color.rgb(40, 35, 35));
            c.drawRect(0, h / 2, w, h, p);

            float fov = (float)Math.toRadians(70);

            for (int x = 0; x < w; x += 3) {

                float ra =
                    angle - fov / 2 +
                    fov * x / (float)w;

                float rx = (float)Math.cos(ra);
                float ry = (float)Math.sin(ra);

                float dist = 0;

                while (dist < 18) {

                    dist += .035f;

                    int mx = (int)(px + rx * dist);
                    int my = (int)(py + ry * dist);

                    if (mx < 0 ||
                        my < 0 ||
                        mx >= 20 ||
                        my >= 20) {
                        break;
                    }

                    if (map[my][mx] == 1)
                        break;
                }

                float corrected =
                    dist *
                    (float)Math.cos(ra - angle);

                if (corrected < .1f)
                    corrected = .1f;

                int height =
                    (int)(h * .8f / corrected);

                if (height > h)
                    height = h;

                int top =
                    h / 2 - height / 2;

                int bottom =
                    h / 2 + height / 2;

                int shade =
                    (int)(170 / (1 + corrected * .08f));

                if (shade < 35)
                    shade = 35;

                p.setColor(Color.rgb(
                    shade,
                    shade / 2,
                    shade / 3
                ));

                c.drawRect(
                    x,
                    top,
                    x + 3,
                    bottom,
                    p
                );
            }

            drawEnemies(c, w, h);
        }

        void drawEnemies(Canvas c, int w, int h) {

            for (Enemy e : enemies) {

                if (e.dead)
                    continue;

                float dx = e.x - px;
                float dy = e.y - py;

                float dist =
                    (float)Math.sqrt(
                        dx * dx + dy * dy
                    );

                if (dist > 15)
                    continue;

                float a =
                    (float)Math.atan2(dy, dx);

                float relative =
                    normalize(a - angle);

                float fov =
                    (float)Math.toRadians(70);

                if (Math.abs(relative) > fov / 2)
                    continue;

                float sx =
                    w / 2f +
                    relative / fov * w;

                float size =
                    h * .7f / dist;

                p.setColor(
                    e.type == 0
                        ? Color.rgb(190,45,45)
                        : Color.rgb(70,190,70)
                );

                c.drawCircle(
                    sx,
                    h / 2f,
                    size * .28f,
                    p
                );

                p.setColor(Color.DKGRAY);

                c.drawRect(
                    sx - size * .23f,
                    h / 2f,
                    sx + size * .23f,
                    h / 2f + size * .45f,
                    p
                );

                p.setColor(Color.YELLOW);

                c.drawCircle(
                    sx - size * .09f,
                    h / 2f - size * .06f,
                    size * .04f,
                    p
                );

                c.drawCircle(
                    sx + size * .09f,
                    h / 2f - size * .06f,
                    size * .04f,
                    p
                );
            }
        }

        // ---------------- WEAPON ----------------

        void drawWeapon(Canvas c, int w, int h) {

            p.setColor(Color.DKGRAY);

            c.drawRect(
                w / 2 - 70,
                h - 160,
                w / 2 + 70,
                h,
                p
            );

            p.setColor(Color.GRAY);

            c.drawRect(
                w / 2 - 20,
                h - 245,
                w / 2 + 20,
                h - 100,
                p
            );
        }

        // ---------------- HUD ----------------

        void drawHUD(Canvas c, int w, int h) {

            p.setColor(Color.argb(190, 0, 0, 0));

            c.drawRoundRect(
                15, 15, 300, 115,
                15, 15, p
            );

            p.setColor(Color.WHITE);
            p.setTextSize(22);

            c.drawText(
                "HEALTH: " + hp,
                30, 45, p
            );

            c.drawText(
                "AMMO: " + ammo,
                30, 73, p
            );

            c.drawText(
                "KILLS: " + kills +
                "/" + enemies.size(),
                30, 101, p
            );
        }

        // =========================================================
        // EASY TABLET CONTROLS
        // =========================================================

        void drawControls(Canvas c, int w, int h) {

            float s = Math.max(
                105,
                Math.min(150, w * .14f)
            );

            float gap = 18;

            float bottom = h - 25;

            // LEFT MOVEMENT

            button(
                c,
                25,
                bottom - s,
                s,
                "◀"
            );

            button(
                c,
                25 + s + gap,
                bottom - s,
                s,
                "▶"
            );

            // RIGHT SIDE

            button(
                c,
                w - s - 25,
                bottom - s * 2 - gap,
                s,
                "JUMP"
            );

            button(
                c,
                w - s - 25,
                bottom - s,
                s,
                "FIRE"
            );

            button(
                c,
                w - s * 2 - gap - 25,
                bottom - s,
                s,
                "TURN"
            );
        }

        void button(
            Canvas c,
            float x,
            float y,
            float s,
            String text) {

            p.setColor(Color.argb(
                170, 25, 25, 25
            ));

            c.drawRoundRect(
                x, y,
                x + s,
                y + s,
                28, 28, p
            );

            p.setColor(Color.WHITE);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(4);

            c.drawRoundRect(
                x, y,
                x + s,
                y + s,
                28, 28, p
            );

            p.setStyle(Paint.Style.FILL);

            p.setTextSize(
                text.length() <= 2
                    ? s * .42f
                    : s * .19f
            );

            float tw = p.measureText(text);

            c.drawText(
                text,
                x + (s - tw) / 2,
                y + s * .61f,
                p
            );
        }

        // =========================================================
        // GAME UPDATE
        // =========================================================

        void update() {

            float speed = .045f;

            float dx = 0;
            float dy = 0;

            if (!moveFingers.isEmpty()) {

                dx +=
                    Math.cos(angle) * speed;

                dy +=
                    Math.sin(angle) * speed;
            }

            if (!turnLFingers.isEmpty())
                angle -= .045f;

            if (!turnRFingers.isEmpty())
                angle += .045f;

            move(dx, dy);

            if (!fireFingers.isEmpty()) {

                long now =
                    System.currentTimeMillis();

                if (now - lastShot > 280) {

                    shoot();

                    lastShot = now;
                }
            }

            enemyAI();

            if (kills >= enemies.size())
                victory = true;
        }

        void move(float dx, float dy) {

            if (!wall(px + dx, py))
                px += dx;

            if (!wall(px, py + dy))
                py += dy;
        }

        boolean wall(float x, float y) {

            int mx = (int)x;
            int my = (int)y;

            if (mx < 0 ||
                my < 0 ||
                mx >= 20 ||
                my >= 20)
                return true;

            return map[my][mx] == 1;
        }

        // =========================================================
        // SHOOT
        // =========================================================

        void shoot() {

            if (ammo <= 0)
                return;

            ammo--;

            Enemy target = null;
            float closest = 999;

            for (Enemy e : enemies) {

                if (e.dead)
                    continue;

                float dx = e.x - px;
                float dy = e.y - py;

                float dist =
                    (float)Math.sqrt(
                        dx * dx + dy * dy
                    );

                float enemyAngle =
                    (float)Math.atan2(
                        dy, dx
                    );

                float difference =
                    Math.abs(
                        normalize(
                            enemyAngle - angle
                        )
                    );

                if (difference <
                    Math.toRadians(8) &&
                    dist < closest &&
                    clearShot(e)) {

                    target = e;
                    closest = dist;
                }
            }

            if (target != null) {

                target.hp -= 25;

                if (target.hp <= 0) {

                    target.dead = true;
                    kills++;
                }
            }
        }

        boolean clearShot(Enemy e) {

            float dx = e.x - px;
            float dy = e.y - py;

            float dist =
                (float)Math.sqrt(
                    dx * dx + dy * dy
                );

            int steps =
                (int)(dist * 20);

            for (int i = 1; i < steps; i++) {

                float t =
                    i / (float)steps;

                float x = px + dx * t;
                float y = py + dy * t;

                if (wall(x, y))
                    return false;
            }

            return true;
        }

        // =========================================================
        // HARD AI
        // =========================================================

        void enemyAI() {

            for (Enemy e : enemies) {

                if (e.dead)
                    continue;

                float dx = px - e.x;
                float dy = py - e.y;

                float dist =
                    (float)Math.sqrt(
                        dx * dx + dy * dy
                    );

                if (dist > 12)
                    continue;

                if (dist > 1.25f) {

                    float nx = dx / dist;
                    float ny = dy / dist;

                    float strafe =
                        (float)Math.sin(
                            System.currentTimeMillis()
                            / 250.0 +
                            e.x
                        );

                    float speed =
                        e.type == 0
                            ? .018f
                            : .024f;

                    float mx =
                        nx * speed -
                        ny * strafe * speed;

                    float my =
                        ny * speed +
                        nx * strafe * speed;

                    if (!wall(e.x + mx, e.y))
                        e.x += mx;

                    if (!wall(e.x, e.y + my))
                        e.y += my;
                }

                if (dist < 1.4f &&
                    clearShot(e)) {

                    long now =
                        System.currentTimeMillis();

                    if (now - e.lastAttack > 700) {

                        hp -=
                            e.type == 0
                                ? 7
                                : 11;

                        e.lastAttack = now;

                        if (hp <= 0)
                            gameOver = true;
                    }
                }
            }
        }

        float normalize(float a) {

            while (a > Math.PI)
                a -= Math.PI * 2;

            while (a < -Math.PI)
                a += Math.PI * 2;

            return a;
        }

        // =========================================================
        // MULTI-TOUCH
        // =========================================================

        @Override
        public boolean onTouchEvent(MotionEvent e) {

            int action =
                e.getActionMasked();

            int index =
                e.getActionIndex();

            int id =
                e.getPointerId(index);

            if (action ==
                MotionEvent.ACTION_DOWN ||
                action ==
                MotionEvent.ACTION_POINTER_DOWN) {

                checkPointer(
                    e.getX(index),
                    e.getY(index),
                    id
                );
            }

            if (action ==
                MotionEvent.ACTION_UP ||
                action ==
                MotionEvent.ACTION_POINTER_UP ||
                action ==
                MotionEvent.ACTION_CANCEL) {

                moveFingers.remove(id);
                fireFingers.remove(id);
                turnLFingers.remove(id);
                turnRFingers.remove(id);
                jumpFingers.remove(id);
            }

            if (action ==
                MotionEvent.ACTION_MOVE) {

                // Re-check every finger.
                moveFingers.clear();
                fireFingers.clear();
                turnLFingers.clear();
                turnRFingers.clear();
                jumpFingers.clear();

                for (int i = 0;
                     i < e.getPointerCount();
                     i++) {

                    checkPointer(
                        e.getX(i),
                        e.getY(i),
                        e.getPointerId(i)
                    );
                }
            }

            return true;
        }

        void checkPointer(
            float x,
            float y,
            int id) {

            int w = getWidth();
            int h = getHeight();

            float s = Math.max(
                105,
                Math.min(150, w * .14f)
            );

            float bottom = h - 25;
            float gap = 18;

            // left

            if (x >= 15 &&
                x <= 35 + s &&
                y >= bottom - s) {

                moveFingers.add(id);
            }

            // right

            if (x >= 20 + s &&
                x <= 55 + s * 2 &&
                y >= bottom - s) {

                turnRFingers.add(id);
            }

            // jump

            if (x >= w - s - 35 &&
                x <= w - 10 &&
                y >= bottom -
                    s * 2 -
                    gap &&
                y <= bottom -
                    s -
                    gap) {

                jumpFingers.add(id);
            }

            // fire

            if (x >= w - s - 35 &&
                x <= w - 10 &&
                y >= bottom - s) {

                fireFingers.add(id);
            }

            // turn

            if (x >= w -
                    s * 2 -
                    gap -
                    35 &&
                x <= w -
                    s -
                    gap -
                    15 &&
                y >= bottom - s) {

                turnRFingers.add(id);
            }
        }

        // =========================================================
        // END
        // =========================================================

        void drawEnd(
            Canvas c,
            int w,
            int h,
            boolean win) {

            c.drawColor(Color.BLACK);

            p.setColor(
                win
                    ? Color.YELLOW
                    : Color.RED
            );

            p.setTextSize(48);

            String text =
                win
                    ? "LEVEL CLEARED"
                    : "GAME OVER";

            c.drawText(
                text,
                w / 2 -
                    p.measureText(text) / 2,
                h / 2,
                p
            );

            p.setColor(Color.WHITE);
            p.setTextSize(22);

            String score =
                "KILLS: " + kills;

            c.drawText(
                score,
                w / 2 -
                    p.measureText(score) / 2,
                h / 2 + 50,
                p
            );
        }

        static class Enemy {

            float x;
            float y;

            int type;
            int hp;

            boolean dead = false;

            long lastAttack = 0;
        }
    }
}
