package com.omdbexample.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.omdbexample.R;
import com.omdbexample.activity.MainActivity;
import com.omdbexample.activity.MovieDetailActivity;
import com.omdbexample.model.Movie;
import com.omdbexample.utils.ClickListener;
import com.omdbexample.utils.ListViewItem;
import com.omdbexample.utils.TypeFaces;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Luis Fernando Briguelli da Silva on 13/03/2016.
 */
public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_MOVIE = 0;

    private ArrayList<ListViewItem> listViewItems;
    private Activity mContext;
    File directory;
    private RecyclerView mRecyclerView;
    private TextView noMovies;

    public MovieAdapter(Activity c, ArrayList<ListViewItem> list, RecyclerView mRecyclerView, TextView noMovies) {
        mContext = c;
        listViewItems = list;
        ContextWrapper cw = new ContextWrapper(mContext.getApplicationContext());
        directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        this.mRecyclerView = mRecyclerView;
        this.noMovies = noMovies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card, parent, false);
        ViewHolderMovie vh = new ViewHolderMovie(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final ViewHolderMovie vh = (ViewHolderMovie) holder;
            final Movie movie = (Movie) listViewItems.get(position).getObject();
            vh.title.setText(movie.getTitle()+ " (" +movie.getYear()+")");
            vh.imdbRating.setText(movie.getImdbRating());
            vh.genre.setText(movie.getGenre());
            vh.runtime.setText(movie.getRuntime());

            if(movie.getPosterPath()!=null){
                File myPath = new File(directory,movie.getPosterPath());
                if(myPath!=null){
                    Uri uri = Uri.fromFile(myPath);
                    vh.moviePoster.setImageURI(uri);
                    vh.moviePoster.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainActivity.mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }else{
                Uri uri = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                        .path(String.valueOf(R.mipmap.ic_image_area_grey600_48dp))
                        .build();
                vh.moviePoster.setImageURI(uri);
                vh.moviePoster.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER);
            }

            vh.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(mContext)
                        .title(mContext.getResources().getString(R.string.delete_movie))
                        .content(mContext.getResources().getString(R.string.really_want_delete_movie) +" "+movie.getTitle()+"?")
                        .theme(Theme.LIGHT)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                new Delete().from(Movie.class).where("imdbID = ?",movie.getImdbID()).execute();
                                listViewItems.remove(position);

                                if(listViewItems.size()==0){
                                    noMovies.setVisibility(View.VISIBLE);
                                    mRecyclerView.setVisibility(View.GONE);
                                }else if(listViewItems.size()==1){
                                    noMovies.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                }
                                MainActivity.mAdapter.notifyDataSetChanged();
                            }
                        })
                        .positiveText(R.string.delete)
                        .negativeText(R.string.cancel)
                        .positiveColorRes(R.color.grey_700)
                        .negativeColorRes(R.color.grey_700)
                        .widgetColorRes(R.color.grey_700)
                        .buttonRippleColorRes(R.color.grey_400)
                        .show();
                }
            });

        vh.setClickListener(new ClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(mContext, MovieDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("imdbID", movie.getImdbID());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return listViewItems.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return listViewItems.size();
    }

    public class ViewHolderMovie extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {
        RelativeLayout relativeLayout;
        SimpleDraweeView moviePoster;
        ImageView delete;
        TextView title, imdbRating,genre,runtime;

        private ClickListener clickListener;


        public ViewHolderMovie(View itemView) {
            super(itemView);

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_movie_card);
            moviePoster = (SimpleDraweeView) itemView.findViewById(R.id.movie_poster);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            title = (TextView) itemView.findViewById(R.id.title_and_year);
            imdbRating = (TextView) itemView.findViewById(R.id.imdb_rating);
            genre = (TextView) itemView.findViewById(R.id.genre);
            runtime = (TextView) itemView.findViewById(R.id.runtime);

            title.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Bold.ttf"));
            imdbRating.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Light.ttf"));
            genre.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Light.ttf"));
            runtime.setTypeface(TypeFaces.getTypeFace(mContext, "Roboto_Light.ttf"));
            itemView.setOnClickListener(this);
        }

        public void setClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }

}
