package com.example.tictactoe;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.util.Random;

public class MainActivity extends Activity {

    GridLayout grid;
    TextView status;
    Button[] blocks = new Button[64];

    boolean[] mines = new boolean[64];
    boolean[] revealed = new boolean[64];

    int health = 3;
    int revealedCount = 0;

    Random random = new Random();

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        startGame();
    }

    void startGame() {
        health = 3;
        revealedCount = 0;

        for (int i = 0; i < 64; i++) {
            mines[i] = false;
            revealed[i] = false;
        }

        // Place 10 hidden mines
        int placed = 0;
        while (placed < 10) {
            int p = random.nextInt(64);

            if (!mines[p]) {
                mines[p] = true;
                placed++;
            }
        }

        buildUI();
    }

    void buildUI() {

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER_HORIZONTAL);
        page.setPadding(12, 20, 12, 12);
        page.setBackgroundColor(Color.rgb(90, 170, 75));

        TextView title = new TextView(this);
        title.setText("⛏️ MineCrafty Sweeper");
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        page.addView(title,
                new LinearLayout.LayoutParams(-1, 65));

        status = new TextView(this);
        status.setText("❤️❤️❤️   Find the mines!");
        status.setTextSize(19);
        status.setTextColor(Color.WHITE);
        status.setGravity(Gravity.CENTER);

        page.addView(status,
                new LinearLayout.LayoutParams(-1, 55));

        grid = new GridLayout(this);
        grid.setColumnCount(8);
        grid.setRowCount(8);

        int screenWidth =
                getResources().getDisplayMetrics().widthPixels;

        int size = Math.min(screenWidth - 30, 600);

        for (int i = 0; i < 64; i++) {

            final int index = i;

            Button block = new Button(this);

            blocks[i] = block;

            block.setText("?");
            block.setTextSize(15);
            block.setTextColor(Color.WHITE);
            block.setBackgroundColor(Color.rgb(110, 75, 45));

            GridLayout.LayoutParams lp =
                    new GridLayout.LayoutParams();

            lp.width = size / 8;
            lp.height = size / 8;

            lp.setMargins(2, 2, 2, 2);

            grid.addView(block, lp);

            block.setOnClickListener(v -> mineBlock(index));
        }

        page.addView(grid);

        Button reset = new Button(this);
        reset.setText("🌎 New World");
        reset.setTextSize(18);

        reset.setOnClickListener(v -> startGame());

        LinearLayout.LayoutParams rp =
                new LinearLayout.LayoutParams(-2, 65);

        rp.topMargin = 15;

        page.addView(reset, rp);

        setContentView(page);
    }

    void mineBlock(int index) {

        if (revealed[index]) return;

        revealed[index] = true;
        revealedCount++;

        Button block = blocks[index];

        if (mines[index]) {

            health--;

            block.setText("💣");
            block.setTextSize(22);
            block.setBackgroundColor(Color.RED);

            updateStatus();

            if (health <= 0) {
                gameOver();
            }

            return;
        }

        // Safe block
        int nearby = countNearbyMines(index);

        if (nearby == 0) {
            block.setText("🟩");
        } else {
            block.setText(String.valueOf(nearby));
        }

        block.setTextColor(Color.BLACK);
        block.setBackgroundColor(Color.rgb(170, 120, 70));

        updateStatus();

        if (revealedCount >= 54) {
            status.setText("🏆 YOU CLEARED THE WORLD!");
        }
    }

    int countNearbyMines(int index) {

        int count = 0;

        int row = index / 8;
        int col = index % 8;

        for (int r = -1; r <= 1; r++) {
            for (int c = -1; c <= 1; c++) {

                if (r == 0 && c == 0) continue;

                int nr = row + r;
                int nc = col + c;

                if (nr >= 0 && nr < 8 &&
                    nc >= 0 && nc < 8) {

                    int nearbyIndex = nr * 8 + nc;

                    if (mines[nearbyIndex]) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    void updateStatus() {

        String hearts = "";

        for (int i = 0; i < health; i++) {
            hearts += "❤️";
        }

        status.setText(
                hearts + "   Blocks mined: " + revealedCount
        );
    }

    void gameOver() {

        status.setText("💥 GAME OVER!");

        for (int i = 0; i < 64; i++) {

            if (mines[i]) {
                blocks[i].setText("💣");
                blocks[i].setBackgroundColor(Color.RED);
            }

            blocks[i].setEnabled(false);
        }
    }
            }
