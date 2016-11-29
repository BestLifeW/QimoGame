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
    private Bitmap smallenemy;
    private Bitmap bulletBitmap;
    private CopyOnWriteArrayList<Enemy> smallenemys;
    private Random aRandom;
    private int step;
    private int count;//这个代表分数
    private MyMusic mMyMusic;
    private boolean gameOver;
    private Paint paint;
    private Bitmap blastBitmap1;
    private Bitmap goodsBitmap;
    private Bitmap bigenemy;
    private CopyOnWriteArrayList<Enemy> bigenemys;
    private Bitmap bigblast;
    private Bitmap myplaneboom;
    private Blast playerboom;

    public MyView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        aMainActivity = (MainActivity) context;
        WindowManager aWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        aWindowManager.getDefaultDisplay().getSize(SCREENSIZE);
        scaleX = (float) SCREENSIZE.x / 480;
        scaleY = (float) SCREENSIZE.y / 800;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(24);


        setLongClickable(true);

        //手势识别
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                aPlayer.move(-distanceX, -distanceY);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }


            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (gameOver) {
                    init();
                }
                return super.onDoubleTap(e);
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
        smallenemy = aMainActivity.getBitmap("small.png");
        bulletBitmap1 = aMainActivity.getBitmap("bullet.png");
        blastBitmap1 = aMainActivity.getBitmap("smallboom.png");
        bigblast = aMainActivity.getBitmap("blast.png");
        goodsBitmap = aMainActivity.getBitmap("goods.png");
        bigenemy = aMainActivity.getBitmap("enemy.png");

        myplaneboom = aMainActivity.getBitmap("myplaneboom.png");
        backgroundY1 = backgroundBitmap.getHeight();
        backgroundY2 = 0;

        aPlayer = new Player(playerBitmap);
        aPlayer.setPosition(240 - aPlayer.getWidth() / 2, 700 - aPlayer.getHeight());
        CopyOnWriteArrayList<Bullet> aBullets = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 100; i++) {
            Bullet aBullet = new Bullet(bulletBitmap);
            aBullet.setSpeedY(-30);
            aBullets.add(aBullet);
        }
        aPlayer.setBullets(aBullets);
        aPlayer.setVisible(true);
        playerboom = new Blast(myplaneboom, 62, 80);
        aPlayer.setaBlast(playerboom);
        smallenemys = new CopyOnWriteArrayList<>();
        bigenemys = new CopyOnWriteArrayList<>();
        aRandom = new Random();
        mMyMusic = new MyMusic(aMainActivity);
        mMyMusic.initSoundPool();
        mMyMusic.play("normalmusic.mp3");
        step = 0;
        gameOver = false;
        count = 0; //分数重置
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
        mMyMusic.stop();
    }

    @Override
    public void run() {
        while (isRun) {
            long startTime = System.currentTimeMillis();

            myDraw();
            if (!gameOver) {
                myLogic();
            }

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

    private void myLogic() {
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
        if (smallenemys != null) {
            for (Enemy i : smallenemys) {
                i.logic();
            }
        }
        if (bigenemys != null) {
            for (Enemy i : bigenemys) {
                i.logic();
            }
        }
        myCollision();
    }

    /*
    *
    * */
    private void myCollision() {
        if (smallenemys != null || bigenemys != null) {

            for (Enemy i : smallenemys) {
                //判断碰到敌人
                if (i.collisionWith(aPlayer)) {
                    i.setVisible(false);
                    Blast blast = aPlayer.getaBlast();
                    blast.setPosition(aPlayer.getX(),aPlayer.getY());
                    blast.setVisible(true);
                    aPlayer.setVisible(false);
                    gameOver = true;
                    mMyMusic.stop();
                }
                //被子弹击中
                if (i.getaBullets() != null) {
                    for (Bullet j : i.getaBullets()) {
                        if (j.collisionWith(aPlayer)) {
                            i.setVisible(false);
                            j.setVisible(false);
                            Blast blast = aPlayer.getaBlast();
                            blast.setPosition(aPlayer.getX(),aPlayer.getY());
                            blast.setVisible(true);
                            aPlayer.setVisible(false);
                            gameOver = true;
                            mMyMusic.stop();
                        }
                    }
                }
                //判断子弹击中敌人
                if (aPlayer.getmBullets() != null) {
                    for (Bullet j : aPlayer.getmBullets()) {
                        if (j.collisionWith(i)) {
                            j.setVisible(false);
                            i.setVisible(false);
                            count += 10;
                            Blast blast = i.getaBlast();
                            blast.setPosition(i.getX(), i.getY());
                            blast.setVisible(true);
                            mMyMusic.play(mMyMusic.boomMusic);
                        }
                    }

                }

            }
            for (Enemy i : bigenemys) {
                //判断碰到敌人
                if (i.collisionWith(aPlayer)) {
                    i.setVisible(false);
                    Blast blast = aPlayer.getaBlast();
                    blast.setPosition(aPlayer.getX(),aPlayer.getY());
                    blast.setVisible(true);
                    aPlayer.setVisible(false);
                    gameOver = true;
                    mMyMusic.stop();
                }
                //被子弹击中
                if (i.getaBullets() != null) {
                    for (Bullet j : i.getaBullets()) {
                        if (j.collisionWith(aPlayer)) {
                            j.setVisible(false);
                            i.setVisible(false);
                            Blast blast = aPlayer.getaBlast();
                            blast.setPosition(aPlayer.getX(),aPlayer.getY());
                            blast.setVisible(true);
                            aPlayer.setVisible(false);
                            gameOver = true;
                            mMyMusic.stop();
                        }
                    }
                }
                //判断子弹击中敌人
                if (aPlayer.getmBullets() != null) {
                    for (Bullet j : aPlayer.getmBullets()) {
                        if (j.collisionWith(i)) {
                            j.setVisible(false);
                            i.setVisible(false);
                            count += 30;
                            Blast blast = i.getaBlast();
                            blast.setPosition(i.getX(), i.getY());
                            blast.setVisible(true);
                            mMyMusic.play(mMyMusic.boomMusic);
                        }
                    }
                }
            }

        }

    }

    /*
    *
    * */
    private void myStep() {
        step++;
        //Log.i("敌人出现", "myStep: "+step);
        //小型飞机
        if (step == 100) {
            Log.i("敌人出现", "第一批");
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 2 * i + 1; j++) {
                    Enemy aEnemy = new Enemy(smallenemy);
                    Enemy bigemy = new Enemy(bigenemy);
                    aEnemy.setPosition(240 - aEnemy.getWidth() / 2 + 60 * (j - i), -aEnemy.getHeight() - 80 * i);
                    bigemy.setPosition(240 - bigemy.getWidth() / 2 + 60 * (j - i), -bigemy.getHeight() - 80 * i);

                    aEnemy.setSpeedY(2);
                    Blast aBlast = new Blast(blastBitmap1, 34, 34);//初始化爆炸效果
                    Blast aBlast1 = new Blast(bigblast, 68, 70);
                    aEnemy.setaBlast(aBlast);
                    aEnemy.setVisible(true);

                    bigemy.setaBlast(aBlast1);
                    bigemy.setVisible(true);
                    smallenemys.add(aEnemy);
                    bigenemys.add(bigemy);
                }
            }
        }
        //炸弹包
        if (step == 120) {

        }

        if (step >= 500 && step <= 1100) {
            Log.i("敌人出现", "第二批");
            if (smallenemys != null) {
                out:
                for (Enemy i : smallenemys) {

                    if (!i.isVisible()) {
                        if (i.getaBullets() != null) {
                            for (Bullet j : i.getaBullets()) {
                                if (j.isVisible()) {
                                    continue out;
                                }
                            }
                        }
                        i.setPosition(aRandom.nextInt(480 - i.getWidth()), -i.getHeight()+30);
                        int speedY = aRandom.nextInt(1) + 2;
                        i.setSpeedY(speedY);//速度
                        int v = (int) (2 - (Math.random() * -1));//随机真负数
                        i.setSpeedX(-v);
                        CopyOnWriteArrayList<Bullet> aBullets = new CopyOnWriteArrayList<>();
                        for (int j = 0; j < aRandom.nextInt(); j++) { //子弹随机发射数量
                            Bullet aBullet = new Bullet(bulletBitmap1);
                            aBullet.setSpeedY(speedY + 3);
                            aBullets.add(aBullet);
                        }
                        i.setaBullets(aBullets);
                        i.setVisible(true);
                        break;
                    }
                }
            }
        }
        //难度加大 出现中型飞机
        if (step >= 1200 && step <= 1800) {
            if (bigenemys != null) {
                out:
                for (Enemy i : bigenemys) {
                    if (!i.isVisible()) {
                        if (!i.isVisible()) {
                            if (i.getaBullets() != null) {
                                for (Bullet j : i.getaBullets()) {
                                    if (j.isVisible()) {
                                        continue out;
                                    }
                                }
                            }
                            i.setPosition(aRandom.nextInt(480 - i.getWidth()), -i.getHeight());
                            int speedY = aRandom.nextInt(8) + 2;
                            i.setSpeedY(speedY);
                            CopyOnWriteArrayList<Bullet> aBullets = new CopyOnWriteArrayList<>();
                            for (int j = 0; j < aRandom.nextInt(); j++) {
                                Bullet aBullet = new Bullet(bulletBitmap1);
                                aBullet.setSpeedY(speedY + 3);
                                aBullets.add(aBullet);
                            }
                            i.setaBullets(aBullets);
                            i.setVisible(true);
                            break;

                        }
                    }
                }
            }

            //BOSS出现   /
            if (step == 2000) {
                Log.i("敌人出现", "老大出现");

            }

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
            aPlayer.draw(lockCanvas);
            //准备开始绘制分数  count
            if (smallenemys != null) {
                for (Enemy i : smallenemys) {
                    i.draw(lockCanvas);
                }
            }
            if (bigenemys != null) {
                for (Enemy i : bigenemys) {
                    i.draw(lockCanvas);
                }
            }
            mPaint.setColor(Color.DKGRAY);
            lockCanvas.drawText("得分:" + count + " 时间" + step, 20, 40, mPaint);


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
