package com.example.coinflip

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.materialswitch.MaterialSwitch
import kotlin.math.roundToInt
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    // Initialize variables
    private lateinit var coinIcon : ImageView
    private lateinit var totalFlips : TextView
    private lateinit var totalHeads : TextView
    private lateinit var totalTails : TextView
    private lateinit var headsPercent : TextView
    private lateinit var tailsPercent: TextView
    private lateinit var headsBar : ProgressBar
    private lateinit var tailsBar : ProgressBar
    private lateinit var simNumber : EditText
    private lateinit var simulateButton : Button
    private lateinit var imm : InputMethodManager

    // Counters
    private var flipsCount = 0
    private var headsCount = 0
    private var tailsCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize
        initializeViews()

        // Button and switch references
        val simMode : MaterialSwitch = findViewById(R.id.switchSimulate)
        val flip : Button = findViewById(R.id.buttonFlip)
        val reset : Button = findViewById(R.id.buttonReset)

        // Button and switch listeners
        simMode.setOnCheckedChangeListener { _, isChecked -> toggleSimMode(isChecked) }
        flip.setOnClickListener { flip() }
        reset.setOnClickListener { reset() }
        simulateButton.setOnClickListener { simulateFlips() }
    }

    // Initialize declared views
    private fun initializeViews() {
        coinIcon = findViewById(R.id.imageViewCoin)
        totalFlips = findViewById(R.id.textViewTotalFlips)
        totalHeads = findViewById(R.id.textViewTotalHeads)
        totalTails = findViewById(R.id.textViewTotalTails)
        headsPercent = findViewById(R.id.textViewHeadsPercent)
        tailsPercent = findViewById(R.id.textViewTailsPercent)
        headsBar = findViewById(R.id.progressBarHeadsPercent)
        tailsBar = findViewById(R.id.progressBarTailsPercent)
        simNumber = findViewById(R.id.editTextSimNumber)
        simulateButton = findViewById(R.id.buttonSubmitSimNumber)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    // Toggle simulation mode
    private fun toggleSimMode(isChecked : Boolean) {
        if (isChecked) {
            reset()
            simNumber.visibility = EditText.VISIBLE
            simulateButton.visibility = Button.VISIBLE
        } else {
            reset()
            simNumber.visibility = EditText.INVISIBLE
            simulateButton.visibility = Button.INVISIBLE
        }
    }

    // Flip coin: Generates 0 or 1 randomly (0 = heads, 1 = tails) and updates coin icon accordingly
    private fun flip() {
        val randomNum = Random.nextInt(0,2)
        val coinSide = if (randomNum == 0) "heads" else "tails"

        updateCoinIcon(coinSide)
        updateCounters(coinSide)
        updateStatViews(coinSide)
    }

    // Update the coin image
    private fun updateCoinIcon(coinSide : String) {
        when (coinSide) {
            "heads" -> coinIcon.setImageResource(R.drawable.ic_heads)
            "tails" -> coinIcon.setImageResource(R.drawable.ic_tails)
            else -> coinIcon.setImageResource(R.drawable.ic_flip)
        }
    }

    // Update counters
    private fun updateCounters(coinSide : String) {
        flipsCount++
        when (coinSide) {
            "heads" -> headsCount++
            "tails" -> tailsCount++
        }
    }

    // Updates stats (total flips, total heads, and total tails)
    private fun updateStatViews(coinSide : String) {
        totalFlips.text = getString(R.string.totalFlips, flipsCount)

        when (coinSide) {
            "heads" -> totalHeads.text = getString(R.string.totalHeads, headsCount)
            "tails" -> totalTails.text = getString(R.string.totalTails, tailsCount)
        }

        val headsPercentage = calculateHeadsPercentage()
        val tailsPercentage = 100 - headsPercentage

        headsPercent.text = getString(R.string.headsPercent, headsPercentage.toString())
        tailsPercent.text = getString(R.string.tailsPercent, tailsPercentage.toString())

        updateProgressBars(headsPercentage, tailsPercentage)
    }

    // Calculate heads percentage
    private fun calculateHeadsPercentage() : Int {
        return ((headsCount.toDouble() / flipsCount) * 100).roundToInt()
    }

    // Update progress bars
    private fun updateProgressBars(headsPercentage : Int, tailsPercentage : Int) {
        headsBar.setProgress(headsPercentage, true)
        tailsBar.setProgress(tailsPercentage, true)
    }

    // Reset all data
    private fun reset() {
        // Reset icon
        coinIcon.setImageResource(R.drawable.ic_flip)

        // Reset counters
        flipsCount = 0
        headsCount = 0
        tailsCount = 0

        // Reset stats
        totalFlips.text = getString(R.string.totalFlips, flipsCount)
        totalHeads.text = getString(R.string.totalHeads, headsCount)
        totalTails.text = getString(R.string.totalTails, tailsCount)
        headsPercent.text = getString(R.string.headsPercent, "0")
        tailsPercent.text = getString(R.string.tailsPercent, "0")

        // Reset progress bars
        headsBar.progress = 0
        tailsBar.progress = 0
    }

    // Simulate coin flips for a set number of flips
    private fun simulateFlips() {
        if (simNumber.text.isEmpty()) {
            Toast.makeText(this, "Enter number of flips", Toast.LENGTH_SHORT).show()
        } else if (simNumber.text.toString().toInt() > 99999) {
            Toast.makeText(this, "Enter number less than 100,000", Toast.LENGTH_SHORT).show()
        } else {
            val numFlips = simNumber.text.toString().toInt()

            // Clear sim number field and reset counters and stats
            simNumber.setText("")
            reset()

            // Hide keyboard and clear focus
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            currentFocus?.clearFocus()

            // Run flips and update appropriate counters
            for (i in 1..numFlips) {
                val randomNum = Random.nextInt(0,2)
                val coinSide = if (randomNum == 0) "heads" else "tails"
                flipsCount++
                if (coinSide == "heads") { headsCount++ } else { tailsCount++ }
            }

            // Update stats after flips are complete
            totalFlips.text = getString(R.string.totalFlips, flipsCount)
            totalHeads.text = getString(R.string.totalHeads, headsCount)
            totalTails.text = getString(R.string.totalTails, tailsCount)
            val headsPercentage = calculateHeadsPercentage()
            val tailsPercentage = 100 - headsPercentage
            headsPercent.text = getString(R.string.headsPercent, headsPercentage.toString())
            tailsPercent.text = getString(R.string.tailsPercent, tailsPercentage.toString())
            updateProgressBars(headsPercentage, tailsPercentage)
        }
    }
}