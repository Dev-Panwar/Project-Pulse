package dev.panwar.projectpulse.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.models.Board

open class BoardItemsAdapter(private val context:Context, private var list:ArrayList<Board>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onCLickListener:OnClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        .return Holder as MyViewHolder(we inflated the layout we wanna put in Recycler View..)
       return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_board,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=list[position]
        if (holder is MyViewHolder){
             holder.itemView.findViewById<CircleImageView>(R.id.iv_board_image).let {
                 Glide
                     .with(context)
                     .load(model.image)
                     .centerCrop()
                     .placeholder(R.drawable.ic_board_place_holder)
                     .into(it)

                 holder.itemView.findViewById<TextView>(R.id.tv_name).text=model.name
                 holder.itemView.findViewById<TextView>(R.id.tv_created_by).text="Created by: ${model.createdBy}"

//                 this set on clickListener is Android Function
                 holder.itemView.setOnClickListener {
                     if (onCLickListener!=null){
                         onCLickListener!!.onClick(position,model)
                     }
                 }
             }
        }
    }

//    when we have list of Board in main content Screen...to know which Board is Clicked we Use this Interface...as after Clicking on particular board we need to go inside That Board and it that will open a Different Activity
    interface OnClickListener{
        fun onClick(position: Int,model:Board)
    }


//    Our View Holder
    private class MyViewHolder(view:View):RecyclerView.ViewHolder(view)
}