package com.example.foodorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SellerOrderDetail extends AppCompatActivity {

    String orderId, orderBy;

    private ImageButton backBtn, editBtn;
    private TextView orderIds, dateId, statusId, amountId, user_email, user_phone, user_name;
    private RecyclerView itemRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelOrderedItem> orderedItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_order_detail);

        orderId = getIntent().getStringExtra("orderId");
        orderBy = getIntent().getStringExtra("orderBy");

        backBtn = findViewById(R.id.backBtn);
        orderIds = findViewById(R.id.orderIds);
        dateId = findViewById(R.id.dateId);
        statusId = findViewById(R.id.statusId);
        amountId = findViewById(R.id.amountId);
        itemRv = findViewById(R.id.itemRv);
        editBtn = findViewById(R.id.editBtn);
        user_email = findViewById(R.id.user_email);
        user_phone = findViewById(R.id.user_phone);
        user_name = findViewById(R.id.user_name);

        firebaseAuth = FirebaseAuth.getInstance();
        
        loadInfo();
        loadOrder();
        loadOrderItem();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                orderStatus();
            }
        });

    }

    private void orderStatus() {

        final String[] options = {"In Progress", "Completed", "Cancelled"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Order Status").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String option = options[which];
                ordersStatus1(option);

            }
        }).show();
    }

    private void ordersStatus1(final String option) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("orderStatus", ""+option);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child("Orders").child(orderId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(SellerOrderDetail.this, "Order "+option, Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(SellerOrderDetail.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadOrderItem() {

        orderedItemArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child("Orders").child(orderId).child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelOrderedItem modelOrderedItem = ds.getValue(ModelOrderedItem.class);

                    orderedItemArrayList.add(modelOrderedItem);
                }

                adapterOrderedItem = new AdapterOrderedItem(SellerOrderDetail.this, orderedItemArrayList);

                itemRv.setAdapter(adapterOrderedItem);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadOrder() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child("Orders").child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String orderBy = ""+snapshot.child("orderBy").getValue();
                String orderPrice = ""+snapshot.child("orderPrice").getValue();
                String orderId = ""+snapshot.child("orderId").getValue();
                String orderTime = ""+snapshot.child("orderTime").getValue();
                String orderStatus = ""+snapshot.child("orderStatus").getValue();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(orderTime));
                String dateFormat = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString();

                if (orderStatus.equals("In Progress")){
                    statusId.setTextColor(getResources().getColor(R.color.black, getTheme()));
                }

                else if (orderStatus.equals("Completed")){
                    statusId.setTextColor(getResources().getColor(R.color.black, getTheme()));
                }

                else if (orderStatus.equals("Cancel")){
                    statusId.setTextColor(getResources().getColor(R.color.black, getTheme()));
                }

                orderIds.setText(orderId);
                statusId.setText(orderStatus);
                amountId.setText(orderPrice);
                dateId.setText(dateFormat);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderBy).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String email = ""+snapshot.child("email").getValue();
                String phone = ""+snapshot.child("phone").getValue();
                String name = ""+snapshot.child("name").getValue();

                user_email.setText(email);
                user_name.setText(name);
                user_phone.setText(phone);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}