package com.movieticket.movies.seed;

import com.movieticket.movies.domain.Movie;
import com.movieticket.movies.repo.MovieRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MovieSeeder implements CommandLineRunner {

    private final MovieRepository movieRepository;

    public MovieSeeder(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public void run(String... args) {
        if (movieRepository.count() > 0) {
            return;
        }

        movieRepository.saveAll(List.of(
                new Movie("Avengers: Demo", "Sample movie for demo", 120, 90000),
                new Movie("Spider-Man: Demo", "Sample movie for demo", 110, 85000),
                new Movie("Batman: Demo", "Sample movie for demo", 130, 95000)
        ));
    }
}
