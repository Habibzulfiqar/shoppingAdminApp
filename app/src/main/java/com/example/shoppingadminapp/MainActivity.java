package com.example.shoppingadminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    private ImageView menuIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuIcon = findViewById(R.id.menuIcon);

        dl = (DrawerLayout)findViewById(R.id.drawer);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();



        nv = (NavigationView)findViewById(R.id.nv);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.logout:
                        logout();
                        break;
                    case R.id.addCategories:
                        startActivity(new Intent(MainActivity.this,AddCategories.class));
                        break;
                    case R.id.viewCategories:
                        startActivity(new Intent(MainActivity.this,ViewCategories.class));
                        break;
                    case R.id.addProduct:
                        startActivity(new Intent(MainActivity.this,AddProducts.class));
                        break;
                    case R.id.viewProducts:
                        startActivity(new Intent(MainActivity.this,ViewProducts.class));
                        break;
                    case R.id.outOfStock:
                        startActivity(new Intent(MainActivity.this,OutOfStockProducts.class));
                        break;
                    case R.id.viewOrders:
                        startActivity(new Intent(MainActivity.this,ViewAllOrders.class));
                        break;
                    case R.id.runningOrders:
                        startActivity(new Intent(MainActivity.this,RunningOrders.class));
                        break;
                    case R.id.cancel:
                        startActivity(new Intent(MainActivity.this,CancelledOrders.class));
                        break;
//                    case R.id.users:
//                        startActivity(new Intent(MainActivity.this,ViewUsers.class));
//                        break;
                    case R.id.settings:
                        startActivity(new Intent(MainActivity.this,Setting.class));
                        break;



                }
                return true;

            }
        });

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dl.openDrawer(Gravity.LEFT);
            }
        });
    }
    private void logout() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Are you sure you want to logout?.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences sharedPreferences = getSharedPreferences("ShoppingRef",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userId",null);
                        editor.commit();
                        editor.apply();
                        startActivity(new Intent(MainActivity.this,SplashScreen.class));
                        finish();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}