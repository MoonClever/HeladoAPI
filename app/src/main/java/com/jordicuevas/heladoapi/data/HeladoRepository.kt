package com.jordicuevas.heladoapi.data

import com.jordicuevas.heladoapi.data.remote.HeladoApi
import com.jordicuevas.heladoapi.data.remote.model.HeladoDetailDto
import com.jordicuevas.heladoapi.data.remote.model.HeladoDto
import retrofit2.Call
import retrofit2.Retrofit

class HeladoRepository(private val retrofit: Retrofit) {

    private val heladoApi = retrofit.create(HeladoApi::class.java)

    fun getHelados(url: String?): Call<List<HeladoDto>> = heladoApi.getHelados(url)

    fun getHeladoDetail(id: String?): Call <HeladoDetailDto> = heladoApi.getHeladoDetail(id)

    //fun getHeladoDetailApiary(id: String?): Call<HeladoDetailDto> = heladoApi.getGameDetailApiary(id)

}