package com.example.foodorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class UserOrderDetail extends AppCompatActivity {

    private String orderId;

    private ImageButton backBtn;
    private TextView orderIds, dateId, statusId, amountId;
    private RecyclerView itemRv;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelOrderedItem> orderedItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_detail);

        backBtn = findViewById(R.id.backBtn);
        orderIds = findViewById(R.id.orderIds);
        dateId = findViewById(R.id.dateId);
        statusId = findViewById(R.id.statusId);
        amountId = findViewById(R.id.amountId);
        itemRv = findViewById(R.id.itemRv);


        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

        firebaseAuth = FirebaseAuth.getInstance();
        loadOrder();
        loadOrderItem();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

                adapterOrderedItem = new AdapterOrderedItem(UserOrderDetail.this, orderedItemArrayList);

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
}