package com.example.myapplication.fragments

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.entities.EstadoEnum
import com.example.myapplication.entities.MedioDePagoEnum
import com.example.myapplication.entities.Paseador
import com.example.myapplication.entities.PaseoProgramado
import com.example.myapplication.entities.User
import com.example.myapplication.entities.UserAbstract
import com.example.myapplication.entities.UserSession
import com.example.myapplication.repository.PaseoRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.absoluteValue


class PaseoProgramadoDetail : Fragment() {

    lateinit var userSession: UserAbstract


    private lateinit var v: View
    private lateinit var fechaPaseo: TextView
    private lateinit var ubicacion: TextView
    private lateinit var duenioPaseo: TextView
    private lateinit var paseadorPaseo: TextView
    private lateinit var mascota: TextView
    private lateinit var valorPaseo: TextView
    private lateinit var calificacion: TextView
    private lateinit var alias: TextView
    private lateinit var aliasImg: ImageView
    private lateinit var btnIniciarPaseo: Button
    private lateinit var btnCancelarPaseo: Button
    private lateinit var btnCalificar: Button
    lateinit var paseosRepository: PaseoRepository
    lateinit var location: FusedLocationProviderClient
    lateinit var viewModel: PaseoProgramadoDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_paseo_programado_detail, container, false)
        fechaPaseo = v.findViewById(R.id.fechaPaseo)
        ubicacion = v.findViewById(R.id.ubicacion)
        duenioPaseo = v.findViewById(R.id.dueño)
        paseadorPaseo = v.findViewById(R.id.paseador)
        mascota = v.findViewById(R.id.mascota)
        valorPaseo = v.findViewById(R.id.valorPaseo)
        calificacion = v.findViewById(R.id.calificacionPaseo)
        btnIniciarPaseo = v.findViewById(R.id.btnIniciarPaseo)
        btnCancelarPaseo = v.findViewById(R.id.btnCancelarPaseo)
        btnCalificar = v.findViewById(R.id.btnCalificar)
        alias = v.findViewById(R.id.paseoAlias)
        aliasImg = v.findViewById(R.id.aliasImg)
        paseosRepository = PaseoRepository.getInstance()
        location = LocationServices.getFusedLocationProviderClient(requireContext())
        userSession = UserSession.user
        viewModel = ViewModelProvider(this).get(PaseoProgramadoDetailViewModel::class.java)
        return v
    }

    override fun onStart() {
        super.onStart()
        // UserSession.user = UserSession.user as User
        val paseo =
            PaseoProgramadoDetailArgs.fromBundle(requireArguments()).paseoProgramadodetalle
        fechaPaseo.text = paseo.fecha
        ubicacion.text = paseo.user.direccion
        duenioPaseo.text = "${paseo.user.lastName}, ${paseo.user.name} "
        paseadorPaseo.text = "${paseo.paseador.lastName}, ${paseo.paseador.name} "
        mascota.text = paseo.user.mascota.nombre
        valorPaseo.text = paseo.paseador.tarifa.toString()
        alias.text = paseo.paseador.alias
        alias.visibility = if(paseo.medioDePago == MedioDePagoEnum.EFECTIVO) {
            View.GONE
        } else {
            View.VISIBLE
        }
        aliasImg.visibility = alias.visibility


        val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val fecha = format.parse(paseo.fecha)
        val fechaHoy = Date()
        btnCalificar.visibility = View.GONE
        if (paseo.calificacion == 0) {
            calificacion.text = ""
        } else {
            calificacion.text = "${paseo.calificacion.toString()} ESTRELLAS"
            btnCalificar.visibility = View.GONE
        }

        if (userSession is User) {
            btnCancelarPaseo.visibility = View.VISIBLE
        }

        if (paseo.estado == EstadoEnum.ACTIVO && UserSession.user is User) {
            btnIniciarPaseo.text = "El paseo ya inicio"
            btnIniciarPaseo.isEnabled = false
            btnCancelarPaseo.visibility = View.GONE
        } else if(paseo.estado == EstadoEnum.ACTIVO){
            btnIniciarPaseo.text = "El paseo ya inicio"
            btnIniciarPaseo.isEnabled = false
            btnCancelarPaseo.text = "Finalizar paseo"
            btnCancelarPaseo.visibility = View.VISIBLE
            btnCalificar.visibility = View.GONE
        } else if (paseo.estado == EstadoEnum.FINALIZADO && userSession is User) {
            btnIniciarPaseo.text = "El paseo ya finalizo"
            btnIniciarPaseo.isEnabled = false
            btnCancelarPaseo.visibility = View.GONE
            btnCalificar.visibility = if(paseo.calificacion == 0) {
                View.VISIBLE
            }else {
                View.GONE
            }
        } else if(paseo.estado == EstadoEnum.FINALIZADO) {
            btnIniciarPaseo.text = "El paseo ya finalizo"
            btnIniciarPaseo.isEnabled = false
            btnCancelarPaseo.visibility = View.GONE
            btnCalificar.visibility = View.GONE
        } else if(paseo.estado == EstadoEnum.SOLICITADO && userSession is Paseador) {
            btnIniciarPaseo.text = "Aceptar paseo"
            btnIniciarPaseo.visibility = View.VISIBLE
            btnCancelarPaseo.text = "Rechazar paseo"
            btnCancelarPaseo.visibility = View.VISIBLE
        } else if(paseo.estado == EstadoEnum.SOLICITADO && userSession is User) {
            btnIniciarPaseo.visibility = View.GONE
            btnCancelarPaseo.visibility = View.VISIBLE
        } else if(paseo.estado == EstadoEnum.PENDIENTE && userSession is User) {
            btnIniciarPaseo.visibility = View.GONE
            btnCalificar.visibility = View.GONE
        }

        if (((fechaHoy.time - 10800000) - fecha.time).absoluteValue >= 600000 && paseo.estado == EstadoEnum.PENDIENTE) {
            Log.d("FECHA", "FECHA")
            btnIniciarPaseo.visibility = View.GONE
        }

        btnIniciarPaseo.setOnClickListener {
                comenzarPaseo(location, paseo)
                findNavController().popBackStack()
                Snackbar.make(v, "Paseo iniciado exitosamente", Snackbar.LENGTH_SHORT).show()
        }

        btnCancelarPaseo.setOnClickListener {
            showConfirmationDialog(paseo)
        }

        btnCalificar.setOnClickListener() {
            val action = PaseoProgramadoDetailDirections.actionPaseoProgramadoDetailToCalificarPaseo(paseo)
            findNavController().navigate(action)
        }


    }

    private fun showConfirmationDialog(paseo: PaseoProgramado) {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Confirmación")
        builder.setMessage("¿Estás seguro de finalizar este paseo?")
        builder.setPositiveButton("Sí") { _, _ ->
            // USUARIO CONFIRMA, HACER VALIDACIÓN DE FECHA DISPONIBLE
            if(paseo.estado == EstadoEnum.PENDIENTE){
                paseosRepository.deletePaseo(paseo)
            } else {
                paseosRepository.updateEstadoPaseo(paseo.id, EstadoEnum.FINALIZADO)
            }
            findNavController().popBackStack()
        }

        builder.setNegativeButton("No") { _, _ ->
            // Usuario canceló, no se realiza la actualización
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun comenzarPaseo(location: FusedLocationProviderClient, paseo: PaseoProgramado) {
        viewModel.createPaseoActivo(location, userSession, paseo.id)
    }

}
