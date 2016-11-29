package wangtianchao.a1521145433.qimogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by lovec on 2016/11/26.
 */


public class Sprite {

    private Bitmap aBitmap;
    private int x;
    private int y;
    private int width;
    private int height;
    private int speedX;
    private int speedY;
    private boolean isVisible;
    private Rect src;
    private Rect dst;
    private int[] frameX;
    private int[] frameY;
    private int frameIndex;
    private int frameNumber;
    private boolean isFire;
    //是否发火


    public void setFire(boolean fire) {
        isFire = fire;
    }

    public Sprite(Bitmap aBitmap) {
        this(aBitmap, aBitmap.getWidth(), aBitmap.getHeight());
    }

    public Sprite(Bitmap aBitmap, int width, int height) {
        this.aBitmap = aBitmap;
        this.width = width;
        this.height = height;
        src = new Rect();
        dst = new Rect();
        int w = aBitmap.getWidth() / width;
        int h = aBitmap.getHeight() / height;
        frameNumber = w * h;
        frameX = new int[frameNumber];
        frameY = new int[frameNumber];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                frameX[i * w + j] = width * j;
                frameY[i * w + j] = height * i;
            }

        }
    }

    public Bitmap getaBitmap() {
        return aBitmap;
    }

    public void setaBitmap(Bitmap aBitmap) {
        this.aBitmap = aBitmap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public int getSpeedX() {
        return speedX;
    }

    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }

    public int getSpeedY() {
        return speedY;
    }

    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }

    public int getFrameIndex() {
        return frameIndex;
    }

    public void setFrameIndex(int frameIndex) {
        this.frameIndex = frameIndex;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas lockCanvas) {
        if (isVisible) {
            src.set(frameX[frameIndex], frameY[frameIndex], frameX[frameIndex] + width, frameY[frameIndex] + height);
            dst.set(x, y, x + width, y + height);
            lockCanvas.drawBitmap(aBitmap, src, dst, null);
        }
    }

    public void move(float speedX, float speedY) {
        x += speedX;
        y += speedY;
    }

    public void logic() {
        move(speedX, speedY);
        outOfBounds();
    }

    public void outOfBounds() {
        if (x < 0 || x > 480 || y < 0 || y > 800) {
            setVisible(false);
        }
    }

    public boolean collisionWith(Sprite aSprite) {
        if (!isVisible || !aSprite.isVisible) {
            return false;
        }
        if (x < aSprite.x && x + width < aSprite.x) {
            return false;
        } else if (aSprite.x < x && aSprite.x + aSprite.width < x) {
            return false;
        } else if (y < aSprite.y && y + height < aSprite.y) {
            return false;
        } else if (aSprite.y < y && aSprite.y + aSprite.height < y) {
            return false;
        }
        return true;
    }

    public void nextFrame() {
        frameIndex = (frameIndex + 1) % frameNumber;
    }

}


