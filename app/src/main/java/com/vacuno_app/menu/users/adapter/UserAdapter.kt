package com.vacuno_app.menu.users.adapter

import android.content.Context
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vacuno_app.R
import com.vacuno_app.data.remote.model.UserToFarm
import com.vacuno_app.domain.model.User

import java.util.ArrayList

class UserAdapter(
    var list: List<User>,
    var listUF: List<UserToFarm>
): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false);
        context = v.context
        return UserViewHolder(v)
    }


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val u = list[position]
        val uF = listUF[position]
        holder.usernameT.text = u.name
        holder.roleT.text = uF.role
        holder.statusT.text = if (uF.status == "A") context.getString(R.string.active) else context.getString(R.string.inactive)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {

        val usernameT: TextView
        val roleT: TextView
        val statusT: TextView
        private val cardView: CardView

        init {
            usernameT = itemView.findViewById(R.id.usernameTextView)
            statusT = itemView.findViewById(R.id.statusTextView)
            roleT = itemView.findViewById(R.id.roleTextView)
            cardView = itemView.findViewById(R.id.cardview_service)
            cardView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(
            contextMenu: ContextMenu,
            view: View?,
            contextMenuInfo: ContextMenu.ContextMenuInfo?
        ) {

            contextMenu.add(this.adapterPosition, 101, 0, view?.context?.getString(R.string.change_role));


            if(statusT.text.equals(view?.context?.getString(R.string.active))){
                contextMenu.add(this.adapterPosition, 102, 0, view?.context?.getString(R.string.go_inactive));
            }else{
                contextMenu.add(this.adapterPosition, 102, 0, view?.context?.getString(R.string.go_active));
            }

        }

    }
}
