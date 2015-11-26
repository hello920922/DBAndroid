package hongik.android.project.best;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;

import layout.api.EditTextPlus;
import layout.api.RadioPlus;
import layout.api.SpinnerAdapter;

public class AccountActivity extends AppCompatActivity {
    private String cid;
    private SpinnerAdapter spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        String [] array = {"Choose Email", "naver.com", "hanmail.net", "gmail.com", "nate.com", "dreamwiz.com", "korea.com", "outlook.com", "hongik.ac.kr"};

        spinnerAdapter = new SpinnerAdapter(this, R.layout.content_account, array);
        Spinner spin = (Spinner)findViewById(R.id.account_email_t);
        spin.setAdapter(spinnerAdapter);

        cid = getIntent().getStringExtra("CID");

        try {
            drawInfo();
        }catch(Exception e){};
    }

    public void drawInfo() throws Exception{
        String query = "func=account&cid=" + cid;
        URLConnector conn = new URLConnector(Constant.QueryURL, "POST", query);
        conn.start();

        conn.join();
        String result = conn.getResult();

        String[] results = result.split(",");

        String name = results[0];
        String birth = results[1];
        String gender = results[2];
        String phone = results[3];
        String[] email = results[4].split("@");

        String phone_h = phone.substring(0, 3);
        String phone_m = phone.substring(3, 7);
        String phone_t = phone.substring(7, 11);

        String email_h = email[0];
        String email_t = email[1];

        ((EditTextPlus)findViewById(R.id.account_id)).setText(cid);
        ((EditTextPlus)findViewById(R.id.account_name)).setText(name);
        ((EditTextPlus)findViewById(R.id.account_person_h)).setText(birth);
        ((EditTextPlus)findViewById(R.id.account_person_t)).setText("1234567");
        if(gender.equals("M")){
            ((RadioPlus)findViewById(R.id.account_radio_m)).setChecked(true);
        }
        else if(gender.equals("F")){
            ((RadioPlus)findViewById(R.id.account_radio_f)).setChecked(true);
        }

        ((EditTextPlus)findViewById(R.id.account_phone_h)).setText(phone_h);
        ((EditTextPlus)findViewById(R.id.account_phone_m)).setText(phone_m);
        ((EditTextPlus)findViewById(R.id.account_phone_t)).setText(phone_t);

        ((EditTextPlus)findViewById(R.id.account_email_h)).setText(email_h);
        ((Spinner) findViewById(R.id.account_email_t)).setSelection(spinnerAdapter.getPosition(email_t));
    }
}
