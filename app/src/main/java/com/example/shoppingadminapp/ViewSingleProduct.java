package com.example.shoppingadminapp;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewSingleProduct extends AppCompatActivity {
    private ImageView image;
    private EditText title,ddescription,price,quantity;
    private Spinner category;
    private Button updateProduct;
    private DatabaseReference databaseReference;
    private String ImageSelect = null;
    private Uri imageUri;
    StorageReference mStorageRef;
    private ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_products);
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        category = findViewById(R.id.category);
        image = findViewById(R.id.image);
        title = findViewById(R.id.title);
        ddescription = findViewById(R.id.description);
        price = findViewById(R.id.price);
        quantity = findViewById(R.id.numberOf);
        updateProduct = findViewById(R.id.updateProduct);

        String productId = getIntent().getStringExtra("productId");

        Toast.makeText(ViewSingleProduct.this, "Product has been uploaded"+productId, Toast.LENGTH_SHORT).show();


//        getData(productId);




    }

//    public void getData(String productId) {
//        databaseReference.child("Products").child(productId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                ddescription.setText(dataSnapshot.child("Description").getValue().toString());
//                try {
//                    Glide.with(ViewSingleProduct.this).load(dataSnapshot.child("Image").getValue(String.class)).into(image);
//                }
//                catch (Exception ex)
//                {
//
//                }
//                price.setText(dataSnapshot.child("Price").getValue().toString());
//                quantity.setText(dataSnapshot.child("Quantity").getValue().toString());
//                title.setText(dataSnapshot.child("Title").getValue().toString());
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }





}
