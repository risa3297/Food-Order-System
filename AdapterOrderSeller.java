package com.example.foodorder;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderSeller extends RecyclerView.Adapter<AdapterOrderSeller.HolderOrderSeller> implements Filterable {

    private Context context;
    public ArrayList<ModelOrderSeller> orderSellerArrayList, filterList;
    private FilterOrderSeller filter;

    public AdapterOrderSeller(Context context, ArrayList<ModelOrderSeller> orderSellerArrayList) {
        this.context = context;
        this.orderSellerArrayList = orderSellerArrayList;
        this.filterList = orderSellerArrayList;
    }

    @NonNull
    @Override
    public HolderOrderSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_order_seller, parent, false);
        return new HolderOrderSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderSeller holder, int position) {

        ModelOrderSeller modelOrderSeller = orderSellerArrayList.get(position);
        String orderId = modelOrderSeller.getOrderId();
        String orderTime = modelOrderSeller.getOrderTime();
        String orderStatus = modelOrderSeller.getOrderStatus();
        String orderPrice = modelOrderSeller.getOrderPrice();
        String orderBy = modelOrderSeller.getOrderBy();

        loadUser(modelOrderSeller, holder);

        holder.orderId.setText("Order Id: "+orderId);
        holder.amountId.setText("RM "+orderPrice);
        holder.statusId.setText(orderStatus);

        if (orderStatus.equals("In Progress")){
            holder.statusId.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
        }

        else if (orderStatus.equals("Completed")){
            holder.statusId.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
        }

        else if (orderStatus.equals("Cancel")){
            holder.statusId.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.dateId.setText(dateFormat);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SellerOrderDetail.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("orderBy", orderBy);
                context.startActivity(intent);

            }
        });

    }

    private void loadUser(ModelOrderSeller modelOrderSeller, HolderOrderSeller holder) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(modelOrderSeller.getOrderBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String email = ""+snapshot.child("email").getValue();
                holder.email_text.setText(email);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return orderSellerArrayList.size();
    }

    @Override
    public Filter getFilter() {

        if (filter == null){
            filter = new FilterOrderSeller(this, filterList);
        }
        return filter;
    }

    class HolderOrderSeller extends RecyclerView.ViewHolder{

        private TextView orderId, dateId, email_text, amountId, statusId;
        public HolderOrderSeller(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.orderId);
            dateId = itemView.findViewById(R.id.dateId);
            email_text = itemView.findViewById(R.id.email_text);
            amountId = itemView.findViewById(R.id.amountId);
            statusId = itemView.findViewById(R.id.statusId);
        }
    }
}
