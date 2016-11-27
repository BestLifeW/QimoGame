package wangtianchao.a1521145433.qimogame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private Bitmap bulletBitmap1;
    private Player aPlayer;
    private Canvas lockCanvas;
    private float backgroundY1;
    private float backgroundY2;
    private Bitmap enemyBitmap;
    private Bitmap bulletBitmap;
    private CopyOnWriteArrayList<Enemy> aEnemies;
    private Random aRandom;
    private int step;
    private int count;//这个代表分数

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
                aPlayer.move(-distanceX, -distanceY);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
        init();
    }

    /*
    * 初始化图片
    * */
    private void init() {

        backgroundBitmap = aMainActivity.getBitmap("bg_02.jpg");
        playerBitmap = aMainActivity.getBitmap("oneplane.png");
        bulletBitmap = aMainActivity.getBitmap("bullet2.png");
        enemyBitmap = aMainActivity.getBitmap("enemy.png");
        bulletBitmap1 = aMainActivity.getBitmap("bullet.png");

        backgroundY1 = backgroundBitmap.getHeight();
        backgroundY2 = 0;

        aPlayer = new Player(playerBitmap);
        aPlayer.setPosition(240 - aPlayer.getWidth() / 2, 700 - aPlayer.getHeight());
        CopyOnWriteArrayList<Bullet> aBullets = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 20; i++) {
            Bullet aBullet = new Bullet(bulletBitmap);
            aBullet.setSpeedY(-10);
            aBullets.add(aBullet);
        }
        aPlayer.setBullets(aBullets);
        aPlayer.setVisible(true);

        aEnemies = new CopyOnWriteArrayList<>();
        aRandom = new Random();

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isRun = true;
        new Thread(this).start();
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
        backgroundY1++;
        backgroundY2++;

        if (backgroundY1 >= backgroundBitmap.getHeight()) {
            backgroundY1 = -backgroundBitmap.getHeight();
        }
        if (backgroundY2 > backgroundBitmap.getHeight()) {
            backgroundY2 = -backgroundBitmap.getHeight();
        }

        myStep();
        aPlayer.logic();
        if (aEnemies != null) {
            for (Enemy i : aEnemies) {
                i.logic();
            }
        }
    }

    /*
    *
    * */
    private void myStep() {
        step++;
        Log.i("敌人出现", "myStep: "+step);
        if (step == 100) {
            Log.i("敌人出现", "第一批");
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j <2 * i + 1; j++) {
                    Enemy aEnemy=new Enemy(enemyBitmap);
                    aEnemy.setPosition(240-aEnemy.getWidth()/2+60*(j-i), -aEnemy.getHeight()-80*i);
                    aEnemy.setSpeedY(2);
                    aEnemy.setVisible(true);
                    aEnemies.add(aEnemy);
                }
            }
        }

        if (step>=300&&step<=1800&&step%10==0){
            Log.i("敌人出现", "第二批");
            if (aEnemies!=null){
                for (Enemy i:aEnemies){
                    if (!i.isVisible()){
                        i.setPosition(aRandom.nextInt(480-i.getWidth()),-i.getHeight());
                        i.setSpeedY(aRandom.nextInt(3)+2);
                        CopyOnWriteArrayList<Bullet> aBullets = new CopyOnWriteArrayList<>();
                        for (int j = 0 ; j<aRandom.nextInt();j++){ //子弹随机发射数量
                            Bullet aBullet = new Bullet(bulletBitmap1);
                            aBullet.setSpeedY(5);
                            aBullets.add(aBullet);
                        }
                        i.setaBullets(aBullets);
                        i.setVisible(true);
                        break;
                    }
                }
            }
        }

        //BOSS出现
        if (step==2000){
            Log.i("敌人出现", "老大出现");

        }
        
    }

    private void myDraw() {

        try {
            lockCanvas = holder.lockCanvas();
            lockCanvas.drawColor(Color.rgb(194, 199, 203));//194,199,203
            lockCanvas.save();
            lockCanvas.scale(scaleX, scaleY);
            Paint mPaint = new Paint();
            mPaint.setStrokeWidth(3);
            mPaint.setTextSize(20);


            lockCanvas.drawBitmap(backgroundBitmap, 0, backgroundY1, null);
            lockCanvas.drawBitmap(backgroundBitmap, 0, backgroundY2, null);
            mPaint.setColor(Color.DKGRAY);
            //if (step==2000&&step)

            aPlayer.draw(lockCanvas);

            //准备开始绘制分数  count
            if (aEnemies!=null){
                for (Enemy i:aEnemies){
                    i.draw(lockCanvas);
                }
            }
            lockCanvas.drawText("得分:"+step,20,40,mPaint);
            lockCanvas.restore();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lockCanvas != null) {
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
