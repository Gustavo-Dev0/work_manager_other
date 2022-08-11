package com.vacuno_app.menu.sheets.adapter;

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
import com.vacuno_app.domain.model.Sheet;

import java.util.ArrayList;

public class SheetAdapter extends RecyclerView.Adapter<SheetAdapter.SheetViewHolder> {
    Context contex;
    ArrayList<Sheet> list;

    public SheetAdapter(Context contex, ArrayList<Sheet> list) {
        this.contex = contex;
        this.list = list;
    }

    @NonNull
    @Override
    public SheetAdapter.SheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contex).inflate(R.layout.item_sheet, parent, false);
        return new SheetAdapter.SheetViewHolder(v);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull SheetAdapter.SheetViewHolder holder, int position) {
        Sheet s = list.get(position);
        holder.nameT.setText(s.getName());
        holder.raceT.setText(s.getRace());
        if(s.getSex().equals("H"))  holder.sexT.setText(contex.getString(R.string.female));
        else  holder.sexT.setText(contex.getString(R.string.male));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class SheetViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView nameT, raceT, sexT;
        CardView cardView;

        public SheetViewHolder(@NonNull View itemView) {
            super(itemView);

            nameT = itemView.findViewById(R.id.nameSheetTextView);
            raceT = itemView.findViewById(R.id.raceSheetTextView);
            sexT = itemView.findViewById(R.id.sexSheetTextView);

            cardView = itemView.findViewById(R.id.cardview_sheet);
            cardView.setOnCreateContextMenuListener(this);

        }

        @SuppressLint("ResourceType")
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(this.getAdapterPosition(), 101, 0, view.getContext().getString(R.string.edit));
        }
    }
}
