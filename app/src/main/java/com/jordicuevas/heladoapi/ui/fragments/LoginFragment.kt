package com.jordicuevas.heladoapi.ui.fragments

import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.jordicuevas.heladoapi.R
import com.jordicuevas.heladoapi.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    private var email = ""
    private var constrasenia = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

//        Firebase.messaging.token.addOnCompleteListener { task ->
//            if(task.isSuccessful){
//                val token = task.result
//                Log.d("APPTOKEN", "Token: $token")
//            }
//        }

        //Si el usuario ya estaba loggueado
        if (firebaseAuth.currentUser != null) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    HeladosListFragment()
                )
                .addToBackStack(null)
                .commit()
        }

        //Los listeners a los botones
        binding.btnLogin.setOnClickListener {
            if (!validateFields()) return@setOnClickListener

            binding.progressBar.visibility = View.VISIBLE

            //autenticamos al usuario
            authenticateUser(email, constrasenia)

        }

        binding.btnRegistrarse.setOnClickListener {
            if (!validateFields()) return@setOnClickListener

            binding.progressBar.visibility = View.VISIBLE

            //Registramos al usuario en firebase
            firebaseAuth.createUserWithEmailAndPassword(email, constrasenia)
                .addOnCompleteListener { authResult ->

                    if (authResult.isSuccessful) {
                        //Opcionalmente le mando un correo de verificación
                        var user_firebase = firebaseAuth.currentUser

                        user_firebase?.sendEmailVerification()?.addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.verifEmailSuccStr),
                                Toast.LENGTH_SHORT
                            ).show()
                        }?.addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.verifEmailErrStr),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        Toast.makeText(
                            requireContext(),
                            getString(R.string.userCreateSuccStr),
                            Toast.LENGTH_SHORT
                        ).show()

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                HeladosListFragment()
                            )
                            .commit()
                    } else {
                        binding.progressBar.visibility = View.GONE
                        handleErrors(authResult)
                    }

                }

        }

        binding.tvRestablecerPassword.setOnClickListener {
            val resetMail = EditText(it.context)
            resetMail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

            val passwordResetDialog = AlertDialog.Builder(it.context)
                .setTitle(getString(R.string.recoverPassAlertStr))
                .setMessage(getString(R.string.inputEmailrecoverStr))
                .setView(resetMail)
                .setPositiveButton(getString(R.string.enviarStrr)) { _, _ ->
                    val mail = resetMail.text.toString()
                    if (mail.isNotEmpty()) {
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.linkEmailSendSuccStr),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }.addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.linkEmailSendFailStr, it.message),
                                Toast.LENGTH_SHORT
                            )
                                .show() //it tiene la excepción
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.askForEmailStr),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }.setNegativeButton(getString(R.string.cancelStr)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

    }

    private fun validateFields(): Boolean {
        email = binding.tietEmail.text.toString().trim()
        constrasenia = binding.tietContrasenia.text.toString().trim()

        if (email.isEmpty()) {
            binding.tietEmail.error = getString(R.string.emailNeededErrStr)
            binding.tietEmail.requestFocus()
            return false
        }

        if (constrasenia.isEmpty() || constrasenia.length < 6) {
            binding.tietContrasenia.error = getString(R.string.weakPassErrStr)
            binding.tietContrasenia.requestFocus()
            return false
        }

        return true
    }

    private fun handleErrors(task: Task<AuthResult>) {
        var errorCode = ""

        try {
            errorCode = (task.exception as FirebaseAuthException).errorCode
        } catch (e: Exception) {
            e.printStackTrace()
        }

        when (errorCode) {
            "ERROR_INVALID_EMAIL" -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.badFormatErrStr), Toast.LENGTH_SHORT
                ).show()
                binding.tietEmail.error = getString(R.string.badFormatErrStr)
                binding.tietEmail.requestFocus()
            }

            "ERROR_WRONG_PASSWORD" -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.badPassErrStr), Toast.LENGTH_SHORT
                ).show()
                binding.tietContrasenia.error = getString(R.string.badPassErrStr)
                binding.tietContrasenia.requestFocus()
                binding.tietContrasenia.setText("")

            }

            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                //An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.
                Toast.makeText(
                    requireContext(),
                    getString(R.string.emailAlreadyregisteredStr),
                    Toast.LENGTH_SHORT
                ).show()
            }

            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.EmailUsedErrStr),
                    Toast.LENGTH_LONG
                ).show()
                binding.tietEmail.error = (getString(R.string.EmailUsedErrStr))
                binding.tietEmail.requestFocus()
            }

            "ERROR_USER_TOKEN_EXPIRED" -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.sessionExpiredStr), Toast.LENGTH_LONG
                ).show()
            }

            "ERROR_USER_NOT_FOUND" -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.UserNotFoundStr),
                    Toast.LENGTH_LONG
                ).show()
            }

            "ERROR_WEAK_PASSWORD" -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.weakPassStr), Toast.LENGTH_LONG
                ).show()
                binding.tietContrasenia.error =
                    getString(R.string.minSizePassStr)
                binding.tietContrasenia.requestFocus()
            }

            "NO_NETWORK" -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.noNetworkErrStr), Toast.LENGTH_LONG
                ).show()
            }

            else -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.authFailStr), Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun authenticateUser(usr: String, psw: String) {

        firebaseAuth.signInWithEmailAndPassword(usr, psw).addOnCompleteListener { authResult ->
            if (authResult.isSuccessful) {


                Toast.makeText(
                    requireContext(),
                    getString(R.string.authSuccStr),
                    Toast.LENGTH_SHORT
                ).show()

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        HeladosListFragment()
                    ).commit()
            } else {
                binding.progressBar.visibility = View.GONE
                handleErrors(authResult)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

