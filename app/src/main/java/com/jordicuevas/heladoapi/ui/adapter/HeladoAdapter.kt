package com.jordicuevas.heladoapi.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jordicuevas.heladoapi.data.remote.model.HeladoDto
import com.jordicuevas.heladoapi.databinding.HeladoElementBinding

class HeladoAdapter(
    private val helados: List<HeladoDto>,
    private val onHeladoClicked: (HeladoDto) -> Unit
) : RecyclerView.Adapter<HeladoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeladoViewHolder{
        val binding = HeladoElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeladoViewHolder(binding)
    }

    override fun getItemCount(): Int = helados.size

    override fun onBindViewHolder(holder: HeladoViewHolder, position: Int){
        val helado = helados[position]

        holder.bind(helado)

        Glide.with(holder.itemView.context)
            .load(helado.thumbnail)
            .into(holder.ivThumbnail)

        holder.itemView.setOnClickListener {
            onHeladoClicked(helado)
        }

    }



}