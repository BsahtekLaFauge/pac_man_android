package com.example.vfaugier.pacman.square;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by vfaugier on 26/06/17.
 */

public class Square {

    private ArrayList<Block> blocks;
    private BlockType shownBlockType;
    private Bitmap actualSprite;
    private boolean isWall;

    public Square(ArrayList<Block> blocks) {
        this.blocks = blocks;
        updateSpriteAndBlockType();
    }

    private void updateSpriteAndBlockType() {
        BlockType blockType;
        shownBlockType = BlockType.EMPTY;
        for (Block block: this.blocks) {
            blockType = block.getBlockType();
            if (blockType == BlockType.WALL) {
                isWall = true;
                actualSprite = block.getSprite();
                shownBlockType = blockType;
                break;
            } else if (blockType == BlockType.PAC_MAN) {
                actualSprite = block.getSprite();
                shownBlockType = blockType;
                break;
            } else if (blockType == BlockType.GHOST) {
                actualSprite = block.getSprite();
                shownBlockType = blockType;
            } else if (blockType == BlockType.PAC_GUM && shownBlockType != BlockType.GHOST) {
                actualSprite = block.getSprite();
                shownBlockType = blockType;
            }
        }
        if (shownBlockType == BlockType.EMPTY) {
            actualSprite = null;
        }
    }

    public Block findBlockByType(BlockType blockType) {
        for (Block block: this.blocks) {
            if (block.getBlockType() == blockType) {
                return block;
            }
        }
        return null;
    }

    public BlockType getShownBlockType() {
        return shownBlockType;
    }

    public Bitmap getActualSprite() {
        return actualSprite;
    }

    public void addBlockToList(Block block) {
        blocks.add(block);
        updateSpriteAndBlockType();
    }

    public void removeBlockFromList(Block block) {
        blocks.remove(block);
        updateSpriteAndBlockType();
    }

    public void removePacGum() {
        for (Block block: blocks) {
            if (block.getBlockType() == BlockType.PAC_GUM) {
                removeBlockFromList(block);
                break;
            }
        }
    }
}
