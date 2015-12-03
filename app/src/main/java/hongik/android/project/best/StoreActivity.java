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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import layout.api.TextViewPlus;

public class StoreActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String license;
    private TableLayout menuTable;
    private TableLayout reviewTable;

    private GoogleMap mMap;
    private String sname;
    private double Lat;
    private double Lng;

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

    public void drawPage() throws Exception {
        String query = "func=storereview" + "&license=" + license;

        DBConnector conn = new DBConnector(query);
        conn.start();
        conn.join();

        JSONObject jsonResult = conn.getResult();
        boolean result = jsonResult.getBoolean("result");
        if (!result)
            return;

        final JSONObject store = jsonResult.getJSONArray("store").getJSONObject(0);

        JSONArray menu = null;
        if(!jsonResult.isNull("menu"))
            menu = jsonResult.getJSONArray("menu");

        JSONArray review = null;
        if(!jsonResult.isNull("review"))
            review = jsonResult.getJSONArray("review");

        //Draw Store Information
        Lat = Double.parseDouble(store.getString("LAT"));
        Lng = Double.parseDouble(store.getString("LNG"));
        sname = store.getString("SNAME");
        ((TextViewPlus) findViewById(R.id.store_storename)).setText(sname);
        ((TextViewPlus) findViewById(R.id.store_address)).setText(store.getString("ADDR"));
        ImageLoader imgLoader = new ImageLoader(store.getString("IMG"));
        imgLoader.start();

        try {
            imgLoader.join();
            Bitmap storeImg = imgLoader.getBitmap();
            ((ImageView) findViewById(R.id.store_image)).setImageBitmap(storeImg);
        } catch (InterruptedException e) {
            Toast.makeText(this, "Can not bring " + license + "store's image", Toast.LENGTH_SHORT).show();
            Log.e("StoreInfo", "Can not bring " + license + "store's image");
        }

        //Draw Menu Table
        if (menu != null) {
            TableRow motive = (TableRow) menuTable.getChildAt(1);

            for (int i = 0; i < menu.length(); i++) {
                JSONObject json = menu.getJSONObject(i);

                TableRow tbRow = new TableRow(this);
                TextViewPlus[] tbCols = new TextViewPlus[3];

                final String[] elements = new String[2];
                elements[0] = json.getString("ITEM#");
                elements[1] = json.getString("PRICE");

                imgLoader = new ImageLoader(json.getString("IMG"));
                imgLoader.start();
                imgLoader.join();

                ImageView img = new ImageView(this);
                Bitmap bitmap = imgLoader.getBitmap();
                img.setImageBitmap(bitmap);
                img.setLayoutParams(motive.getChildAt(0).getLayoutParams());
                img.setScaleType(ImageView.ScaleType.FIT_XY);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent storeIntent = new Intent(originActivity, MenuActivity.class);
                        storeIntent.putExtra("LICENSE", license);
                        storeIntent.putExtra("MENU", elements[0]);
                        startActivity(storeIntent);
                    }
                });

                tbRow.addView(img);

                for (int j = 0; j < 2; j++) {
                    tbCols[j] = new TextViewPlus(this);
                    tbCols[j].setText(elements[j]);
                    tbCols[j].setLayoutParams(motive.getChildAt(j).getLayoutParams());
                    tbCols[j].setGravity(Gravity.CENTER);
                    tbCols[j].setTypeface(Typeface.createFromAsset(tbCols[j].getContext().getAssets(), "InterparkGothicBold.ttf"));
                    tbCols[j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent storeIntent = new Intent(originActivity, MenuActivity.class);
                            storeIntent.putExtra("LICENSE", license);
                            storeIntent.putExtra("MENU", elements[0]);
                            startActivity(storeIntent);
                        }
                    });

                    Log.i("StoreMenu", "COL" + j + ":" + elements[j]);
                    tbRow.addView(tbCols[j]);
                }
                menuTable.addView(tbRow);
            }
        }
        menuTable.removeViewAt(1);

        //Draw Review Table
        if (review != null) {
            TableRow motive = (TableRow) reviewTable.getChildAt(1);

            int rowCnt = 5;
            if (review.length() < 5)
                rowCnt = review.length();
            for (int i = 0; i < rowCnt; i++) {
                JSONObject json = review.getJSONObject(i);

                final String[] elements = new String[4];
                elements[0] = json.getString("GRADE");
                elements[1] = json.getString("NOTE");
                elements[2] = json.getString("CID#");
                elements[3] = json.getString("DAY");

                TableRow tbRow = new TableRow(this);
                TextViewPlus[] tbCols = new TextViewPlus[4];

                if (elements[1].length() > 14)
                    elements[1] = elements[1].substring(0, 14) + "...";

                for (int j = 0; j < 4; j++) {
                    tbCols[j] = new TextViewPlus(this);
                    tbCols[j].setText(elements[j]);
                    tbCols[j].setLayoutParams(motive.getChildAt(j).getLayoutParams());
                    tbCols[j].setGravity(Gravity.CENTER);
                    tbCols[j].setTypeface(Typeface.createFromAsset(tbCols[j].getContext().getAssets(), "InterparkGothicBold.ttf"));
                    tbCols[j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent reviewIntent = new Intent(originActivity, ReviewDetailActivity.class);
                            reviewIntent.putExtra("ACCESS", "STORE");
                            reviewIntent.putExtra("CID", elements[2]);
                            reviewIntent.putExtra("LICENSE", license);
                            Log.i("StoreReview", "StartActivity");
                            startActivity(reviewIntent);
                        }
                    });

                    Log.i("StoreMenu", "COL" + j + ":" + elements[j]);
                    tbRow.addView(tbCols[j]);
                }
                reviewTable.addView(tbRow);
            }
        }
        reviewTable.removeViewAt(1);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.store_map);
        mapFragment.getMapAsync(this);
    }

    public void storeClick(View view) {
        if(view.getId() == R.id.store_more){
            Intent storeReviewIntent = new Intent(this, StoreReviewActivity.class);
            storeReviewIntent.putExtra("LICENSE", license);
            startActivity(storeReviewIntent);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng myPosition = new LatLng(Lat, Lng);
        mMap.addMarker(new MarkerOptions().position(myPosition).title(sname));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 16));
    }
}