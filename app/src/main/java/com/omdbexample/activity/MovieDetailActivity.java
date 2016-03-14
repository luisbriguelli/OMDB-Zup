package com.omdbexample.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.omdbexample.R;
import com.omdbexample.adapter.MovieAdapter;
import com.omdbexample.model.Movie;
import com.omdbexample.service.ApiService;
import com.omdbexample.service.RetrofitClient;
import com.omdbexample.utils.InternetConexion;
import com.omdbexample.utils.JacksonMapper;
import com.omdbexample.utils.ListViewItem;
import com.omdbexample.utils.TypeFaces;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Luis Fernando Briguelli da Silva on 13/03/2016.
 */
public class MovieDetailActivity extends AppCompatActivity {

    private TextView title, imdbRating, imdbVotes,genre,runtime,metascore,plot,director,actors,writer,country,language,awards;
    private SimpleDraweeView moviePoster;
    private Context mContext;
    private String imdbID;
    private File directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_movie_detail);
        mContext = this;

        ContextWrapper cw = new ContextWrapper(mContext.getApplicationContext());
        directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        moviePoster = (SimpleDraweeView) findViewById(R.id.poster_movie_detail);
        title = (TextView) findViewById(R.id.title_and_year_movie_detail);
        imdbRating = (TextView) findViewById(R.id.imdb_rating_movie_detail);
        imdbVotes = (TextView) findViewById(R.id.imdb_votes_movie_detail);
        genre = (TextView) findViewById(R.id.genre_movie_detail);
        runtime = (TextView) findViewById(R.id.runtime_movie_detail);
        metascore = (TextView) findViewById(R.id.imdb_metascore_movie_detail);
        plot = (TextView) findViewById(R.id.plot_movie_detail);
        director = (TextView) findViewById(R.id.director_movie_detail);
        actors = (TextView) findViewById(R.id.actors_movie_detail);
        writer = (TextView) findViewById(R.id.writer_movie_detail);
        country = (TextView) findViewById(R.id.country_movie_detail);
        language = (TextView) findViewById(R.id.language_movie_detail);
        awards = (TextView) findViewById(R.id.awards_movie_detail);

        title.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Bold.ttf"));
        imdbRating.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Medium.ttf"));
        imdbVotes.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Light.ttf"));
        genre.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Light.ttf"));
        runtime.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Medium.ttf"));
        metascore.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Medium.ttf"));
        plot.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Light.ttf"));
        director.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Light.ttf"));
        actors.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Light.ttf"));
        writer.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Light.ttf"));
        country.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Light.ttf"));
        language.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Light.ttf"));
        awards.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Light.ttf"));

        if (savedInstanceState != null) {
            imdbID = savedInstanceState.getString("imdbID");
        } else {
            Bundle bundle = this.getIntent().getExtras();
            if(bundle!=null)
            {
                imdbID = bundle.getString("imdbID");
            }
        }

        loadMovieData();
    }

    public void loadMovieData() {
        //Get the movie according the imdbID
        Movie movie = new Select().from(Movie.class).where("imdbID=?", imdbID).executeSingle();

        if (movie!=null) {
            if(movie.getPosterPath()!=null){
                File myPath = new File(directory,movie.getPosterPath());
                if(myPath!=null){
                    Uri uri = Uri.fromFile(myPath);
                    moviePoster.setImageURI(uri);
                }
            }else{
                Uri uri = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                        .path(String.valueOf(R.mipmap.ic_image_area_grey600_48dp))
                        .build();
                moviePoster.setImageURI(uri);
                moviePoster.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER);
            }

            title.setText(movie.getTitle()+ " (" +movie.getYear()+")");
            imdbRating.setText(movie.getImdbRating());
            imdbVotes.setText("("+movie.getImdbVotes() +" votos)");
            genre.setText(movie.getGenre());
            runtime.setText(movie.getRuntime());


            ForegroundColorSpan grey800 = new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.grey_800));
            ForegroundColorSpan grey600 = new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.grey_600));

            int textLenght =0;

            SpannableStringBuilder sbPlot = new SpannableStringBuilder();
            //Add the label like(Director, Plot,Actors ) defined in xml file to SpannableStringBuilder
            sbPlot.append(plot.getText());
            textLenght = sbPlot.length();

            //Set label color
            sbPlot.setSpan(grey800, 0, sbPlot.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            //Add the Plot text to SpannableStringBuilder
            sbPlot.append(" ");
            sbPlot.append(movie.getPlot());

            //Set Plot text color
            sbPlot.setSpan(grey600, textLenght, sbPlot.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);


            SpannableStringBuilder sbDirector = new SpannableStringBuilder();
            sbDirector.append(director.getText());
            textLenght = sbDirector.length();
            sbDirector.setSpan(grey800, 0, sbDirector.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sbDirector.append(" ");
            sbDirector.append(movie.getDirector());
            sbDirector.setSpan(grey600, textLenght, sbDirector.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            SpannableStringBuilder sbActors = new SpannableStringBuilder();
            sbActors.append(actors.getText());
            textLenght = sbActors.length();
            sbActors.setSpan(grey800, 0, sbActors.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sbActors.append(" ");
            sbActors.append(movie.getActors());
            sbActors.setSpan(grey600, textLenght, sbActors.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            SpannableStringBuilder sbWriter = new SpannableStringBuilder();
            sbWriter.append(writer.getText());
            textLenght = sbWriter.length();
            sbWriter.setSpan(grey800, 0, sbWriter.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sbWriter.append(" ");
            sbWriter.append(movie.getWriter());
            sbWriter.setSpan(grey600, textLenght, sbWriter.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            SpannableStringBuilder sbCountry = new SpannableStringBuilder();
            sbCountry.append(country.getText());
            textLenght = sbCountry.length();
            sbCountry.setSpan(grey800, 0, sbCountry.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sbCountry.append(" ");
            sbCountry.append(movie.getCountry());
            sbCountry.setSpan(grey600, textLenght, sbCountry.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            SpannableStringBuilder sbLanguage = new SpannableStringBuilder();
            sbLanguage.append(language.getText());
            textLenght = sbLanguage.length();
            sbLanguage.setSpan(grey800, 0, sbLanguage.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sbLanguage.append(" ");
            sbLanguage.append(movie.getLanguage());
            sbLanguage.setSpan(grey600, textLenght, sbLanguage.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            SpannableStringBuilder sbAwards = new SpannableStringBuilder();
            sbAwards.append(awards.getText());
            textLenght = sbAwards.length();
            sbAwards.setSpan(grey800, 0, sbAwards.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sbAwards.append(" ");
            sbAwards.append(movie.getAwards());
            sbAwards.setSpan(grey600, textLenght, sbAwards.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            plot.setText(sbPlot);
            director.setText(sbDirector);
            actors.setText(sbActors);
            writer.setText(sbWriter);
            country.setText(sbCountry);
            language.setText(sbLanguage);
            awards.setText(sbAwards);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("imdbID", imdbID);
        super.onSaveInstanceState(outState);
    }
}
