package hongik.android.project.best;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.wizturn.sdk.central.Central;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.PeripheralScanListener;

import layout.api.TextViewPlus;

/**
 * Created by Mingyu Park on 2015-11-23.
 */
public class HistoryActivity extends AppCompatActivity {
    private BackPressCloseHandler backHandler;

    //For Beacon Scanner
    private CentralManager centralManager;
    private Peripheral nowPeripheral;
    private long timeStamp;
    private long recent;

    private TableLayout historyTable;
    private String cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        backHandler = new BackPressCloseHandler(this);

        Intent intent = new Intent(this.getIntent());
        cid = intent.getStringExtra("CID");
        historyTable = (TableLayout)findViewById(R.id.history_table);

        setCentralManager();
        drawHistory();
    }

    public void setCentralManager(){
        centralManager = CentralManager.getInstance();
        centralManager.init(getApplicationContext());
        centralManager.setPeripheralScanListener(new PeripheralScanListener() {
            @Override
            public void onPeripheralScan(Central central, Peripheral peripheral) {
                UpdateTask updateTask = new UpdateTask(peripheral);
                updateTask.execute();
            }
        });
        centralManager.startScanning();
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

            String [] rows = result.split("/nextline");
            for(String row : rows){
                final String [] elements = row.split(",");
                Log.i("History", elements.toString());
                int colnums = elements.length-1;

                TableRow tbrow = new TableRow(this);
                final TextViewPlus[] tbcols = new TextViewPlus[colnums];

                if(elements[2].length()>14)
                    elements[2] = elements[2].substring(0, 14) + "...";
                //String[] days = elements[3].split("-");
                //elements[3] = days[0].substring(2,4) + "/" + days[1] + "/" + days[2];

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
            startActivityForResult(userIntent, 1);
        }
        else if(view.getId() == R.id.history_qrcode){
            IntentIntegrator.initiateScan(this);
        }
        else if(view.getId() == R.id.testHistory){
            Toast.makeText(this, "BD Address : " + nowPeripheral.getBDAddress(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                String license = result.getContents();
                Intent storeIntent = new Intent(this, StoreActivity.class);
                storeIntent.putExtra("LICENSE",license);
                startActivity(storeIntent);
            }
        } else {
            Log.d("MainActivity", "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
        if(requestCode==1){
            if(resultCode==1)
                finish();
        }
    }

    class UpdateTask extends AsyncTask<Void, Void, Void> {
        private Peripheral peripheral;

        public UpdateTask(Peripheral peripheral){
            this.peripheral = peripheral;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (nowPeripheral == null) {
                nowPeripheral = peripheral;
            } else if ((!nowPeripheral.getBDAddress().equals(peripheral.getBDAddress())) && nowPeripheral.getRssi() < peripheral.getRssi()
                    //&& System.currentTimeMillis() - timeStamp > 60000
                    ) {
                publishProgress();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {}
                nowPeripheral = peripheral;
                timeStamp = System.currentTimeMillis();
            }
            recent = System.currentTimeMillis();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... value){
            recommendReview();
        }
    }

    class TimeTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            while(true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {}
                if(System.currentTimeMillis() - recent > 10000) {
                    publishProgress();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... value){
            recommendReview();
        }
    }

    public void recommendReview() {
        Toast.makeText(this, nowPeripheral.getBDAddress(), Toast.LENGTH_SHORT).show();

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);

        Intent reviewIntent = new Intent(this, ReviewActivity.class);
        reviewIntent.putExtra("BUID", nowPeripheral.getBDAddress().replace(":", ""));
        startActivity(reviewIntent);
    }
}
