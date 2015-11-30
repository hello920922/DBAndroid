package hongik.android.project.best;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.wizturn.sdk.central.Central;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.PeripheralScanListener;

/**
 * Created by Administrator on 2015-11-30.
 */
public class BeaconScanner extends AppCompatActivity implements Runnable{
    private CentralManager centralManager;
    private Context context;
    private Peripheral nowPeripheral;
    private long timeStamp;

    public BeaconScanner(Context context){
        this.context = context;
        this.nowPeripheral = null;
    }

    @Override
    public void run(){
        centralManager = CentralManager.getInstance();
        centralManager.init(context);
        centralManager.setPeripheralScanListener(new PeripheralScanListener() {
            @Override
            public void onPeripheralScan(Central central, Peripheral peripheral) {
                UpdateThread upTh = new UpdateThread(peripheral);
                upTh.start();
            }
        });

        if(!centralManager.isBluetoothEnabled()) {
            Toast.makeText(context, "Please turn on bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        centralManager.startScanning();
        new TimeStampThread().start();
    }

    class UpdateThread extends Thread{
        private Peripheral peripheral;
        public UpdateThread(Peripheral peripheral){
            this.peripheral = peripheral;
        }

        @Override
        public void run(){
            if(nowPeripheral == null) {
                nowPeripheral = peripheral;
                timeStamp = System.currentTimeMillis();
                return;
            }
            synchronized (nowPeripheral){
                if(!nowPeripheral.getBDAddress().equals(peripheral.getBDAddress())) {
                    int nowRssi = nowPeripheral.getRssi();
                    int thisRssi = peripheral.getRssi();

                    if (nowRssi < thisRssi) {
                        checkTime();
                        nowPeripheral = peripheral;
                        timeStamp = System.currentTimeMillis();
                    }
                }
            }
        }
    }

    class TimeStampThread extends Thread{
        @Override
        public void run(){
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {}
            checkTime();
        }
    }

    public void checkTime(){
        long now = System.currentTimeMillis();
        if(now - timeStamp > 60000){

            Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);

            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
            alert_confirm.setMessage("Please write a review").setCancelable(true).setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent reviewIntent = new Intent(context, ReviewActivity.class);
                            startActivity(reviewIntent);
                        }
                    }).setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            AlertDialog alert = alert_confirm.create();
            alert.show();
        }
    }

    public Peripheral getPeripheral(){
        return nowPeripheral;
    }
}
