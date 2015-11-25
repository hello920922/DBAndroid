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

        Intent intent = this.getIntent();
        license = intent.getStringExtra("LICENSE");
        item = intent.getStringExtra("MENU");

        drawMenu();
    }


    public void drawMenu(){
        String query = "func=menudetail&license=" + license + "&item=" + item;
        URLConnector conn = new URLConnector(Constant.QueryURL, "POST", query);

        Toast.makeText(this, query, Toast.LENGTH_LONG).show();
    }
}
