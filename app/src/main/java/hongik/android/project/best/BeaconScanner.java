package hongik.android.project.best;

import android.content.Context;

import com.wizturn.sdk.central.Central;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.PeripheralScanListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015-11-30.
 */
public class BeaconScanner extends Thread{
    private Context context;
    private HashMap<String, Peripheral> itemMap;
    private ArrayList<Peripheral> items;

    public BeaconScanner(Context context){
        this.context = context;
        this.itemMap = new HashMap<>();
        this.items = new ArrayList<>();
    }

    @Override
    public void run(){
        CentralManager centralManager = CentralManager.getInstance();
        centralManager.init(context);
        centralManager.setPeripheralScanListener(new PeripheralScanListener() {
            @Override
            public void onPeripheralScan(Central central, Peripheral peripheral) {
                UpdateThread upTh = new UpdateThread(peripheral);
                upTh.start();
            }
        });
    }

    class UpdateThread extends Thread{
        private Peripheral peripheral;
        public UpdateThread(Peripheral peripheral){
            this.peripheral = peripheral;
        }

        @Override
        public void run(){
            addOrUpdateItem(peripheral);
        }

        public void addOrUpdateItem(Peripheral peripheral){
            if(itemMap.containsKey(peripheral.getBDAddress())) {
                itemMap.get(peripheral.getBDAddress()).setRssi(peripheral.getRssi());
            } else {
                items.add(peripheral);
                itemMap.put(peripheral.getBDAddress(), peripheral);
            }
        }
    }
}
