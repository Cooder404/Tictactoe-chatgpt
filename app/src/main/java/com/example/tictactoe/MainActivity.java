package com.example.sandboxgame;

import java.util.Random;

public class World {

    public static final int TILE_SIZE = 48;
    public final int width;
    public final int height;

    private final int[][] tiles;
    public final int[] surfaceHeight;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new int[width][height];
        this.surfaceHeight = new int[width];
        generate();
    }

    private void generate() {
        Random rnd = new Random(1337);
        int base = height / 2;

        for (int x = 0; x < width; x++) {
            double h = Math.sin(x * 0.08) * 3.0 + Math.sin(x * 0.21) * 1.5;
            int surface = base + (int) Math.round(h);
            surfaceHeight[x] = surface;

            for (int y = 0; y < height; y++) {
                int type;
                if (y < surface) {
                    type = Block.AIR;
                } else if (y == surface) {
                    type = Block.GRASS;
                } else if (y < surface + 4) {
                    type = Block.DIRT;
                } else {
                    type = Block.STONE;
                    if (y > surface + 6 && rnd.nextInt(100) < 6) {
                        type = Block.AIR;
                    }
                }
                tiles[x][y] = type;
            }
        }

        for (int x = 3; x < width - 3; x++) {
            if (rnd.nextInt(100) < 10) {
                int surface = surfaceHeight[x];
                int trunkTop = surface - (3 + rnd.nextInt(2));
                for (int ty = trunkTop; ty < surface; ty++) {
                    if (inBounds(x, ty)) tiles[x][ty] = Block.WOOD;
                }
                for (int lx = -1; lx <= 1; lx++) {
                    for (int ly = -1; ly <= 0; ly++) {
                        int bx = x + lx, by = trunkTop - 1 + ly;
                        if (inBounds(bx, by) && tiles[bx][by] == Block.AIR) {
                            tiles[bx][by] = Block.LEAVES;
                        }
                    }
                }
            }
        }
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public int getBlock(int x, int y) {
        if (!inBounds(x, y)) return Block.STONE;
        return tiles[x][y];
    }

    public void setBlock(int x, int y, int type) {
        if (inBounds(x, y)) tiles[x][y] = type;
    }

    public boolean isSolidAt(int x, int y) {
        return Block.isSolid(getBlock(x, y));
    }

    public int spawnSurfaceY(int x) {
        return inBounds(x, 0) ? surfaceHeight[Math.max(0, Math.min(width - 1, x))] : height / 2;
    }
}
