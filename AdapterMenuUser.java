package com.example.foodorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterMenuUser extends  RecyclerView.Adapter<AdapterMenuUser.HolderMenu> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterMenu filter;


    public AdapterMenuUser(Context context, ArrayList<ModelProduct> menuLists) {
        this.context = context;
        this.productList = menuLists;
        this.filterList = menuLists;
    }




    @NonNull
    @Override
    public HolderMenu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_menu, parent, false);
        return new HolderMenu(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMenu holder, int position) {

        ModelProduct modelProduct = productList.get(position);
        String id = modelProduct.getProductId();
        String uid = modelProduct.getUid();
        String title = modelProduct.getProductTitle();
        String icon = modelProduct.getProductIcon();
        String category = modelProduct.getProductCategory();
        String price = modelProduct.getProductPrice();
        String timestamp = modelProduct.getTimestamp();

        holder.titleTv.setText(title);
        holder.priceTv.setText("RM"+price);

        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_addimage).into(holder.productIconTv);
        }
        catch (Exception e){
            holder.productIconTv.setImageResource(R.drawable.ic_addimage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsShow(modelProduct);
            }
        });

    }

    private double cost = 0;
    private double finalCost = 0;
    private int quantity = 0;
    private void detailsShow(ModelProduct modelProduct) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, null);
        bottomSheetDialog.setContentView(view);

        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageView foodImage = view.findViewById(R.id.foodImage);
        TextView foodName = view.findViewById(R.id.foodName);
        TextView foodDescription = view.findViewById(R.id.foodDescription);
        TextView foodCategory = view.findViewById(R.id.foodCategory);
        TextView  foodPrice = view.findViewById(R.id.foodPrice);
        Button cartBtn = view.findViewById(R.id.cartBtn);
        ImageButton increaseBtn= view.findViewById(R.id.increaseBtn);
        ImageButton decreaseBtn = view.findViewById(R.id.decreaseBtn);
        TextView quantityTv = view.findViewById(R.id.quantityTv);

        String id = modelProduct.getProductId();
        String uid = modelProduct.getUid();
        String productCategory = modelProduct.getProductCategory();
        String productDescription = modelProduct.getProductDescription();
        String icon = modelProduct.getProductIcon();
        String title = modelProduct.getProductTitle();
        String price = modelProduct.getProductPrice();
        String timestamp = modelProduct.getTimestamp();
        String productQuantity = modelProduct.getProductQuantity();


        cost = Double.parseDouble(price.replaceAll("RM", ""));
        finalCost = Double.parseDouble(price.replaceAll("", ""));
        quantity = 1;

        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_addimage).into(foodImage);
        }
        catch (Exception e){
            foodImage.setImageResource(R.drawable.ic_addimage);
        }

        foodName.setText(""+title);
        foodDescription.setText(""+productDescription);
        foodCategory.setText(""+productCategory);
        foodPrice.setText("RM"+finalCost);

        bottomSheetDialog.show();



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = foodName.getText().toString().trim();
                String price = foodPrice.getText().toString().trim();
                String quantity = quantityTv.getText().toString().trim();

                addToCart(id, title, price, quantity);
                
                bottomSheetDialog.dismiss();


            }
        });

        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost + cost;
                quantity++;
                quantityTv.setText(""+quantity);
                foodPrice.setText("RM"+ finalCost);
            }
        });

        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1){
                    finalCost = finalCost - cost;
                    quantity--;
                    quantityTv.setText(""+quantity);
                    foodPrice.setText("RM"+finalCost);
                }
            }
        });
        
        
    }

    private int itemId = 1;
    private void addToCart(String id, String title, String price, String quantity) {
        itemId++;

        EasyDB easyDB = EasyDB.init(context, "ITEMS_DB").setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Item_Id", itemId)
                .addData("Item_PID", id)
                .addData("Item_Name", title)
                .addData("Item_Price", price)
                .addData("Item_Quantity", quantity)
                .doneDataAdding();

        Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();

    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {

        if (filter == null){
            filter = new FilterMenu(this, filterList);
        }
        return filter;
    }

    class HolderMenu extends RecyclerView.ViewHolder{

        private ImageView productIconTv;
        private TextView titleTv, priceTv;

        public HolderMenu(@NonNull View itemView) {
            super(itemView);

             productIconTv = itemView.findViewById(R.id.productIconTv);
             titleTv = itemView.findViewById(R.id.titleTv);
             priceTv = itemView.findViewById(R.id.priceTv);
        }
    }
}
