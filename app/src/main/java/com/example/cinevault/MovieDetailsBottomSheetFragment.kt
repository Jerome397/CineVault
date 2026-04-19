package com.example.cinevault

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.BundleCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import coil.load
import kotlinx.coroutines.launch

class MovieDetailsBottomSheetFragment :
    BottomSheetDialogFragment(R.layout.bottom_sheet_movie_details) {

    private lateinit var movieId: String
    private lateinit var currentMovie: MovieUIModel
    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var repository: MovieRepository

    private var isInWatchlist = false

    private lateinit var imagePoster: ImageView
    private lateinit var titleText: TextView
    private lateinit var metaText: TextView
    private lateinit var plotText: TextView
    private lateinit var actorsText: TextView
    private lateinit var favoriteButton: ImageButton
    private lateinit var closeButton: ImageButton
    private lateinit var watchlistButton: MaterialButton
    private lateinit var genreChipGroup: ChipGroup

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialMovie = extractMovie()
        currentMovie = initialMovie
        movieId = initialMovie.id

        imagePoster = view.findViewById(R.id.ivPoster)
        titleText = view.findViewById(R.id.tvTitle)
        metaText = view.findViewById(R.id.tvMeta)
        plotText = view.findViewById(R.id.tvPlot)
        actorsText = view.findViewById(R.id.tvActors)
        favoriteButton = view.findViewById(R.id.btnFavorite)
        closeButton = view.findViewById(R.id.btnClose)
        watchlistButton = view.findViewById(R.id.btnWatchlist)
        genreChipGroup = view.findViewById(R.id.chipGroupGenres)

        val db = AppDatabase.getDatabase(requireContext())
        repository = MovieRepository(
            RetrofitInstance.api,
            db.favoriteMovieDao(),
            db.watchlistMovieDao()
        )
        val factory = AppViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MovieDetailsViewModel::class.java]

        bindMovie(currentMovie)

        favoriteButton.isEnabled = false
        favoriteButton.alpha = 0.5f

        watchlistButton.isEnabled = false
        watchlistButton.alpha = 0.5f
        watchlistButton.text = "Add to Watchlist"

        observeViewModel()

        favoriteButton.setOnClickListener {
            if (!favoriteButton.isEnabled) return@setOnClickListener
            viewModel.toggleFavorite()
            animateFavorite(favoriteButton)
        }

        watchlistButton.setOnClickListener {
            val movieDetails = viewModel.movieDetails.value ?: return@setOnClickListener

            viewLifecycleOwner.lifecycleScope.launch {
                if (isInWatchlist) {
                    repository.removeFromWatchlist(movieDetails)
                    isInWatchlist = false
                } else {
                    repository.addToWatchlist(movieDetails)
                    isInWatchlist = true
                }

                updateWatchlistButton()
            }
        }

        closeButton.setOnClickListener { dismiss() }

        if (movieId.isNotBlank()) {
            viewModel.loadMovieDetails(movieId)
        }
    }

    private fun observeViewModel() {
        viewModel.movieDetails.observe(viewLifecycleOwner) { details ->
            currentMovie = MovieUIModel(
                id = details.imdbID,
                title = details.Title,
                year = details.Year,
                posterUrl = details.Poster.takeUnless { it == "N/A" },
                genre = details.Genre.takeUnless { it == "N/A" } ?: "",
                plot = details.Plot.takeUnless { it == "N/A" } ?: "",
                rating = details.imdbRating.takeUnless { it == "N/A" } ?: "",
                actors = "",
                runtime = "",
                isFavorite = true
            )

            bindMovie(currentMovie)

            favoriteButton.isEnabled = true
            favoriteButton.alpha = 1f

            watchlistButton.isEnabled = true
            watchlistButton.alpha = 1f

            viewLifecycleOwner.lifecycleScope.launch {
                isInWatchlist = repository.isInWatchlist(details.imdbID)
                updateWatchlistButton()
            }
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            favoriteButton.setImageResource(
                if (isFavorite) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrBlank()) {
                android.widget.Toast.makeText(
                    requireContext(),
                    error,
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateWatchlistButton() {
        watchlistButton.text =
            if (isInWatchlist) "Remove from Watchlist"
            else "Add to Watchlist"
    }

    private fun bindMovie(movie: MovieUIModel) {
        imagePoster.load(movie.posterUrl) {
            crossfade(true)
            placeholder(R.drawable.poster_placeholder)
            error(R.drawable.poster_placeholder)
        }

        titleText.text = movie.title
        metaText.text = listOf(
            movie.year,
            movie.runtime,
            "⭐ ${movie.rating.ifBlank { "N/A" }}"
        ).filter { it.isNotBlank() }
            .joinToString(" • ")

        plotText.text = movie.plot.ifBlank { getString(R.string.plot_not_available) }
        actorsText.text = movie.actors.ifBlank { getString(R.string.cast_not_available) }

        val genres = movie.genre.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

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

    private fun extractMovie(): MovieUIModel {
        val movie = BundleCompat.getSerializable(
            requireArguments(),
            ARG_MOVIE,
            MovieUIModel::class.java
        )

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