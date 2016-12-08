package wangtianchao.a1521145433.qimogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by TianChaoWang on 2016/11/25.
 * 玩家类
 */

public class Player extends Sprite {


    private CopyOnWriteArrayList<Bullet> mBullets;
    private int count;
    private Blast aBlast;
    private int firecount = 5;

    public Player(Bitmap bitmap) {
        super(bitmap);

    }

    public Blast getaBlast() {
        return aBlast;
    }

    public void setaBlast(Blast aBlast) {
        this.aBlast = aBlast;
    }


    public void setBullets(CopyOnWriteArrayList<Bullet> mBullets) {
        this.mBullets = mBullets;
    }

    public CopyOnWriteArrayList<Bullet> getmBullets() {
        return mBullets;
    }

    public void draw(Canvas lockCanvas) {
        super.draw(lockCanvas);
        if (mBullets != null) {
            for (Bullet bullet : mBullets) {
                bullet.draw(lockCanvas);
            }
        }
        if (aBlast != null) {
            aBlast.draw(lockCanvas);
        }
    }


    //玩家逻辑
    public void logic() {
        if (count++ > firecount) {
            if (isVisible()) {
                fire(); //发射方法
            }
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
        if (aBlast != null) {
            aBlast.logic();
        }
        outOfBounds();
    }

    //开火
    private void fire() {

        if (mBullets != null) {
            for (Bullet bullet : mBullets) {
                if (!bullet.isVisible()) {
                    bullet.setPosition(getX() + getWidth() / 2 - bullet.getWidth() / 2, getY());
                    bullet.setVisible(true);
                    break;
                }
            }
        }

    }

    /*
    * 碰撞检测
    * */
    public void outOfBounds() {

        if (getX() < 0) {
            setX(0);
        } else if (getX() + getWidth() > 480) {
            setX(480 - getWidth());
        }

        if (getY() < 0) {
            setY(0);
        } else if (getY() + getHeight() > 800) {
            setY(800 - getHeight());
        }
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFirecount() {
        return firecount;
    }

    public void setFirecount(int firecount) {
        this.firecount = firecount;
    }
}
