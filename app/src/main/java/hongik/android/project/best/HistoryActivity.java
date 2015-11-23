package hongik.android.project.best;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        backHandler = new BackPressCloseHandler(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
