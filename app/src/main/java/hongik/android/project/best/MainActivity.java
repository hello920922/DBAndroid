package hongik.android.project.best;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import layout.api.EditTextPlus;
import layout.api.SpinnerAdapter;

public class MainActivity extends AppCompatActivity {
    private BackPressCloseHandler backHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        backHandler = new BackPressCloseHandler(this);

        String [] array = {"gmail.com", "naver.com", "hanmail.net", "hongik.ac.kr"};

        SpinnerAdapter spinadapter = new SpinnerAdapter(this, R.layout.content_account, array);
        Spinner spin = (Spinner)findViewById(R.id.account_email_t);
        spin.setAdapter(spinadapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void mainClick(View view) {
        if(view.getId() == R.id.main_signin){
            Signin();
        }
    }

    public void Signin(){
        EditTextPlus viewId = (EditTextPlus)findViewById(R.id.main_id);
        EditTextPlus viewpasswd = (EditTextPlus)findViewById(R.id.main_passwd);

        String query = "func=signin&cid=" + viewId.getText().toString() + "&passwd=" + viewpasswd.getText().toString();
        URLConnector conn = new URLConnector(Constant.SERVER, "POST", query);
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

    @Override
    public void onBackPressed(){
        backHandler.onBackPressed();
    }
}