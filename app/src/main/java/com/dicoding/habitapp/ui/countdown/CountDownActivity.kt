package com.dicoding.habitapp.ui.countdown

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import java.util.concurrent.TimeUnit

class CountDownActivity : AppCompatActivity() {
    private lateinit var habit: Habit
    private lateinit var  channelName : String
    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        supportActionBar?.title = "Count Down"

        channelName = getString(R.string.notify_channel_name)
        workManager = WorkManager.getInstance(this)


        val habit = intent.getParcelableExtra<Habit>(HABIT) as Habit
        this.habit = habit

        findViewById<TextView>(R.id.tv_count_down_title).text = habit.title

        val viewModel = ViewModelProvider(this).get(CountDownViewModel::class.java)

        //TODO 10 : Set initial time and observe current time. Update button state when countdown is finished
        viewModel.setInitialTime(habit.minutesFocus)
        viewModel.currentTimeString.observe(this) { currentTime ->
            findViewById<TextView>(R.id.tv_count_down).text = currentTime
        }

        //TODO 13 : Start and cancel One Time Request WorkManager to notify when time is up.
        viewModel.eventCountDownFinish.observe(this) { isFinished ->
            updateButtonState(!isFinished)
        }

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            viewModel.startTimer()
        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            viewModel.resetTimer()
            stopOneTimeTask()
        }
    }

    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning

        if (isRunning) {
            startOneTimeTask(habit)
        }
    }

    private fun startOneTimeTask(habit: Habit) {
        val data = Data.Builder()
            .putInt(HABIT_ID, habit.id)
            .putString(HABIT_TITLE, habit.title)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(habit.minutesFocus, TimeUnit.MINUTES)
            .addTag(channelName)
            .setInputData(data)
            .build()

        workManager.enqueue(workRequest)
        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(this) { workInfo ->
                if (workInfo.state == WorkInfo.State.ENQUEUED) {
                    Toast.makeText(this, "Count Down Session Started!", Toast.LENGTH_SHORT).show()
                } else if (workInfo.state == WorkInfo.State.CANCELLED) {
                    Toast.makeText(this, "Count Down Session Cancelled!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun stopOneTimeTask() {
        workManager.cancelAllWorkByTag(channelName)
    }
}