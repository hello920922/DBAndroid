package hongik.android.project.best;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;

import layout.api.SpinnerAdapter;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        String [] array = {"gmail.com", "naver.com", "hanmail.net", "hongik.ac.kr"};

        SpinnerAdapter spinadapter = new SpinnerAdapter(this, R.layout.account, array);
        Spinner spin = (Spinner)findViewById(R.id.account_email_t);
        spin.setAdapter(spinadapter);
    }


}
