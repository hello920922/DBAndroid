package hongik.android.project.best;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import layout.api.EditTextPlus;
import layout.api.RadioPlus;
import layout.api.SpinnerAdapter;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        String [] array = {"Choose Email", "naver.com", "hanmail.net", "gmail.com", "nate.com", "dreamwiz.com", "korea.com", "outlook.com", "hongik.ac.kr"};

        SpinnerAdapter spinadapter = new SpinnerAdapter(this, R.layout.content_signup, array);
        Spinner spin = (Spinner)findViewById(R.id.sign_email_t);
        spin.setAdapter(spinadapter);
    }

    public void signupClick(View view) {
        if(view.getId() == R.id.sign_signup){
            String id = ((EditTextPlus)findViewById(R.id.sign_id)).getText().toString();
            String passwd = ((EditTextPlus)findViewById(R.id.sign_passwd)).getText().toString();
            String repeat = ((EditTextPlus)findViewById(R.id.sign_repeat)).getText().toString();
            String name = ((EditTextPlus)findViewById(R.id.sign_name)).getText().toString();
            String birth = ((EditTextPlus)findViewById(R.id.sign_person_h)).getText().toString();
            String personNo = ((EditTextPlus)findViewById(R.id.sign_person_t)).getText().toString();
            String gender = "";
            Log.i("Signup", "success fetching each string");

            if(((RadioPlus)findViewById(R.id.sign_radio_m)).isChecked()){
                gender = "M";
            }
            else if(((RadioPlus)findViewById(R.id.sign_radio_f)).isChecked()){
                gender = "F";
            }
            Log.i("Signup", "success fetching gender");

            if(!passwd.equals(repeat)){
                Toast.makeText(this, "Repeat Password does not match", Toast.LENGTH_SHORT).show();
                return;
            }

            String phone_h = ((EditTextPlus)findViewById(R.id.sign_phone_h)).getText().toString();
            String phone_m = ((EditTextPlus)findViewById(R.id.sign_phone_m)).getText().toString();
            String phone_t = ((EditTextPlus)findViewById(R.id.sign_phone_t)).getText().toString();
            String email_h = ((EditTextPlus)findViewById(R.id.sign_email_h)).getText().toString();
            int email_t = ((Spinner)findViewById(R.id.sign_email_t)).getSelectedItemPosition();
            Log.i("Signup", "success fetching other strings");

            if(id.equals("") || passwd.equals("") || name.equals("") || birth.equals("") || personNo.equals("") || gender.equals("") || phone_h.equals("") || phone_m.equals("") || phone_t.equals("") || email_h.equals("") || email_t == 0){
                Toast.makeText(this,"Please fill in all forms", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i("Signup", "All form is filled");

            String phone = phone_h + phone_m + phone_t;
            String email = email_h + "@" + ((Spinner)findViewById(R.id.sign_email_t)).getItemAtPosition(email_t).toString();

            String query = "func=signup" + "&id=" + id + "&passwd=" + passwd + "&name=" + name + "&birth=" + birth + "&personNo=" + personNo + "&gender=" + gender + "&phone=" + phone + "&email=" + email;
            Log.i("Signup", "Success making query : " + query);

            URLConnector conn = new URLConnector(Constant.QueryURL, "POST",query);
            conn.start();
            Log.i("Signup", "Success send query to server");

            try{
                conn.join();
            }catch(InterruptedException ex){Log.e("Signup",ex.toString());return;};
            String result = conn.getResult();
            Log.i("Signup", "Fetching php result : " + result);

            if(result.equals("ERROR")){
                Toast.makeText(this, "Signup Failure", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Success Signup", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }
}
