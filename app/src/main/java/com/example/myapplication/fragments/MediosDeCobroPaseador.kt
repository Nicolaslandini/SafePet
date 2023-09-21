package com.example.myapplication.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.entities.UserAbstract
import com.example.myapplication.entities.UserSession
import com.example.myapplication.repository.PaseadorRepository
import com.google.android.material.snackbar.Snackbar

class MediosDeCobroPaseador : Fragment() {

    lateinit var v : View
    lateinit var tarifa : EditText
    lateinit var btnEdit: Button

    private lateinit var viewModel: MediosDeCobroPaseadorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v =  inflater.inflate(R.layout.fragment_medios_de_cobro_paseador, container, false)
        tarifa = v.findViewById(R.id.paseadorTarifas)
        btnEdit = v.findViewById(R.id.editDatosCobro)

        return v
    }

    override fun onStart() {
        super.onStart()
        val user = UserSession.user

        // INICIALIZAR EL EDITTEXT CON LA TARIFA !!!

        btnEdit.setOnClickListener {
            val enteredTarifa = tarifa.text.toString()
            if (enteredTarifa.isNotEmpty()) {
                showConfirmationDialog(user, enteredTarifa)
            } else {
                Snackbar.make(v, "Todos los campos son requeridos", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    private fun showConfirmationDialog(user: UserAbstract, enteredTarifa: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmación")
        builder.setMessage("¿Estás seguro de actualizar tus datos?")

        builder.setPositiveButton("Sí") { _, _ ->
            // Usuario confirma, realiza la actualización
            val paseadorRepository = PaseadorRepository.getInstance()
            paseadorRepository.updateTarifa(user.dni, enteredTarifa)
            Snackbar.make(v, "Datos actualizados con éxito", Snackbar.LENGTH_SHORT).show()
            // ARREGLAR ESTO>> actualizar la tarifa

            val action = MediosDeCobroPaseadorDirections.actionMediosDeCobroPaseadorToHomePaseador()
            findNavController().navigate(action)
        }

        builder.setNegativeButton("No") { _, _ ->
            // Usuario cancela, no se realiza la actualizacion
        }

        val dialog = builder.create()
        dialog.show()
    }


}