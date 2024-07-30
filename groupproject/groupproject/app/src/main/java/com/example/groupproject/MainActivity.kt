package com.example.groupproject

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView


class MainActivity : AppCompatActivity() {
    private lateinit var fitness : Fitness
    private lateinit var title : TextView
    private lateinit var workoutType : EditText
    private lateinit var workoutDuration : EditText
    private lateinit var workoutDay : Spinner
    private lateinit var restDay : Spinner
    private lateinit var theme : Spinner
    private lateinit var addWorkoutButton : Button
    private lateinit var viewWorkoutsButton : Button
    private lateinit var calendarButton : Button
    private lateinit var preferencesButton : Button
    private lateinit var firebaseReference : DatabaseReference
    private lateinit var adView : AdView
    private var color = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val firebase = FirebaseDatabase.getInstance()
        firebaseReference = firebase.getReference("workouts")

        fitness = Fitness(this)

        title = findViewById(R.id.title)
        workoutType = findViewById(R.id.workout_type)
        workoutDuration = findViewById(R.id.workout_duration)
        workoutDay = findViewById(R.id.workout_day)
        restDay = findViewById(R.id.preferences_rest)
        theme = findViewById(R.id.preferences_color)

        // Sets dropdowns for the user inputs
        var adapter : ArrayAdapter<CharSequence> =
            ArrayAdapter.createFromResource(this, R.array.days, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        workoutDay.adapter = adapter
        restDay.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.colors, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        theme.adapter = adapter

        addWorkoutButton = findViewById(R.id.add_workout)
        viewWorkoutsButton = findViewById(R.id.workout)
        calendarButton = findViewById(R.id.calendar)
        preferencesButton = findViewById(R.id.set_preferences)


        if (fitness.getTheme() != 0) {
            color = fitness.getTheme()
            title.setTextColor(color)
            addWorkoutButton.setBackgroundColor(color)
            viewWorkoutsButton.setBackgroundColor(color)
            calendarButton.setBackgroundColor(color)
            preferencesButton.setBackgroundColor(color)
        }

        addWorkoutButton.setOnClickListener{addWorkout()}
        viewWorkoutsButton.setOnClickListener{toWorkouts()}
        calendarButton.setOnClickListener{toCalendar()}
        preferencesButton.setOnClickListener{changePreferences()}

        adView = AdView(this)
        adView.setAdSize(AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT))
        val adUnitId = "ca-app-pub-3940256099942544/6300978111"
        adView.adUnitId = adUnitId

        val builder : AdRequest.Builder = AdRequest.Builder()
        builder.addKeyword("workout" ).addKeyword( "fitness")
        val request : AdRequest = builder.build()

        val adLayout : RelativeLayout = findViewById(R.id.ad_view)
        adLayout.addView(adView)
        adView.loadAd(request)
    }

    fun addWorkout() {
        var message = "Workout added!"
        if (workoutDay.selectedItem.toString() == fitness.getRestDay()) {
            message = "You can't add a workout on a rest day!"
        } else {
            // Firebase stuff here
            val newWorkout =
                Workout(workoutType.text.toString(), Integer.parseInt(workoutDuration.text.toString()), workoutDay.selectedItem.toString())
            firebaseReference.push().setValue(newWorkout)

            val workoutId = firebaseReference.key
        }

        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        toast.show()
    }

    fun toWorkouts() {
        val myIntent = Intent(this, WorkoutActivity::class.java)
        this.startActivity(myIntent)
    }

    fun toCalendar() {
        val myIntent = Intent(this, CalendarActivity::class.java)
        this.startActivity(myIntent)
    }

    fun changePreferences() {
        val restDay = restDay.selectedItem.toString()
        val theme = theme.selectedItem.toString()

        when (theme) {
            "Red" -> color = Color.RED
            "Blue" ->  color = Color.BLUE
            "Green" -> color = Color.GREEN
            "Black" -> color = Color.BLACK
        }
        fitness.setRestDay(restDay)
        fitness.setTheme(color)

        title.setTextColor(color)
        addWorkoutButton.setBackgroundColor(color)
        viewWorkoutsButton.setBackgroundColor(color)
        calendarButton.setBackgroundColor(color)
        preferencesButton.setBackgroundColor(color)

        fitness.setPreferences(this)
    }

}