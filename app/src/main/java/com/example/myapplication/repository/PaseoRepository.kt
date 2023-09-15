package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.entities.Paseo
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PaseoRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val paseosReference: DatabaseReference = database.getReference("paseos")

    companion object {
        @Volatile
        private var instance: PaseoRepository? = null

        fun getInstance(): PaseoRepository {
            return instance ?: synchronized(this) {
                instance ?: PaseoRepository().also { instance = it }
            }
        }
    }

    fun getPaseo(dniUser: String): Task<DataSnapshot?> {
       return paseosReference.orderByChild("user/dni").equalTo(dniUser).get()
    }

    fun addPaseo(paseo: Paseo) {
        val paseadorKey = paseosReference.push().key // erar una clave única para el paseador
        paseadorKey?.let {
            paseosReference.child(it).setValue(paseo)
        }
    }

}