package wangtianchao.a1521145433.qimogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by TianChaoWang on 2016/11/25.
 * 玩家类
 */

public class Player extends Sprite{


    private CopyOnWriteArrayList<Bullet> mBullets;
    private int count;

    public Player(Bitmap bitmap) {
        super(bitmap);

    }

    public void setBullets(CopyOnWriteArrayList<Bullet> mBullets) {
        this.mBullets = mBullets;
    }


    public void draw(Canvas locakCanvas) {
        super.draw(locakCanvas);
        if (mBullets != null) {
            for (Bullet bullet : mBullets) {
                bullet.draw(locakCanvas);
            }
        }
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

    //开火
    private void fire() {
            if (mBullets!=null){
                for (Bullet bullet :mBullets){
                    if (!bullet.isVisible()){
                        bullet.setPosition(getX()+getWidth()/2-bullet.getWidth()/2,getY());
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
}
