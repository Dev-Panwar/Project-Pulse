package dev.panwar.projectpulse.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.adapters.LabelColorListItemsAdapter

abstract class LabelColorListDialog(
    context: Context,
    private var list: ArrayList<String>,
    private val title: String = "",
    private val mSelectedColor: String = ""
) : Dialog(context) {
//    Inheriting Dialog...because it will be shown as Dialog

    private var adapter: LabelColorListItemsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

//        inflating dialog list layout to this class
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)

//        setting up the content for this class
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
//        for setting up recycler view
        setUpRecyclerView(view)
    }

//    setting up recycler view...showing colors
    private fun setUpRecyclerView(view: View) {
        view.findViewById<TextView>(R.id.tvTitle).text = title
        view.findViewById<RecyclerView>(R.id.rvList).layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListItemsAdapter(context, list, mSelectedColor)
        view.findViewById<RecyclerView>(R.id.rvList).adapter = adapter

//        for handling click events on particular color
        adapter!!.onItemClickListener = object : LabelColorListItemsAdapter.OnItemClickListener {

            override fun onClick(position: Int, color: String) {
                dismiss()
//                function of this Class...implemented in CardDetailActivity
                onItemSelected(color)
            }
        }
    }

//    will be implemented in CardDetailActivity
    protected abstract fun onItemSelected(color: String)
}
// END