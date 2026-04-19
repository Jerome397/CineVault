package com.example.cinevault

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.cinevault.databinding.FragmentMovieDetailsBinding

class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MovieDetailsViewModel
    private var imdbId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imdbId = arguments?.getString("imdbId").orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        val repository = MovieRepository(
            RetrofitInstance.api,
            db.favoriteMovieDao(),
            db.watchlistMovieDao()
        )
        val factory = AppViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MovieDetailsViewModel::class.java]

        binding.btnBackFromDetails.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }

        viewModel.movieDetails.observe(viewLifecycleOwner) { details ->
            binding.tvTitle.text = details.Title
            binding.tvYear.text = "Year: ${details.Year}"
            binding.tvGenre.text = "Genre: ${details.Genre}"
            binding.tvRating.text = "IMDb Rating: ${details.imdbRating}"
            binding.tvPlot.text = details.Plot
            binding.ivMovieDetailsPoster.load(details.Poster)
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            binding.btnFavorite.text =
                if (isFavorite) "Remove from Favorites" else "Add to Favorites"
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrBlank()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }

        if (imdbId.isNotBlank()) {
            viewModel.loadMovieDetails(imdbId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}