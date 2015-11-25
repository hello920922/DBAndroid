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
import android.widget.Toast;

import layout.api.TextViewPlus;

public class StoreActivity extends AppCompatActivity {
    private String license;
    private TableLayout menuTable;
    private TableLayout reviewTable;
    final StoreActivity originActivity = this;

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

        //Draw Store Information
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

        //Draw Menu Table
        if(menu != null){
            TableRow motive = (TableRow) menuTable.getChildAt(1);

            String[] menu_rows = menu.split("/");
            for(String menu_row : menu_rows){
                final String [] elements = menu_row.split(",");
                int colnums = elements.length;

                TableRow tbrow = new TableRow(this);
                TextViewPlus[] tbcols = new TextViewPlus[colnums];

                imgLoader = new ImageLoader(elements[0]);
                imgLoader.start();
                imgLoader.join();

                ImageView img = new ImageView(this);
                Bitmap bitmap = imgLoader.getBitmap();
                img.setImageBitmap(bitmap);
                img.setLayoutParams(motive.getChildAt(0).getLayoutParams());
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent storeIntent = new Intent(originActivity, MenuActivity.class);
                        storeIntent.putExtra("LICENSE", license);
                        storeIntent.putExtra("MENU", elements[1]);
                        startActivity(storeIntent);
                    }
                });

                tbrow.addView(img);
                for(int i=1; i<colnums; i++){
                    tbcols[i] = new TextViewPlus(this);
                    tbcols[i].setText(elements[i]);
                    tbcols[i].setLayoutParams(motive.getChildAt(i).getLayoutParams());
                    tbcols[i].setGravity(Gravity.CENTER);
                    tbcols[i].setTypeface(Typeface.createFromAsset(tbcols[i].getContext().getAssets(), "InterparkGothicBold.ttf"));
                    tbcols[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent storeIntent = new Intent(originActivity, MenuActivity.class);
                            storeIntent.putExtra("LICENSE", license);
                            storeIntent.putExtra("MENU", elements[1] );
                            startActivity(storeIntent);
                        }
                    });

                    Log.i("StoreMenu", "COL" + i + ":" + elements[i]);
                    tbrow.addView(tbcols[i]);
                }
                menuTable.addView(tbrow);
            }
        }
        menuTable.removeViewAt(1);

        //Draw Review Table
        if(review != null){
            TableRow motive = (TableRow) reviewTable.getChildAt(1);

            String[] review_rows = review.split("/");
            for(String review_row : review_rows){
                final String [] elements = review_row.split(",");
                int colnums = elements.length;

                TableRow tbrow = new TableRow(this);
                TextViewPlus[] tbcols = new TextViewPlus[colnums];

                for(int i=0; i<colnums; i++){
                    tbcols[i] = new TextViewPlus(this);
                    tbcols[i].setText(elements[i]);
                    tbcols[i].setLayoutParams(motive.getChildAt(i).getLayoutParams());
                    tbcols[i].setGravity(Gravity.CENTER);
                    tbcols[i].setTypeface(Typeface.createFromAsset(tbcols[i].getContext().getAssets(), "InterparkGothicBold.ttf"));
                    tbcols[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent storeIntent = new Intent(originActivity, MenuActivity.class);
                            storeIntent.putExtra("LICENSE", license);
                            startActivity(storeIntent);
                        }
                    });

                    Log.i("StoreMenu", "COL" + i + ":" + elements[i]);
                    tbrow.addView(tbcols[i]);
                }
                reviewTable.addView(tbrow);
            }
        }
        reviewTable.removeViewAt(1);
    }

    public void storeClick(View view) {
        if(view.getId() == R.id.store_more){
            Intent storeReviewIntent = new Intent(this, StoreReviewActivity.class);
            storeReviewIntent.putExtra("LICENSE", license);
            startActivity(storeReviewIntent);
        }
    }
}