package com.flixfluxclient.demo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;

@SpringBootApplication
public class FluxClient {


    public static void main(String[] args) {
        SpringApplication.run(FluxClient.class, args);
    }

    @Bean
    WebClient client() {
        return WebClient.create();
    }

    @Bean
    CommandLineRunner demo(WebClient client) {
        return args -> {

            client.get().uri("http://localhost:8080/movies")
                    .exchange()
                    .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Movie.class))
                    .filter(movie -> movie.getTitle().toLowerCase().contains("Silence".toLowerCase()))
                    .subscribe(movie -> client.get().uri("http://localhost:8080/movies/{id}/events", movie.getId())
                            .exchange()
                            .flatMapMany(cr -> cr.bodyToFlux(MovieEvent.class))
                            .subscribe(System.out::println));

        };
    }
}


@Data
@NoArgsConstructor
@ToString
class Movie {


    private String id;

    private String title;
    private String genre;


    public Movie(String id, String title, String genre) {
        this.id = id;
        this.title = title;
        this.genre = genre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}


@Data
@NoArgsConstructor
@ToString
class MovieEvent {

    private Movie movie;
    private Date when;
    private String user;

    public MovieEvent(Movie movie, Date when, String user) {
        this.movie = movie;
        this.when = when;
        this.user = user;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}