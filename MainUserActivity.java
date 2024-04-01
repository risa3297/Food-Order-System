package com.example.foodorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.HasApiKey;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.channels.AlreadyBoundException;
import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class MainUserActivity extends AppCompatActivity {

    private TextView nameTv, emailTv, tabMenuTv, tabOrdersTv, filterProductTv;
    private EditText searchProductEt;
    private RelativeLayout menuRl, ordersRl;
    private ImageButton logoutBtn, filterProductBtn, cartBtn;
    private RecyclerView menuRv, ordersRv;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelProduct> productList;
    private AdapterMenuUser adapterMenuUser;

    private AdapterCartItem adapterCartItem;
    private ArrayList<ModelCartItem> cartItemsList;

    private ArrayList<ModelUserOrder> ordersList;
    private AdapterUserOrder adapterUserOrder;

    private EasyDB easyDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        logoutBtn = findViewById(R.id.logoutBtn);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        filterProductTv = findViewById(R.id.filterProductTv);
        searchProductEt = findViewById(R.id.searchProductEt);
        cartBtn = findViewById(R.id.cartBtn);
        tabMenuTv = findViewById(R.id.tabMenuTv);
        tabOrdersTv = findViewById(R.id.tabOrdersTv);
        menuRl = findViewById(R.id.menuRl);
        ordersRl = findViewById(R.id.ordersRl);
        menuRv = findViewById(R.id.menuRv);
        ordersRv = findViewById(R.id.ordersRv);
        firebaseAuth = FirebaseAuth.getInstance();


        checkUser();
        showMenu();
        loadMenu();

        easyDB = EasyDB.init(this, "ITEMS_DB").setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        deleteCart();


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeMeOffline();
            }
        });

        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    adapterMenuUser.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainUserActivity.this);
                builder.setTitle("Choose Category").setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String selected = Constants.productCategories1[which];
                        filterProductTv.setText(selected);
                        if (selected.equals("All")){
                            loadMenu();
                        }
                        else{
                            adapterMenuUser.getFilter().filter(selected);
                        }

                    }
                }).show();
            }
        });

        tabMenuTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });

        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrders();
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCartItem();
            }
        });


    }

    private void deleteCart() {
        easyDB.deleteAllDataFromTable();
    }

    public TextView total_text, totalPrice;
    private void showCartItem() {

        cartItemsList = new ArrayList<>();

        View view = LayoutInflater.from(this).inflate(R.layout.all_cart_item, null);

        RecyclerView cartItemsRv = view.findViewById(R.id.cartItemsRv);
        total_text = view.findViewById(R.id.total_text);
        totalPrice = view.findViewById(R.id.totalPrice);
        Button checkOutBtn = view.findViewById(R.id.checkOutBtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        EasyDB easyDB = EasyDB.init(this, "ITEMS_DB").setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        Cursor res = easyDB.getAllData();
        while (res.moveToNext()){
            String id = res.getString(1);
            String pID = res.getString(2);
            String title = res.getString(3);
            String price = res.getString(4);
            String quantity = res.getString(5);


            ModelCartItem modelCartItem = new ModelCartItem(""+id, ""+pID, ""+title, ""+price, ""+quantity);

            cartItemsList.add(modelCartItem);
        }

        adapterCartItem = new AdapterCartItem(this, cartItemsList);

        cartItemsRv.setAdapter(adapterCartItem);


        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });

        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cartItemsList.size() == 0){
                    Toast.makeText(MainUserActivity.this, "Empty cart", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                checkOut();
            }
        });
    }

    private void checkOut() {

        String timestamp = ""+System.currentTimeMillis();

        String cost = totalPrice.getText().toString().trim().replace("RM", "");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId", ""+timestamp);
        hashMap.put("orderTime", ""+timestamp);
        hashMap.put("orderStatus", "In Progress");
        hashMap.put("orderPrice", ""+cost);
        hashMap.put("orderBy", ""+firebaseAuth.getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Orders");
        reference.child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                for (int i = 0; i < cartItemsList.size(); i++){
                    String pID = cartItemsList.get(i).getpID();
                    String id = cartItemsList.get(i).getId();
                    String name = cartItemsList.get(i).getName();
                    String price = cartItemsList.get(i).getPrice();
                    String quantity = cartItemsList.get(i).getQuantity();

                    HashMap<String, String> hashMap1 = new HashMap<>();
                    hashMap1.put("pID", pID);
                    hashMap1.put("id", id);
                    hashMap1.put("name", name);
                    hashMap1.put("price", price);
                    hashMap1.put("quantity", quantity);

                    reference.child(timestamp).child("Items").child(pID).setValue(hashMap1);

                }

                Toast.makeText(MainUserActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainUserActivity.this, UserOrderDetail.class);
                intent.putExtra("orderId", timestamp);
                startActivity(intent);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MainUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });






    }

    private void checkUser() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
            finish();
        }
        else {
                loadInfo();
        }
    }

    private void loadInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String name = ""+ds.child("name").getValue();
                    String email = ""+ds.child("email").getValue();

                    nameTv.setText(name);
                    emailTv.setText(email);

                    loadOrder();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadOrder() {

        ordersList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ordersList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    String uid = ""+ds.getRef().getKey();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Orders");
                    ref.orderByChild("orderBy").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()){
                                for (DataSnapshot ds : snapshot.getChildren()){
                                    ModelUserOrder modelUserOrder = ds.getValue(ModelUserOrder.class);

                                    ordersList.add(modelUserOrder);
                                }

                                adapterUserOrder = new AdapterUserOrder(MainUserActivity.this, ordersList);
                                ordersRv.setAdapter(adapterUserOrder);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMenu() {

        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                    productList.add(modelProduct);
                }

                adapterMenuUser = new AdapterMenuUser(MainUserActivity.this, productList);

                menuRv.setAdapter(adapterMenuUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void makeMeOffline() {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "false");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseAuth.signOut();
                checkUser();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showMenu() {

        menuRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabMenuTv.setTextColor(getResources().getColor(R.color.black, getTheme()));
        tabMenuTv.setBackgroundResource(R.drawable.shape_button2);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.white, getTheme()));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent, getTheme()));
    }

    private void showOrders() {

        menuRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabMenuTv.setTextColor(getResources().getColor(R.color.white, getTheme()));
        tabMenuTv.setBackgroundColor(getResources().getColor(android.R.color.transparent, getTheme()));

        tabOrdersTv.setTextColor(getResources().getColor(R.color.black, getTheme()));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_button2);
    }
}