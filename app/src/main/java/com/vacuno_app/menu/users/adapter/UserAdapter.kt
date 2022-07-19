package com.vacuno_app.menu.users.adapter

import android.view.*
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vacuno_app.R
import com.vacuno_app.domain.model.User

import java.util.ArrayList

class UserAdapter(
    var list: List<User>
): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false);
        return UserViewHolder(v)
    }


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val u = list[position]
        holder.usernameT.text = u.name
        holder.statusT.text = if (u.status != null) "Activo" else "Inactivo"
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {

        val usernameT: TextView
        val statusT: TextView
        private val cardView: CardView

        init {
            usernameT = itemView.findViewById(R.id.usernameTextView)
            statusT = itemView.findViewById(R.id.statusTextView)
            cardView = itemView.findViewById(R.id.cardview_service)
            cardView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(
            contextMenu: ContextMenu?,
            view: View?,
            contextMenuInfo: ContextMenu.ContextMenuInfo?
        ) {
            /*contextMenu.add(this.getAdapterPosition(), 101, 0, "Editar");*/
            if(statusT.text.equals("Activo")){
                contextMenu?.add(this.adapterPosition, 102, 0, "Inactivar");
            }else{
                contextMenu?.add(this.adapterPosition, 102, 0, "Activar");
            }

            //contextMenu?.add(this.adapterPosition, 103, 0, "Eliminar");
        }

    }
}
