package com.example.todo

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.todo.databinding.ActivityCreateTaskBinding
import java.util.*

class CreateTask : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: ActivityCreateTaskBinding

    companion object{
        const val IMPORTANT = 0;
        const val STUDY = 1;
        const val SPORT = 2;
        const val FREE = 3;
        const val TRIP = 4;
        const val OTHER = 5;
    }

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0
    var prevButton = -1

    var pickedDay = -1
    var pickedMonth = -1
    var pickedYear = -1
    var pickedHour = -1
    var pickedMinute = -1
    var formattedDay = ""
    var formattedMonth = ""
    var formattedMinute = ""
    var formattedHour = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun create(view: View){
        var caption: String = binding.editText.text.toString()
        if (caption.isEmpty()){
            Toast.makeText(this, "Brak opisu!", Toast.LENGTH_SHORT).show()
            return
        }
        var image: Int = -1
        when (prevButton){
            binding.btnImportant.id -> image = IMPORTANT
            binding.btnStudy.id -> image = STUDY
            binding.btnSports.id -> image = SPORT
            binding.btnFree.id -> image = FREE
            binding.btnTrips.id -> image = TRIP
            binding.btnOthers.id -> image = OTHER
        }
        if (image == -1){
            Toast.makeText(this, "Brak ikonki!", Toast.LENGTH_SHORT).show()
            return
        }
        if (pickedDay == -1){
            Toast.makeText(this, "Brak daty!", Toast.LENGTH_SHORT).show()
            return
        }
        if (pickedHour == -1){
            Toast.makeText(this, "Brak godziny!", Toast.LENGTH_SHORT).show()
            return
        }

        val myIntent = Intent()
        myIntent.putExtra("opis", caption)
        myIntent.putExtra("ikonka", image)
        myIntent.putExtra("data", "$formattedDay/$formattedMonth/$pickedYear")
        myIntent.putExtra("godzina", "$formattedHour:$formattedMinute")
        setResult(Activity.RESULT_OK, myIntent)
        finish()
    }

    fun createRandom(view: View){
        val myIntent = Intent()
        myIntent.putExtra("opis", getRandomString())
        myIntent.putExtra("ikonka", Random().nextInt(6))
        var randomDay = "0" + (Random().nextInt(9) + 1)
        var randomMonth = "0" + (Random().nextInt(9) + 1)
        var randomHour = "0" + (Random().nextInt(9) + 1)
        var randomMinute = "0" + (Random().nextInt(9) + 1)
        myIntent.putExtra("data", "$randomDay/$randomMonth/2020")
        myIntent.putExtra("godzina", "$randomHour:$randomMinute")
        setResult(Activity.RESULT_OK, myIntent)
        finish()
    }

    fun btnClick(view: View){
        binding.editText.clearFocus()
        if (prevButton != -1){
            findViewById<ImageButton>(prevButton).setColorFilter(Color.BLACK)
        }
        prevButton = (view as ImageButton).id
        findViewById<ImageButton>(prevButton).setColorFilter(Color.WHITE)
    }

    fun dateClick(view: View) {
        binding.editText.clearFocus()
        updateDateTime()
        DatePickerDialog(this, this, year, month, day).show()
    }

    fun timeClick(view: View) {
        binding.editText.clearFocus()
        updateDateTime()
        TimePickerDialog(this, this, hour, minute, true).show()
    }

    private fun updateDateTime(){
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        pickedDay = dayOfMonth
        pickedMonth = month
        pickedYear = year
        setDateValues()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        pickedHour = hourOfDay
        pickedMinute = minute
        setTimeValues()
    }

    fun setDateValues(){
        formattedDay = ""
        formattedMonth = ""
        if (pickedDay < 10){
            formattedDay += "0"
        }
        if (pickedMonth < 10){
            formattedMonth += "0"
        }
        formattedDay += pickedDay
        formattedMonth += (pickedMonth + 1)

        binding.dateBtn.text = "$formattedDay.$formattedMonth.$pickedYear"
        binding.dateBtn.setTextColor(Color.WHITE)
    }

    fun setTimeValues(){
        formattedMinute = ""
        formattedHour = ""
        if (pickedMinute < 10){
            formattedMinute += "0"
        }
        if (pickedHour < 10){
            formattedHour += "0"
        }
        formattedMinute += pickedMinute
        formattedHour += pickedHour
        binding.timeBtn.text = "$formattedHour:$formattedMinute"
        binding.timeBtn.setTextColor(Color.WHITE)
    }

    private fun getRandomString(): String? {
        val ALLOWED_CHARACTERS ="qwertyuiopasdfghjklzxcvbnm";
        val random = Random()
        var sizeOfRandomString = random.nextInt(24) + 1
        val sb = StringBuilder(sizeOfRandomString)
        for (i in 0 until sizeOfRandomString)
            sb.append(ALLOWED_CHARACTERS[(random.nextInt(ALLOWED_CHARACTERS.length))]
        )
        return sb.toString()
    }
}