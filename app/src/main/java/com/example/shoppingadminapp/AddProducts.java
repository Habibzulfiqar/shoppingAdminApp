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
import java.util.UUID;

public class AddProducts extends AppCompatActivity {
    private ImageView image;
    private EditText title,ddescription,price,quantity;
    private Spinner category;
    private Button addProduct;
    private DatabaseReference databaseReference;
    private String ImageSelect = null;
    private Uri imageUri;
    StorageReference mStorageRef;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        category = findViewById(R.id.category);
        image = findViewById(R.id.image);
        title = findViewById(R.id.title);
        ddescription = findViewById(R.id.description);
        price = findViewById(R.id.price);
        quantity = findViewById(R.id.numberOf);
        addProduct = findViewById(R.id.addProduct);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);
            }
        });
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Title = title.getText().toString();
                String Description = ddescription.getText().toString();
                String Price = price.getText().toString();
                String Quantity = quantity.getText().toString();
                if(Title.isEmpty() || Description.isEmpty() || Price.isEmpty() || Quantity.isEmpty() || ImageSelect ==null)
                {
                    Toast.makeText(AddProducts.this, "all fields and image required", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog = new ProgressDialog(AddProducts.this);
                    progressDialog.setTitle("Please Wait");
                    progressDialog.setMessage("Product is publishing");
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
                                    String uniqueID = UUID.randomUUID().toString();
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("Title",Title);
                                    hashMap.put("Image",uri.toString());
                                    hashMap.put("Description",Description);
                                    hashMap.put("Price",Price);
                                    hashMap.put("Category",category.getSelectedItem().toString());
                                    hashMap.put("Quantity",Quantity);

                                    databaseReference.child("Products").child(uniqueID).updateChildren(hashMap);
                                    Toast.makeText(AddProducts.this, "Product has been uploaded", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(AddProducts.this,ViewProducts.class));
//                                    finish();
                                }
                            });
                        }
                    });



                }
            }
        });
        getCategorries();
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

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddProducts.this, android.R.layout.simple_spinner_item, categories);

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