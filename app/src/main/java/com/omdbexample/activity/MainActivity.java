package com.omdbexample.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.omdbexample.R;
import com.omdbexample.adapter.MovieAdapter;
import com.omdbexample.model.Movie;
import com.omdbexample.service.ApiService;
import com.omdbexample.service.RetrofitClient;
import com.omdbexample.utils.InternetConexion;
import com.omdbexample.utils.JacksonMapper;
import com.omdbexample.utils.ListViewItem;

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
public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button btRegister;
    private EditText editTextTitle;
    private TextView noMovies;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    public static MovieAdapter mAdapter;
    private ArrayList<ListViewItem> listOfViewItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        ActiveAndroid.initialize(this);
        setContentView(R.layout.activity_main);
        mContext = this;


        mToolbar = (Toolbar) findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        btRegister = (Button) findViewById(R.id.bt_register);
        editTextTitle = (EditText) findViewById(R.id.et_movie_name);
        noMovies = (TextView) findViewById(R.id.tv_dont_have_movies);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_movies_activity_main);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        loadMovieList();
    }

    public void loadMovieList() {
        if(!listOfViewItemList.isEmpty()){
            listOfViewItemList.clear();
        }

        //Get all movies ordered by movie title
        List<Movie> listAllMovie = new Select().from(Movie.class)
                .orderBy("lower(Movie.title) ASC").execute();

        ListViewItem listViewItem;

        if (!listAllMovie.isEmpty()) {
            noMovies.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            for (int i = 0; i < listAllMovie.size(); i++) {
                listViewItem = new ListViewItem(listAllMovie.get(i), MovieAdapter.TYPE_MOVIE);
                listOfViewItemList.add(listViewItem);
            }
        }else{
            noMovies.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        mAdapter = new MovieAdapter(this, listOfViewItemList, mRecyclerView, noMovies);
        if (mAdapter != null) {
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    public void onClickRegister(View v){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title(R.string.loading)
                .content(R.string.please_wait);

        final MaterialDialog dialog = builder.build();

        String title = null;
        if(editTextTitle.getText().toString().isEmpty()){
            editTextTitle.setError("É necessário preencher o nome");
        }else{
            title = editTextTitle.getText().toString();
            if (InternetConexion.getInstance(mContext).hasInternetConexion()) {
                dialog.show();

                Call<Movie> movieCall = RetrofitClient.getInstance().getApiService().getMovie(title);
                movieCall.enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(Call<Movie> call, Response<Movie> response) {
                        if (response.isSuccess()) {
                            Movie movie = response.body();
                            if(movie!=null){
                                if(movie.getResponse().equals("False")){
                                    Snackbar.make(editTextTitle, movie.getError().toString(),Snackbar.LENGTH_SHORT).show();
                                }else{
                                    if(!movie.getPosterUrl().equals("N/A")) {
                                        //Create the image name
                                        String imageName = movie.getImdbID() + ".jpg";
                                        movie.setPosterPath(imageName);

                                        //Download Poster and save
                                        savePoster(imageName, movie.getPosterUrl());
                                    }

                                    //Save the movie
                                    movie = Movie.createOrUpdate(movie);
                                    loadMovieList();

                                    //Clear the EditText Title
                                    editTextTitle.setText("");
                                    Snackbar.make(editTextTitle,getResources().getString(R.string.successfully_registered),Snackbar.LENGTH_SHORT).show();
                                }
                            }
                            dialog.dismiss();

                        }else{
                            Log.e("Get Movie", String.valueOf(response.code()));
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Movie> call, Throwable t) {
                    }
                });
            } else {
                Snackbar.make(editTextTitle,getResources().getString(R.string.connect_to_internet), Snackbar.LENGTH_SHORT).show();
            }
        }

    }

    private void savePoster(final String imageName, String posterUrl) {
        ContextWrapper cw = new ContextWrapper(mContext);
        final File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        JacksonConverterFactory jacksonConverterFactory = JacksonConverterFactory.create(JacksonMapper.getInstance().getObjectMapper());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ia.media-imdb.com/images/M/")
                .addConverterFactory(jacksonConverterFactory)
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        String url = posterUrl.substring(posterUrl.indexOf("/M/") + 3, posterUrl.length());

        Call<ResponseBody> call = apiService.getPoster(url);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccess()) {
                    try {
                        byte[] data = response.body().bytes();
                        if (data != null) {
                            FileOutputStream fos = null;
                            try {
                                File myPath = new File(directory, imageName);
                                fos = new FileOutputStream(myPath);
                                Bitmap bmp;
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inMutable = true;
                                bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);

                                //Resize the image to save space
                                int width = bmp.getWidth();
                                int height = bmp.getHeight();
                                //Setting 180 because it is the largest size that will be displayed
                                int newWidht = convertToPx(mContext, 180);
                                int value = (newWidht * 100)/width;
                                int newHeight = (height*value)/100;

                                bmp = Bitmap.createScaledBitmap(bmp, newWidht, newHeight, false);
                                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    public static int convertToPx(Context context, int input) {
        final float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (input * scale + 0.5f);
    }
}
