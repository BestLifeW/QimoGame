package wangtianchao.a1521145433.qimogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by lovec on 2016/11/26.
 */

public class Enemy extends Sprite {

    private CopyOnWriteArrayList<Bullet> aBullets;
    private int count;

    public Enemy(Bitmap aBitmap) {
        super(aBitmap);
    }

    public CopyOnWriteArrayList<Bullet> getaBullets() {
        return aBullets;
    }

    public void setaBullets(CopyOnWriteArrayList<Bullet> aBullets) {
        this.aBullets = aBullets;
    }


    public void draw(Canvas lockCanvas) {
        super.draw(lockCanvas);
        if (aBullets!=null){
            for (Bullet bullet :aBullets){
                bullet.draw(lockCanvas);
            }
        }
    }


    public void logic() {
        move(getSpeedX(),getSpeedY());
        if (count++>10){
            fire();
            count = 0;
        }
        if (aBullets!=null){
            for (Bullet bullet :aBullets){
                bullet.logic();
            }
        }
        outOfBounds();
    }

    private void fire() {
        if (aBullets!=null){
            for (Bullet bullet : aBullets) {
                if (!bullet.isVisible()){
                    bullet.setPosition(getX()+getWidth()/2-bullet.getWidth()/2,getY()+getHeight());
                    bullet.setVisible(true);
                    break;
                }
            }
        }
    }

    public void outOfBounds(){
        if (getX()<0||getY()>800||getY()>800){//480的屏幕太小 导致敌机还未出屏幕就消失  所以改成800
            setVisible(false);
        }
    }
}
