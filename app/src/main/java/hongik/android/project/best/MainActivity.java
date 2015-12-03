package hongik.android.project.best;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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
        DBConnector conn = new DBConnector(query);
        conn.start();

        try{
            conn.join();
        }catch(InterruptedException ex){
            Log.e("Signin","InterruptedException");
            return;
        }

        JSONObject jsonResult = conn.getResult();
        try {
            boolean result = (boolean)jsonResult.get("result");
            if(!result){
                Toast.makeText(this, "Login Failure",Toast.LENGTH_SHORT).show();
                return;
            }
            String name = (String)((JSONObject)jsonResult.getJSONArray("values").get(0)).get("NAME");
            Toast.makeText(this, "Nice to meet you " + name, Toast.LENGTH_SHORT).show();

            Intent historyIntent = new Intent(this, HistoryActivity.class);
            historyIntent.putExtra("CID", viewId.getText().toString());
            startActivity(historyIntent);
        } catch (JSONException e) { }
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