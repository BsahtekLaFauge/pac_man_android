package com.example.vfaugier.pacman.square;

import android.graphics.Bitmap;

/**
 * Created by vfaugier on 19/05/17.
 */

public class Block {
    private Bitmap sprite;
    private BlockType blockType;

    public Block(Bitmap sprite, BlockType blockType) {
        this.sprite = sprite;
        this.blockType = blockType;
    }

    public void setSprite(Bitmap sprite) {
        this.sprite = sprite;
    }

    public Bitmap getSprite() {
        return sprite;
    }

    public void setBlockType(BlockType blockType) {
        this.blockType = blockType;
    }

    public BlockType getBlockType() {
        return blockType;
    }
}
