package com.cliplay.ui.activity

import android.animation.Animator
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.cliplay.R
import com.cliplay.license.showLicense
import com.cliplay.model.MultipleItem
import com.cliplay.model.SMS
import com.cliplay.ui.adapter.HomePageAdapter
import com.cliplay.ui.fragment.BottomSheetFragment
import com.cliplay.util.DoubleTapDetector
import com.cliplay.views.AutoPlayVideoRecyclerView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_page.*
import java.util.*

class HomePage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.nav_item_about -> {
                showLicense(this)
            }
        }
        return true
    }

    private lateinit var mDrawerToggle: ActionBarDrawerToggle
    private lateinit var mDrawerLayout: DrawerLayout
    private var mActivityTitle: String? = null
    private lateinit var mNavigationView: NavigationView

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
                supportActionBar!!.title = mActivityTitle
                invalidateOptionsMenu()
            }
        }
        mDrawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.isDrawerIndicatorEnabled = true
        mDrawerToggle.syncState()
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
        val random = Random()
        for (i in 0 until array.size) {
            val sms = SMS()
            sms.id = array[i]
            var type = MultipleItem.TEXT
            if (sms.id.endsWith("mp4") || sms.id.endsWith("flv")) {
                type = MultipleItem.VIDEO
            } else if (array[i].endsWith("png") || array[i].endsWith("jpg")) {
                type = MultipleItem.IMG
                sms.id = "https://source.unsplash.com/random?sig=${random.nextInt(100000)}"
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
        mNavigationView.getHeaderView(0).setOnClickListener { view ->
            startActivity(Intent(this@HomePage, ProfileActivity::class.java))
            drawer_layout.closeDrawers()
        }
        Glide.with(this).load(HomePageAdapter.URL).apply(RequestOptions
                .circleCropTransform()).into(
                mNavigationView.getHeaderView(0).findViewById<View>(R.id
                        .nav_header_imageView) as ImageView)
        mNavigationView.setNavigationItemSelectedListener(this)
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
                    bigPhotoFrame.visibility = View.VISIBLE
                    val view1 = LottieDrawable()
                    val inputStream = resources.openRawResource(R.raw.animation_heart)
                    val x = LottieComposition.Factory.fromInputStreamSync(inputStream)
                    view1.composition = x
                    zoomed_image.setImageDrawable(view1)
                    view1.playAnimation()
                    view1.addAnimatorListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(p0: Animator?) {
                        }

                        override fun onAnimationEnd(p0: Animator?) {
                            bigPhotoFrame.visibility = View.GONE
                        }

                        override fun onAnimationCancel(p0: Animator?) {
                        }

                        override fun onAnimationStart(p0: Animator?) {
                        }

                    })
                }
                R.id.share -> {
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Clipay best social networking app")
                    sendIntent.type = "text/plain"
                    startActivity(sendIntent)
                }
                R.id.image, R.id.user_name -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, getString(R.string.transition))
                    startActivity(intent, options.toBundle())
                }
            }
        }
        val itemDecor = DividerItemDecoration(this, RecyclerView.VERTICAL)
        recyclerView.addItemDecoration(itemDecor)
        fab.setOnClickListener {
            startActivity(Intent(this@HomePage, ContentCreationActivity::class.java))
        }

        homePageAdapter.setOnDoubleTabListener(DoubleTapDetector {
            val item = homePageAdapter.getItem(it) as MultipleItem
            Glide.with(this).load(item.content.id)
                    .apply(RequestOptions.fitCenterTransform()).transition(DrawableTransitionOptions.withCrossFade())
                    .into(zoomed_image)
            bigPhotoFrame.visibility = View.VISIBLE
            appbar.visibility = View.GONE
            zoomed_image.setScale(1.5f, true)
            zoomed_image.minimumScale = 1.3f
            fab.hide()
        })
        bigPhotoFrame.setOnClickListener {
            if (zoomed_image.isVisible) {
                bigPhotoFrame.visibility = View.GONE
                appbar.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        if (bigPhotoFrame.isVisible) {
            bigPhotoFrame.visibility = View.GONE
            appbar.visibility = View.VISIBLE
            fab.show()
            return
        }
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START)
            return
        }
        super.onBackPressed()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mDrawerToggle.onOptionsItemSelected(item)) {
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
