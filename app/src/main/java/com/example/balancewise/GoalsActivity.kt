package com.example.balancewise

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.balancewise.database.AppDatabase
import com.example.balancewise.database.Goal
import kotlinx.coroutines.launch

class GoalsActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private lateinit var seekMin: SeekBar
    private lateinit var seekMax: SeekBar
    private lateinit var tvMinValue: TextView
    private lateinit var tvMaxValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        seekMin = findViewById(R.id.seekMin)
        seekMax = findViewById(R.id.seekMax)
        tvMinValue = findViewById(R.id.tvMinValue)
        tvMaxValue = findViewById(R.id.tvMaxValue)

        seekMin.setOnSeekBarChangeListener(simpleListener { tvMinValue.text = "R $it" })
        seekMax.setOnSeekBarChangeListener(simpleListener { tvMaxValue.text = "R $it" })

        val userId = SessionManager.getUserId(this)

        lifecycleScope.launch {
            val goal = db.goalDao().getGoalForUser(userId)
            runOnUiThread {
                seekMin.progress = goal?.minGoal?.toInt() ?: 500
                seekMax.progress = goal?.maxGoal?.toInt() ?: 5000
                tvMinValue.text = "R ${seekMin.progress}"
                tvMaxValue.text = "R ${seekMax.progress}"
            }
        }

        findViewById<Button>(R.id.btnSaveGoals).setOnClickListener {
            lifecycleScope.launch {
                db.goalDao().insertOrUpdateGoal(
                    Goal(userId = userId, minGoal = seekMin.progress.toDouble(), maxGoal = seekMax.progress.toDouble())
                )
                runOnUiThread {
                    Toast.makeText(this@GoalsActivity, "Goals saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun simpleListener(onChange: (Int) -> Unit) = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) = onChange(progress)
        override fun onStartTrackingTouch(s: SeekBar?) {}
        override fun onStopTrackingTouch(s: SeekBar?) {}
    }
}