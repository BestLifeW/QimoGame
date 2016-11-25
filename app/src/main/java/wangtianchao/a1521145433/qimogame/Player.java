package wangtianchao.a1521145433.qimogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by TianChaoWang on 2016/11/25.
 * 玩家类
 */

public class Player {

    private Bitmap mBitmap;
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean isVisible;
    private CopyOnWriteArrayList<Bullet> mBullets;
    private int count;

    public Player(Bitmap bitmap) {
        super();
        this.mBitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
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

    public boolean isVisible() {
        return isVisible;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public CopyOnWriteArrayList<Bullet> getmBullets() {
        return mBullets;
    }

    public void setmBullets(CopyOnWriteArrayList<Bullet> mBullets) {
        this.mBullets = mBullets;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas locakCanvas) {
        if (isVisible) {
            locakCanvas.drawBitmap(mBitmap, x, y, null);
        }
        if (mBullets != null) {
            for (Bullet bullet : mBullets) {
                bullet.draw(locakCanvas);
            }
        }
    }

    //移动的方法
    public void move(float distanceX, float distanceY) {
        x += distanceX;
        y += distanceY;
    }


    //玩家逻辑
    public void logic() {
        if (count++ > 10) {
            fire(); //发射方法
            count = 0;
        }
        if (mBullets != null) {
            for (Bullet bullet : mBullets) {
                bullet.logic();
                if (bullet.getY() > getY()) {
                    bullet.setVisible(false);
                }
            }
        }
        outOfBounds();
    }


    private void fire() {

    }

    /*
    * 碰撞检测
    * */
    private void outOfBounds() {

        if (x < 0) {
            x = 0;
        } else if (x + width > 480) {
            x = 480 - width;
        }

        if (y < 0) {
            y = 0;
        } else if (y + height > 800) {
            y = 800 - height;
        }
    }
}
