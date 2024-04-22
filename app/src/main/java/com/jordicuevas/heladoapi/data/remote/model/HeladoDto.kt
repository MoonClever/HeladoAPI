package com.jordicuevas.heladoapi.data.remote.model

import com.google.gson.annotations.SerializedName

data class HeladoDto(

    @SerializedName("id")
    var idHelado: Int? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("thumbnail")
    var thumbnail: String? = null

)
