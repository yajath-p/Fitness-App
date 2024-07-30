package com.example.groupproject

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Chronometer
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class WorkoutActivity : AppCompatActivity() {
    private lateinit var startStopButton: Button
    private lateinit var reset: Button
    private lateinit var back: Button
    private lateinit var workoutActivities: Spinner
    private lateinit var databaseReference: DatabaseReference
    private lateinit var goal: TextView
    private lateinit var warning : TextView
    private lateinit var watch : TextView
    private lateinit var workoutText : TextView
    private lateinit var countDownTimer: CountDownTimer

    private var activateReset = false
    private var timerRunning = false
    private val workoutList: MutableList<Workout> = mutableListOf()
    private val workoutListString: MutableList<String> = mutableListOf()
    private var timeRemaining: Long = 600000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        startStopButton = findViewById(R.id.start)
        reset = findViewById(R.id.reset)
        back = findViewById(R.id.back)
        workoutActivities = findViewById(R.id.workout_activities)
        goal = findViewById(R.id.goal)
        warning = findViewById(R.id.warning)
        watch = findViewById(R.id.stop_watch)
        workoutText = findViewById(R.id.workout_text)

        databaseReference = FirebaseDatabase.getInstance().getReference("workouts")
        val calendar = Calendar.getInstance()
        val dayOfWeekNum = calendar.get(Calendar.DAY_OF_WEEK)
        var dayOfWeek = ""

        val fitness = Fitness(this)
        val themeColor = fitness.getTheme()

        back.setBackgroundColor(themeColor)
        reset.setBackgroundColor(themeColor)

        when (dayOfWeekNum) {
            1 -> dayOfWeek = "Sunday"
            2 -> dayOfWeek = "Monday"
            3 -> dayOfWeek = "Tuesday"
            4 -> dayOfWeek = "Wednesday"
            5 -> dayOfWeek = "Thursday"
            6 -> dayOfWeek = "Friday"
            7 -> dayOfWeek = "Saturday"
        }

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.value != null)
                    Log.w("MainActivity",dataSnapshot.value.toString())

                for (snapshot in dataSnapshot.children) {
                    val value = snapshot.value
                    val valueString = value.toString()
                    val keyValuePairs = valueString
                        .removeSurrounding("{", "}")
                        .split(", ")
                        .map { it.split("=") }
                        .map { it[0] to it[1] }

                    val valueMap = keyValuePairs.toMap()
                    val duration = valueMap["duration"]?.toInt() ?: 0
                    val type = valueMap["type"] ?: ""
                    val day = valueMap["day"] ?: ""

                    val workout = Workout(type, duration, day)

                    if (day == dayOfWeek) {
                        workoutList.add(workout)
                        workoutListString.add(type)
                    }
                }
                
                if(workoutListString.isEmpty() || fitness.getRestDay() == dayOfWeek) {
                    if(fitness.getRestDay() == dayOfWeek) {
                        warning.text="Get some Rest!"
                    }
                    warning.visibility=View.VISIBLE
                    goal.visibility=View.INVISIBLE
                    watch.visibility=View.INVISIBLE
                    reset.visibility=View.INVISIBLE
                    startStopButton.visibility=View.INVISIBLE
                    workoutActivities.visibility=View.INVISIBLE
                    workoutText.visibility=View.INVISIBLE
                } else {
                    val adapter: ArrayAdapter<String> = ArrayAdapter(this@WorkoutActivity, android.R.layout.simple_spinner_item, workoutListString)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    workoutActivities.adapter = adapter
                    val x = workoutList[0].duration
                    goal.text = "Goal: $x minutes"
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        }

        databaseReference.addValueEventListener(valueEventListener)

        workoutActivities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(!timerRunning) {
                    selectedString()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    fun selectedString() : Int {
        val inputString = workoutActivities.selectedItem.toString()
        var duration = 0
        for (x in workoutList) {
            if (x.type == inputString) {
                duration = x.duration
            }
        }
        timeRemaining = (duration * 60000).toLong()
        goal.text = "Goal: $duration minutes"
        updateTimerUI(timeRemaining)
        return duration
    }

    fun timerStartStop(v: View) {
        if (!timerRunning) {
            reset.isEnabled = false
            workoutActivities.isEnabled = false
            activateReset = true
            updateTimer(timeRemaining)
            startStopButton.text = "STOP"
            startStopButton.setBackgroundColor(Color.RED)
            timerRunning = true
        } else {
            reset.isEnabled = true
            workoutActivities.isEnabled = true
            countDownTimer.cancel()
            startStopButton.text = "START"
            startStopButton.setBackgroundColor(Color.GREEN)
            timerRunning = false
        }
    }

    private fun updateTimer(duration: Long) {
        countDownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                updateTimerUI(millisUntilFinished)
            }

            override fun onFinish() {
                updateTimerUI(0)
                countDownTimer.cancel()
                startStopButton.text = "START"
                startStopButton.setBackgroundColor(Color.GREEN)
                timerRunning = false
                var min = duration/60000
                goal.text = "Goal: $min minutes, Reached!"
            }
        }

        countDownTimer.start()
    }

    fun updateTimerUI(millisUntilFinished: Long) {
        val minutes = (millisUntilFinished / 60000).toInt()
        val seconds = (millisUntilFinished / 1000 % 60).toInt()
        val timeString = String.format("%02d:%02d", minutes, seconds)
        findViewById<TextView>(R.id.stop_watch).text = timeString
    }

    fun timerReset(v: View) {
        if(!timerRunning && activateReset) {
            countDownTimer.cancel()
            timerRunning = false
            var newTime = selectedString() * 60000L
            updateTimerUI(newTime)
            timeRemaining = newTime
        }
    }

    fun lastPage(v: View) {
        finish()
    }
}