package com.vacuno_app.menu.alarms.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vacuno_app.R;
import com.vacuno_app.domain.model.Alarm;

import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    Context contex;
    ArrayList<Alarm> list;

    public AlarmAdapter(Context contex, ArrayList<Alarm> list) {
        this.contex = contex;
        this.list = list;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contex).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(v);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm s = list.get(position);
        holder.dateT.setText(s.getDate());
        holder.sheetT.setText(s.getSheetName());
        holder.typeT.setText(s.getType());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView dateT, sheetT, typeT;
        CardView cardView;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);

            dateT = itemView.findViewById(R.id.dateAlarmTextView);
            sheetT = itemView.findViewById(R.id.sheetAlarmTextView);
            typeT = itemView.findViewById(R.id.typeAlarmTextView);
            cardView = itemView.findViewById(R.id.cardView_alarm);
            cardView.setOnCreateContextMenuListener(this);

        }

        @SuppressLint("ResourceType")
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            contextMenu.add(this.getAdapterPosition(), 101, 0, view.getContext().getString(R.string.edit));
            MenuItem item = contextMenu.add(this.getAdapterPosition(), 102, 0, view.getContext().getString(R.string.delete));

            SpannableString spanString = new SpannableString(item.getTitle());
            spanString.setSpan(new ForegroundColorSpan(Color.RED), 0, spanString.length(), 0);
            item.setTitle(spanString);

        }
    }

}

