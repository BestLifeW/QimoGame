package wangtianchao.a1521145433.qimogame;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by lovec on 2016/11/27.
 */

public class MyMusic {

    private MainActivity context;
    private MediaPlayer aMediaPlayer;
    private SoundPool aSoundPool;
    private int normalMusic;
    private int bossMusic;
    public int fireMusic;
    public int boomMusic;

    public MyMusic(MainActivity mainActivity){
        super();
        this.context = mainActivity;
    }
    public void play(String fileName){

        try {
            if (aMediaPlayer==null){
                aMediaPlayer = new MediaPlayer();
            }

            aMediaPlayer.reset();
            AssetFileDescriptor assetFileDescriptor = context.getAssets().openFd(fileName);
            FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();
            aMediaPlayer.setDataSource(fileDescriptor,assetFileDescriptor.getStartOffset(),assetFileDescriptor.getLength());
            aMediaPlayer.prepare();
            aMediaPlayer.setLooping(true);
            aMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stop(){
        if (aMediaPlayer.isPlaying()){
            aMediaPlayer.stop();
        }
    }
    public void initSoundPool(){

        try {
            aSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
            normalMusic = aSoundPool.load(context.getAssets().openFd("normalmusic.mp3"),1);
            boomMusic = aSoundPool.load(context,R.raw.boom,1);
            fireMusic =aSoundPool.load(context,R.raw.fire,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(int soundID){
        aSoundPool.play(soundID,0.8f, 0.8f,1, 0, 1);
    }
}
