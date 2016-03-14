package com.omdbexample.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Luis Fernando Briguelli da Silva on 13/03/2016.
 */
@JsonIgnoreProperties({"Rated","Type"})
@Table(name = "Movie")
public class Movie extends Model {
    @Column(name = "imdbID", notNull = true, unique = true,onUniqueConflict = Column.ConflictAction.REPLACE)
    private String imdbID;

    @Column(name = "title")
    private String title;

    @Column(name = "year")
    private String year;

    @Column(name = "released")
    private String released;

    @Column(name = "runtime")
    private String runtime;

    @Column(name = "genre")
    private String genre;

    @Column(name = "director")
    private String director;

    @Column(name = "writer")
    private String writer;

    @Column(name = "actors")
    private String actors;

    @Column(name = "plot")
    private String plot;

    @Column(name = "language")
    private String language;

    @Column(name = "country")
    private String country;

    @Column(name = "awards")
    private String awards;

    @Column(name = "posterUrl")
    private String posterUrl;

    @Column(name = "metascore")
    private String metascore;

    @Column(name = "imdbRating")
    private String imdbRating;

    @Column(name = "imdbVotes")
    private String imdbVotes;

    @Column(name = "posterPath")
    private String posterPath;

    private String response;
    private String error;

    public Movie(){
        super();
    }


    @JsonCreator
    public Movie(@JsonProperty("imdbID") String imdbID,
                   @JsonProperty("Title") String title,
                   @JsonProperty("Year") String year,
                   @JsonProperty("Released") String released,
                   @JsonProperty("Runtime") String runtime,
                   @JsonProperty("Genre") String genre,
                   @JsonProperty("Director") String director,
                   @JsonProperty("Writer") String writer,
                   @JsonProperty("Actors") String actors,
                   @JsonProperty("Plot") String plot,
                   @JsonProperty("Language") String language,
                   @JsonProperty("Country") String country,
                   @JsonProperty("Awards") String awards,
                   @JsonProperty("Poster") String posterUrl,
                   @JsonProperty("Metascore") String metascore,
                   @JsonProperty("imdbRating") String imdbRating,
                   @JsonProperty("imdbVotes") String imdbVotes,
                   @JsonProperty("Response") String response,
                   @JsonProperty("Error") String error
                 ){
        super();
        this.imdbID = imdbID;
        this.title = title;
        this.year = year;
        this.released = released;
        this.runtime = runtime;
        this.genre = genre;
        this.director = director;
        this.writer = writer;
        this.actors = actors;
        this.plot = plot;
        this.language = language;
        this.country = country;
        this.awards = awards;
        this.posterUrl = posterUrl;
        this.metascore = metascore;
        this.imdbRating = imdbRating;
        this.imdbVotes = imdbVotes;
        this.response = response;
        this.error = error;
    }


    public static Movie findByImdbId(String imdbID){
        Movie movie = null;
        movie = new Select()
                .from(Movie.class)
                .where("imdbID = ?", imdbID)
                .executeSingle();

        return movie;
    }

    public static Movie createOrUpdate(Movie newData) {
        Movie objToUpdate = findByImdbId(newData.imdbID);
        if(objToUpdate == null){
            objToUpdate = new Movie();
        }
        objToUpdate.imdbID = newData.imdbID;
        objToUpdate.title = newData.title;
        objToUpdate.year = newData.year;
        objToUpdate.released = newData.released;
        objToUpdate.runtime = newData.runtime;
        objToUpdate.genre = newData.genre;
        objToUpdate.director = newData.director;
        objToUpdate.writer = newData.writer;
        objToUpdate.actors = newData.actors;
        objToUpdate.plot = newData.plot;
        objToUpdate.language = newData.language;
        objToUpdate.country = newData.country;
        objToUpdate.awards = newData.awards;
        objToUpdate.posterUrl = newData.posterUrl;
        objToUpdate.metascore = newData.metascore;
        objToUpdate.imdbRating = newData.imdbRating;
        objToUpdate.imdbVotes = newData.imdbVotes;
        objToUpdate.posterPath = newData.posterPath;
        objToUpdate.save();

        return objToUpdate;
    }

    public String getImdbID() {
        return imdbID;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getReleased() {
        return released;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getGenre() {
        return genre;
    }

    public String getDirector() {
        return director;
    }

    public String getWriter() {
        return writer;
    }

    public String getActors() {
        return actors;
    }

    public String getPlot() {
        return plot;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public String getAwards() {
        return awards;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getMetascore() {
        return metascore;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public String getImdbVotes() {
        return imdbVotes;
    }

    public String getResponse() {
        return response;
    }

    public String getError() {
        return error;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
