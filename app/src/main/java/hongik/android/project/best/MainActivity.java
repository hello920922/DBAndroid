package hongik.android.project.best;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import layout.api.EditTextPlus;

public class MainActivity extends AppCompatActivity {
    private BackPressCloseHandler backHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backHandler = new BackPressCloseHandler(this);
    }

    public void mainClick(View view) {
        if(view.getId() == R.id.main_signin){
            Signin();
        }
        else if(view.getId() == R.id.main_signup){
            Signup();
        }
    }

    public void Signin(){
        EditTextPlus viewId = (EditTextPlus)findViewById(R.id.main_id);
        EditTextPlus viewpasswd = (EditTextPlus)findViewById(R.id.main_passwd);

        String query = "func=signin&cid=" + viewId.getText().toString() + "&passwd=" + viewpasswd.getText().toString();
        URLConnector conn = new URLConnector(Constant.QueryURL, "POST", query);
        conn.start();

        try{
            conn.join();
        }catch(InterruptedException ex){
            Log.e("Signin","InterruptedException");
            return;
        }

        String result = conn.getResult();
        if(result.equals("ERROR")){
            Toast.makeText(this, "Login Failure",Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Nice to meet you " + result, Toast.LENGTH_SHORT).show();

        Intent historyIntent = new Intent(this, HistoryActivity.class);
        historyIntent.putExtra("CID", viewId.getText().toString());
        startActivity(historyIntent);
    }

    public void Signup(){
        Intent signupIntent = new Intent(this, SignupActivity.class);
        startActivity(signupIntent);
    }

    @Override
    public void onBackPressed(){
        backHandler.onBackPressed();
    }
}