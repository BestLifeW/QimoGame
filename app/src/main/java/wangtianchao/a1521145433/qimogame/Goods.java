package wangtianchao.a1521145433.qimogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by lovecanon0823 on 16-11-30.
 */

public class Goods extends Sprite {

    public Goods(Bitmap aBitmap) {
        super(aBitmap);
    }


    public void logic() {  //逻辑
        move(getSpeedX(),getSpeedY());
            outOfBounds();
        }
    public void outOfBounds(){   //碰撞检测

        if (getX()<0||getY()>800||getY()>800){//480的屏幕太小 导致敌机还未出屏幕就消失  所以改成800
            setVisible(false);
        }
    }

    public void draw(Canvas lockCanvas) {
        super.draw(lockCanvas);
    }
}
