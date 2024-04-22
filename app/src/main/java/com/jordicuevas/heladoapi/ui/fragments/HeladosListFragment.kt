package com.jordicuevas.heladoapi.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jordicuevas.heladoapi.R
import com.jordicuevas.heladoapi.application.HeladosRFApp
import com.jordicuevas.heladoapi.data.HeladoRepository
import com.jordicuevas.heladoapi.data.remote.model.HeladoDto
import com.jordicuevas.heladoapi.databinding.FragmentHeladosListBinding
import com.jordicuevas.heladoapi.ui.adapter.HeladoAdapter
import com.jordicuevas.heladoapi.utils.Constants
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HeladosListFragment : Fragment() {

    private var _binding: FragmentHeladosListBinding? = null

    private val binding get() = _binding!!

    private lateinit var repository: HeladoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHeladosListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = (requireActivity().application as HeladosRFApp).repository

        lifecycleScope.launch {
            val call: Call<List<HeladoDto>> = repository.getHelados("flavors")

            call.enqueue(object : Callback<List<HeladoDto>> {
                override fun onResponse(
                    p0: Call<List<HeladoDto>>,
                    response: Response<List<HeladoDto>>
                ) {

                    binding.pbLoading.visibility = View.GONE

                    Log.d(Constants.LOGTAG, getString(R.string.respuesta_recibida, response.body()))

                    response.body()?.let { helados ->
                        binding.rvHelados.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = HeladoAdapter(helados) { helado ->
                                requireActivity().supportFragmentManager.beginTransaction()
                                    .replace(
                                        R.id.fragment_container,
                                        HeladoDetailFragment.newInstance(helado.idHelado.toString())
                                    )
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }

                    }

                }

                override fun onFailure(p0: Call<List<HeladoDto>>, error: Throwable) {
                    //Manejo del error
                    binding.pbLoading.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_conexion_string, error.message),
                        Toast.LENGTH_SHORT
                    ).show()
                }


            })
        }
    }
}
