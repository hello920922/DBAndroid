package hongik.android.project.best;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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
            String menu_item = data[2];

            Toast.makeText(this, store_same + "/" + menu_item + "/" + item_price, Toast.LENGTH_LONG).show();
        }

    }
}

