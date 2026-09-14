package com.example.weapontester;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.content.Context;
import android.view.*;
import android.widget.*;
import android.animation.*;
import android.view.animation.DecelerateInterpolator;

public class MainActivity extends Activity {

    LinearLayout root;

    TextView weaponText;
    TextView ammoText;
    TextView targetText;
    TextView coinText;
    TextView levelText;

    ProgressBar levelBar;

    Button[] slots = new Button[10];

    String equippedWeapon = "Empty";

    int ammo = 100;
    int dummyHP = 999999;

    int coins = 0;

    int level = 1;
    int xp = 0;

    final int MAX_LEVEL = 30;
    final int XP_PER_LEVEL = 100;

    boolean revolverOwned = false;

    StickmanView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showMenu();
    }

    // =========================
    // MAIN MENU
    // =========================

    void showMenu() {

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(30, 30, 30, 30);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.rgb(235, 235, 235));

        TextView title = new TextView(this);

        title.setText("WEAPON TESTER");
        title.setTextSize(38);
        title.setTextColor(Color.DKGRAY);
        title.setGravity(Gravity.CENTER);

        root.addView(title, new LinearLayout.LayoutParams(
                -1, 130
        ));

        Button play = new Button(this);

        play.setText("PLAY");
        play.setTextSize(30);

        root.addView(play, new LinearLayout.LayoutParams(
                -1, 120
        ));

        Button shop = new Button(this);

        shop.setText("SHOP");
        shop.setTextSize(30);

        root.addView(shop, new LinearLayout.LayoutParams(
                -1, 120
        ));

        TextView coinsMenu = new TextView(this);

        coinsMenu.setText("Coins: " + coins);
        coinsMenu.setTextSize(25);
        coinsMenu.setTextColor(Color.rgb(180, 130, 0));
        coinsMenu.setGravity(Gravity.CENTER);

        root.addView(coinsMenu, new LinearLayout.LayoutParams(
                -1, 80
        ));

        play.setOnClickListener(v -> buildGame());

        shop.setOnClickListener(v -> openShop());

        setContentView(root);
    }

    // =========================
    // GAME
    // =========================

    void buildGame() {

        root = new LinearLayout(this);

        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(15, 15, 15, 15);

        root.setBackgroundColor(
                Color.rgb(235, 235, 235)
        );

        // TOP BAR

        FrameLayout topBar = new FrameLayout(this);

        levelText = new TextView(this);

        levelText.setText(
                "LEVEL " + level
        );

        levelText.setTextSize(20);
        levelText.setTextColor(Color.DKGRAY);
        levelText.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(
                        260, 55
                );

        lp.gravity =
                Gravity.TOP | Gravity.RIGHT;

        topBar.addView(levelText, lp);

        levelBar = new ProgressBar(
                this,
                null,
                android.R.attr.progressBarStyleHorizontal
        );

        levelBar.setMax(XP_PER_LEVEL);
        levelBar.setProgress(xp);

        FrameLayout.LayoutParams bp =
                new FrameLayout.LayoutParams(
                        260, 35
                );

        bp.gravity =
                Gravity.TOP | Gravity.RIGHT;

        bp.topMargin = 55;

        topBar.addView(levelBar, bp);

        root.addView(
                topBar,
                new LinearLayout.LayoutParams(
                        -1, 100
                )
        );

        // COINS

        coinText = new TextView(this);

        coinText.setText(
                "Coins: " + coins
        );

        coinText.setTextSize(23);
        coinText.setTextColor(
                Color.rgb(170, 120, 0)
        );

        coinText.setGravity(Gravity.CENTER);

        root.addView(
                coinText,
                new LinearLayout.LayoutParams(
                        -1, 60
                )
        );

        // WEAPON

        weaponText = new TextView(this);

        weaponText.setText(
                "Equipped: Empty"
        );

        weaponText.setTextSize(24);
        weaponText.setTextColor(Color.DKGRAY);
        weaponText.setGravity(Gravity.CENTER);

        root.addView(
                weaponText,
                new LinearLayout.LayoutParams(
                        -1, 75
                )
        );

        // AMMO

        ammoText = new TextView(this);

        ammoText.setText(
                "Ammo: 100"
        );

        ammoText.setTextSize(22);
        ammoText.setTextColor(Color.DKGRAY);
        ammoText.setGravity(Gravity.CENTER);

        root.addView(
                ammoText,
                new LinearLayout.LayoutParams(
                        -1, 55
                )
        );

        // GAME AREA

        FrameLayout gameArea =
                new FrameLayout(this);

        gameArea.setBackgroundColor(
                Color.rgb(225, 225, 225)
        );

        // LAMP

        LampView lamp =
                new LampView(this);

        gameArea.addView(
                lamp,
                new FrameLayout.LayoutParams(
                        -1, 260
                )
        );

        // PLAYER

        player =
                new StickmanView(this);

        FrameLayout.LayoutParams playerLP =
                new FrameLayout.LayoutParams(
                        -1, 300
                );

        playerLP.gravity =
                Gravity.CENTER;

        gameArea.addView(
                player,
                playerLP
        );

        // DUMMY

        DummyView dummy =
                new DummyView(this);

        FrameLayout.LayoutParams dummyLP =
                new FrameLayout.LayoutParams(
                        180, 260
                );

        dummyLP.gravity =
                Gravity.RIGHT | Gravity.CENTER_VERTICAL;

        dummyLP.rightMargin = 80;

        gameArea.addView(
                dummy,
                dummyLP
        );

        root.addView(
                gameArea,
                new LinearLayout.LayoutParams(
                        -1, 330
                )
        );

        // DUMMY HP

        targetText = new TextView(this);

        targetText.setText(
                "DUMMY HP: 999999"
        );

        targetText.setTextSize(22);
        targetText.setTextColor(Color.DKGRAY);
        targetText.setGravity(Gravity.CENTER);

        root.addView(
                targetText,
                new LinearLayout.LayoutParams(
                        -1, 60
                )
        );

        // FIRE

        Button fire =
                new Button(this);

        fire.setText("FIRE");
        fire.setTextSize(28);

        fire.setOnClickListener(
                v -> fireWeapon()
        );

        root.addView(
                fire,
                new LinearLayout.LayoutParams(
                        -1, 110
                )
        );

        // INVENTORY

        TextView inventoryTitle =
                new TextView(this);

        inventoryTitle.setText(
                "INVENTORY"
        );

        inventoryTitle.setTextSize(24);
        inventoryTitle.setTextColor(Color.DKGRAY);
        inventoryTitle.setGravity(Gravity.CENTER);

        root.addView(
                inventoryTitle,
                new LinearLayout.LayoutParams(
                        -1, 60
                )
        );

        GridLayout inventory =
                new GridLayout(this);

        inventory.setColumnCount(5);
        inventory.setRowCount(2);

        String[] weapons = {

                "MACHINE GUN",
                "FISHING ROD",
                "SHOTGUN",
                "RIFLE",
                "LASER",

                "SMG",
                "SNIPER",
                "ROCKET",
                "PLASMA",
                "EMPTY"
        };

        for (int i = 0; i < 10; i++) {

            final int slot = i;

            slots[i] =
                    new Button(this);

            slots[i].setText(
                    "SLOT " +
                    (i + 1) +
                    "\n" +
                    weapons[i]
            );

            slots[i].setTextSize(14);

            GridLayout.LayoutParams p =
                    new GridLayout.LayoutParams();

            p.width = 0;
            p.height = 115;

            p.columnSpec =
                    GridLayout.spec(
                            i % 5, 1f
                    );

            p.rowSpec =
                    GridLayout.spec(
                            i / 5, 1f
                    );

            inventory.addView(
                    slots[i], p
            );

            slots[i].setOnClickListener(
                    v -> equipWeapon(
                            weapons[slot],
                            slot
                    )
            );
        }

        root.addView(
                inventory,
                new LinearLayout.LayoutParams(
                        -1, 250
                )
        );

        // CONTROLS

        LinearLayout controls =
                new LinearLayout(this);

        controls.setGravity(
                Gravity.CENTER
        );

        Button left =
                new Button(this);

        left.setText("◀");
        left.setTextSize(32);

        Button jump =
                new Button(this);

        jump.setText("▲");
        jump.setTextSize(32);

        Button right =
                new Button(this);

        right.setText("▶");
        right.setTextSize(32);

        controls.addView(
                left,
                new LinearLayout.LayoutParams(
                        0, 100, 1
                )
        );

        controls.addView(
                jump,
                new LinearLayout.LayoutParams(
                        0, 100, 1
                )
        );

        controls.addView(
                right,
                new LinearLayout.LayoutParams(
                        0, 100, 1
                )
        );

        root.addView(controls);

        jump.setOnClickListener(
                v -> player.jump()
        );

        setContentView(root);
    }

    // =========================
    // EQUIP
    // =========================

    void equipWeapon(
            String weapon,
            int slot) {

        if (weapon.equals("REVOLVER")
                && !revolverOwned) {

            weaponText.setText(
                    "Buy the Revolver first!"
            );

            return;
        }

        equippedWeapon = weapon;

        if (weapon.equals("EMPTY")) {

            weaponText.setText(
                    "Equipped: Empty"
            );

            ammo = 0;

        } else if (
                weapon.equals("FISHING ROD")) {

            weaponText.setText(
                    "Equipped: FISHING ROD\n" +
                    "Skill 1: CAST"
            );

            ammo = 0;

        } else {

            weaponText.setText(
                    "Equipped: " +
                    weapon +
                    "\nSlot " +
                    (slot + 1)
            );

            ammo = 100;
        }

        updateAmmo();

        for (int i = 0; i < 10; i++) {

            slots[i].setTextColor(
                    Color.DKGRAY
            );
        }

        slots[slot].setTextColor(
                Color.rgb(200, 130, 0)
        );
    }

    // =========================
    // FIRE
    // =========================

    void fireWeapon() {

        if (equippedWeapon.equals(
                "FISHING ROD")) {

            weaponText.setText(
                    "FISHING ROD\n" +
                    "Skill 1: CAST\n" +
                    "CAST!"
            );

            targetText.setText(
                    "Fishing line cast!"
            );

            return;
        }

        if (equippedWeapon.equals(
                "Empty")) {

            weaponText.setText(
                    "Equip a weapon first!"
            );

            return;
        }

        if (ammo <= 0) {

            ammoText.setText(
                    "OUT OF AMMO"
            );

            return;
        }

        ammo--;

        int damage = 10;

        if (equippedWeapon.equals(
                "REVOLVER"))
            damage = 40;

        if (equippedWeapon.equals(
                "SHOTGUN"))
            damage = 30;

        if (equippedWeapon.equals(
                "SNIPER"))
            damage = 80;

        if (equippedWeapon.equals(
                "ROCKET"))
            damage = 100;

        hitDummy(damage);

        updateAmmo();
    }

    // =========================
    // DUMMY
    // =========================

    void hitDummy(int damage) {

        dummyHP -= damage;

        if (dummyHP < 0)
            dummyHP = 0;

        coins += 50;

        addXP(25);

        coinText.setText(
                "Coins: " + coins
        );

        targetText.setText(
                "DUMMY HP: " +
                dummyHP
        );
    }

    // =========================
    // XP
    // =========================

    void addXP(int amount) {

        if (level >= MAX_LEVEL)
            return;

        xp += amount;

        while (
                xp >= XP_PER_LEVEL &&
                level < MAX_LEVEL) {

            xp -= XP_PER_LEVEL;

            level++;

            Toast.makeText(
                    this,
                    "LEVEL UP! Level " +
                    level,
                    Toast.LENGTH_SHORT
            ).show();
        }

        if (level >= MAX_LEVEL) {

            level = MAX_LEVEL;
            xp = XP_PER_LEVEL;
        }

        levelText.setText(
                "LEVEL " + level
        );

        levelBar.setProgress(
                xp
        );
    }

    // =========================
    // SHOP
    // =========================

    void openShop() {

        root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                30, 30, 30, 30
        );

        root.setBackgroundColor(
                Color.rgb(
                        235, 235, 235
                )
        );

        TextView title =
                new TextView(this);

        title.setText(
                "WEAPON SHOP"
        );

        title.setTextSize(34);
        title.setTextColor(Color.DKGRAY);
        title.setGravity(
                Gravity.CENTER
        );

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        -1, 100
                )
        );

        TextView coinsView =
                new TextView(this);

        coinsView.setText(
                "Coins: " + coins
        );

        coinsView.setTextSize(25);
        coinsView.setTextColor(
                Color.rgb(170, 120, 0)
        );

        coinsView.setGravity(
                Gravity.CENTER
        );

        root.addView(
                coinsView,
                new LinearLayout.LayoutParams(
                        -1, 70
                )
        );

        Button revolver =
                new Button(this);

        revolver.setText(
                "REVOLVER\n" +
                "Damage: 40\n" +
                "Price: 300 Coins"
        );

        revolver.setTextSize(20);

        root.addView(
                revolver,
                new LinearLayout.LayoutParams(
                        -1, 160
                )
        );

        if (revolverOwned) {

            revolver.setText(
                    "REVOLVER\n" +
                    "OWNED\n" +
                    "Damage: 40"
            );
        }

        revolver.setOnClickListener(
                v -> {

                    if (revolverOwned) {

                        Toast.makeText(
                                this,
                                "Already owned!",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    if (coins >= 300) {

                        coins -= 300;

                        revolverOwned = true;

                        coinsView.setText(
                                "Coins: " +
                                coins
                        );

                        revolver.setText(
                                "REVOLVER\n" +
                                "OWNED\n" +
                                "Damage: 40"
                        );

                        Toast.makeText(
                                this,
                                "Revolver purchased!",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                this,
                                "Need 300 coins!",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        Button back =
                new Button(this);

        back.setText("BACK");
        back.setTextSize(25);

        root.addView(
                back,
                new LinearLayout.LayoutParams(
                        -1, 100
                )
        );

        back.setOnClickListener(
                v -> showMenu()
        );

        setContentView(root);
    }

    void updateAmmo() {

        ammoText.setText(
                "Ammo: " + ammo
        );
    }

    // =========================
    // ORANGE STICKMAN
    // =========================

    class StickmanView extends View {

        Paint paint =
                new Paint(
                        Paint.ANTI_ALIAS_FLAG
                );

        boolean jumping = false;

        float jumpOffset = 0;

        StickmanView(Context context) {

            super(context);

            paint.setStrokeCap(
                    Paint.Cap.ROUND
            );
        }

        void jump() {

            if (jumping)
                return;

            jumping = true;

            ValueAnimator animator =
                    ValueAnimator.ofFloat(
                            0, 1, 0
                    );

            animator.setDuration(650);

            animator.setInterpolator(
                    new DecelerateInterpolator()
            );

            animator.addUpdateListener(
                    animation -> {

                        float t =
                                (float)
                                animation
                                .getAnimatedValue();

                        jumpOffset =
                                (float)
                                (
                                    Math.sin(
                                        t * Math.PI
                                    ) * 130
                                );

                        invalidate();
                    }
            );

            animator.addListener(
                    new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(
                                Animator animation) {

                            jumpOffset = 0;

                            jumping = false;

                            invalidate();
                        }
                    }
            );

            animator.start();
        }

        @Override
        protected void onDraw(
                Canvas canvas) {

            super.onDraw(canvas);

            float x =
                    getWidth() / 2f;

            float y =
                    getHeight() / 2f
                    - jumpOffset;

            paint.setColor(
                    Color.rgb(
                            255, 140, 0
                    )
            );

            paint.setStyle(
                    Paint.Style.STROKE
            );

            paint.setStrokeWidth(12);

            // HEAD

            canvas.drawCircle(
                    x,
                    y - 90,
                    35,
                    paint
            );

            // BODY

            canvas.drawLine(
                    x,
                    y - 55,
                    x,
                    y + 45,
                    paint
            );

            // ARMS

            canvas.drawLine(
                    x,
                    y - 30,
                    x - 70,
                    y + 10,
                    paint
            );

            canvas.drawLine(
                    x,
                    y - 30,
                    x + 70,
                    y + 10,
                    paint
            );

            // LEGS

            canvas.drawLine(
                    x,
                    y + 45,
                    x - 55,
                    y + 110,
                    paint
            );

            canvas.drawLine(
                    x,
                    y + 45,
                    x + 55,
                    y + 110,
                    paint
            );
        }
    }

    // =========================
    // DUMMY VIEW
    // =========================

    class DummyView extends View {

        Paint paint =
                new Paint(
                        Paint.ANTI_ALIAS_FLAG
                );

        DummyView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(
                Canvas canvas) {

            float x =
                    getWidth() / 2f;

            paint.setColor(
                    Color.rgb(
                            120, 120, 120
                    )
            );

            paint.setStrokeWidth(15);

            // HEAD

            canvas.drawCircle(
                    x, 55, 35, paint
            );

            // BODY

            canvas.drawRect(
                    x - 45,
                    90,
                    x + 45,
                    190,
                    paint
            );

            // LEGS

            canvas.drawLine(
                    x - 20,
                    190,
                    x - 55,
                    245,
                    paint
            );

            canvas.drawLine(
                    x + 20,
                    190,
                    x + 55,
                    245,
                    paint
            );
        }
    }

    // =========================
    // LAMP
    // =========================

    class LampView extends View {

        Paint paint =
                new Paint(
                        Paint.ANTI_ALIAS_FLAG
                );

        LampView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(
                Canvas canvas) {

            float x =
                    getWidth() / 2f;

            // POLE

            paint.setColor(
                    Color.DKGRAY
            );

            paint.setStrokeWidth(12);

            canvas.drawLine(
                    x,
                    20,
                    x,
                    getHeight() - 20,
                    paint
            );

            // ARM

            canvas.drawLine(
                    x,
                    20,
                    x + 100,
                    20,
                    paint
            );

            // GLOW

            paint.setColor(
                    Color.rgb(
                            255, 245, 190
                    )
            );

            canvas.drawCircle(
                    x + 100,
                    75,
                    70,
                    paint
            );

            // BULB

            paint.setColor(
                    Color.rgb(
                            255, 220, 80
                    )
            );

            canvas.drawCircle(
                    x + 100,
                    35,
                    30,
                    paint
            );
        }
    }
}
