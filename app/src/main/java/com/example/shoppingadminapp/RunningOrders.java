package com.example.shoppingadminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shoppingadminapp.Modal.OrdersModal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RunningOrders extends AppCompatActivity {
    private RecyclerView recyclerView;
    ArrayList<OrdersModal> ordersModals;
    MyAdapter myAdapter;

    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_orders);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        ordersModals = new ArrayList<>();
        myAdapter=new MyAdapter(this,this,ordersModals);
        recyclerView.setAdapter(myAdapter);



        findViewById(R.id.backIcons).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getOrders();
    }
    public void getOrders()
    {
        databaseReference.child("SellerOrders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersModals.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    if(dataSnapshot.child("status").getValue().equals("accept")) {
                        OrdersModal ordersModal = new OrdersModal();
                        ordersModal.setOrderId(dataSnapshot.getKey());
                        ordersModal.setCartDateTime(dataSnapshot.child("CartDateTime").getValue().toString());
                        ordersModal.setProductId(dataSnapshot.child("ProductId").getValue().toString());
                        ordersModal.setDateTime(dataSnapshot.child("OrderTime").getValue().toString());
                        ordersModal.setQuantity(dataSnapshot.child("Quantity").getValue().toString());
                        ordersModal.setUserId(dataSnapshot.child("userId").getValue().toString());
                        ordersModal.setStatus(dataSnapshot.child("status").getValue().toString());
                        ordersModals.add(ordersModal);
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
        ArrayList<OrdersModal> data;
        Context context;
        Activity activity;
        String TAG;
        public class MyViewHolder extends RecyclerView.ViewHolder  {

            private ImageView flowerImage,crossImage;
            private TextView flowerName,price,TextProcess,totalPrice,customerNames,customerPhones,customerAddress;
            public MyViewHolder(View view) {
                super(view);
                flowerImage = view.findViewById(R.id.image);
                crossImage = view.findViewById(R.id.crossIcons);
                flowerName = view.findViewById(R.id.flowerName);
                price = view.findViewById(R.id.price);
                TextProcess = view.findViewById(R.id.TextProcess);
                totalPrice = view.findViewById(R.id.totalPrice);
                customerNames = view.findViewById(R.id.customerNames);
                customerPhones = view.findViewById(R.id.customerPhones);
                customerAddress = view.findViewById(R.id.customerAddress);
                crossImage.setVisibility(View.INVISIBLE);

            }
        }
        public MyAdapter(Context c, Activity a , ArrayList<OrdersModal> cartModelss){
            this.data =cartModelss;
            context=c;
            activity=a;
            TAG="***Adapter";
        }
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.seller_delivery_adapter, parent, false);
            return new MyAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyAdapter.MyViewHolder viewHolder, final int position) {
            OrdersModal modal = data.get(position);


            if(modal.getStatus().equals("accept"))
            {
                viewHolder.TextProcess.setText("delivered?");
                viewHolder.TextProcess.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("status","delivered");
                        databaseReference.child("SellerOrders").child(modal.getOrderId()).updateChildren(hashMap);
                        databaseReference.child("UserOrders").child(modal.getUserId()).child(modal.getOrderId()).updateChildren(hashMap);

                        Toast.makeText(RunningOrders.this, "Order has been accepted", Toast.LENGTH_SHORT).show();
                        myAdapter.notifyDataSetChanged();
                    }
                });

            }
            if(modal.getStatus().equals("delivered"))
            {
                viewHolder.TextProcess.setText("delivered");
            }
            databaseReference.child("Products").child(modal.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        Glide.with(context).load(snapshot.child("Image").getValue().toString()).into(viewHolder.flowerImage);
                        viewHolder.flowerName.setText(snapshot.child("Title").getValue().toString());
                        viewHolder.price.setText("Rs. "+snapshot.child("Price").getValue().toString());


                        viewHolder.totalPrice.setText("Rs. "+((Integer.parseInt(snapshot.child("Price").getValue().toString()))*(Integer.parseInt(modal.getQuantity()))));
                    }
                    catch (Exception ex)
                    {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            databaseReference.child("Users").child(modal.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    viewHolder.customerNames.setText("User Name : "+snapshot.child("Name").getValue().toString());
                    viewHolder.customerPhones.setText("Phone No : "+snapshot.child("Contact").getValue().toString());
                    viewHolder.customerAddress.setText("Delivery Address : "+snapshot.child("Address").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




        }
        @Override
        public int getItemCount() {
//        return  5;
            return data.size();
        }

        public void setFilter(ArrayList<OrdersModal> newList){
            data=new ArrayList<>();
            data.addAll(newList);
            notifyDataSetChanged();
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
    }

}