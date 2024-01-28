package dev.panwar.projectpulse.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.activities.TaskListActivity
import dev.panwar.projectpulse.models.Card
import dev.panwar.projectpulse.models.SelectedMembers
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
//          if label color not empty
            if (model.labelColor.isNotEmpty()){
                holder.itemView.findViewById<View>(R.id.view_label_color).visibility=View.VISIBLE
                holder.itemView.findViewById<View>(R.id.view_label_color).setBackgroundColor(Color.parseColor(model.labelColor))
            }else{
                holder.itemView.findViewById<View>(R.id.view_label_color).visibility=View.GONE
            }

            holder.itemView.findViewById<TextView>(R.id.tv_card_name).text=model.name

//            we are using RV in RV means card RV containing RV of MEMBERS in particular card So the RV of members will be setup here and CardListItemAdapter will be setup in TaskListActivity
            if ((context as TaskListActivity).mAssignedMemberDetailList.size>0){
                  val selectedMembersList:ArrayList<SelectedMembers> = ArrayList()

                for (i in (context as TaskListActivity).mAssignedMemberDetailList.indices){
                      for (j in model.assignedTo){
                          if ((context as TaskListActivity).mAssignedMemberDetailList[i].id ==j ){
                              val selectedMember=SelectedMembers((context as TaskListActivity).mAssignedMemberDetailList[i].id,(context as TaskListActivity).mAssignedMemberDetailList[i].image)
                              selectedMembersList.add(selectedMember)
                          }
                      }
                }
                if (selectedMembersList.size>0){
//                    if the selected member is one who created the card...as when we created a card we added the creator as member
                    if (selectedMembersList.size==1 && selectedMembersList[0].id == model.createdBy){
                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).visibility=View.GONE
                    }else{
                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).visibility=View.VISIBLE

                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).layoutManager=GridLayoutManager(context,4)
//                        we are not assigning members here...we are just displaying members
                        val adapter=CardMemberListItemsAdapter(context,selectedMembersList, false)
                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).adapter=adapter

                        adapter.setOnClickListener(object : CardMemberListItemsAdapter.OnClickListener{
                            override fun onClick() {
                                if (onClickListener!=null){
//                                    just a empty click
                                    onClickListener!!.onClick(position)
                                }
                            }

                        })
                    }
                }else{
                    holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).visibility=View.GONE
                }
            }

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