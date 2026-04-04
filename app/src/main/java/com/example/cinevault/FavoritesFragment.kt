package com.example.cinevault

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinevault.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var adapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        val repository = MovieRepository(RetrofitInstance.api, db.favoriteMovieDao())
        val factory = AppViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FavoritesViewModel::class.java]

        adapter = FavoritesAdapter(emptyList()) { movie ->
            val bundle = Bundle().apply {
                putString("imdbId", movie.imdbID)
            }
            findNavController().navigate(R.id.action_favoritesFragment_to_movieDetailsFragment, bundle)
        }

        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = adapter

        binding.btnBackFromFavorites.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            adapter.updateData(favorites)
            binding.tvEmptyFavorites.visibility =
                if (favorites.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}