package wangtianchao.a1521145433.qimogame;

import android.graphics.Bitmap;

/**
 * Created by lovecanon0823 on 16-11-30.
 */

public class BossEnemy extends Enemy {


    //BOSS的血量
    private int blood ;


    public BossEnemy(Bitmap aBitmap) {
        super(aBitmap);
    }


    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }
}
