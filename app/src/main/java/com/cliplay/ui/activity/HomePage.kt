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
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.cliplay.R
import com.cliplay.license.showLicense
import com.cliplay.model.MultipleItem
import com.cliplay.model.SMS
import com.cliplay.networking.services.RetrofitService
import com.cliplay.ui.adapter.HomePageAdapter
import com.cliplay.ui.fragment.BottomSheetFragment
import com.cliplay.ui.fragment.home.HomeFragment
import com.cliplay.util.DoubleTapDetector
import com.cliplay.views.AutoPlayVideoRecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_page.*
import java.util.*

class HomePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fm = supportFragmentManager
        var fragment = fm.findFragmentByTag("myFragmentTag")
        if (fragment == null) {
            val ft = fm.beginTransaction()
            fragment = HomeFragment()
            ft.add(android.R.id.content, fragment, "myFragmentTag")
            ft.commit()
        }
    }

    override fun onBackPressed() {
//        if (bigPhotoFrame.isVisible) {
//            bigPhotoFrame.visibility = View.GONE
//            appbar.visibility = View.VISIBLE
//            fab.show()
//            return
//        }
//        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
//            mDrawerLayout.closeDrawer(Gravity.START)
//            return
//        }
        super.onBackPressed()
    }

}
