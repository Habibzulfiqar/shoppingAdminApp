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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shoppingadminapp.Modal.CategoryModal;
import com.example.shoppingadminapp.Modal.ProductModal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ViewProducts extends AppCompatActivity {
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<ProductModal> productModals;
    private DatabaseReference databaseReference;
    String ProductIds = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products);
        databaseReference = FirebaseDatabase.getInstance().getReference("Products");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        // new way
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewProducts.this,RecyclerView.VERTICAL,false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

//        recyclerView.setLayoutManager(new LinearLayoutManager(ViewProducts.this,RecyclerView.VERTICAL,false));


        productModals = new ArrayList<>();

        myAdapter=new MyAdapter(ViewProducts.this,ViewProducts.this,productModals);
        recyclerView.setAdapter(myAdapter);

        findViewById(R.id.backIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getData();
    }
    public void getData()
    {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    ProductModal modal = new ProductModal();
                    modal.setCategory(dataSnapshot.child("Category").getValue().toString());
                    modal.setDescription(dataSnapshot.child("Description").getValue().toString());
                    modal.setImage(dataSnapshot.child("Image").getValue(String.class));
                    modal.setPrice(dataSnapshot.child("Price").getValue().toString());
                    modal.setQuantity(dataSnapshot.child("Quantity").getValue().toString());
                    modal.setTitle(dataSnapshot.child("Title").getValue().toString());
                    modal.setId(dataSnapshot.getKey());
                    productModals.add(modal);

                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        ArrayList<ProductModal> data;
        Context context;
        Activity activity;
        String TAG;
        public class MyViewHolder extends RecyclerView.ViewHolder  {
            private TextView title,price,quantity,description,category;
            private ImageView image;
            private ImageView edit,delete;
            public MyViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.title);
                price = view.findViewById(R.id.price);
                quantity = view.findViewById(R.id.quantity);
                description = view.findViewById(R.id.description);
                category = view.findViewById(R.id.category);
                image = view.findViewById(R.id.image);
                edit = view.findViewById(R.id.edit);
                delete = view.findViewById(R.id.delete);
//                delete.setVisibility(View.GONE);
            }
        }
        public MyAdapter(Context c, Activity a , ArrayList<ProductModal> skillsModal){
            this.data =skillsModal;
            context=c;
            activity=a;
            TAG="***Adapter";
        }
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_adapter, parent, false);
            return new MyAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
            ProductModal modal = data.get(position);


            viewHolder.title.setText(modal.getTitle());
            viewHolder.price.setText("Rs."+modal.getPrice());
            viewHolder.quantity.setText("Quantity :"+modal.getQuantity());
            viewHolder.description.setText(modal.getDescription());
            viewHolder.category.setText("Category :"+modal.getCategory());
            try {
                Glide.with(ViewProducts.this).load(modal.getImage()).into(viewHolder.image);
            }
            catch (Exception ex)
            {

            }
            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ViewProducts.this,EditProducts.class).putExtra("productId",modal.getId()));
                }
            });
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProductIds = modal.getId();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewProducts.this);
                    builder1.setMessage("Are you sure you want to delete this Product?."+ProductIds);
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    databaseReference.child(ProductIds).removeValue();
                                    myAdapter.notifyDataSetChanged();
                                    Toast.makeText(context, "Product has been removed", Toast.LENGTH_SHORT).show();

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

        public void setFilter(ArrayList<ProductModal> newList){
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