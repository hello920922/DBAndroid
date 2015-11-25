package hongik.android.project.best;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

        URLConnector conn = new URLConnector(Constant.SERVER, "GET", query);
    }

    public void storeClick(View view) {
        if(view.getId() == R.id.store_more){
            Intent storeReviewIntent = new Intent(this, StoreReviewActivity.class);
            storeReviewIntent.putExtra("LICENSE", license);
            startActivity(storeReviewIntent);
        }
    }
}