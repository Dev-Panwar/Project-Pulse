package dev.panwar.projectpulse.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.models.Card
import dev.panwar.projectpulse.models.Task

class CardListItemAdapter(private var context: Context, private var list: ArrayList<Card>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener:OnClickListener?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType  : Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=list[position]

        if (holder is MyViewHolder){
            holder.itemView.findViewById<TextView>(R.id.tv_card_name).text=model.name

//            calling onClick at specific position...this setOnCLickListener is Android Function not this class's
            holder.itemView.setOnClickListener {
                if (onClickListener!=null){
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

//    called from TaskListItemAdapter because rv cardList is inside rv taskList
    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener=onClickListener
    }

//    implemented in TaskListItem Adapter
    interface OnClickListener{
        fun onClick(position: Int)
    }

    //    our view Holder
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}