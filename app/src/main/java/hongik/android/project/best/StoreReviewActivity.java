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

import org.json.JSONArray;
import org.json.JSONObject;

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
        DBConnector conn = new DBConnector(query);
        conn.start();

        conn.join();
        JSONObject jsonResult = conn.getResult();
        boolean result = jsonResult.getBoolean("result");

        if(!result){
            return;
        }

        String storeName = jsonResult.getString("sname");
        ((TextViewPlus)findViewById(R.id.storereview_storename)).setText(storeName);

        JSONArray review = null;
        if(!jsonResult.isNull("review")){
            review = jsonResult.getJSONArray("review");
        }

        //Draw Review Table
        if(review != null){
            TableRow motive = (TableRow) reviewTable.getChildAt(1);

            for(int i=0; i<review.length(); i++){
                JSONObject json = review.getJSONObject(i);
                final String [] elements = new String[4];
                elements[0] = Double.parseDouble(json.getString("GRADE")) + "";
                elements[1] = json.getString("NOTE");
                elements[2] = json.getString("CID#");
                elements[3] = json.getString("DAY");

                TableRow tbRow = new TableRow(this);
                TextViewPlus[] tbCols = new TextViewPlus[4];

                if(elements[1].length()>14)
                    elements[1] = elements[1].substring(0, 14) + "...";

                for(int j=0; j<4; j++){
                    tbCols[j] = new TextViewPlus(this);
                    tbCols[j].setText(elements[j]);
                    tbCols[j].setLayoutParams(motive.getChildAt(j).getLayoutParams());
                    tbCols[j].setGravity(Gravity.CENTER);
                    tbCols[j].setTypeface(Typeface.createFromAsset(tbCols[j].getContext().getAssets(), "InterparkGothicBold.ttf"));
                    tbCols[j].setOnClickListener(new View.OnClickListener() {
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

                    Log.i("StoreMenu", "COL" + j + ":" + elements[j]);
                    tbRow.addView(tbCols[j]);
                }
                reviewTable.addView(tbRow);
            }
        }
        reviewTable.removeViewAt(1);
    }
}
