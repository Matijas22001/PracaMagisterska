package com.example.myapplication.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Student
import kotlinx.android.synthetic.main.user_list_item.view.*

class UserListAdapter(val items : ArrayList<Student>, val context: Context) : RecyclerView.Adapter<UserListAdapter.ViewHolder>()  {

    var currentlyChosenUser: Int? = null

    fun setCurrentlyChosenUser(currentlyChosenUser: Int){
        this.currentlyChosenUser = currentlyChosenUser
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder?.tvAnimalType?.text = items.get(position)
        holder.userId.text = items[position].id.toString()
        holder.userName.text = items[position].name
        holder.userSurname.text = items[position].surname
        if(position==currentlyChosenUser){
            holder.itemView.setBackgroundColor(Color.GREEN)
        }else{
            holder.itemView.setBackgroundColor(Color.WHITE)
        }
    }

    fun getItem(position: Int): Student{
        return items[position]
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val userId = view.tv_id
        val userName = view.tv_name
        val userSurname = view.tv_surname
    }
}

