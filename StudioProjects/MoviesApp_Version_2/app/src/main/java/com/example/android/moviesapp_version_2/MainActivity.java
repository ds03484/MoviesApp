package com.example.android.moviesapp_version_2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.moviesapp_version_2.sync.MoviesSyncAdapter;

public class MainActivity extends AppCompatActivity implements MoviesFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MoviesFragment fragment = ((MoviesFragment)getSupportFragmentManager().findFragmentById(R.id.movie_fragment));

        MoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        Intent intent = new Intent(this, DetailActivity.class)
                .setData(contentUri);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MoviesFragment movie = (MoviesFragment)getSupportFragmentManager().findFragmentById(R.id.movie_fragment);
        if(null != movie){
            movie.onSettingChange();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

}
