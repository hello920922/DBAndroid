package hongik.android.project.best;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Mingyu Park on 2015-11-23.
 */
public class BackPressGotoMainHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressGotoMainHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.moveTaskToBack(true);
            activity.finish();
            android.os.Process.killProcess(android.os.Process.myPid());

            toast.cancel();
        }
    }
    private void showGuide() {
        toast = Toast.makeText(activity, "If you want to go to main page, press back button again.",
                Toast.LENGTH_SHORT);
        toast.show();
    }
}