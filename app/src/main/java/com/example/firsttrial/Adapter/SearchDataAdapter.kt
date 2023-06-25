package com.example.firsttrial.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.firsttrial.R
import com.example.firsttrial.View.DetailAudio.DetailAudioActivity

class SearchDataAdapter (private var listAudio : ArrayList<String>) : RecyclerView.Adapter<SearchDataAdapter.ListViewHolder>(){

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvNameAudio: TextView = itemView.findViewById(R.id.tv_audio_name)
        var cardView : CardView = itemView.findViewById(R.id.card_recycler_view)

    }

    fun setFilteredList(newlistAudio: ArrayList<String>){
        this.listAudio = newlistAudio
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.activity_list_audio, viewGroup, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val filePath = listAudio.get(position)
        Log.e("filepath" ,filePath)

        val title = filePath.substring(filePath.lastIndexOf("/")+1)
        holder.tvNameAudio.setText(title)

        val mContext = holder.itemView.context
        holder.cardView.setOnClickListener({
            val intent = Intent(mContext, DetailAudioActivity::class.java)
            intent.putExtra(DetailAudioActivity.EXTRA_TITLE, title)
            intent.putExtra(DetailAudioActivity.EXTRA_FILEPATH, filePath)
            intent.putExtra(DetailAudioActivity.EXTRA_POSITION, position)
            intent.putExtra(DetailAudioActivity.EXTRA_LIST, listAudio)

            mContext.startActivity(intent)
        })

    }

    override fun getItemCount(): Int {
        return listAudio.size
    }
}