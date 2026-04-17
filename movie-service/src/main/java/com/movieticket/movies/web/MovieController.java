package com.movieticket.movies.web;

import com.movieticket.movies.domain.Movie;
import com.movieticket.movies.repo.MovieRepository;
import com.movieticket.movies.web.dto.CreateMovieRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieController {

    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping("/movies")
    public List<Movie> list() {
        return movieRepository.findAll();
    }

    @PostMapping("/movies")
    public ResponseEntity<Movie> create(@Valid @RequestBody CreateMovieRequest request) {
        Movie movie = new Movie(request.getTitle(), request.getDescription(), request.getDurationMinutes(), request.getPrice());
        movie = movieRepository.save(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(movie);
    }

    @PutMapping("/movies/{id}")
    public ResponseEntity<Movie> update(@PathVariable String id, @Valid @RequestBody CreateMovieRequest request) {
        Optional<Movie> existingOpt = movieRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Movie existing = existingOpt.get();
        existing.setTitle(request.getTitle());
        existing.setDescription(request.getDescription());
        existing.setDurationMinutes(request.getDurationMinutes());
        existing.setPrice(request.getPrice());

        Movie saved = movieRepository.save(existing);
        return ResponseEntity.ok(saved);
    }
}
