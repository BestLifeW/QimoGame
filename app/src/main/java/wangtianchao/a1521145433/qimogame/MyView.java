package wangtianchao.a1521145433.qimogame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * Created by TianChaoWang on 2016/11/25.
 */
public class MyView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final Point SCREENSIZE = new Point();


    private final SurfaceHolder holder;
    private final MainActivity aMainActivity;
    private final float scaleX;
    private final float scaleY;
    private final GestureDetector gestureDetector;
    private boolean isRun;
    private Bitmap backgroundBitmap;
    private Bitmap playerBitmap;
    private Bitmap bulletBitmap;
    private Player aPlayer;
    private Canvas lockCanvas;

    public MyView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        aMainActivity = (MainActivity) context;
        WindowManager aWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        aWindowManager.getDefaultDisplay().getSize(SCREENSIZE);
        scaleX = (float) SCREENSIZE.x / 480;
        scaleY = (float) SCREENSIZE.y / 800;

        setLongClickable(true);

        //手势识别
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
        init();
    }

    /*
    * 初始化图片
    * */
    private void init() {

        backgroundBitmap = aMainActivity.getBitmap("bg_01.jpg");
        playerBitmap = aMainActivity.getBitmap("myplane.png");
        bulletBitmap = aMainActivity.getBitmap("bullet.png");


        aPlayer = new Player(playerBitmap);
        aPlayer.setPosition(240 - aPlayer.getWidth() / 2, 800 - aPlayer.getHeight());

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isRun = true;
        new Thread(this).run();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isRun = false;
    }

    @Override
    public void run() {
        while (isRun) {
            long startTime = System.currentTimeMillis();
            myDraw();
            myLogin();
            long endTime = System.currentTimeMillis();
            long diffTime = endTime - startTime;
            if (diffTime < 1000 / 30) {
                try {
                    Thread.sleep(1000 / 30 - diffTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void myLogin() {
    }

    private void myDraw() {

        try {
            lockCanvas = holder.lockCanvas();
            lockCanvas.drawColor(Color.WHITE);
            lockCanvas.save();
            lockCanvas.scale(scaleX,scaleY);
            lockCanvas.drawBitmap(backgroundBitmap,0,0,null);
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            if (lockCanvas!=null){
                holder.unlockCanvasAndPost(lockCanvas);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);

    }
}
