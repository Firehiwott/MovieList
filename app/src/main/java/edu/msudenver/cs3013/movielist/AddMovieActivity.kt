package edu.msudenver.cs3013.movielist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddMovieActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movie)

        val inputTitle = findViewById<EditText>(R.id.titleEditText)
        val inputYear = findViewById<EditText>(R.id.yearEditText)
        val inputGenre = findViewById<EditText>(R.id.genreEditText)
        val inputRating = findViewById<EditText>(R.id.ratingEditText)
        val submitButton = findViewById<Button>(R.id.saveButton)

        submitButton.setOnClickListener {
            val titleText = inputTitle.text.toString()
            val yearText = inputYear.text.toString()
            val genreText = inputGenre.text.toString()
            val ratingValue = inputRating.text.toString().toDoubleOrNull()

            if (titleText.isNotEmpty() && yearText.isNotEmpty() && genreText.isNotEmpty() && ratingValue != null) {
                val dataIntent = Intent().apply {
                    putExtra("title", titleText)
                    putExtra("year", yearText)
                    putExtra("genre", genreText)
                    putExtra("rating", ratingValue)
                }
                setResult(RESULT_OK, dataIntent)
                finish()
            } else {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
