package com.jordicuevas.heladoapi.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.jordicuevas.heladoapi.data.remote.model.HeladoDto
import com.jordicuevas.heladoapi.databinding.HeladoElementBinding

class HeladoViewHolder(private var binding: HeladoElementBinding):
    RecyclerView.ViewHolder(binding.root){

        val ivThumbnail = binding.ivThumbnail

    fun bind(helado: HeladoDto){
        binding.tvSabor.text = helado.name
        binding.tvId.text = helado.idHelado.toString()
    }

}