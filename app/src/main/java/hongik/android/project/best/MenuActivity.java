package hongik.android.project.best;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import org.json.JSONObject;

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

        try{drawintent();}
        catch (Exception ex){}
    }
    public void drawintent() throws Exception {
        String query = ("func=menudetail&license=" + license + "&item=" + item);
        DBConnector conn = new DBConnector(query);

        conn.start();
        conn.join();

        JSONObject jsonResult = conn.getResult();
        boolean result = jsonResult.getBoolean("result");

        if (!result)
            return;

        JSONObject json = jsonResult.getJSONArray("values").getJSONObject(0);
        String sname = json.getString("SNAME");
        String item = json.getString("PRICE");
        String img = json.getString("IMG");

        ImageView imageView = (ImageView) findViewById(R.id.menudetail_image);
        TextViewPlus tvItem = (TextViewPlus) findViewById(R.id.menudetail_item);
        TextViewPlus tvStore = (TextViewPlus) findViewById(R.id.menudetail_name);
        TextViewPlus tvPrice = (TextViewPlus) findViewById(R.id.menudetail_price);

        tvItem.setText(this.item);
        tvStore.setText(sname);
        tvPrice.setText(item);

        ImageLoader imageLoader = new ImageLoader(img);
        imageLoader.start();
        imageLoader.join();

        Bitmap bitmap = imageLoader.getBitmap();

        imageView.setImageBitmap(bitmap);
    }
}

