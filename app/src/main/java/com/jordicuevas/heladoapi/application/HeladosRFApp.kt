package com.jordicuevas.heladoapi.application

import android.app.Application
import com.jordicuevas.heladoapi.data.HeladoRepository
import com.jordicuevas.heladoapi.data.remote.RetroFitHelper

class HeladosRFApp: Application() {

    private val retrofit by lazy {
        RetroFitHelper().getRetroFit()
    }

    val repository by lazy {
        HeladoRepository(retrofit)
    }

}