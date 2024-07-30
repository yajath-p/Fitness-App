package com.example.groupproject

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import android.content.Intent
import android.util.Log
import java.util.Calendar


class CalendarActivity : AppCompatActivity() {
    private lateinit var Sunday: TextView
    private lateinit var Monday: TextView
    private lateinit var Tuesday: TextView
    private lateinit var Wednesday: TextView
    private lateinit var Thursday: TextView
    private lateinit var Friday: TextView
    private lateinit var Saturday: TextView
    private lateinit var email: EditText
    private lateinit var sendButton: Button
    private lateinit var databaseReference: DatabaseReference
    var workoutsMap : Map<String,List<Workout>> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        Sunday = findViewById(R.id.Sunday)
        Monday = findViewById(R.id.Monday)
        Tuesday = findViewById(R.id.Tuesday)
        Wednesday = findViewById(R.id.Wednesday)
        Thursday = findViewById(R.id.Thursday)
        Friday = findViewById(R.id.Friday)
        Saturday = findViewById(R.id.Saturday)

        email = findViewById(R.id.email)
        sendButton = findViewById(R.id.send)
        databaseReference = FirebaseDatabase.getInstance().getReference("workouts")
        val fitness = Fitness(this)
        val themeColor = fitness.getTheme()
        val back = findViewById<Button>(R.id.back)
        back.setBackgroundColor(themeColor)

        val calendar = Calendar.getInstance()
        val dayOfWeekNum = calendar.get(Calendar.DAY_OF_WEEK)

        when (dayOfWeekNum) {
            1 -> Sunday.setTextColor(themeColor)
            2 -> Monday.setTextColor(themeColor)
            3 -> Tuesday.setTextColor(themeColor)
            4 -> Wednesday.setTextColor(themeColor)
            5 -> Thursday.setTextColor(themeColor)
            6 -> Friday.setTextColor(themeColor)
            7 -> Saturday.setTextColor(themeColor)
        }

        sendButton.setBackgroundColor(themeColor)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                workoutsMap = populateWorkoutsMap(dataSnapshot)
                updateUI(workoutsMap)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
        sendButton.setOnClickListener { sendEmail() }
    }

    private fun populateWorkoutsMap(dataSnapshot: DataSnapshot): Map<String, List<Workout>> {
        val workoutsMap: MutableMap<String, MutableList<Workout>> = mutableMapOf()

        for (workoutSnapshot in dataSnapshot.children) {
            val workoutId = workoutSnapshot.key ?: continue
            val workoutData = workoutSnapshot.value as? Map<*, *> ?: continue

            val type = workoutData["type"] as? String ?: continue
            val duration = (workoutData["duration"] as? Long)?.toInt() ?: continue
            val day = workoutData["day"] as? String ?: continue

            val workout = Workout(type, duration, "")

            if (workoutsMap.containsKey(day)) {
                workoutsMap[day]?.add(workout)
            } else {
                workoutsMap[day] = mutableListOf(workout)
            }
        }

        return workoutsMap
    }

    private fun updateUI(workoutsMap: Map<String, List<Workout>>) {
        val daysOfWeek = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val fitness = Fitness(this)
        val restDay = fitness.getRestDay()

        daysOfWeek.forEach { dayOfWeek ->
            val textViewId = resources.getIdentifier("${dayOfWeek.lowercase()}_workouts", "id", packageName)
            val textView = findViewById<TextView>(textViewId)

            if (dayOfWeek == restDay) {
                textView.text = "Rest day!"
            } else if (workoutsMap.containsKey(dayOfWeek)) {
                val workouts = workoutsMap[dayOfWeek]
                val workoutDetails = workouts?.joinToString(", ") { "${it.type} (${it.duration} min)" }
                textView.text = workoutDetails
            } else {
                textView.text = "Nothing Planned!"
            }
        }
    }

    private fun sendEmail() {
        val emailString = email.text.toString().trim()
        if (emailString.isNotEmpty()) {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "text/plain"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailString))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Weekly Workout Schedule")

            val emailBody = StringBuilder()
            for (dayOfWeek in workoutsMap.keys) {
                val workouts = workoutsMap[dayOfWeek]
                emailBody.append("$dayOfWeek:\n")
                if (workouts != null && workouts.isNotEmpty()) {
                    for (workout in workouts) {
                        emailBody.append("${workout.type} (${workout.duration} min)\n")
                    }
                } else {
                    emailBody.append("Rest Day!\n")
                }
                emailBody.append("\n")
            }
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody.toString())

            startActivity(Intent.createChooser(emailIntent, "Send Email"))
        } else {
            Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show()
        }
    }

    fun lastPage(v: View) {
        finish()
    }
}