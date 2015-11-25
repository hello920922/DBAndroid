package hongik.android.project.best;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import layout.api.TextViewPlus;

public class StoreActivity extends AppCompatActivity {
    private String license;
    private TableLayout menuTable;
    private TableLayout reviewTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        Intent intent = this.getIntent();
        license = intent.getStringExtra("LICENSE");
        menuTable = (TableLayout)findViewById(R.id.store_menu);
        reviewTable = (TableLayout)findViewById(R.id.store_review);

        try {
            drawPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawPage() throws Exception{
        String query = "func=storereview" + "&license=" + license;

        URLConnector conn = new URLConnector(Constant.QueryURL, "POST", query);
        conn.start();
        conn.join();

        String result = conn.getResult();

        String[] results = result.split("/nextResult/");

        String store = results[0];
        String menu = results[1];
        String review = results[2];

        String[] store_info = store.split(",");
        ((TextViewPlus)findViewById(R.id.store_storename)).setText(store_info[0]);
        ((TextViewPlus)findViewById(R.id.store_address)).setText(store_info[1]);
        ImageLoader imgLoader = new ImageLoader(store_info[2]);
        imgLoader.start();
        try {
            imgLoader.join();
            Bitmap storeImg = imgLoader.getBitmap();
            ((ImageView)findViewById(R.id.store_image)).setImageBitmap(storeImg);
        } catch (InterruptedException e) {
            Toast.makeText(this, "Can not bring " + license + "store's image", Toast.LENGTH_SHORT).show();
            Log.e("StoreInfo", "Can not bring " + license + "store's image");
        }

        if(menu != null){

            TableRow blankrow = new TableRow(this);
            for(int i=0; i<4; i++) {
                TextView tv = new TextViewPlus(this);
                tv.setHeight(15);
                blankrow.addView(tv);
            }
            menuTable.addView(blankrow);

            TableRow motive = (TableRow) menuTable.getChildAt(0);

            String[] menu_rows = menu.split("/");
            for(String menu_row : menu_rows){
                final String [] elements = menu_row.split(",");
                int colnums = elements.length-1;

                TableRow marginrow = new TableRow(this);
                for(int i=0; i<colnums; i++) {
                    TextView tv = new TextViewPlus(this);
                    tv.setHeight(15);
                    marginrow.addView(tv);
                }

                TableRow tbrow = new TableRow(this);
                final TextViewPlus[] tbcols = new TextViewPlus[colnums];

                for(int i=0; i<colnums; i++){
                    tbcols[i] = new TextViewPlus(this);
                    if(i==2 && elements[i].length()>14)
                        elements[i] = elements[i].substring(0, 14) + "...";
                    tbcols[i].setText(elements[i]);
                    tbcols[i].setLayoutParams(motive.getChildAt(i).getLayoutParams());
                    tbcols[i].setGravity(Gravity.CENTER);
                    tbcols[i].setTypeface(Typeface.createFromAsset(tbcols[i].getContext().getAssets(), "InterparkGothicBold.ttf"));
                    final StoreActivity originActivity = this;
                    tbcols[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent storeIntent = new Intent(originActivity, MenuActivity.class);
                            storeIntent.putExtra("LICENSE", elements[elements.length-1] );
                            startActivity(storeIntent);
                        }
                    });

                    Log.i("History", "COL" + i + ":" + elements[i]);

                    tbrow.addView(tbcols[i]);
                }
                menuTable.addView(tbrow);
                menuTable.addView(marginrow);
            }
        }
    }

    public void storeClick(View view) {
        if(view.getId() == R.id.store_more){
            Intent storeReviewIntent = new Intent(this, StoreReviewActivity.class);
            storeReviewIntent.putExtra("LICENSE", license);
            startActivity(storeReviewIntent);
        }
    }
}