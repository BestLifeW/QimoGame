package wangtianchao.a1521145433.qimogame;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.io.IOException;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));
    }

    public Bitmap getBitmap(String fileName){
        Bitmap bitmap = null;

        try {
            //context.getClass().getClassLoader().getResourceAsStream("assets/"+资源名);
            bitmap = BitmapFactory.decodeStream(getAssets().open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
