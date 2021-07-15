package com.example.datepicker.dialogs

import android.content.Context
import android.os.Bundle
import android.widget.Button
import com.aigestudio.wheelpicker.widgets.WheelYearPicker
import com.example.datepicker.R
import java.util.*

class BirthYearDialog(
    context: Context,
    private val listner: Listner,
):BaseDialog(context) {

    companion object{
        const val DEFAULT_SELECTED_YEAR = 1995
    }

    override val layoutId = R.layout.age_group_popup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val  currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val wheelYearPicker = findViewById<WheelYearPicker>(R.id.yearWheel)
        wheelYearPicker.setYearFrame(1900, currentYear)
        wheelYearPicker.selectedYear = currentYear-25
        wheelYearPicker.visibleItemCount = 4
        wheelYearPicker.setIndicator(true)

        val btnOk = findViewById<Button>(R.id.btn_Ok)
        val btnCancel = findViewById<Button>(R.id.btn_cancel_age)

        btnOk.setOnClickListener {
            dismiss()
//            listner.onClickOk(wheelYearPicker.cur)
        }
        btnCancel.setOnClickListener {
            dismiss()
            listner.onCancel()
        }
    }



    interface Listner{
        fun onClickOk(year:Int)
        fun onCancel()
    }
}