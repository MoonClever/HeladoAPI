package com.jordicuevas.heladoapi.data.remote.model

import com.google.gson.annotations.SerializedName

data class HeladoDetailDto(

    @SerializedName("name")
    var name : String?,

    @SerializedName("image")
    var image : String?,

    @SerializedName("description")
    var description : String?,

    @SerializedName("calories")
    var calories : Int?,

    @SerializedName("popularity")
    var popularity : Int?,

    @SerializedName("Vegan_Option")
    var vegan : Boolean?,

    @SerializedName("video")
    var video: String?

)
