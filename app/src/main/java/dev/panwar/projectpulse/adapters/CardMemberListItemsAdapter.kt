package dev.panwar.projectpulse.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.models.SelectedMembers

// RV adapter to show member card Members in Card Detail Activity

open class CardMemberListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<SelectedMembers>,
    private val assignMembers:Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_card_selected_member,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
//              when assignMembers is true than only we show iv_add_member...means we are allowing to add members
            if (position == list.size - 1  && assignMembers) {
//                when we are at last position we show the + icon means iv add member
                holder.itemView.findViewById<ImageView>(R.id.iv_add_member).visibility = View.VISIBLE
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member_image).visibility = View.GONE
            } else {
//                otherwise show profile picture of member
                holder.itemView.findViewById<ImageView>(R.id.iv_add_member).visibility = View.GONE
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member_image).visibility = View.VISIBLE

                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.itemView.findViewById<ImageView>(R.id.iv_selected_member_image))
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick()
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }


    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }


    interface OnClickListener {
        fun onClick()
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
