package wangtianchao.a1521145433.qimogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by lovecanon0823 on 16-11-30.
 */

public class BossEnemy extends Sprite {


    private int state;
    private static final int LEFT= 0;
    private static  final int RIGHT=1;

    private CopyOnWriteArrayList<Bullet> aBullets;
    private int count;
    private Blast aBlast;

    //BOSS的血量
    private int blood =100;

    public BossEnemy(Bitmap aBitmap) {
        super(aBitmap);
    }

    public void setState(int state) {
        this.state = state;
    }


    public CopyOnWriteArrayList<Bullet> getaBullets() {
        return aBullets;
    }


    public void setaBullets(CopyOnWriteArrayList<Bullet> aBullets) {
        this.aBullets = aBullets;
    }

    public Blast getaBlast() {
        return aBlast;
    }

    public void setaBlast(Blast aBlast) {
        this.aBlast = aBlast;
    }

    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }

    public void ReduceBlood(){
        blood--;
    }

    @Override
    public void logic() {
        move(getSpeedX(),getSpeedY());
        if (count++ > 10) {
            if (isVisible()) {
                if(getY()>=80){
                fire();
                }
            }
            count = 0;
        }
        if (aBullets != null) {
            for (Bullet bullet : aBullets) {
                bullet.logic();
            }
        }
        if (aBlast != null) {
            aBlast.logic();
        }
        outOfBounds();

        if(getY()>80){
            setY(80);
            switch (state){
                case LEFT:
                    move(4,0);
                    break;
                case RIGHT:
                    move(-4,0);
                    break;
                default:
                    move(4,0);
                    break;
            }
        }
    }

    public void outOfBounds() {
        if (getX() < 0) {
            setState(LEFT);
        } else if (getX() + getWidth() > 480) {
            setState(RIGHT);
        }
    }
    private void fire(){
        if (aBullets != null) {
            for (Bullet bullet : aBullets) {
                if (!bullet.isVisible()) {
                    bullet.setPosition(getX() + getWidth() / 2 -
                            bullet.getWidth() / 2, getY() + getHeight());
                    bullet.setVisible(true);
                    break;
                }
            }
        }
    }

    public void draw(Canvas lockCanvas) {
        super.draw(lockCanvas);
        if (aBullets != null) {
            for (Bullet bullet : aBullets) {
                bullet.draw(lockCanvas);
            }
        }
        if (aBlast != null) {
            aBlast.draw(lockCanvas);
        }
    }
}
