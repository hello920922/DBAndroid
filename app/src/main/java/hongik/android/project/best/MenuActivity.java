package hongik.android.project.best;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import layout.api.TextViewPlus;

public class MenuActivity extends AppCompatActivity {
    private String license;
    private String item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menudetail);

        Intent intent = getIntent();
        license = intent.getStringExtra("LICENSE");
        item = intent.getStringExtra("MENU");

        drawintent();
    }
    public void drawintent(){
        String query =("func=menudetail&license="+license+"&item="+item);
        URLConnector conn = new URLConnector(Constant.QueryURL,"POST",query);

        conn.start();

        String result = null;

        try{
            conn.join();
            result = conn.getResult();
            }catch(InterruptedException e){
            e.printStackTrace();
        }
        if(result !=null){
            String[] data = result.split(",");
            String store_same = data[0];
            String item_price= data[1];
            String menu_img = data[2];

            ImageView imageView = (ImageView)findViewById(R.id.menudetail_image);
            TextViewPlus tvItem = (TextViewPlus)findViewById(R.id.menudetail_item);
            TextViewPlus tvStore = (TextViewPlus)findViewById(R.id.menudetail_name);
            TextViewPlus tvPrice = (TextViewPlus)findViewById(R.id.menudetail_price);

            tvItem.setText(item);
            tvStore.setText(store_same);
            tvPrice.setText(item_price);

            ImageLoader imageLoader = new ImageLoader(menu_img);
            imageLoader.start();
            try {
                imageLoader.join();
                Bitmap bitmap = imageLoader.getBitmap();

                imageView.setImageBitmap(bitmap);
            }catch (Exception ex){};
        }

    }
}

