package hongik.android.project.best;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Mingyu Park on 2015-11-23.
 */
public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
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
        toast = Toast.makeText(activity, "Press 'back' button once more to exit.",
                Toast.LENGTH_SHORT);
        toast.show();
    }
}