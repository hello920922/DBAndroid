package hongik.android.project.best;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import layout.api.TextViewPlus;

public class ReviewActivity extends AppCompatActivity {
    private String sname;
    private String addr;
    private String buid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        buid = intent.getStringExtra("BUID");

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

            String [] results = result.split(",");

            sname = results[0];
            addr = results[1];

            ((TextViewPlus)findViewById(R.id.review_title)).setText(sname);
            ((TextViewPlus)findViewById(R.id.review_address)).setText(addr);
        } catch (InterruptedException e) {}
    }

    public void showAlertDialog(){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
        alert_confirm.setMessage("Please write a review : about " + sname).setCancelable(true);
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    public void reviewClick(View view) {
        int viewId = view.getId();
    }
}
