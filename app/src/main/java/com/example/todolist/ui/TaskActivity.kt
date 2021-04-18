package com.example.todolist.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.example.todolist.data.AppDatabase
import com.example.todolist.data.TodoModel
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.custom_dialogs.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


const val DB_NAME = "todo.db"
class TaskActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var myCalendar: Calendar
    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
   lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    var finalDate = 0L
    var finalTime = 0L

   private var labels= arrayListOf("Personal", "Buisness", "Insurance", "Banking", "Other")

    val db by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        dateEdt.setOnClickListener(this)
        timeEdt.setOnClickListener(this)
        saveBtn.setOnClickListener(this)
        imgAddCategory.setOnClickListener(this)

        setUpSpinner()
    }

    private fun setUpSpinner() {
        val adapter=ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, labels)
        labels.sort()

        spinnerCategory.adapter=adapter
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View) {
        when(v.id){
            R.id.dateEdt -> {
                setDateListener()
            }
            R.id.timeEdt -> {
                setTimeListener()
            }
            R.id.saveBtn -> {
                saveTodo()
            }
            R.id.imgAddCategory -> {
                showAlertDialog()
            }
        }
    }

    private fun showAlertDialog() {
        val builder=AlertDialog.Builder(this)
        builder.setTitle("Add a category")
        val cusTomLayout=layoutInflater.inflate(R.layout.custom_dialogs,null)
        builder.setView(cusTomLayout)




            builder.setPositiveButton("Add") { dialog, which ->

            }



        builder.setNegativeButton("Cancel"){dialog,which->

        }
        val dialog=builder.create()
        dialog.show()


    }

    private fun saveTodo() {
        val category = spinnerCategory.selectedItem.toString()
        val title = titleInplay.editText?.text.toString()
        val description = taskInpLay.editText?.text.toString()

        GlobalScope.launch(Dispatchers.Main) {
            val id = withContext(Dispatchers.IO) {
                return@withContext db.todoDao().insertTask(
                        TodoModel(
                                title,
                                description,
                                category,
                                finalDate,
                                finalTime
                        )
                )
            }
            finish()
        }

    }


    private fun setTimeListener() {
        myCalendar= Calendar.getInstance()

        timeSetListener=TimePickerDialog.OnTimeSetListener { Time, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            upDateTime()
        }
        val timePickerDialog=TimePickerDialog(
                this,
                timeSetListener,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE),
                false
        )
        timePickerDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun upDateTime() {
        //11:20 pm
       val myFormat="h:mm a"
        val sdf=SimpleDateFormat(myFormat)
        finalTime=myCalendar.time.time
        timeEdt.setText(sdf.format(myCalendar.time))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setDateListener() {
        myCalendar= Calendar.getInstance()

     dateSetListener=DatePickerDialog.OnDateSetListener { _Date, year, month, dayOfMonth ->
         myCalendar.set(Calendar.YEAR, year)
         myCalendar.set(Calendar.MONTH, month)
         myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
         updateDate()
     }

        val datePickerDialog=DatePickerDialog(
                this,
                dateSetListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate=System.currentTimeMillis()
        datePickerDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateDate() {
        //Mon 5 Jan 2021
        val myFormat="EEE, d MMM yyyy"
        val sdf=SimpleDateFormat(myFormat)
        finalDate=myCalendar.time.time
        dateEdt.setText(sdf.format(myCalendar.time))

        timeInptLay.visibility=View.VISIBLE
    }
}