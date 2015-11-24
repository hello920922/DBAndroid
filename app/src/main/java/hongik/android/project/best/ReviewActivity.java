package hongik.android.project.best;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
    }

    public void reviewClick(View view) {
        int viewId = view.getId();
    }
}
