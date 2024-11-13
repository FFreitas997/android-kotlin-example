package com.example.dobcalc

import android.app.DatePickerDialog
import android.view.View
import android.widget.TextView
import java.util.Calendar

class ButtonOnClickListener(private val context: MainActivity) : View.OnClickListener {

    private val selectedDateTextView: TextView = context.findViewById(R.id.selected_date)
    private val resultTextView: TextView = context.findViewById(R.id.result)

    init {
        selectedDateTextView.text = context
            .getString(R.string.selected_date_value, "DD", "MM", "YYYY")
        resultTextView.text = "0"
    }

    override fun onClick(view: View?) {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            context,
            { view, year, month, dayOfMonth ->
                selectedDateTextView.text = context
                    .getString(
                        R.string.selected_date_value,
                        handleDayMonthFormat(dayOfMonth),
                        handleDayMonthFormat(month + 1),
                        year.toString()
                    )

                resultTextView.text = calculateAgeInMinutes(year, month, dayOfMonth)
            },
            year, month, day
        )
        dialog.datePicker.maxDate = System.currentTimeMillis() - 86400000
        dialog.show()
    }


    fun handleDayMonthFormat(value: Int): String = when (value) {
        in 1..9 -> "0$value"
        else -> value.toString()
    }

    fun calculateAgeInMinutes(year: Int, month: Int, dayOfMonth: Int): String {
        val dob = Calendar.getInstance()
        dob.set(year, month, dayOfMonth)
        val currentTime = Calendar.getInstance()
        val diff = currentTime.timeInMillis - dob.timeInMillis
        val minutes = diff / 60000
        return minutes.toString()
    }
}