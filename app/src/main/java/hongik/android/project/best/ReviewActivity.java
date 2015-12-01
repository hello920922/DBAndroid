package hongik.android.project.best;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

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

        drawReview();
        showAlertDialog();
    }

    public void drawReview(){
        String query = "func=beaconreview" + "&buid=" + buid;
        URLConnector conn = new URLConnector(Constant.QueryURL, "POST", query);
        conn.start();

        String result = "";
        try {
            conn.join();
            result = conn.getResult();

            if(result.equals("ERROR")){
                Toast.makeText(this,"This store is not beacon store",Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                String[] results = result.split(",");

                sname = results[0];
                addr = results[1];
                license = results[2];

                ((TextViewPlus) findViewById(R.id.review_title)).setText(sname);
                ((TextViewPlus) findViewById(R.id.review_address)).setText(addr);
            }
        } catch (InterruptedException e) {}
    }

    public void showAlertDialog(){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
        alert_confirm.setMessage("Please write a review : about " + sname).setCancelable(true);
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
            URLConnector conn = new URLConnector(Constant.QueryURL, "POST", query);
            conn.start();

            try {
                conn.join();
                String result = conn.getResult();
                if(result.equals("ERROR")){
                    Toast.makeText(this, "Submit Error", Toast.LENGTH_SHORT).show();
                    setResult(0, new Intent());
                    finish();
                    return;
                }
                Toast.makeText(this, "Thank you for your opinion", Toast.LENGTH_SHORT).show();
                setResult(1, new Intent());
                finish();
            } catch (InterruptedException e) {}
        }
    }
}
