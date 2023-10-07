package dev.panwar.projectpulse.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.models.Task

class TaskListItemAdapter(private var context: Context,private var list: ArrayList<Task>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        inflating the view
        val view =LayoutInflater.from(context).inflate(R.layout.item_task,parent,false)
//        setting the Layout Parameters...width only 70% of parent and height wrap content
        val layoutParams=LinearLayout.LayoutParams((parent.width*0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
//        setting margins..left,top,right,bottom
        layoutParams.setMargins((15.toDp().toPx()),0,(40.toDp().toPx()),0)
//        the view we inflated should use the layout parameters defined by us
        view.layoutParams=layoutParams

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val model= list[position]
        if (holder is MyViewHolder){
//            when there is no Task item in the list
            if (position==list.size-1){
                holder.itemView.findViewById<MaterialTextView>(R.id.tv_add_task_list).visibility=View.VISIBLE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility=View.GONE
            }else{
                holder.itemView.findViewById<TextureView>(R.id.tv_add_task_list).visibility=View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility=View.VISIBLE
            }
        }
    }

//    this will give us densityPixels from pixels
    private fun Int.toDp(): Int = (this/Resources.getSystem().displayMetrics.density).toInt()
//    this will give us pixels form densityPixels
    private fun Int.toPx():Int = (this*Resources.getSystem().displayMetrics.density).toInt()


//    our view Holder
    class MyViewHolder(view:View): RecyclerView.ViewHolder(view)
}