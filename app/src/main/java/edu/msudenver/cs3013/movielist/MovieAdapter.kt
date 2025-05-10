package edu.msudenver.cs3013.movielist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(private val movies: ArrayList<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.movie_item, parent, false)
        return MovieHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        holder.bindMovie(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    inner class MovieHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleView: TextView = view.findViewById(R.id.titleTextView)
        private val yearView: TextView = view.findViewById(R.id.yearTextView)
        private val genreView: TextView = view.findViewById(R.id.genreTextView)
        private val ratingView: TextView = view.findViewById(R.id.ratingTextView)

        fun bindMovie(movie: Movie) {
            titleView.text = movie.title
            yearView.text = movie.year
            genreView.text = movie.genre
            ratingView.text = "${movie.rating}"
        }
    }
}
