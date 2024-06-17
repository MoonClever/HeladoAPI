package com.jordicuevas.heladoapi.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.jordicuevas.heladoapi.R
import com.jordicuevas.heladoapi.application.HeladosRFApp
import com.jordicuevas.heladoapi.data.HeladoRepository
import com.jordicuevas.heladoapi.data.remote.model.HeladoDetailDto
import com.jordicuevas.heladoapi.data.remote.model.HeladoDto
import com.jordicuevas.heladoapi.databinding.FragmentHeladoDetailBinding
import com.jordicuevas.heladoapi.utils.Constants
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

private const val HELADO_ID = "param1"

class HeladoDetailFragment : Fragment(), OnMapReadyCallback {

    //Maps
    private lateinit var map: GoogleMap

    //Binding
    private var _binding: FragmentHeladoDetailBinding? = null

    private val binding get() = _binding!!

    private var helado_id: String? = null

    private lateinit var repository: HeladoRepository
    private lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mediaController = MediaController(requireContext())

        arguments?.let {
            helado_id = it.getString(HELADO_ID)

            Log.d(Constants.LOGTAG, getString(R.string.id_recibido_string, helado_id))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHeladoDetailBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onDestroy(){
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Map
        binding.map.onCreate(savedInstanceState)
        binding.map.getMapAsync(this)

        //Programar la conexión
        repository = (requireActivity().application as HeladosRFApp).repository

        lifecycleScope.launch {
            helado_id?.let { id ->

                //val call: Call<GameDetailDto> = repository.getGameDetail(id)

                //Con Apiary:
                val call: Call<HeladoDetailDto> = repository.getHeladoDetail(id)

                call.enqueue(object: Callback<HeladoDetailDto> {
                    override fun onResponse(p0: Call<HeladoDetailDto>, response: Response<HeladoDetailDto>) {

                        val cal = response.body()?.calories.toString()

                        val pop = response.body()?.popularity.toString()

                        val vegan = response.body()?.vegan.toString()

                        val videoURL = response.body()?.video.toString()

                        binding.apply {
                            pbLoading.visibility = View.INVISIBLE
                            binding.tvErrorDetail.visibility = View.INVISIBLE

                            tvSabor.text = response.body()?.name

                            tvDesc.text = response.body()?.description

                            tvCalories.text = getString(R.string.calorias_binding_string, cal)

                            tvPopularity.text = getString(R.string.popularity_rank_string, pop)

                            tvVeganOption.text = getString(R.string.opcion_vegana_string, vegan)

                            Glide.with(requireActivity())
                                .load(response.body()?.image)
                                .into(ivImage)


                            videoView.setMediaController(mediaController)
                            videoView.setVideoURI(Uri.parse(videoURL))
                            mediaController.setAnchorView(videoView)

                            createMarker(
                                response.body()?.latitud!!,
                                response.body()?.longitud!!,
                                tvSabor.text.toString(),
                                tvDesc.text.toString()
                            )

                        }

                    }

                    override fun onFailure(p0: Call<HeladoDetailDto>, p1: Throwable) {
                        //Manejar el error sin conexión
                        binding.pbLoading.visibility = View.INVISIBLE
                        binding.tvErrorDetail.visibility = View.VISIBLE
                    }

                })
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //El mapa está listo
        map = googleMap
        //createMarker(latitude, longitude, binding.tvNombrePerro.text.toString(), binding.tvDesc.text.toString())
    }

    private fun createMarker(lat: Double, long: Double, titulo: String, snippet: String){
        val coordinates = LatLng(lat, long)

        val marker = MarkerOptions()
            .position(coordinates)
            .title(titulo)
            .snippet(snippet)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.la_michoacana_logo))

        map.addMarker(marker)

        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
            1000,
            null
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(heladoId: String) =
            HeladoDetailFragment().apply{
                arguments = Bundle().apply{
                    putString(HELADO_ID, heladoId)
                }
            }



    }
}