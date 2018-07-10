package com.example.dikson.prototype21menubars;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {


//    Coordinates Variables
    ImageView image1;

    StringBuilder sb;
    int x2, y2, x1, y1, xDiff, yDiff;

    Canvas canvas;
    Bitmap bitmap, bmp, mutedBitmap;
    Paint paint, paintOrigin;
    int bitmapWidth, bitmapHeight, screenWidth, screenHeight;

    boolean setState = true;

//    Drawer Variable
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        image1 = findViewById(R.id.layout);

        paint = new Paint();
        paint.setColor(Color.RED);
        paintOrigin = new Paint();
        paintOrigin.setColor(Color.BLACK);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ioi_layout);
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();

        bitmap = Bitmap.createBitmap(bitmap, 0 , 0 ,bitmapWidth, bitmapHeight);
        bmp = Bitmap.createScaledBitmap(bitmap, screenWidth, screenHeight, true );
        mutedBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(mutedBitmap);
        image1.setImageBitmap(mutedBitmap);
        image1.setOnTouchListener(this);

//        Drawer

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.T_MainTools);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.DL_MainMenuBar);

        NavigationView navigationView = findViewById(R.id.NV_MainLists);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //Selected item is highlighted
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {

                    case R.id.I_F3:
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.f3_map);
                        Toast.makeText(MainActivity.this, "Ground Floor", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.I_WifiList:
                        Intent Wifi = new Intent(MainActivity.this, WifiActivity.class);
                        startActivity(Wifi);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.ioi:
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ioi_layout);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.malaysia:
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.malaysia_map);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.campus:
                        drawerLayout.closeDrawers();
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.campus_map);
                        Toast.makeText(MainActivity.this, "Level 1", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                }

                bitmapWidth = bitmap.getWidth();
                bitmapHeight = bitmap.getHeight();
                bitmap = Bitmap.createBitmap(bitmap, 0 , 0 ,bitmapWidth, bitmapHeight);
                bmp = Bitmap.createScaledBitmap(bitmap, screenWidth, screenHeight, true );
                mutedBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
                canvas = new Canvas(mutedBitmap);
                image1.setImageBitmap(mutedBitmap);
                return true;
            }
        });
    }

    public boolean onTouch(View v, MotionEvent event) {
        x2 = (int) event.getX();
        y2 = (int) event.getY();
        int action = event.getAction();
        int xPrev = 0, yPrev = 0;


        mutedBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(mutedBitmap);
        image1.setImageBitmap(mutedBitmap);

        if(setState){
            xDiff = 0;
            yDiff = 0;
            x1 = (int) event.getX();
            y1 = (int) event.getY();

            if (action == MotionEvent.ACTION_UP) {
                setState = false;
            }

            if (x1 > 0 && y1 > 0 && x1 < mutedBitmap.getWidth() && y1 < mutedBitmap.getHeight()) {
                canvas.drawCircle(x1, y1, 20, paintOrigin);
                xPrev = x1;
                yPrev = y1;
                sb = new StringBuilder();
                sb.append("x1:" + x1 + "," + "y1:" + y1);
//                textView.setText(sb.toString());
            }
            else{
                x1 = xPrev;
                y1 = yPrev;
            }
        }
        else{
            if (x2 > 0 && y2 > 0 && x2 < mutedBitmap.getWidth() && y2 < mutedBitmap.getHeight()) {
                canvas.drawCircle(x1, y1, 20, paintOrigin);
                canvas.drawCircle(x2, y2, 20, paint);
                xDiff = x2 - x1;
                yDiff = y2 - y1;
                sb = new StringBuilder();
                sb.append("x1: " + x1 + "," + "y1: " + y1 + "\n"
                        + "x2: " + xDiff + "," + "y2: " + yDiff);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();

        } else {
            Intent exit = new Intent(Intent.ACTION_MAIN);
            exit.addCategory(Intent.CATEGORY_HOME);
            exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(exit);
        }
    }
}
