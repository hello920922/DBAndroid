package hongik.android.project.best;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

    public void onClick(View view) {
        if(view.getId() == R.id.btnsubmit){
            // Setting URL
            String url = "http://hello0922.iptime.org/Database/android.php";

            // Setting Method
            String method = "";
            RadioButton radget = (RadioButton)findViewById(R.id.radget);
            RadioButton radpost = (RadioButton)findViewById(R.id.radpost);

            if(radget.isChecked())
                method = "GET";
            else if(radpost.isChecked())
                method = "POST";
            else{
                Toast.makeText(this, "Please check method.", Toast.LENGTH_LONG).show();
                return;
            }

            // Setting Query
            String query = "";
            CharSequence txtid = ((EditText)findViewById(R.id.txtid)).getText().toString();
            CharSequence txtpasswd = ((EditText)findViewById(R.id.txtpasswd)).getText().toString();
            query = "id=" + txtid + "&passwd=" + txtpasswd;

            // Show Toast Log
            Toast.makeText(this, "Send to Server id: " + txtid + " password: " + txtpasswd, Toast.LENGTH_LONG).show();

            // Run URLConnector
            URLConnector test = new URLConnector(url, method, query);
            test.start();

            // Waiting for Thread
            try{
                test.join();
                Log.i("SampleHTTP","Waiting for result....");
            }catch(InterruptedException e){}

            // Take Result
            CharSequence result = test.getResult();

            // Set TextView
            TextView txtTest = (TextView)findViewById(R.id.txtResult);
            txtTest.setText(result);
        }
    }
}