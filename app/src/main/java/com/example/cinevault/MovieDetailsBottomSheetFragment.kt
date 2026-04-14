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
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import coil.load

class MovieDetailsBottomSheetFragment :
    BottomSheetDialogFragment(R.layout.bottom_sheet_movie_details) {

    private lateinit var movieId: String
    private lateinit var currentMovie: MovieUIModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialMovie = extractMovie()
        currentMovie = MovieStore.getMovieById(initialMovie.id) ?: initialMovie
        movieId = currentMovie.id

        val imagePoster: ImageView = view.findViewById(R.id.ivPoster)
        val titleText: TextView = view.findViewById(R.id.tvTitle)
        val metaText: TextView = view.findViewById(R.id.tvMeta)
        val plotText: TextView = view.findViewById(R.id.tvPlot)
        val actorsText: TextView = view.findViewById(R.id.tvActors)
        val favoriteButton: ImageButton = view.findViewById(R.id.btnFavorite)
        val closeButton: ImageButton = view.findViewById(R.id.btnClose)
        val watchlistButton: MaterialButton = view.findViewById(R.id.btnWatchlist)
        val genreChipGroup: ChipGroup = view.findViewById(R.id.chipGroupGenres)

        bindMovie(
            movie = currentMovie,
            imagePoster = imagePoster,
            titleText = titleText,
            metaText = metaText,
            plotText = plotText,
            actorsText = actorsText,
            genreChipGroup = genreChipGroup
        )

        refreshActionButtons(favoriteButton, watchlistButton)

        favoriteButton.setOnClickListener {
            MovieStore.toggleFavorite(movieId)
            refreshActionButtons(favoriteButton, watchlistButton)
            animateFavorite(favoriteButton)
        }

        watchlistButton.setOnClickListener {
            MovieStore.toggleWatchlist(movieId)
            refreshActionButtons(favoriteButton, watchlistButton)
        }

        closeButton.setOnClickListener { dismiss() }

        if (movieId.isNotBlank()) {
            loadFullMovieDetails(
                imagePoster = imagePoster,
                titleText = titleText,
                metaText = metaText,
                plotText = plotText,
                actorsText = actorsText,
                genreChipGroup = genreChipGroup,
                favoriteButton = favoriteButton,
                watchlistButton = watchlistButton
            )
        }
    }

    private fun loadFullMovieDetails(
        imagePoster: ImageView,
        titleText: TextView,
        metaText: TextView,
        plotText: TextView,
        actorsText: TextView,
        genreChipGroup: ChipGroup,
        favoriteButton: ImageButton,
        watchlistButton: MaterialButton
    ) {
        Thread {
            try {
                val detailedMovie = BackendApi.getMovieDetails(movieId)
                currentMovie = detailedMovie
                MovieStore.upsertMovie(detailedMovie)

                activity?.runOnUiThread {
                    if (!isAdded) return@runOnUiThread

                    bindMovie(
                        movie = detailedMovie,
                        imagePoster = imagePoster,
                        titleText = titleText,
                        metaText = metaText,
                        plotText = plotText,
                        actorsText = actorsText,
                        genreChipGroup = genreChipGroup
                    )
                    refreshActionButtons(favoriteButton, watchlistButton)
                }
            } catch (_: Exception) {
            }
        }.start()
    }

    private fun bindMovie(
        movie: MovieUIModel,
        imagePoster: ImageView,
        titleText: TextView,
        metaText: TextView,
        plotText: TextView,
        actorsText: TextView,
        genreChipGroup: ChipGroup
    ) {
        imagePoster.load(movie.posterUrl) {
            crossfade(true)
            placeholder(R.drawable.poster_placeholder)
            error(R.drawable.poster_placeholder)
        }

        titleText.text = movie.title
        metaText.text = listOf(movie.year, movie.runtime, "⭐ ${movie.rating.ifBlank { "N/A" }}")
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
    }

    private fun refreshActionButtons(
        favoriteButton: ImageButton,
        watchlistButton: MaterialButton
    ) {
        val isFavorite = MovieStore.isFavorite(movieId)
        val isInWatchlist = MovieStore.isInWatchlist(movieId)

        favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
        )

        watchlistButton.text = if (isInWatchlist) {
            "Remove from Watchlist"
        } else {
            "Add to Watchlist"
        }
    }

    private fun extractMovie(): MovieUIModel {
        val movie = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            BundleCompat.getSerializable(requireArguments(), ARG_MOVIE, MovieUIModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getSerializable(ARG_MOVIE) as? MovieUIModel
        }

        return movie ?: MovieUIModel(
            id = "",
            title = "Unknown Movie",
            year = "",
            posterUrl = null,
            genre = "",
            plot = "",
            rating = "",
            actors = "",
            runtime = "",
            isFavorite = false
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