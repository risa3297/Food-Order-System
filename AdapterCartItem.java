package com.example.foodorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem>{

    private Context context;
    private ArrayList<ModelCartItem> cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_cart_items, parent, false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, int position) {

        ModelCartItem modelCartItem = cartItems.get(position);
        String id = modelCartItem.getId();
        String pID = modelCartItem.getpID();
        String name = modelCartItem.getName();
        String price = modelCartItem.getPrice();
        String quantity = modelCartItem.getQuantity();

        holder.itemNameTv.setText(""+name);
        holder.itemPriceTv.setText(""+price);
        holder.itemQuantityTv.setText(""+quantity);

        holder.removeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EasyDB easyDB = EasyDB.init(context, "ITEMS_DB").setTableName("ITEMS_TABLE")
                        .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                        .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                        .doneTableColumn();

                easyDB.deleteRow(1, id);
                Toast.makeText(context, "Remove from cart", Toast.LENGTH_SHORT).show();

                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class HolderCartItem extends RecyclerView.ViewHolder{

        private TextView itemNameTv, itemPriceTv, itemQuantityTv, removeTv;


        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            itemNameTv = itemView.findViewById(R.id.itemNameTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
            removeTv = itemView.findViewById(R.id.removeTv);

        }
    }
}
