package com.vacuno_app.menu.production.adapter

import android.view.*
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vacuno_app.R
import com.vacuno_app.domain.model.Production

class ProductionAdapter(
    var list: List<Production>
): RecyclerView.Adapter<ProductionAdapter.ProductionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductionViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_production, parent, false);
        return ProductionViewHolder(v)
    }


    override fun onBindViewHolder(holder: ProductionViewHolder, position: Int) {
        val p = list[position]
        holder.dateT.text = p.dateCreated
        holder.nameT.text = p.sheetName
        holder.turnT.text = p.turn
        holder.totalT.text = p.total.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ProductionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {

        val dateT: TextView
        val nameT: TextView
        val turnT: TextView
        val totalT: TextView
        private val cardView: CardView

        init {
            nameT = itemView.findViewById(R.id.cowNameProductionTextView)
            turnT = itemView.findViewById(R.id.turnProductionTextView)
            dateT = itemView.findViewById(R.id.dateProductionTextView)
            totalT = itemView.findViewById(R.id.totalProductionTextView)
            cardView = itemView.findViewById(R.id.cardView_production)
            cardView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(
            contextMenu: ContextMenu?,
            view: View?,
            contextMenuInfo: ContextMenu.ContextMenuInfo?
        ) {
            /*contextMenu.add(this.getAdapterPosition(), 101, 0, "Editar");*/
            /*if(statusT.text.equals("Activo")){
                contextMenu?.add(this.adapterPosition, 102, 0, "Inactivar");
            }else{
                contextMenu?.add(this.adapterPosition, 102, 0, "Activar");
            }*/
            contextMenu?.add(this.adapterPosition, 102, 0, "Ver");

            //contextMenu?.add(this.adapterPosition, 103, 0, "Eliminar");
        }

    }
}
