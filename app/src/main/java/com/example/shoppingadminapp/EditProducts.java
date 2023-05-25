package com.example.shoppingadminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shoppingadminapp.Modal.ProductModal;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditProducts extends AppCompatActivity {
    private ImageView image;
    private EditText title,ddescription,price,quantity;
    private Spinner category;
    private Button updateProduct;
    private DatabaseReference databaseReference;
    private String ImageSelect = null;
    private Uri imageUri;
    StorageReference mStorageRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);
            }
        });
        String productId = getIntent().getStringExtra("productId");
        updateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Title = title.getText().toString();
                String Description = ddescription.getText().toString();
                String Price = price.getText().toString();
                String Quantity = quantity.getText().toString();
                if(Title.isEmpty() || Description.isEmpty() || Price.isEmpty() || Quantity.isEmpty())
                {
                    Toast.makeText(EditProducts.this, "all fields and image required", Toast.LENGTH_SHORT).show();
                }
                else if(ImageSelect == null)
                {
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("Title",Title);
                    hashMap.put("Description",Description);
                    hashMap.put("Price",Price);
                    hashMap.put("Category",category.getSelectedItem().toString());
                    hashMap.put("Quantity",Quantity);

                    databaseReference.child("Products").child(productId).updateChildren(hashMap);
                    Toast.makeText(EditProducts.this, "Product has been updated", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    progressDialog = new ProgressDialog(EditProducts.this);
                    progressDialog.setTitle("Please Wait");
                    progressDialog.setMessage("Job is posting");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();


                    final StorageReference riversRef = mStorageRef.child("User/_"+System.currentTimeMillis());

                    riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressDialog.dismiss();
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("Title",Title);
                                    hashMap.put("Image",uri.toString());
                                    hashMap.put("Description",Description);
                                    hashMap.put("Price",Price);
                                    hashMap.put("Category",category.getSelectedItem().toString());
                                    hashMap.put("Quantity",Quantity);

                                    databaseReference.child("Products").child(productId).updateChildren(hashMap);
                                    Toast.makeText(EditProducts.this, "Product has been updated", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(EditProducts.this,ViewProducts.class));
                                    finish();
                                }
                            });
                        }
                    });



                }
            }
        });
        getCategorries();
        getData(productId);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void getCategorries()
    {
        databaseReference.child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> categories = new ArrayList<String>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    categories.add(dataSnapshot.child("Name").getValue().toString());
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EditProducts.this, android.R.layout.simple_spinner_item, categories);

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                category.setAdapter(dataAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void getData(String productId) {
        databaseReference.child("Products").child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    ddescription.setText(dataSnapshot.child("Description").getValue().toString());
                    try {
                        Glide.with(EditProducts.this).load(dataSnapshot.child("Image").getValue(String.class)).into(image);
                    }
                    catch (Exception ex)
                    {

                    }
                    price.setText(dataSnapshot.child("Price").getValue().toString());
                    quantity.setText(dataSnapshot.child("Quantity").getValue().toString());
                    title.setText(dataSnapshot.child("Title").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100 && resultCode==RESULT_OK){
            imageUri = data.getData();
            ImageSelect="123";
            image.setImageURI(imageUri);
        }
    }
}