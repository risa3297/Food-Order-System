package com.example.foodorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainSellerActivity extends AppCompatActivity {

    private TextView nameTv, emailTv, tabProductsTv, tabOrdersTv, filterProductTv, show_text;
    private EditText searchProductEt;
    private ImageButton logoutBtn, addProductBtn, filterProductBtn, filter_im;
    private RelativeLayout productsRl, ordersRl;
    private RecyclerView productRv, ordersRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;

    private ArrayList<ModelOrderSeller> orderSellerArrayList;
    private AdapterOrderSeller adapterOrderSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);

        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        tabProductsTv = findViewById(R.id.tabProductsTv);
        tabOrdersTv = findViewById(R.id.tabOrdersTv);
        logoutBtn = findViewById(R.id.logoutBtn);
        addProductBtn = findViewById(R.id.addProductBtn);
        productsRl = findViewById(R.id.productsRl);
        ordersRl = findViewById(R.id.ordersRl);
        searchProductEt = findViewById(R.id.searchProductEt);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        filterProductTv = findViewById(R.id.filterProductTv);
        show_text = findViewById(R.id.show_text);
        filter_im = findViewById(R.id.filter_im);
        productRv = findViewById(R.id.productRv);
        ordersRv = findViewById(R.id.ordersRv);
        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();
        loadAllProduct();
        loadAllOrders();

        showProductsUI();

        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                        adapterProductSeller.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSellerActivity.this, AddProductActivity.class));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeMeOffline();
            }
        });

        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProductsUI();

            }
        });

        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                showOrdersUI();

            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Choose Category").setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String selected = Constants.productCategories1[which];
                        filterProductTv.setText(selected);
                        if (selected.equals("All")){
                            loadAllProduct();
                        }
                        else{
                            loadFilteredProducts(selected);
                        }

                    }
                }).show();
            }
        });

        filter_im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] options = {"All", "In Progress", "Completed", "Cancelled"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Filter Orders: ").setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0){

                            show_text.setText("All Orders");
                            adapterOrderSeller.getFilter().filter("");
                        }
                        else {
                            String option = options[which];
                            show_text.setText(option);
                            adapterOrderSeller.getFilter().filter(option);
                        }

                    }
                }).show();
            }
        });
    }

    private void loadAllOrders() {

        orderSellerArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child("Orders").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                orderSellerArrayList.clear();

                for (DataSnapshot ds : snapshot.getChildren()){

                    ModelOrderSeller modelOrderSeller = ds.getValue(ModelOrderSeller.class);

                    orderSellerArrayList.add(modelOrderSeller);
                }

                adapterOrderSeller = new AdapterOrderSeller(MainSellerActivity.this, orderSellerArrayList);

                ordersRv.setAdapter(adapterOrderSeller);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadFilteredProducts(String selected) {

        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){

                    String productCategory = ""+ds.child("productCategory").getValue();

                    if (selected.equals(productCategory)){

                        ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                        productList.add(modelProduct);
                    }


                }

                adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);

                productRv.setAdapter(adapterProductSeller);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadAllProduct() {

        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                    productList.add(modelProduct);
                }

                adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);

                productRv.setAdapter(adapterProductSeller);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showProductsUI() {

        productsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.black, getTheme()));
        tabProductsTv.setBackgroundResource(R.drawable.shape_button2);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.white, getTheme()));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent, getTheme()));
    }

    private void showOrdersUI() {

        productsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.white, getTheme()));
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent, getTheme()));

        tabOrdersTv.setTextColor(getResources().getColor(R.color.black, getTheme()));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_button2);
    }




    private void makeMeOffline() {
        HashMap<String, Object>hashMap = new HashMap<>();
        hashMap.put("online", "false");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseAuth.signOut();
                checkUser();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainSellerActivity.this, LoginActivity.class));
            finish();
        }
        else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){

                    String name = ""+ds.child("name").getValue();
                    String email = ""+ds.child("email").getValue();
                    String accountType = ""+ds.child("accountType").getValue();

                    nameTv.setText(name +" ("+accountType+")");
                    emailTv.setText(email);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}