package com.jordicuevas.heladoapi.data.remote

import com.jordicuevas.heladoapi.data.remote.model.HeladoDetailDto
import com.jordicuevas.heladoapi.data.remote.model.HeladoDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface HeladoApi {

    //Aquí ponemos las funciones para los endpoints

    //https://www.serverbpw.com/cm/games/games_list.php

    @GET
    fun getHelados(
        @Url url: String?
    ): Call<List<HeladoDto>>    //Así se llamaría: getGames("cm/games/games_list.php")"

    //https://www.serverbpw.com/cm/games/game_detail.php?id=21357&name=amaury

    @GET("flavors/{id}")
    fun getHeladoDetail(
        @Path("id") id: String?/*,
        @Query("name") name: String?*/
    ): Call<HeladoDetailDto>

    //Se llamaría a la función: getGameDetail("21357")
    //Se genera la url: https://www.serverbpw.com/cm/games/game_detail.php?id=21357


    //---------------------------------------------------
    //Para generar endpoints del estilo
    //https://www.serverbpw.com/cm/games/21357/amaury

//    @GET("flavors/{id}")
//    fun getGameTest(
//        @Path("id") id: String?,
//    ): Call<HeladoDetailDto>
//    //Se llamaría a la función: getGameTest("21357", "amaury")
//    //Se genera la url: https://www.serverbpw.com/cm/games/21357/amaury

    //Con Apiary
    @GET("games/game_detail/{id}")
    fun getGameDetailApiary(
        @Path("id") id: String?
    ): Call<HeladoDetailDto>

}