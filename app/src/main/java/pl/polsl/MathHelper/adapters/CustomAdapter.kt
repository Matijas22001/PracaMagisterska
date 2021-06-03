package pl.polsl.MathHelper.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.polsl.MathHelper.R

class CustomAdapter(private val dataSet: ArrayList<String>) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    var currentlyChosenValue: Int? = null

    fun setcurrentlyChosenValue(currentlyChosenValue: Int){
        this.currentlyChosenValue = currentlyChosenValue
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = dataSet[position]
        if(position==currentlyChosenValue){
            viewHolder.itemView.setBackgroundColor(Color.GREEN)
        }else{
            viewHolder.itemView.setBackgroundColor(Color.WHITE)
        }
    }

    override fun getItemCount() = dataSet.size

    fun getItem(position: Int): String {
        return dataSet[position]
    }
}
