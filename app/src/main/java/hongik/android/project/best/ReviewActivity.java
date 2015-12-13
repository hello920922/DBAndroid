package hongik.android.project.best;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONObject;

import layout.api.EditTextPlus;
import layout.api.TextViewPlus;

public class ReviewActivity extends AppCompatActivity {
    private String sname;
    private String addr;
    private String buid;
    private String cid;
    private String license;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        buid = intent.getStringExtra("BUID");
        cid = intent.getStringExtra("CID");

        try{drawReview();}catch (Exception ex){}
        showAlertDialog();
    }

    public void drawReview() throws Exception {
        String query = "func=beaconreview" + "&buid=" + buid;
        DBConnector conn = new DBConnector(query);
        conn.start();
        conn.join();
        JSONObject jsonResult = conn.getResult();
        boolean result = jsonResult.getBoolean("result");

        if (!result) {
            Toast.makeText(this, "This store is not beacon store",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        JSONObject json = jsonResult.getJSONArray("values").getJSONObject(0);

        sname = json.getString("SNAME");
        addr = json.getString("ADDR");
        license = json.getString("LICENSE#");

        ((TextViewPlus) findViewById(R.id.review_title)).setText(sname);
        ((TextViewPlus) findViewById(R.id.review_address)).setText(addr);
    }

    public void showAlertDialog(){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
        alert_confirm.setMessage("Please write a review : about "
                + sname).setCancelable(true);
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    public void reviewClick(View view) {
        if(view.getId() == R.id.review_submit){
            String note = ((EditTextPlus)findViewById(R.id.review_text)).getText().toString();
            float grade = ((RatingBar)findViewById(R.id.review_grade)).getRating();

            String query = "func=submitreview" +
                    "&license=" + license +
                    "&cid=" + cid +
                    "&note=" + note +
                    "&grade=" + grade;
            DBConnector conn = new DBConnector(query);
            conn.start();

            try {
                conn.join();
                JSONObject jsonResult = conn.getResult();
                boolean result = jsonResult.getBoolean("result");

                if(!result){
                    Toast.makeText(this, "Submit Error", Toast.LENGTH_SHORT).show();
                    setResult(0, new Intent());
                    finish();
                    return;
                }
                Toast.makeText(this, "Thank you for your opinion", Toast.LENGTH_SHORT).show();
                setResult(1, new Intent());
                finish();
            } catch (Exception e) {}
        }
    }
}
