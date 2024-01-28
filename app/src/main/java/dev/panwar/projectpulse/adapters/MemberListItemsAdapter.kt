package dev.panwar.projectpulse.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.models.User
import dev.panwar.projectpulse.utils.Constants

open class MemberListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_member,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.findViewById<ImageView>(R.id.iv_member_image).let {
                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(it)
            }

            holder.itemView.findViewById<TextView>(R.id.tv_member_name).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tv_member_email).text = model.email

            if (model.selected){
               holder.itemView.findViewById<ImageView>(R.id.iv_selected_member).visibility=View.VISIBLE
            }else{
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member).visibility=View.GONE
            }

            holder.itemView.setOnClickListener {
                if (onClickListener!=null){
                    if (model.selected){
//                        if selected than on click it is unselected
                        onClickListener!!.onClick(position,model,Constants.UNSELECT)
                    }else{
//                        if not selected than on Click it is selected
                        onClickListener!!.onClick(position,model,Constants.SELECT)
                    }
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener=onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int,user:User,action:String)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
// END