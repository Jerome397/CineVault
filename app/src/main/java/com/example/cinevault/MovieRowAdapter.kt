package com.example.cinevault

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load

class MovieRowAdapter(
    private val onMovieClick: (MovieUIModel) -> Unit
) : ListAdapter<MovieUIModel, MovieRowAdapter.MovieRowViewHolder>(MovieRowDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieRowViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_row, parent, false)
        return MovieRowViewHolder(view, onMovieClick)
    }

    override fun onBindViewHolder(holder: MovieRowViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MovieRowViewHolder(
        itemView: View,
        private val onMovieClick: (MovieUIModel) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val posterImage: ImageView = itemView.findViewById(R.id.ivPoster)
        private val titleText: TextView = itemView.findViewById(R.id.tvTitle)
        private val metaText: TextView = itemView.findViewById(R.id.tvMeta)
        private val plotText: TextView = itemView.findViewById(R.id.tvPlot)
        private val ratingText: TextView = itemView.findViewById(R.id.tvRating)

        fun bind(movie: MovieUIModel) {
            titleText.text = movie.title
            metaText.text = listOf(movie.year, movie.genre, movie.runtime)
                .filter { it.isNotBlank() }
                .joinToString(" • ")
            plotText.text = movie.plot
            ratingText.text = if (movie.rating.isBlank()) "N/A" else movie.rating

            posterImage.load(movie.posterUrl) {
                crossfade(true)
                placeholder(R.drawable.poster_placeholder)
                error(R.drawable.poster_placeholder)
            }

            itemView.setOnClickListener { onMovieClick(movie) }
        }
    }
}

private object MovieRowDiffCallback : DiffUtil.ItemCallback<MovieUIModel>() {
    override fun areItemsTheSame(oldItem: MovieUIModel, newItem: MovieUIModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MovieUIModel, newItem: MovieUIModel): Boolean {
        return oldItem == newItem
    }
}