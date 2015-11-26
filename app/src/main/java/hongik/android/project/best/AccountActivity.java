package hongik.android.project.best;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

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


    public void accountClick(View view) {
        int id = view.getId();
        if(id == R.id.account_changes){
            try{
                changeAccount();
            }catch (Exception ex){
                ex.printStackTrace();
                Log.e("Account", ex.getMessage());
            }
        }
        else if(id == R.id.account_delete){
            try{
                deleteAccount();
            }catch (Exception ex){
                ex.printStackTrace();
                Log.e("Account", ex.getMessage());
            }
        }
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

    public void changeAccount() throws Exception{
        String newpasswd = ((EditTextPlus)findViewById(R.id.account_newpasswd)).getText().toString();
        String repeat = ((EditTextPlus)findViewById(R.id.account_repeat)).getText().toString();

        if(!newpasswd.equals(repeat)){
            Toast.makeText(this, "Repeat Password does not match", Toast.LENGTH_SHORT).show();
            return;
        }

        String passwd = ((EditTextPlus)findViewById(R.id.account_passwd)).getText().toString();
        String name = ((EditTextPlus)findViewById(R.id.account_name)).getText().toString();
        String birth = ((EditTextPlus)findViewById(R.id.account_person_h)).getText().toString();
        String phone_h = ((EditTextPlus)findViewById(R.id.account_phone_h)).getText().toString();
        String phone_m = ((EditTextPlus)findViewById(R.id.account_phone_m)).getText().toString();
        String phone_t = ((EditTextPlus)findViewById(R.id.account_phone_t)).getText().toString();
        String email_h = ((EditTextPlus)findViewById(R.id.account_email_h)).getText().toString();
        int email_t = ((Spinner)findViewById(R.id.account_email_t)).getSelectedItemPosition();

        if(passwd.equals("") || name.equals("") || birth.equals("") || phone_h.equals("") || phone_m.equals("") || phone_t.equals("") || email_h.equals("") || email_t==0){
            Toast.makeText(this,"Please fill in all forms", Toast.LENGTH_SHORT).show();
            return;
        }

        String query = "func=changeacc" + "&cid=" + cid + "&passwd=" + passwd + "&name=" + name +
                "&birth=" + birth + "&phone=" + phone_h + phone_m + phone_t +
                "&email=" + email_h + "@" + ((Spinner)findViewById(R.id.account_email_t)).getItemAtPosition(email_t).toString();

        if(!newpasswd.equals("")){
            query += "&newpasswd=" + newpasswd;
        }

        URLConnector conn = new URLConnector(Constant.QueryURL, "POST", query);
        conn.start();
        conn.join();

        String result = conn.getResult();

        if(result.equals("WRONG")) {
            Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(result.equals("ERROR")){
            Toast.makeText(this, "Change Failure", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(result.equals("SUCCESS")){
            Toast.makeText(this, "Success Change", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    public void deleteAccount() throws Exception{
        String passwd = ((EditTextPlus)findViewById(R.id.account_passwd)).getText().toString();
        if(passwd.equals("")){
            Toast.makeText(this,"Please input password", Toast.LENGTH_SHORT).show();
            return;
        }

        String query = "func=deleteacc" + "&cid=" + cid + "&passwd=" + passwd;

        URLConnector conn = new URLConnector(Constant.QueryURL, "POST", query);
        conn.start();
        conn.join();

        String result = conn.getResult();

        if(result.equals("WRONG")) {
            Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(result.equals("ERROR")){
            Toast.makeText(this, "Delete Failure", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(result.equals("SUCCESS")){
            Toast.makeText(this, "Success Delete", Toast.LENGTH_SHORT).show();
            setResult(1, new Intent());
            finish();
        }
    }
}