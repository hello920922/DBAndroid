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
import android.widget.Toast;

import layout.api.TextViewPlus;

/**
 * Created by Mingyu Park on 2015-11-23.
 */
public class HistoryActivity extends AppCompatActivity {
    private BackPressCloseHandler backHandler;
    private TableLayout historyTable;
    private String cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        backHandler = new BackPressCloseHandler(this);

        Intent intent = new Intent(this.getIntent());
        cid = intent.getStringExtra("CID");     // 인텐트 객체에 담긴 데이터 읽어오기
        historyTable = (TableLayout)findViewById(R.id.history_table);

        drawHistory();
    }

    public void drawHistory(){
        String query = "func=history&cid=" + cid;
        URLConnector conn = new URLConnector(Constant.QueryURL, "POST",query);
        conn.start();

        try {
            conn.join();
            String result = conn.getResult();
            if(result.equals("ERROR")){
                Toast.makeText(this, "Can not bring data", Toast.LENGTH_SHORT).show();
                return;
            }

            TableRow motive = (TableRow)historyTable.getChildAt(1);     // 자식객체에 숫자인덱스로 접근해서 제어

            String [] rows = result.split("/");
            for(String row : rows){
                final String [] elements = row.split(",");
                int colnums = elements.length-1;

                TableRow tbrow = new TableRow(this);
                final TextViewPlus[] tbcols = new TextViewPlus[colnums];

                if(elements[2].length()>14)
                    elements[2] = elements[2].substring(0, 14) + "...";
                String[] days = elements[3].split("-");
                elements[3] = days[0].substring(2,4) + "/" + days[1] + "/" + days[2];

                for(int i=0; i<colnums; i++){
                    tbcols[i] = new TextViewPlus(this);
                    tbcols[i].setText(elements[i]);
                    tbcols[i].setLayoutParams(motive.getChildAt(i).getLayoutParams());
                    tbcols[i].setGravity(Gravity.CENTER);
                    tbcols[i].setTypeface(Typeface.createFromAsset(tbcols[i].getContext().getAssets(), "InterparkGothicBold.ttf"));
                    final HistoryActivity originActivity = this;
                    tbcols[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent reviewIntent = new Intent(originActivity, ReviewDetailActivity.class);
                            reviewIntent.putExtra("ACCESS", "HISTORY");
                            reviewIntent.putExtra("CID", cid);
                            reviewIntent.putExtra("LICENSE", elements[elements.length - 1]);
                            startActivity(reviewIntent);
                        }
                    });

                    Log.i("History", "COL" + i + ":" + elements[i]);

                    tbrow.addView(tbcols[i]);
                }
                historyTable.addView(tbrow);
            }
            historyTable.removeViewAt(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed(){
        backHandler.onBackPressed();
    }

    public void historyClick(View view) {
        if(view.getId() == R.id.history_user){
            Intent userIntent = new Intent(this, AccountActivity.class);
            userIntent.putExtra("CID",cid);
            startActivity(userIntent);
        }
    }
}
