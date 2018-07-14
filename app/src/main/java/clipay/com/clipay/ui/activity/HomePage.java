package clipay.com.clipay.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.material.navigation.NavigationView;
import com.vpaliy.loginconcept.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import clipay.com.clipay.R;
import clipay.com.clipay.model.MultipleItem;
import clipay.com.clipay.model.SMS;
import clipay.com.clipay.ui.adapter.HomePageAdapter;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomePage extends AppCompatActivity {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private NavigationView mNavigationView;
    private SimpleExoPlayer mExoPlayer;
    private String TAG = this.getClass().getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mActivityTitle = getTitle().toString();
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }
        };
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory
                (bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        mExoPlayer.addListener(listenr);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();
        List<MultipleItem> list = new ArrayList<>();
        int size = 100;
        for (int i = 0; i < size; i++) {
            list.add(new MultipleItem(i % 3, new SMS()));
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        HomePageAdapter homePageAdapter = new HomePageAdapter(list, this, mExoPlayer);
        recyclerView.setAdapter(homePageAdapter);
        homePageAdapter.setOnItemClickListener((adapter, view, position) -> startActivity(new Intent
                (HomePage.this, ContentCreationActivity.class)));
        mNavigationView.getHeaderView(0).setOnClickListener(view -> startActivity(new Intent
                (HomePage.this, LoginActivity.class)));
        Glide.with(this).load(HomePageAdapter.Companion.getURL()).apply(RequestOptions
                .circleCropTransform()).into(
                (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id
                        .nav_header_imageView));

    }

    ExoPlayer.EventListener listenr = new ExoPlayer.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            Log.v(TAG, "Listener-onTimelineChanged...");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray
                trackSelections) {
            Log.v(TAG, "Listener-onTracksChanged...");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.v(TAG, "Listener-onLoadingChanged...isLoading:" + isLoading);
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            Log.v(TAG, "Listener-onRepeatModeChanged...");
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.v(TAG, "Listener-onPlayerError...");
            mExoPlayer.stop();
//            mExoPlayer.prepare(loopingSource);
//            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPositionDiscontinuity() {
            Log.v(TAG, "Listener-onPositionDiscontinuity...");
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            Log.v(TAG, "Listener-onPlaybackParametersChanged...");
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mExoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mExoPlayer.setPlayWhenReady(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mExoPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()...");
        mExoPlayer.release();
    }
}
