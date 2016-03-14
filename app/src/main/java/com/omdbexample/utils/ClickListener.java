package com.omdbexample.utils;

import android.view.View;

/**
 * Created by Luis Fernando Briguelli da Silva on 14/03/2016.
 */
public interface ClickListener {
    /**
     * Called when the view is clicked.
     *
     * @param v view that is clicked
     * @param position of the clicked item
     */
    public void onClick(View v, int position);
}
