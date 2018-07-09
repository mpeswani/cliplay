package clipay.com.clipay.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import clipay.com.clipay.R
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.create_content.*


class ContentCreationActivity : AppCompatActivity(), BlankFragment.OnFragmentInteractionListener {


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
        setContentView(R.layout.create_content)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        loadFragByTag(FRAG_QUOTES)
    }

    private fun loadFragByTag(tag: String) {
        var frag = supportFragmentManager.findFragmentByTag(tag)
        if (frag == null) {
            when (tag) {
                FRAG_GALLERY -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PermUtil.checkForCamara_WritePermissions(this) {
                            frag = Pix()

                        }
                    } else {
                        frag = Pix()

                    }
                }
                FRAG_QUOTES -> {
                    frag = BlankFragment.newInstance("", "");
                }
                FRAG_CAMERA -> {
                    frag = BlankFragment.newInstance("", "");
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
        super.onActivityResult(requestCode, resultCode, data)
    }

}
