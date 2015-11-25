package hongik.android.project.best;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import layout.api.TextViewPlus;

public class StoreActivity extends AppCompatActivity {
    private String license;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        Intent intent = this.getIntent();
        license = intent.getStringExtra("LICENSE");
    }

    public void drawPage(){
        String query = "func=storereview" + "&license=" + license;

        URLConnector conn = new URLConnector(Constant.QueryURL, "GET", query);
        String result = conn.getResult();
        String[] results = result.split("/nextResult/");

        String store = results[0];
        String[] store_info = store.split(",");

        String menu = results[1];
        String review = results[2];

        ((TextViewPlus)findViewById(R.id.store_storename)).setText(store_info[0]);
        ((TextViewPlus)findViewById(R.id.store_address)).setText(store_info[1]);
        ((TextViewPlus)findViewById(R.id.store_storename)).setText(store_info[0]);
    }

    public void storeClick(View view) {
        if(view.getId() == R.id.store_more){
            Intent storeReviewIntent = new Intent(this, StoreReviewActivity.class);
            storeReviewIntent.putExtra("LICENSE", license);
            startActivity(storeReviewIntent);
        }
    }
}