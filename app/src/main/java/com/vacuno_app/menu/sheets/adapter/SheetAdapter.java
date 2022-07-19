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
import com.vacuno_app.utils.BackupList;
import com.vacuno_app.domain.model.Sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        holder.statusT.setText(s.getStatus());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class SheetViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView nameT, statusT;
        CardView cardView;

        public SheetViewHolder(@NonNull View itemView) {
            super(itemView);

            nameT = itemView.findViewById(R.id.nameSheetTextView);
            statusT = itemView.findViewById(R.id.statusSheetTextView);
            cardView = itemView.findViewById(R.id.cardview_sheet);
            cardView.setOnCreateContextMenuListener(this);

        }

        @SuppressLint("ResourceType")
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(this.getAdapterPosition(), 101, 0, "Editar");
            if(statusT.getText().equals("Activo")){
                contextMenu.add(this.getAdapterPosition(), 102, 0, "Inactivar");
            }else{
                contextMenu.add(this.getAdapterPosition(), 102, 0, "Activar");
            }

            contextMenu.add(this.getAdapterPosition(), 103, 0, "Eliminar");
            MenuItem item = contextMenu.getItem(2);
            SpannableString spanString = new SpannableString(item.getTitle());
            spanString.setSpan(new ForegroundColorSpan(Color.RED), 0, spanString.length(), 0);
            item.setTitle(spanString);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filtered(String txt){
        int len = txt.length();
        if(len == 0){
            list.clear();
            list.addAll(BackupList.listSheetsBackup);
        }else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Sheet> coll = list.stream()
                        .filter(i -> i.getName().toLowerCase().contains(txt.toLowerCase()))
                        .collect(Collectors.toList());
                list.clear();
                list.addAll(coll);
            }else{
                /*list.clear();
                for (Service dt: BackupList.listServiceBackup) {
                    if(dt.getName().toLowerCase().contains(txt.toLowerCase())){
                        list.add(dt);
                    }
                }*/
            }
        }
        notifyDataSetChanged();

    }
}
