package com.example.foodorder;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterUserOrder extends RecyclerView.Adapter<AdapterUserOrder.HolderUserOrder>{

    private Context context;
    private ArrayList<ModelUserOrder> userOrdersList;

    public AdapterUserOrder(Context context, ArrayList<ModelUserOrder> userOrdersList) {
        this.context = context;
        this.userOrdersList = userOrdersList;
    }

    @NonNull
    @Override
    public HolderUserOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_order, parent, false);
        return new HolderUserOrder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderUserOrder holder, int position) {

        ModelUserOrder modelUserOrder = userOrdersList.get(position);
        String orderId = modelUserOrder.getOrderId();
        String orderBy = modelUserOrder.getOrderBy();
        String orderPrice = modelUserOrder.getOrderPrice();
        String orderStatus = modelUserOrder.getOrderStatus();
        String orderTime = modelUserOrder.getOrderTime();

        holder.amountId.setText("RM "+orderPrice);
        holder.statusId.setText(orderStatus);
        holder.orderId.setText("Order Id:"+orderId);

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

                Intent intent = new Intent(context, UserOrderDetail.class);
                intent.putExtra("orderId", orderId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userOrdersList.size();
    }

    class HolderUserOrder extends RecyclerView.ViewHolder{

        private TextView orderId, dateId, amountId, statusId;

        public HolderUserOrder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.orderId);
            dateId = itemView.findViewById(R.id.dateId);
            amountId = itemView.findViewById(R.id.amountId);
            statusId = itemView.findViewById(R.id.statusId);


        }
    }
}
