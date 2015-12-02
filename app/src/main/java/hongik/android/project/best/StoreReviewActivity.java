package hongik.android.project.best;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import layout.api.TextViewPlus;

public class StoreReviewActivity extends AppCompatActivity {
    private String license;
    private TableLayout reviewTable;

    final StoreReviewActivity originActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storereview);

        license = getIntent().getStringExtra("LICENSE");
        reviewTable = (TableLayout)findViewById(R.id.storereview_table);

        try{
            drawTable();
        }catch (Exception ex){
            Log.i("StoreMenu", ex.toString());
        }
    }

    public void drawTable() throws Exception{
        String query = "func=morereview" + "&license=" + license;
        URLConnector conn = new URLConnector(Constant.QueryURL, "POST", query);
        conn.start();

        conn.join();
        String result = conn.getResult();

        String[] results = result.split("/nextResult/");
        String storeName = results[0];
        String review = null;
        if(results.length > 1)
            review = results[1];

        ((TextViewPlus)findViewById(R.id.storereview_storename)).setText(storeName);

        //Draw Review Table
        if(review != null){
            TableRow motive = (TableRow) reviewTable.getChildAt(1);

            String[] review_rows = review.split("/nextline");
            for(String review_row : review_rows){
                final String [] elements = review_row.split(",");
                int colnums = elements.length;

                TableRow tbrow = new TableRow(this);
                TextViewPlus[] tbcols = new TextViewPlus[colnums];

                if(elements[1].length()>14)
                    elements[1] = elements[1].substring(0, 14) + "...";
                //String[] days = elements[3].split("-");
                //elements[3] = days[0].substring(2,4) + "/" + days[1] + "/" + days[2];

                for(int i=0; i<colnums; i++){
                    tbcols[i] = new TextViewPlus(this);
                    tbcols[i].setText(elements[i]);
                    tbcols[i].setLayoutParams(motive.getChildAt(i).getLayoutParams());
                    tbcols[i].setGravity(Gravity.CENTER);
                    tbcols[i].setTypeface(Typeface.createFromAsset(tbcols[i].getContext().getAssets(), "InterparkGothicBold.ttf"));
                    tbcols[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent reviewIntent = new Intent(originActivity, ReviewDetailActivity.class);
                            reviewIntent.putExtra("ACCESS", "STORE");
                            reviewIntent.putExtra("CID", elements[2]);
                            reviewIntent.putExtra("LICENSE", license);
                            Log.i("StoreReview", "StartActivity");
                            startActivity(reviewIntent);
                        }
                    });

                    Log.i("StoreMenu", "COL" + i + ":" + elements[i]);
                    tbrow.addView(tbcols[i]);
                }
                reviewTable.addView(tbrow);
            }
        }
        reviewTable.removeViewAt(1);
    }
}
