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
    private Goods mGoods;
    private static final String TAG = "MyView";
    private BossEnemy mBossEnemy;
    private Bitmap BossBitmap;

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

        iniBitmap();
        backgroundY1 = backgroundBitmap.getHeight();
        backgroundY2 = 0;

        aPlayer = new Player(playerBitmap);
        aPlayer.setPosition(240 - aPlayer.getWidth() / 2, 700 - aPlayer.getHeight());
        //初始化食物


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
        aRandom = new Random(20);
        mMyMusic = new MyMusic(aMainActivity);
        mMyMusic.initSoundPool();
        mMyMusic.play("normalmusic.mp3");
        step = 0;
        gameOver = false;
        count = 0; //分数重置
    }


    /*
    *
    * 初始化图片*/
    private void iniBitmap() {
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
        BossBitmap = aMainActivity.getBitmap("bossplane1.png");
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
        if (mGoods!=null){
            mGoods.logic();
        }
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
        //小型飞机的碰撞方法
        if (smallenemys != null || bigenemys != null) {

            for (Enemy enemy : smallenemys) {
                //判断碰到敌人
                if (enemy.collisionWith(aPlayer)) {
                    enemy.setVisible(false);
                    Blast blast = aPlayer.getaBlast();
                    blast.setPosition(aPlayer.getX(),aPlayer.getY());
                    blast.setVisible(true);
                    aPlayer.setVisible(false);
                    //gameOver = true;
                    mMyMusic.stop();
                }
                //被子弹击中
                if (enemy.getaBullets() != null) {
                    for (Bullet bullet : enemy.getaBullets()) {
                        if (bullet.collisionWith(aPlayer)) {
                            bullet.setVisible(true);
                            enemy.setVisible(false);
                            Blast blast = aPlayer.getaBlast();
                            blast.setPosition(aPlayer.getX(),aPlayer.getY());
                            //blast.setVisible(true);
                            //aPlayer.setVisible(false);
                            //gameOver = true;
                            mMyMusic.stop();
                        }
                    }
                }
                //判断子弹击中敌人
                if (aPlayer.getmBullets() != null) {
                    for (Bullet bullet : aPlayer.getmBullets()) {
                        if (bullet.collisionWith(enemy)) {
                            bullet.setVisible(true);
                            enemy.setVisible(false);
                            enemy.setFire(false);
                            count += 10;//积分
                            Blast blast = enemy.getaBlast();
                            blast.setPosition(enemy.getX(), enemy.getY());
                            blast.setVisible(true);
                            mMyMusic.play(mMyMusic.boomMusic);
                        }
                    }

                }

            }

            //大型飞机的碰撞
            for (Enemy i : bigenemys) {
                //判断碰到敌人
                if (i.collisionWith(aPlayer)) {
                    i.setVisible(false);
                    Blast blast = aPlayer.getaBlast();
                    blast.setPosition(aPlayer.getX(),aPlayer.getY());
                    //blast.setVisible(true);
                    aPlayer.setVisible(false);
                    //gameOver = true;
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
                            //blast.setVisible(true);
                            aPlayer.setVisible(false);
                           // gameOver = true;
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

                //食物的碰撞
                if (aPlayer!=null){
                    if (mGoods!=null){
                    if (aPlayer.collisionWith(mGoods)){
                        mGoods.setVisible(false);
                        aPlayer.setFirecount(1);
                    }
                }}

            }

        }

    }

    /*
    *
    * */
    private void myStep() {
        step++;

        //小型飞机
        if (step == 100) {
            FristEnemy();

        }
        //炸弹包
        if (step == 120&&step<=500) {
            ShowGoods();

        }
        //展示小型飞机
        if (step >= 500 && step <= 1100) {
            ShowSmallEnemy();

        }
        //难度加大 出现中型飞机
        if (step >= 1200 && step <= 1800) {
            ShowBigEnemy();
        }
        //出现BOSS
        if (step >= 1801&&step<=2500) {
            ShowBoss();
        }
    }

    private void FristEnemy() {
        //Log.i("敌人出现", "第一批");
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

    private void ShowBoss() {
        Log.i("敌人出现", "老大出现");
        mBossEnemy = new BossEnemy(BossBitmap);
        Log.i(TAG, "ShowBoss: "+mBossEnemy.getPosition()+mBossEnemy.getWidth()+mBossEnemy.getHeight());
        mBossEnemy.setSpeedX(5);
        mBossEnemy.outOfBounds();
        mBossEnemy.setPosition(30,80);

        CopyOnWriteArrayList<Bullet> abullets = new CopyOnWriteArrayList<>();
        for (int i=1;i<=step;i++){
            Bullet abullet= new Bullet(bulletBitmap);
            abullets.add(abullet);
            abullet.setSpeedY(3);
        }
        mBossEnemy.setaBullets(abullets);

        mBossEnemy.logic();
        mBossEnemy.setVisible(true);
    }

    private void ShowBigEnemy() {
        if (bigenemys != null) {
            out:
            for (Enemy enemy : bigenemys) {
                if (!enemy.isVisible()) {
                    if (!enemy.isVisible()) {
                        if (enemy.getaBullets() != null) {
                            for (Bullet bullet : enemy.getaBullets()) {
                                if (bullet.isVisible()) {
                                    continue out;
                                }
                            }
                        }
                        enemy.setPosition(aRandom.nextInt(480 - enemy.getWidth()), -enemy.getHeight());
                        int speedY = aRandom.nextInt(8) + 2;
                        enemy.setSpeedY(speedY);
                        CopyOnWriteArrayList<Bullet> aBullets = new CopyOnWriteArrayList<>();
                        for (int j = 0; j < 3; j++) {
                            Bullet aBullet = new Bullet(bulletBitmap1);
                            aBullet.setSpeedY(speedY+2);
                            aBullets.add(aBullet);
                        }
                        enemy.setaBullets(aBullets);
                        enemy.setVisible(true);
                        break;

                    }
                }
            }
        }
    }

    private void ShowSmallEnemy() {
        //Log.i("敌人出现", "第二批");
        if (smallenemys != null) {
            out:
            for (Enemy enemy : smallenemys) {

                if (!enemy.isVisible()) {
                    if (enemy.getaBullets() != null) {
                        for (Bullet bullet : enemy.getaBullets()) {
                            if (bullet.isVisible()) {
                                continue out;
                            }
                        }
                    }
                    enemy.setPosition(aRandom.nextInt(480 - enemy.getWidth()), -enemy.getHeight());
                    int speedY = aRandom.nextInt(1) + 2;
                    enemy.setSpeedY(6);
                    //Log.i(TAG, "myStep: ");

                    //int v = (int) (2 - (Math.random() * -1));//随机真负数
                    //enemy.setSpeedX(-v);
                    CopyOnWriteArrayList<Bullet> aBullets = new CopyOnWriteArrayList<>();
                   /* for (int j = 0; j < aRandom.nextInt(); j++) { //子弹随机发射数量
                        Bullet aBullet = new Bullet(bulletBitmap1);
                        aBullet.setSpeedY(speedY + 3);
                        aBullets.add(aBullet);
                    }*/
                    //enemy.setaBullets(aBullets);
                    enemy.setVisible(true);
                    break;
                }
            }
        }
    }

    /*
    *
    * 出现失误*/
    private void ShowGoods() {
        mGoods = new Goods(goodsBitmap);
        //设置食物的位置
        mGoods.setPosition(240-mGoods.getWidth(),mGoods.getHeight()/2-mGoods.getHeight()-80);
        //Log.i(TAG, "init: "+mGoods.getPosition());
        mGoods.setSpeedY(5);
        mGoods.setVisible(true);
        mGoods.logic();
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
            if (mBossEnemy!=null){
                mBossEnemy.draw(lockCanvas);
            }
            if (mGoods!=null){
                mGoods.draw(lockCanvas);
            }
            mPaint.setColor(Color.DKGRAY);
            //Log.i(TAG, "食物的位置: "+mGoods.getPosition());
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
