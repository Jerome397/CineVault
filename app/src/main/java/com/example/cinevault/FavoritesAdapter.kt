package com.example.cinevault

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cinevault.databinding.ItemMovieBinding

class FavoritesAdapter(
    private var movies: List<FavoriteMovieEntity>,
    private val onItemClick: (FavoriteMovieEntity) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    inner class FavoritesViewHolder(val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val movie = movies[position]
        holder.binding.tvMovieTitle.text = movie.title
        holder.binding.tvMovieYear.text = movie.year
        holder.binding.ivMoviePoster.load(movie.poster)
        holder.binding.root.setOnClickListener {
            onItemClick(movie)
        }
    }

    override fun getItemCount(): Int = movies.size

    fun updateData(newMovies: List<FavoriteMovieEntity>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}