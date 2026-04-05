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

class MoviePosterAdapter(
    private val onMovieClick: (MovieUIModel) -> Unit
) : ListAdapter<MovieUIModel, MoviePosterAdapter.MoviePosterViewHolder>(MoviePosterDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviePosterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_poster, parent, false)
        return MoviePosterViewHolder(view, onMovieClick)
    }

    override fun onBindViewHolder(holder: MoviePosterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MoviePosterViewHolder(
        itemView: View,
        private val onMovieClick: (MovieUIModel) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val posterImage: ImageView = itemView.findViewById(R.id.ivPoster)
        private val titleText: TextView = itemView.findViewById(R.id.tvTitle)
        private val subtitleText: TextView = itemView.findViewById(R.id.tvSubtitle)
        private val ratingText: TextView = itemView.findViewById(R.id.tvRating)

        fun bind(movie: MovieUIModel) {
            titleText.text = movie.title
            subtitleText.text = "${movie.year} • ${movie.genre}"
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

private object MoviePosterDiffCallback : DiffUtil.ItemCallback<MovieUIModel>() {
    override fun areItemsTheSame(oldItem: MovieUIModel, newItem: MovieUIModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MovieUIModel, newItem: MovieUIModel): Boolean {
        return oldItem == newItem
    }
}