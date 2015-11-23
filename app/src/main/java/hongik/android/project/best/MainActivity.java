package hongik.android.project.best;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import layout.api.EditTextPlus;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RatingBar ratingbar = (RatingBar)findViewById(R.id.reviewid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinner_count =(Spinner)findViewById(R.id.countemail);

        /*ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.array,android.
                R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_count.setAdapter(adapter);*/

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
        if(view.getId() == R.id.main_signin){
            Signin();
        }
    }
    public void Signin(){
        EditTextPlus viewId = (EditTextPlus)findViewById(R.id.main_id);
        EditTextPlus viewpasswd = (EditTextPlus)findViewById(R.id.main_passwd);

        String query = "query=select * from CUSTOMER where CID='" + viewId.getText().toString() + "'and PASSWORD='" + viewpasswd.getText().toString() + "'";
        //Toast.makeText(this, query, Toast.LENGTH_LONG).show();
        URLConnector conn = new URLConnector(Constant.SERVER + "signin.php", "POST", query);
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
}