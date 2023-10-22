package dev.panwar.projectpulse.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.activities.TaskListActivity
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
//            when there is no Task item in the list or we reach end while Scrolling it shows addList button
            if (position==list.size-1){
                holder.itemView.findViewById<MaterialTextView>(R.id.tv_add_task_list).visibility=View.VISIBLE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility=View.GONE
            }else{
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility=View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility=View.VISIBLE
            }

            holder.itemView.findViewById<TextView>(R.id.tv_task_list_title).text=model.title

            holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).setOnClickListener {
//                hiding add list tv and showing card view for entering task List name
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility=View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility=View.VISIBLE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_close_list_name).setOnClickListener{
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility=View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility=View.GONE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_done_list_name).setOnClickListener{
                val listName=holder.itemView.findViewById<EditText>(R.id.et_task_list_name).text.toString()
                if (listName.isNotEmpty()){
                    if (context is TaskListActivity){
                        (context as TaskListActivity).createTaskList(listName)
                    }else{
                        Toast.makeText(context,"Please Enter List Name.",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_edit_list_name).setOnClickListener{
                holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).setText(model.title)
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility=View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_edit_task_list_name).visibility=View.VISIBLE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_close_editable_view).setOnClickListener{
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility=View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility=View.GONE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_done_edit_list_name).setOnClickListener {
                val listName=holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).text.toString()

                if (listName.isNotEmpty()){
                    if (context is TaskListActivity){
                        (context as TaskListActivity).updateTaskList(position, listName, model)
                    }else{
                        Toast.makeText(context,"Please Enter a List Name.",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_list).setOnClickListener {
                alertDialogForDeleteList(position,model.title)
            }

        }
    }

//    this will give us densityPixels from pixels
    private fun Int.toDp(): Int = (this/Resources.getSystem().displayMetrics.density).toInt()
//    this will give us pixels form densityPixels
    private fun Int.toPx():Int = (this*Resources.getSystem().displayMetrics.density).toInt()




//    for showing alert dialogue for when deleting a list
    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is TaskListActivity) {
                (context as TaskListActivity).deleteTaskList(position)
            }
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }


//    our view Holder
    class MyViewHolder(view:View): RecyclerView.ViewHolder(view)
}