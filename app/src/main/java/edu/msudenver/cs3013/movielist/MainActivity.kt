package edu.msudenver.cs3013.movielist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import android.content.Intent
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    private lateinit var movieList: ArrayList<Movie>
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var myPlace: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movieList = ArrayList()
        movieAdapter = MovieAdapter(movieList)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = movieAdapter

        myPlace = File(filesDir, "MOVIELIST.csv")

        if (!myPlace.exists()) {
            // Add default movies only once
            movieList.add(Movie("The Godfather", "1972", "Crime", 9.2))
            movieList.add(Movie("The Dark Knight", "2008", "Action", 9.0))
            writeFile()
        } else {
            readFile()
        }

        val swipeToDeleteCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val removedMovie = movieList[position]
                    movieList.removeAt(position)
                    movieAdapter.notifyItemRemoved(position)

                    Snackbar.make(recyclerView, "Movie deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            movieList.add(position, removedMovie)
                            movieAdapter.notifyItemInserted(position)
                        }
                        .show()
                }
            }

        val touchHelper = ItemTouchHelper(swipeToDeleteCallback)
        touchHelper.attachToRecyclerView(recyclerView)

        val addMovieButton = findViewById<Button>(R.id.addMovieButton)
        val saveListButton = findViewById<Button>(R.id.saveListButton)

        val addMovieLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                val title = data?.getStringExtra("title") ?: run {
                    Toast.makeText(this, "Title is missing", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }
                val year = data.getStringExtra("year") ?: run {
                    Toast.makeText(this, "Year is missing", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }
                val genre = data.getStringExtra("genre") ?: run {
                    Toast.makeText(this, "Genre is missing", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }
                val rating = data.getDoubleExtra("rating", -1.0)
                if (rating < 0) {
                    Toast.makeText(this, "Invalid rating", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }

                val newMovie = Movie(title, year, genre, rating)
                movieList.add(newMovie)
                movieAdapter.notifyItemInserted(movieList.size - 1)
            }
        }

        addMovieButton.setOnClickListener {
            val intent = Intent(this, AddMovieActivity::class.java)
            addMovieLauncher.launch(intent)
        }

        saveListButton.setOnClickListener {
            writeFile()
        }
    }

    private fun readFile() {
        try {
            if (myPlace.exists()) {
                println("Reading from: ${myPlace.absolutePath}")
                val reader = InputStreamReader(openFileInput(myPlace.name))
                val bufferedReader = reader.buffered()
                bufferedReader.forEachLine {
                    val movieData = it.split(",")
                    if (movieData.size == 4) {
                        val rating = movieData[3].toDoubleOrNull()
                        if (rating != null) {
                            val movie = Movie(
                                title = movieData[0],
                                year = movieData[1],
                                genre = movieData[2],
                                rating = rating
                            )
                            movieList.add(movie)
                            println("Loaded: $movieData")
                        } else {
                            println("Invalid rating in: $it")
                        }
                    }
                }
                reader.close()
                movieAdapter.notifyDataSetChanged()
            } else {
                println("File not found: ${myPlace.absolutePath}")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun writeFile() {
        try {
            val writer = OutputStreamWriter(openFileOutput(myPlace.name, MODE_PRIVATE))
            val bufferedWriter = writer.buffered()
            movieList.forEach { movie ->
                val line = "${movie.title},${movie.year},${movie.genre},${movie.rating}"
                bufferedWriter.write("$line\n")
                println("Saving: $line")
            }
            bufferedWriter.flush()
            writer.close()
            Toast.makeText(this, "List saved!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ratingSort -> {
                movieList.sortBy { it.rating }
                movieAdapter.notifyDataSetChanged()
            }

            R.id.yearSort -> {
                movieList.sortBy { it.year }
                movieAdapter.notifyDataSetChanged()
            }

            R.id.genreSort -> {
                movieList.sortBy { it.genre }
                movieAdapter.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}