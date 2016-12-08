package wangtianchao.a1521145433.qimogame;

import android.graphics.Bitmap;

/**
 * Created by lovec on 2016/11/27.
 */

public class Blast extends Sprite {

    public Blast(Bitmap aBitmap,int width ,int height) {
        super(aBitmap,width,height);
    }
    public void logic(){
        if (isVisible()){
            nextFrame();
            if (getFrameIndex()==0){
                setVisible(false);
            }
        }
    }

}
