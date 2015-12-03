package hongik.android.project.best;

import android.bluetooth.BluetoothAdapter;
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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.wizturn.sdk.central.Central;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.PeripheralScanListener;

import org.json.JSONArray;
import org.json.JSONObject;

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

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableBtIntent);

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
        int rowCnt = historyTable.getChildCount();
        Log.i("ROWCOUNT", rowCnt+"");
        String query = "func=history&cid=" + cid;
        DBConnector conn = new DBConnector(query);
        conn.start();

        try {
            conn.join();
            JSONObject jsonResult = conn.getResult();
            boolean result = (boolean)jsonResult.get("result");

            if(!result)
                return;

            TableRow motive = (TableRow)historyTable.getChildAt(1);
            JSONArray jsonArray = jsonResult.getJSONArray("values");

            for(int i=0; i< jsonArray.length(); i++){
                TableRow tbRow = new TableRow(this);
                final TextViewPlus[] tbCols = new TextViewPlus[4];
                JSONObject json = jsonArray.getJSONObject(i);
                String[] elements = new String[4];

                elements[0] = json.getString("SNAME");
                elements[1] = Double.parseDouble(json.getString("GRADE")) + "";
                elements[2] = json.getString("NOTE");
                elements[3] = json.getString("DAY");
                final String license = json.getString("LICENSE#");

                if(elements[2].length() > 14)
                    elements[2] = elements[2].substring(0, 14) + "...";

                for(int j=0; j<4; j++){
                    tbCols[j] = new TextViewPlus(this);
                    tbCols[j].setText(elements[j]);
                    tbCols[j].setLayoutParams(motive.getChildAt(j).getLayoutParams());
                    tbCols[j].setGravity(Gravity.CENTER);
                    tbCols[j].setTypeface(Typeface.createFromAsset(tbCols[j].getContext().getAssets(), "InterparkGothicBold.ttf"));
                    final HistoryActivity originActivity = this;
                    tbCols[j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent reviewIntent = new Intent(originActivity, ReviewDetailActivity.class);
                            reviewIntent.putExtra("ACCESS", "HISTORY");
                            reviewIntent.putExtra("CID", cid);
                            reviewIntent.putExtra("LICENSE", license);
                            startActivity(reviewIntent);
                        }
                    });

                    Log.i("History", "COL" + j + ":" + elements[j]);

                    tbRow.addView(tbCols[j]);
                }
                historyTable.addView(tbRow);
            }
            for(int i=1; i<rowCnt; i++) {
                historyTable.removeViewAt(1);
            }
        } catch (Exception e) {
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==2){
            if(resultCode==1){
                drawHistory();
            }
        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
            } else {
                Log.d("MainActivity", "Scanned");
                String license = result.getContents();
                Intent storeIntent = new Intent(this, StoreActivity.class);
                storeIntent.putExtra("LICENSE",license);
                startActivity(storeIntent);
            }
        } else {
            Log.d("MainActivity", "Weird");
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
                    && System.currentTimeMillis() - timeStamp > 60000
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
                if(System.currentTimeMillis() - recent > 10000
                        && System.currentTimeMillis() - timeStamp > 60000
                        ) {
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
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);

        Intent reviewIntent = new Intent(this, ReviewActivity.class);
        reviewIntent.putExtra("BUID", nowPeripheral.getBDAddress().replace(":", ""));
        reviewIntent.putExtra("CID", cid);
        nowPeripheral = null;
        startActivityForResult(reviewIntent, 2);
    }
}