package cz.kureii.raintext.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import cz.kureii.raintext.R
import cz.kureii.raintext.services.SavePasswordWorker

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var workManager: WorkManager
    private lateinit var workRequest: OneTimeWorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val sharedPrefTheme = this.getSharedPreferences("THEME", Context.MODE_PRIVATE)

        when (sharedPrefTheme.getInt("Main", 0)) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Light
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // Dark
        }

        workManager = WorkManager.getInstance(this)
        workRequest = OneTimeWorkRequest.from(SavePasswordWorker::class.java)

        workManager.getWorkInfoByIdLiveData(workRequest.id).observe(this) { workInfo ->
            if (workInfo != null && workInfo.state.isFinished) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        workManager.enqueue(workRequest)
    }
}