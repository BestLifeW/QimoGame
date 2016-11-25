package wangtianchao.a1521145433.qimogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by TianChaoWang on 2016/11/25.
 *
 * 子弹类
 */
public class Bullet {

    private Bitmap aBitmap;
    private int x;
    private int y;

    private int width;
    private int height;

    private int speedX;
    private int speedY;
    private boolean isVisible;

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

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas lockCanvas){
        if (isVisible){
            lockCanvas.drawBitmap(aBitmap,x,y,null);

        }
    }
    public void move(float speedX,float speedY){
        x+=speedX;
        y+=speedX;
    }
    public void logic(){
        move(speedX,speedY);
        outOfBounds();
    }

    private void outOfBounds() {
        if (x<0||x>480||y<0||y>800){
            setVisible(false);
        }
    }
}
