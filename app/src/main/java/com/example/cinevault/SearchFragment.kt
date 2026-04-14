package com.example.cinevault

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var searchInput: TextInputEditText
    private lateinit var micButton: ImageButton
    private lateinit var recentChipContainer: LinearLayout
    private lateinit var recyclerResults: RecyclerView
    private lateinit var emptyState: View
    private lateinit var helperText: TextView

    private val resultAdapter = MovieRowAdapter(::showMovieDetails)
    private val recentSearches = listOf("Interstellar", "Batman", "Sci-Fi", "Action")

    private val searchHandler = Handler(Looper.getMainLooper())
    private var pendingSearch: Runnable? = null

    private val speechLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val spokenText = matches?.firstOrNull().orEmpty()

                if (spokenText.isNotBlank()) {
                    searchInput.setText(spokenText)
                    searchInput.setSelection(spokenText.length)
                    performSearch(spokenText)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchInput = view.findViewById(R.id.etSearch)
        micButton = view.findViewById(R.id.btnMic)
        recentChipContainer = view.findViewById(R.id.recentChipContainer)
        recyclerResults = view.findViewById(R.id.recyclerResults)
        emptyState = view.findViewById(R.id.layoutEmptyState)
        helperText = view.findViewById(R.id.tvHelper)

        recyclerResults.layoutManager = LinearLayoutManager(requireContext())
        recyclerResults.adapter = resultAdapter

        if (recyclerResults.itemDecorationCount == 0) {
            recyclerResults.addItemDecoration(SpacingItemDecoration(16))
        }

        renderRecentSearches()
        renderInitialState()

        micButton.setOnClickListener {
            startVoiceSearch()
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) = Unit

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                pendingSearch?.let { searchHandler.removeCallbacks(it) }

                val query = text?.toString().orEmpty()
                pendingSearch = Runnable { performSearch(query) }
                searchHandler.postDelayed(pendingSearch!!, 350)
            }
        })
    }

    private fun startVoiceSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a movie title, actor, or genre")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)
        }

        try {
            speechLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "Speech recognition is not available on this device.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun renderInitialState() {
        resultAdapter.submitList(emptyList())
        recyclerResults.visibility = View.GONE
        emptyState.visibility = View.VISIBLE
        helperText.text = "Search movies by title."
    }

    private fun performSearch(query: String) {
        val normalized = query.trim()

        if (normalized.isEmpty()) {
            renderInitialState()
            return
        }

        helperText.text = "Searching..."
        recyclerResults.visibility = View.GONE
        emptyState.visibility = View.VISIBLE

        Thread {
            try {
                val results = BackendApi.searchMovies(normalized)
                MovieStore.upsertMovies(results)

                activity?.runOnUiThread {
                    if (!isAdded) return@runOnUiThread

                    if (results.isEmpty()) {
                        resultAdapter.submitList(emptyList())
                        recyclerResults.visibility = View.GONE
                        emptyState.visibility = View.VISIBLE
                        helperText.text = "No results found for \"$normalized\"."
                    } else {
                        emptyState.visibility = View.GONE
                        recyclerResults.visibility = View.VISIBLE
                        helperText.text = "Results for \"$normalized\""
                        resultAdapter.submitList(results)
                    }
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    if (!isAdded) return@runOnUiThread

                    resultAdapter.submitList(emptyList())
                    recyclerResults.visibility = View.GONE
                    emptyState.visibility = View.VISIBLE
                    helperText.text = "Could not load movies."
                    Toast.makeText(
                        requireContext(),
                        e.message ?: "Search failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }

    private fun renderRecentSearches() {
        recentChipContainer.removeAllViews()

        recentSearches.forEach { label ->
            val chip = Chip(requireContext()).apply {
                text = label
                isClickable = true
                isCheckable = false
                setChipBackgroundColorResource(R.color.surface_variant)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                setOnClickListener {
                    searchInput.setText(label)
                    searchInput.setSelection(label.length)
                    performSearch(label)
                }
            }

            recentChipContainer.addView(chip)
        }
    }

    private fun showMovieDetails(movie: MovieUIModel) {
        MovieStore.upsertMovie(movie)
        MovieDetailsBottomSheetFragment.newInstance(movie)
            .show(parentFragmentManager, MovieDetailsBottomSheetFragment.TAG)
    }

    override fun onDestroyView() {
        pendingSearch?.let { searchHandler.removeCallbacks(it) }
        super.onDestroyView()
    }
}