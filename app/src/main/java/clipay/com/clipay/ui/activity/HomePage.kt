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
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import clipay.com.clipay.R
import clipay.com.clipay.license.showLicesnse
import clipay.com.clipay.model.MultipleItem
import clipay.com.clipay.model.SMS
import clipay.com.clipay.ui.adapter.HomePageAdapter
import clipay.com.clipay.ui.fragment.BottomSheetFragment
import clipay.com.clipay.util.DoubleTapImageView
import clipay.com.clipay.views.AutoPlayVideoRecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.vpaliy.loginconcept.LoginActivity
import kotlinx.android.synthetic.main.app_bar_main.*

class HomePage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.nav_item_about -> {
                showLicesnse(this)
            }
        }

        return true
    }

    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var mActivityTitle: String? = null
    private var mNavigationView: NavigationView? = null

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
        mDrawerLayout!!.addDrawerListener(mDrawerToggle!!)
        mDrawerToggle!!.isDrawerIndicatorEnabled = true
        mDrawerToggle!!.syncState()
        val list = ArrayList<MultipleItem>()
        val array = arrayOf(
                "https://images.pexels.com/photos/160699/girl-dandelion-yellow-flowers-160699.jpeg?cs=srgb&dl=beautiful-beauty-dandelion-160699.jpg&fm=jpg",
                "https://cdn.pixabay.com/photo/2014/09/17/20/03/profile-449912_1280.jpg",
                "https://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_30mb.mp4",
                "http://mirrors.standaloneinstaller.com/video-sample/page18-movie-4.mp4",
                "https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg?cs=srgb&dl=adult-attractive-beautiful-415829.jpg&fm=jpg",
                "https://images.pexels.com/photos/805367/pexels-photo-805367.jpeg?cs=srgb&dl=accessories-adult-beautiful-805367.jpg&fm=jpg",
                "https://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_30mb.mp4",
                "https://images.pexels.com/photos/428340/pexels-photo-428340.jpeg?cs=srgb&dl=adult-dark-facial-expression-428340.jpg&fm=jpg",
                "https://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_30mb.mp4",
                "https://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_30mb.mp4",
                "http://mirrors.standaloneinstaller.com/video-sample/video-sample.3gp",
                "https://cdn.pixabay.com/photo/2015/01/07/15/51/woman-591576_1280.jpg",
                "",
                "http://mirrors.standaloneinstaller.com/video-sample/metaxas-keller-Bell.mov",
                "",
                "http://mirrors.standaloneinstaller.com/video-sample/page18-movie-4.mp4",
                "https://cdn.pixabay.com/photo/2014/11/14/21/24/young-girl-531252_1280.jpg",
                "https://images.pexels.com/photos/220418/pexels-photo-220418.jpeg?cs=srgb&dl=beach-beautiful-blonde-220418.jpg&fm=jpg",
                "https://cdn.pixabay.com/photo/2017/04/03/10/42/woman-2197947_1280.jpg",
                "http://mirrors.standaloneinstaller.com/video-sample/metaxas-keller-Bell.mpeg",
                "http://i.imgur.com/1ALnB2s.gif",
                "",
                "https://cdn.pixabay.com/photo/2015/03/17/14/05/sparkler-677774_1280.jpg",
                "https://cdn.pixabay.com/photo/2016/01/19/16/49/holding-hands-1149411_1280.jpg",
                "",
                "https://cdn.pixabay.com/photo/2018/01/25/14/12/nature-3106213_1280.jpg",
                "https://www.sample-videos.com/video/mp4/360/big_buck_bunny_360p_30mb.mp4",
                "",
                "https://www.sample-videos.com/img/Sample-jpg-image-100kb.jpg")
        for (i in 0 until array.size) {
            val sms = SMS()
            sms.id = array[i]
            var type = MultipleItem.TEXT
            if (sms.id.endsWith("mp4") || sms.id.endsWith("flv")) {
                type = MultipleItem.VIDEO
            } else if (array[i].endsWith("png") || array[i].endsWith("jpg")) {
                type = MultipleItem.IMG
            }
            list.add(MultipleItem(type, sms))
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val homePageAdapter = HomePageAdapter(list, this)
        homePageAdapter.bindToRecyclerView(recyclerView)
        recyclerView.adapter = homePageAdapter
        homePageAdapter.setOnItemClickListener { adapter, _, position ->
            if (adapter.getItemViewType(position) == MultipleItem.VIDEO) {
                val rec = recyclerView as AutoPlayVideoRecyclerView
                rec.playOrStop()
            }
        }
        mNavigationView!!.getHeaderView(0).setOnClickListener { view -> startActivity(Intent(this@HomePage, LoginActivity::class.java)) }
        Glide.with(this).load(HomePageAdapter.URL).apply(RequestOptions
                .circleCropTransform()).into(
                mNavigationView!!.getHeaderView(0).findViewById<View>(R.id
                        .nav_header_imageView) as ImageView)
        mNavigationView!!.setNavigationItemSelectedListener(this)
        homePageAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.reply -> {
                    startActivity(Intent(this, CommentActivity::class.java))
                }
                R.id.more -> {
                    val bottomSheetFragment = BottomSheetFragment()
                    bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
                }

                R.id.favourite -> {
                    val animationView = view as LottieAnimationView
                    animationView.setAnimation(R.raw.animation_heart1)
                    animationView.playAnimation()
                }

            }
        }
        val itemDecor = DividerItemDecoration(this, RecyclerView.VERTICAL)
        recyclerView.addItemDecoration(itemDecor)
        fab.setOnClickListener {
            startActivity(Intent(this@HomePage, ContentCreationActivity::class.java))
        }

        homePageAdapter.setOnDoubleTabListener(DoubleTapImageView.DoubleTapDetector {
            val item = homePageAdapter.getItem(it) as MultipleItem
            Glide.with(this).load(item.content.id)
                    .apply(RequestOptions.fitCenterTransform()).transition(DrawableTransitionOptions.withCrossFade())
                    .into(zoomed_image)
            bigPhotoFrame.visibility = View.VISIBLE
            appbar.visibility = View.GONE
            zoomed_image.setScale(1.5f, true)
            zoomed_image.minimumScale = 1.3f
        })
        bigPhotoFrame.setOnClickListener {
            if (zoomed_image.isVisible) {
                bigPhotoFrame.visibility = View.GONE
                appbar.visibility = View.VISIBLE
                fab.visibility = View.GONE
            }
        }
    }

    override fun onBackPressed() {
        if (bigPhotoFrame.isVisible) {
            bigPhotoFrame.visibility = View.GONE
            appbar.visibility = View.VISIBLE
            fab.visibility = View.VISIBLE
            return
        }
        super.onBackPressed()
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
        recyclerView.playWhenReady(true)
    }

    override fun onPause() {
        super.onPause()
        recyclerView.playWhenReady(false)
    }

    override fun onStop() {
        super.onStop()
        recyclerView.stopPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("des", "onDestroy()...")
        recyclerView.releasePlayer()
    }
}
