package clipay.com.clipay.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import clipay.com.clipay.R
import clipay.com.clipay.ui.fragment.Calligraphy
import com.blankj.utilcode.util.BarUtils
import com.fxn.interfaces.MediaFetcher
import com.fxn.pix.Pix
import com.fxn.pix.Vid
import com.fxn.utility.PermUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hoanganhtuan95ptit.editphoto.ui.activity.EditPhotoActivity
import kotlinx.android.synthetic.main.create_content.*


class ContentCreationActivity : AppCompatActivity(), Calligraphy.OnFragmentInteractionListener,
        Pix.OnPixFinished {
    override fun onPixChoosed(urls: MutableList<String>?) {
        EditPhotoActivity.start(this, urls?.get(0))

//        val resultIntent = Intent(this, PixEditorActivity::class.java)
//        resultIntent.putStringArrayListExtra("image_results", urls as ArrayList<String>?)
//        startActivityForResult(resultIntent, 0)
    }


    override fun onFragmentInteraction(uri: Uri) {
    }

    private val FRAG_CAMERA = "home_frag"
    private val FRAG_GALLERY = "dashboard_frag"
    private val FRAG_QUOTES = "notifications_frag"

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_camera -> {
                loadFragByTag(FRAG_CAMERA)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_gallery -> {
                loadFragByTag(FRAG_GALLERY)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_quote -> {
                loadFragByTag(FRAG_QUOTES)
                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarVisibility(this, false)
        setContentView(R.layout.create_content)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        loadFragByTag(FRAG_CAMERA)
    }

    private fun loadFragByTag(tag: String) {
        var frag = supportFragmentManager.findFragmentByTag(tag)
        if (frag == null) {
            when (tag) {
                FRAG_GALLERY -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PermUtil.checkForCamara_WritePermissions(this) {
                            frag = Pix.getInstance(MediaFetcher.IMAGE)

                        }
                    } else {
                        frag = Pix.getInstance(MediaFetcher.IMAGE)

                    }
                }
                FRAG_QUOTES -> {
                    frag = Calligraphy.newInstance("", "")
                }
                FRAG_CAMERA -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PermUtil.checkForCamara_WritePermissions(this) {
                            frag = Vid.getInstance(MediaFetcher.VIDEO)
                        }
                    } else {
                        frag = Vid.getInstance(MediaFetcher.VIDEO)

                    }
                }

            }

        } else {
            Log.d("TAG", "$tag found.")
        }

        if (frag != null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_main, frag!!, tag)
                    .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_CANCELED && requestCode == 0) {
            val frag = supportFragmentManager.findFragmentByTag(FRAG_GALLERY)
            if (frag is Pix) {
                frag.clear()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
