package clipay.com.clipay.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import clipay.com.clipay.R
import clipay.com.clipay.model.MultipleItem
import clipay.com.clipay.model.SMS
import clipay.com.clipay.ui.adapter.HomePageAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.material.navigation.NavigationView
import com.vpaliy.loginconcept.LoginActivity

class HomePage : AppCompatActivity() {
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var mActivityTitle: String? = null
    private var mNavigationView: NavigationView? = null
    private var mExoPlayer: SimpleExoPlayer? = null
    private val TAG = this.javaClass.canonicalName

    private var listener: ExoPlayer.EventListener = object : ExoPlayer.EventListener {
        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
            Log.v(TAG, "Listener-onTimelineChanged...")
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
            Log.v(TAG, "Listener-onTracksChanged...")
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            Log.v(TAG, "Listener-onLoadingChanged...isLoading:$isLoading")
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Log.v(TAG, "Listener-onPlayerStateChanged...$playbackState")
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            Log.v(TAG, "Listener-onRepeatModeChanged...")
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            Log.v(TAG, "Listener-onPlayerError...")
            mExoPlayer!!.stop()
            //            mExoPlayer.prepare(loopingSource);
            //            mExoPlayer.setPlayWhenReady(true);
        }

        override fun onPositionDiscontinuity() {
            Log.v(TAG, "Listener-onPositionDiscontinuity...")
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            Log.v(TAG, "Listener-onPlaybackParametersChanged...")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)
        mDrawerLayout = findViewById(R.id.drawer_layout)
        mNavigationView = findViewById(R.id.nav_view)
        mActivityTitle = title.toString()
        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        mDrawerToggle = object : ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
             override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                supportActionBar!!.setTitle(R.string.app_name)
                invalidateOptionsMenu()
            }

            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                supportActionBar!!.setTitle(mActivityTitle)
                invalidateOptionsMenu()
            }
        }
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        mExoPlayer!!.addListener(listener)
        mDrawerLayout!!.addDrawerListener(mDrawerToggle!!)
        mDrawerToggle!!.isDrawerIndicatorEnabled = true
        mDrawerToggle!!.syncState()
        val list = ArrayList<MultipleItem>()
        val size = 100
        for (i in 0 until size) {
            list.add(MultipleItem(i % 3, SMS()))
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val homePageAdapter = HomePageAdapter(list, this, mExoPlayer!!)
        recyclerView.setAdapter(homePageAdapter)
        homePageAdapter.setOnItemClickListener { adapter, view, position -> startActivity(Intent(this@HomePage, ContentCreationActivity::class.java)) }
        mNavigationView!!.getHeaderView(0).setOnClickListener { view -> startActivity(Intent(this@HomePage, LoginActivity::class.java)) }
        Glide.with(this).load(HomePageAdapter.URL).apply(RequestOptions
                .circleCropTransform()).into(
                mNavigationView!!.getHeaderView(0).findViewById<View>(R.id
                        .nav_header_imageView) as ImageView)
        homePageAdapter.setOnItemChildClickListener { adapter, view, position -> startActivity(Intent(this, CommentActivity::class.java)) }
        val itemDecor = DividerItemDecoration(this, RecyclerView.VERTICAL)
        recyclerView.addItemDecoration(itemDecor)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        mExoPlayer!!.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        mExoPlayer!!.playWhenReady = false
    }

    override fun onStop() {
        super.onStop()
        mExoPlayer!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy()...")
        mExoPlayer!!.release()
    }
}
