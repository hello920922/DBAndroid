package hongik.android.project.best;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
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
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        backHandler = new BackPressCloseHandler(this);

        Intent intent = new Intent(this.getIntent());
        id = intent.getStringExtra("CID");
        historyTable = (TableLayout)findViewById(R.id.history_table);

        drawHistory();
    }

    public void drawHistory(){
        String query = "func=history&cid=" + id;
        URLConnector conn = new URLConnector(Constant.SERVER, "POST",query);
        conn.start();

        try {
            conn.join();
            String result = conn.getResult();
            if(result.equals("ERROR")){
                Toast.makeText(this, "Can not bring data", Toast.LENGTH_SHORT).show();
                return;
            }

            TableRow motive = (TableRow)historyTable.getChildAt(0);

            String [] rows = result.split("/");
            for(String row : rows){
                String [] elements = row.split(",");
                int colnums = elements.length;

                TableRow marginrow = new TableRow(this);
                marginrow.addView(new TextViewPlus(this));
                marginrow.addView(new TextViewPlus(this));
                marginrow.addView(new TextViewPlus(this));
                marginrow.addView(new TextViewPlus(this));

                TableRow tbrow = new TableRow(this);
                TextViewPlus[] tbcols = new TextViewPlus[colnums];

                for(int i=0; i<colnums; i++){
                    tbcols[i] = new TextViewPlus(this);
                    tbcols[i].setText(elements[i]);
                    tbcols[i].setLayoutParams(motive.getChildAt(i).getLayoutParams());
                    tbcols[i].setGravity(Gravity.CENTER);

                    Log.i("History", "COL" + i + ":" + elements[i]);

                    tbrow.addView(tbcols[i]);
                }
                historyTable.addView(marginrow);
                historyTable.addView(tbrow);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed(){
        backHandler.onBackPressed();
    }
}
