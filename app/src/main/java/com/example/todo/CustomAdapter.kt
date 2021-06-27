package com.example.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val itemList: ArrayList<MainActivity.Item>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    var pos: Int = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val caption: TextView = view.findViewById(R.id.itemCaption)
        val image: ImageView = view.findViewById(R.id.itemImg)
        val date: TextView = view.findViewById(R.id.itemDate)
        val time: TextView = view.findViewById(R.id.itemTime)
        val tic: ImageView = view.findViewById(R.id.itemTic)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.caption.text = itemList[position].caption
        viewHolder.date.text = itemList[position].date
        viewHolder.time.text = itemList[position].time

        when (itemList[position].image){
            CreateTask.IMPORTANT -> viewHolder.image.setImageResource(R.drawable.important)
            CreateTask.STUDY -> viewHolder.image.setImageResource(R.drawable.study)
            CreateTask.SPORT -> viewHolder.image.setImageResource(R.drawable.sports)
            CreateTask.FREE -> viewHolder.image.setImageResource(R.drawable.free)
            CreateTask.TRIP -> viewHolder.image.setImageResource(R.drawable.trip)
            CreateTask.OTHER -> viewHolder.image.setImageResource(R.drawable.other)
        }

        viewHolder.itemView.setOnLongClickListener{
            itemList[position].picked = !itemList[position].picked
            notifyDataSetChanged()
            true
        }

        if (itemList[position].picked){
            viewHolder.tic.visibility = View.VISIBLE
            viewHolder.itemView.setBackgroundResource(R.drawable.custom_item_done)
        }
        else{
            viewHolder.tic.visibility = View.GONE
            viewHolder.itemView.setBackgroundResource(R.drawable.custom_item)
        }
    }

    override fun getItemCount() = itemList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

