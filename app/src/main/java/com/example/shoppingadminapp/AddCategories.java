package com.example.shoppingadminapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddCategories extends AppCompatActivity {
    private EditText name;
    private Button AddCategry;

    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_categories);

        databaseReference = FirebaseDatabase.getInstance().getReference("Categories");

        name = findViewById(R.id.addCategory);
        AddCategry = findViewById(R.id.addCategoryBtn);

        AddCategry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Names = name.getText().toString();

                if(Names.isEmpty())
                {
                    Toast.makeText(AddCategories.this, "fields required", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("Name",Names);
                    databaseReference.child(String.valueOf(System.currentTimeMillis())).updateChildren(hashMap);
                    Toast.makeText(AddCategories.this, "Category has been added", Toast.LENGTH_SHORT).show();
                    name.setText("");
                }
            }
        });
    }
}