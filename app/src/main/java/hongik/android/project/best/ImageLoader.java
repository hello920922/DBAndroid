package hongik.android.project.best;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2015-11-25.
 */
public class ImageLoader extends Thread{
    private String img = "";
    private Bitmap bitmap = null;

    public ImageLoader(String img){
        this.img = img;
    }

    @Override
    public void run(){
        try {
            URL url =  new URL(Constant.IMAGESERVER+img);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap(){
        return bitmap;
    }
}
