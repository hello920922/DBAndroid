package hongik.android.project.best;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONObject;

import layout.api.ButtonPlus;
import layout.api.EditTextPlus;
import layout.api.TextViewPlus;

public class ReviewDetailActivity extends AppCompatActivity {
    private String access;
    private String cid;
    private String license;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewdetail);

        Intent intent = getIntent();
        access = intent.getStringExtra("ACCESS");
        cid = intent.getStringExtra("CID");
        license = intent.getStringExtra("LICENSE");

        if(access.equals("STORE"))
            ((ButtonPlus)findViewById(R.id.reviewdetail_button)).setText("Go Back");
        else if(access.equals("HISTORY"))
            ((ButtonPlus)findViewById(R.id.reviewdetail_button)).setText("StoreInfo");

        drawReview();
    }

    public void drawReview(){
        String query = "func=reviewdetail" + "&cid=" + cid + "&license=" + license;
        DBConnector conn = new DBConnector(query);

        conn.start();
        try {
            conn.join();
            JSONObject jsonResult = conn.getResult();
            boolean result = jsonResult.getBoolean("result");

            if(!result){
                Toast.makeText(this,"Can not bring data",Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject json = jsonResult.getJSONArray("values").getJSONObject(0);

            ((TextViewPlus)findViewById(R.id.reviewdetail_title)).setText(json.getString("SNAME"));
            ((TextViewPlus)findViewById(R.id.reviewdetail_address)).setText(json.getString("ADDR"));
            ((TextViewPlus)findViewById(R.id.reviewdetail_author)).setText(cid);
            ((RatingBar)findViewById(R.id.reviewdetail_grade)).setRating(Float.parseFloat(json.getString("GRADE")));
            ((EditTextPlus)findViewById(R.id.reviewdetail_text)).setText(json.getString("NOTE"));
            ((TextViewPlus)findViewById(R.id.reviewdetail_date)).setText(json.getString("DAY"));

        }catch (Exception ex){}
    }

    public void reviewdetailClick(View view) {
        if(view.getId()==R.id.reviewdetail_button){
            if(access.equals("STORE")){
                this.finish();
            }
            else if(access.equals("HISTORY")){
                Intent storeIntent = new Intent(this, StoreActivity.class);
                storeIntent.putExtra("LICENSE", license);
                startActivity(storeIntent);
            }
        }
    }
}
