package wangtianchao.a1521145433.qimogame;

import android.graphics.Bitmap;

/**
 * Created by lovec on 2016/11/27.
 * 补给 食物类
 */

public class Goods extends Sprite {

    private int count;
    public Goods(Bitmap aBitmap) {
        super(aBitmap);
    }


    public void logic() {
        move(getSpeedX(),getSpeedY());
        if (count++>10){

            count = 0;
        }

        outOfBounds();
    }
    public void outOfBounds(){
        if (getX()<0||getY()>800||getY()>800){//480的屏幕太小 导致敌机还未出屏幕就消失  所以改成800
            setVisible(false);
        }
    }
}
