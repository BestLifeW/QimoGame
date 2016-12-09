package wangtianchao.a1521145433.qimogame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
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
    private final MainActivity context;
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
    private Bitmap BossBoomBitmap;
    private Bitmap musicBitmap;
    private RectF musicPlay;
    private int isPlay = 0;


    public MyView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        this.context = (MainActivity) context;
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
        mMyMusic = new MyMusic(context);
        mMyMusic.initSoundPool();
        mMyMusic.play("normalmusic.mp3");
        step = 0;
        gameOver = false;
        count = 0; //分数重置
    }

    /* 初始化图片*/
    private void iniBitmap() {
        backgroundBitmap = context.getBitmap("bg_02.jpg");
        playerBitmap = context.getBitmap("oneplane.png");
        bulletBitmap = context.getBitmap("bullet2.png");
        smallenemy = context.getBitmap("small.png");
        bulletBitmap1 = context.getBitmap("bullet.png");
        blastBitmap1 = context.getBitmap("smallboom.png");
        bigblast = context.getBitmap("blast.png");
        goodsBitmap = context.getBitmap("goods.png");
        bigenemy = context.getBitmap("enemy.png");
        myplaneboom = context.getBitmap("myplaneboom.png");
        BossBitmap = context.getBitmap("bossplane1.png");
        BossBoomBitmap = context.getBitmap("bossboom.png");
        musicBitmap = context.getBitmap("music.png");
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
        if (mGoods != null) {
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
        if (mBossEnemy!=null){
            mBossEnemy.logic();
        }
        myCollision();
    }

    private void myCollision() {
        //小型飞机的碰撞方法
        if (smallenemys != null || bigenemys != null) {
            for (Enemy enemy : smallenemys) {
                //判断碰到敌人
                if (enemy.collisionWith(aPlayer)) {
                    enemy.setVisible(false);
                    Blast blast = aPlayer.getaBlast();
                    blast.setPosition(aPlayer.getX(), aPlayer.getY());
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
                            blast.setPosition(aPlayer.getX(), aPlayer.getY());
                            blast.setVisible(true);
                            aPlayer.setVisible(false);
                            gameOver = true;
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
            for (Enemy enemy : bigenemys) {
                //判断碰到敌人
                if (enemy.collisionWith(aPlayer)) {
                    enemy.setVisible(false);
                    Blast blast = aPlayer.getaBlast();
                    blast.setPosition(aPlayer.getX(), aPlayer.getY());
                    //blast.setVisible(true);
                    aPlayer.setVisible(false);
                    //gameOver = true;
                    mMyMusic.stop();
                }
                //被子弹击中
                if (enemy.getaBullets() != null) {
                    for (Bullet bullet : enemy.getaBullets()) {
                        if (bullet.collisionWith(aPlayer)) {
                            bullet.setVisible(false);
                            enemy.setVisible(false);
                            Blast blast = aPlayer.getaBlast();
                            blast.setPosition(aPlayer.getX(), aPlayer.getY());
                            //blast.setVisible(true);
                            aPlayer.setVisible(false);
                            // gameOver = true;
                            mMyMusic.stop();
                        }
                    }
                }
                //判断子弹击中敌人
                if (aPlayer.getmBullets() != null) {
                    for (Bullet bullet : aPlayer.getmBullets()) {
                        if (bullet.collisionWith(enemy)) {
                            bullet.setVisible(false);
                            enemy.setVisible(false);
                            count += 30;
                            Blast blast = enemy.getaBlast();
                            blast.setPosition(enemy.getX(), enemy.getY());
                            blast.setVisible(true);
                            mMyMusic.play(mMyMusic.boomMusic);
                        }
                    }
                }
                //食物的碰撞
                if (aPlayer != null) {
                    if (mGoods != null) {
                        if (aPlayer.collisionWith(mGoods)) {
                            mGoods.setVisible(false);
                            aPlayer.setFirecount(1);//设置子弹速度
                        }
                    }
                }
                //判断boss被攻击
                if (aPlayer != null) {
                    for (Bullet bullet : aPlayer.getmBullets()) {
                        if (mBossEnemy != null) {
                            if (bullet.collisionWith(mBossEnemy)) {
                                bullet.setVisible(false);
                                mBossEnemy.ReduceBlood(); //BOSS扣血
                                if (mBossEnemy.getBlood() == 0) {
                                    Log.i(TAG, "myCollision: 啊啊啊啊我死了啦");
                                    Blast blast = mBossEnemy.getaBlast();
                                    blast.setPosition(mBossEnemy.getX(), mBossEnemy.getY());
                                    blast.setVisible(true);
                                    mBossEnemy.setVisible(false);
                                }

                            }
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

        //小型飞机
        if (step == 100) {
            FristEnemy();

        }
        //炸弹包
        if (step == 120 && step <= 500) {
            //加快子弹的速度
            ShowGoods();

        }
        //展示小型飞机
        if (step >= 600 && step <= 1100) {
            ShowSmallEnemy();

        }
        //难度加大 出现中型飞机
        if (step >= 1200 && step <= 1800) {
            ShowBigEnemy();
        }
        //出现BOSS
        if (step >= 1801 && step <= 2500) {
            ShowBoss();
        }
    }

    private void FristEnemy() {
        //Log.i("敌人出现", "第一批");


        /*
        *
        * for (int i = 1; i < 4; i++) {
				for (int j = 0; j < 12; j++) {
					if(j<=2-i||(j>=2+i&&j<=5-i)||j>=5+i){
					}else{
						Gold aGold=new Gold(aGoldBitmaps, 30, 30);
						aGold.setPosition(375+30*j, 30+30*i);
						aGold.setState(Gold.GOLD_EXIST);
						aGold.setVisible(true);
						aGolds.add(aGold);


						  Enemy aEnemy = new Enemy(smallenemy);
                          Enemy bigemy = new Enemy(bigenemy);
                aEnemy.setPosition(375+30*j, 30+30*i);
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
			for (int i = 1; i <5; i++) {
				for (int j = 0; j < 12; j++) {
					if(j>=i-1&&j<=8-i){
						Gold aGold=new Gold(aGoldBitmaps, 30, 30);
						aGold.setPosition(375+30*j, 120+30*i);
						aGold.setState(Gold.GOLD_EXIST);
						aGold.setVisible(true);
						aGolds.add(aGold);
					}else {

					}
				}
			}
		}
        * */
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 12; j++) {
                //if(j<=2-i||(j>=2+i&&j<=5-i)||j>=5+i){
               // }else{
                Enemy aEnemy = new Enemy(smallenemy);
                Enemy bigemy = new Enemy(bigenemy);
                aEnemy.setPosition(240 - aEnemy.getWidth() / 2 + 60 *i, -aEnemy.getHeight() - 80 * j);
                bigemy.setPosition(240 - aEnemy.getWidth() / 2 + 60 *i, -aEnemy.getHeight() - 80 * j);
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
           // }
        }
    }

    private void ShowBoss() {
        Log.i("敌人出现", "老大出现");
        mBossEnemy = new BossEnemy(BossBitmap);
        // Log.i(TAG, "ShowBoss: "+mBossEnemy.getPosition()+mBossEnemy.getWidth()+mBossEnemy.getHeight());
        mBossEnemy.setSpeedX(5);
        mBossEnemy.setPosition(240 - mBossEnemy.getWidth() / 2, 80);

        CopyOnWriteArrayList<Bullet> abullets = new CopyOnWriteArrayList<>();
        for (int i = 1; i <= 50; i++) {
            Bullet abullet = new Bullet(bulletBitmap);
            abullets.add(abullet);
            abullet.setSpeedY(3);
        }
        Blast blast = new Blast(BossBoomBitmap, 162, 108);
        mBossEnemy.setaBlast(blast);
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
                            aBullet.setSpeedY(speedY + 2);
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
                    CopyOnWriteArrayList<Bullet> aBullets = new CopyOnWriteArrayList<>();
                    for (int j = 0; j < aRandom.nextInt(); j++) { //子弹随机发射数量
                        Bullet aBullet = new Bullet(bulletBitmap1);
                        aBullet.setSpeedY(speedY + 3);
                        aBullets.add(aBullet);
                    }
                    enemy.setaBullets(aBullets);
                    enemy.setVisible(true);
                    break;
                }
            }
        }
    }

    private void ShowGoods() {
        mGoods = new Goods(goodsBitmap);
        mGoods.setPosition(240 - mGoods.getWidth(), mGoods.getHeight() / 2 - mGoods.getHeight() - 80);
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
            //音乐图标绘制
            lockCanvas.drawBitmap(musicBitmap, 240 + musicBitmap.getWidth(), 20, null);

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
            if (mBossEnemy != null) {
                mBossEnemy.draw(lockCanvas);
            }
            if (mGoods != null) {
                mGoods.draw(lockCanvas);
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

        gestureDetector.onTouchEvent(event);//
        //判断举行
        musicPlay = new RectF();
        musicPlay.set((240 + musicBitmap.getWidth()) * scaleX, 20 * scaleY, (240 + musicBitmap.getWidth() * 2) * scaleX, (20 + musicBitmap.getWidth()) * scaleY);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (musicPlay.contains(event.getX(), event.getY())) {
                if (isPlay % 2 == 0) {  //奇数
                    mMyMusic.stop();
                    isPlay++;
                } else {
                    mMyMusic.play("normalmusic.mp3");
                    isPlay++;
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
