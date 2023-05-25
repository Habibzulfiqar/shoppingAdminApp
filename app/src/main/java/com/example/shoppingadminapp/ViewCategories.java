package com.example.shoppingadminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppingadminapp.Modal.CategoryModal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewCategories extends AppCompatActivity {
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<CategoryModal> categoryModals;
    private DatabaseReference databaseReference;
    String CatIds = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_categories);

        databaseReference = FirebaseDatabase.getInstance().getReference("Categories");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewCategories.this));

        categoryModals = new ArrayList<>();

        myAdapter=new MyAdapter(ViewCategories.this,ViewCategories.this,categoryModals);
        recyclerView.setAdapter(myAdapter);

        getData();

        findViewById(R.id.backIcons).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    public void getData()
    {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    CategoryModal modal = new CategoryModal();
                    modal.setId(dataSnapshot.getKey());
                    modal.setName(dataSnapshot.child("Name").getValue().toString());
                    categoryModals.add(modal);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        ArrayList<CategoryModal> data;
        Context context;
        Activity activity;
        String TAG;
        public class MyViewHolder extends RecyclerView.ViewHolder  {
            private TextView name;
            private ImageView edit,delete;
            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.name);
                edit = view.findViewById(R.id.edit);
                delete = view.findViewById(R.id.delete);
            }
        }
        public MyAdapter(Context c, Activity a , ArrayList<CategoryModal> skillsModal){
            this.data =skillsModal;
            context=c;
            activity=a;
            TAG="***Adapter";
        }
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_adapter, parent, false);
            return new MyAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyAdapter.MyViewHolder viewHolder, final int position) {
            CategoryModal modal = data.get(position);
            viewHolder.name.setText(modal.getName());


            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ViewCategories.this,EditCategories.class).putExtra("CategId",modal.getId()));
                }
            });
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CatIds = modal.getId();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewCategories.this);
                    builder1.setMessage("Are you sure you want to delete this category?.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                   databaseReference.child(CatIds).removeValue();
                                    Toast.makeText(context, "Category has been removed", Toast.LENGTH_SHORT).show();
                                    myAdapter.notifyDataSetChanged();
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
            });
        }
        @Override
        public int getItemCount() {
//        return  5;
            return data.size();
        }

        public void setFilter(ArrayList<CategoryModal> newList){
            data=new ArrayList<>();
            data.addAll(data);
            notifyDataSetChanged();
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
    }

}