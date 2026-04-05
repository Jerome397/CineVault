package com.example.cinevault

object DemoMovies {

    val featured = MovieUIModel(
        id = "tt0133093",
        title = "The Matrix",
        year = "1999",
        posterUrl = "https://m.media-amazon.com/images/M/MV5BNzQzOTk3NTAtNDQzMC00ZDM3LWE2NjEtODg3NzZhN2ZlYjYwXkEyXkFqcGc@._V1_SX300.jpg",
        genre = "Action, Sci-Fi",
        plot = "A computer hacker discovers that reality is a simulation and joins a rebellion against the machines.",
        rating = "8.7",
        actors = "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss",
        runtime = "136 min",
        isFavorite = true
    )

    val popular = listOf(
        featured,
        MovieUIModel(
            id = "tt0816692",
            title = "Interstellar",
            year = "2014",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BMjIxMjgxNzM5NF5BMl5BanBnXkFtZTgwNzUxNjM3MjE@._V1_SX300.jpg",
            genre = "Adventure, Drama, Sci-Fi",
            plot = "A team travels through a wormhole in space to ensure humanity's survival.",
            rating = "8.7",
            actors = "Matthew McConaughey, Anne Hathaway, Jessica Chastain",
            runtime = "169 min"
        ),
        MovieUIModel(
            id = "tt4154796",
            title = "Avengers: Endgame",
            year = "2019",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BMTc5MDY2MTE1OV5BMl5BanBnXkFtZTgwNzYzMjY4NzM@._V1_SX300.jpg",
            genre = "Action, Adventure, Drama",
            plot = "The Avengers assemble one last time to reverse the damage caused by Thanos.",
            rating = "8.4",
            actors = "Robert Downey Jr., Chris Evans, Mark Ruffalo",
            runtime = "181 min"
        ),
        MovieUIModel(
            id = "tt1375666",
            title = "Inception",
            year = "2010",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BMmYxYzE0NjktMzZkMC00M2E5LTg3ZjEtYjNjM2E4YzNkZjI0XkEyXkFqcGc@._V1_SX300.jpg",
            genre = "Action, Adventure, Sci-Fi",
            plot = "A skilled thief enters dreams to steal secrets but gets one final impossible mission.",
            rating = "8.8",
            actors = "Leonardo DiCaprio, Joseph Gordon-Levitt, Elliot Page",
            runtime = "148 min",
            isFavorite = true
        ),
        MovieUIModel(
            id = "tt4154756",
            title = "Avengers: Infinity War",
            year = "2018",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BZjcyMGU5MGQtY2Q3Yi00N2U4LTliODAtMDM3N2QzZjc4OWQwXkEyXkFqcGc@._V1_SX300.jpg",
            genre = "Action, Adventure, Sci-Fi",
            plot = "The Avengers and their allies face Thanos before he gets all the Infinity Stones.",
            rating = "8.4",
            actors = "Robert Downey Jr., Chris Hemsworth, Josh Brolin",
            runtime = "149 min"
        )
    )

    val topRated = listOf(
        MovieUIModel(
            id = "tt0111161",
            title = "The Shawshank Redemption",
            year = "1994",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BMDFkYTc0MGEtZmRhMC00ZDI1LWFmNTEtODM1ZmRlYTcwNzg4XkEyXkFqcGc@._V1_SX300.jpg",
            genre = "Drama",
            plot = "Two imprisoned men bond over years, finding solace and eventual redemption.",
            rating = "9.3",
            actors = "Tim Robbins, Morgan Freeman",
            runtime = "142 min"
        ),
        MovieUIModel(
            id = "tt0068646",
            title = "The Godfather",
            year = "1972",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BNjY2ZmYxMmEtZTYzOC00Y2ZhLWE2NmEtYzRlNDQzZjQ0YTg0XkEyXkFqcGc@._V1_SX300.jpg",
            genre = "Crime, Drama",
            plot = "The aging patriarch of an organized crime dynasty transfers control to his reluctant son.",
            rating = "9.2",
            actors = "Marlon Brando, Al Pacino",
            runtime = "175 min"
        ),
        MovieUIModel(
            id = "tt0468569",
            title = "The Dark Knight",
            year = "2008",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODIwNF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_SX300.jpg",
            genre = "Action, Crime, Drama",
            plot = "Batman faces the Joker, whose chaos threatens Gotham City.",
            rating = "9.0",
            actors = "Christian Bale, Heath Ledger, Aaron Eckhart",
            runtime = "152 min",
            isFavorite = true
        ),
        MovieUIModel(
            id = "tt0109830",
            title = "Forrest Gump",
            year = "1994",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BNWIwODNlMzctYjE3OS00ZmRjLTkwNmQtMWQ2MWE2ZWQzZWQzXkEyXkFqcGc@._V1_SX300.jpg",
            genre = "Drama, Romance",
            plot = "The life journey of Forrest Gump intersects with key moments in American history.",
            rating = "8.8",
            actors = "Tom Hanks, Robin Wright",
            runtime = "142 min"
        )
    )

    val favorites = (popular + topRated).filter { it.isFavorite }.distinctBy { it.id }
    val all = (popular + topRated).distinctBy { it.id }

    val watchlist = listOfNotNull(
        all.find { it.id == "tt0816692" }, // Interstellar
        all.find { it.id == "tt4154756" }, // Infinity War
        all.find { it.id == "tt0068646" }  // The Godfather
    )
}