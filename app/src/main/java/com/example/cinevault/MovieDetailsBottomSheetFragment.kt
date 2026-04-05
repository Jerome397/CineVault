package com.example.cinevault

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.BundleCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import coil.load

class MovieDetailsBottomSheetFragment :
    BottomSheetDialogFragment(R.layout.bottom_sheet_movie_details) {

    private var isFavorite = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movie = extractMovie()

        val imagePoster: ImageView = view.findViewById(R.id.ivPoster)
        val titleText: TextView = view.findViewById(R.id.tvTitle)
        val metaText: TextView = view.findViewById(R.id.tvMeta)
        val plotText: TextView = view.findViewById(R.id.tvPlot)
        val actorsText: TextView = view.findViewById(R.id.tvActors)
        val favoriteButton: ImageButton = view.findViewById(R.id.btnFavorite)
        val closeButton: ImageButton = view.findViewById(R.id.btnClose)
        val genreChipGroup: ChipGroup = view.findViewById(R.id.chipGroupGenres)

        isFavorite = movie.isFavorite
        updateFavoriteIcon(favoriteButton)

        imagePoster.load(movie.posterUrl) {
            crossfade(true)
            placeholder(R.drawable.poster_placeholder)
            error(R.drawable.poster_placeholder)
        }

        titleText.text = movie.title
        metaText.text = listOf(movie.year, movie.runtime, "⭐ ${movie.rating}")
            .filter { it.isNotBlank() }
            .joinToString(" • ")

        plotText.text = movie.plot.ifBlank { getString(R.string.plot_not_available) }
        actorsText.text = movie.actors.ifBlank { getString(R.string.cast_not_available) }

        val genres = movie.genre.split(",").map { it.trim() }.filter { it.isNotBlank() }
        genreChipGroup.removeAllViews()

        genres.forEach { genre ->
            genreChipGroup.addView(
                Chip(requireContext()).apply {
                    text = genre
                    isClickable = false
                    isCheckable = false
                    setChipBackgroundColorResource(R.color.surface_variant)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                }
            )
        }

        favoriteButton.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteIcon(favoriteButton)
            animateFavorite(favoriteButton)
        }

        closeButton.setOnClickListener { dismiss() }
    }

    private fun extractMovie(): MovieUIModel {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            BundleCompat.getSerializable(requireArguments(), ARG_MOVIE, MovieUIModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getSerializable(ARG_MOVIE) as? MovieUIModel
        } ?: DemoMovies.featured
    }

    private fun updateFavoriteIcon(button: ImageButton) {
        button.setImageResource(
            if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
        )
    }

    private fun animateFavorite(view: View) {
        ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.18f, 1f).apply {
            duration = 240
            interpolator = OvershootInterpolator()
            start()
        }

        ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.18f, 1f).apply {
            duration = 240
            interpolator = OvershootInterpolator()
            start()
        }
    }

    companion object {
        const val TAG = "MovieDetailsBottomSheet"
        private const val ARG_MOVIE = "arg_movie"

        fun newInstance(movie: MovieUIModel): MovieDetailsBottomSheetFragment {
            return MovieDetailsBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_MOVIE, movie)
                }
            }
        }
    }
}