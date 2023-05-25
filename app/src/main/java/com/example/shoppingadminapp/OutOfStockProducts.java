package com.example.shoppingadminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shoppingadminapp.Modal.ProductModal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OutOfStockProducts extends AppCompatActivity {
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<ProductModal> productModals;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_of_stock_products);

        databaseReference = FirebaseDatabase.getInstance().getReference("Products");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(OutOfStockProducts.this));

        productModals = new ArrayList<>();

        myAdapter=new MyAdapter(OutOfStockProducts.this,OutOfStockProducts.this,productModals);
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
                    if(dataSnapshot.child("Quantity").getValue().equals("0")) {
                        ProductModal modal = new ProductModal();
                        modal.setCategory(dataSnapshot.child("Category").getValue().toString());
                        modal.setDescription(dataSnapshot.child("Description").getValue().toString());
                        modal.setImage(dataSnapshot.child("Image").getValue().toString());
                        modal.setPrice(dataSnapshot.child("Price").getValue().toString());
                        modal.setQuantity(dataSnapshot.child("Quantity").getValue().toString());
                        modal.setTitle(dataSnapshot.child("Title").getValue().toString());
                        modal.setId(dataSnapshot.getKey());
                        productModals.add(modal);
                    }
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
                delete.setVisibility(View.GONE);
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
        public void onBindViewHolder(final MyAdapter.MyViewHolder viewHolder, final int position) {
            ProductModal modal = data.get(position);

            viewHolder.title.setText(modal.getTitle());
            viewHolder.price.setText("Rs."+modal.getPrice());
            viewHolder.quantity.setText("Quantity :"+modal.getQuantity());
            viewHolder.description.setText(modal.getDescription());
            viewHolder.category.setText("Category :"+modal.getCategory());
            try {
                Glide.with(OutOfStockProducts.this).load(modal.getImage()).into(viewHolder.image);
            }
            catch (Exception ex)
            {

            }
            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(OutOfStockProducts.this,EditProducts.class).putExtra("productId",modal.getId()));
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