package com.example.datepicker.dialogs

import android.content.Context
import android.os.Bundle
import android.widget.Button
import com.example.datepicker.R

class GenderDialog(
    context: Context,
    private val listner:Listener
):BaseDialog(context) {

    override val layoutId = R.layout.pop_gender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val btnFemale = findViewById<Button>(R.id.btn_female)
        val btnMale = findViewById<Button>(R.id.btn_male)
        val btnCancelBack = findViewById<Button>(R.id.btn_cancel_gender)

        btnFemale.setOnClickListener {
            dismiss()
            listner.onChooseFemale()
        }
        btnMale.setOnClickListener {
            dismiss()
            listner.onChooseMale()
        }
        btnCancelBack.setOnClickListener {
            dismiss()
        }

        listner.onInit()

    }




    interface Listener{
    fun onInit()
    fun onChooseFemale()
    fun onChooseMale()
    }
}