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
        item = intent.getStringExtra("ITEM");

        drawIntent();
    }

    protected void  drawIntent(){
        String query =  ("func=menudetail&license=" + license + "&item=" + item);
        URLConnector conn = new URLConnector(Constant.QueryURL, "POST", query);

        conn.start();

        String result = null;

        try{
            conn.join();
            result = conn.getResult();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        if(result != null){
            Toast.makeText(this, "result", Toast.LENGTH_SHORT).show();
        }
    }
}
