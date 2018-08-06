package clipay.com.clipay.ui.activity

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import clipay.com.clipay.R
import clipay.com.clipay.ui.fragment.profile.ProfileFragment

class ProfileActivity : AppCompatActivity(), ProfileFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportFragmentManager.beginTransaction().add(R.id.content,
                ProfileFragment.newInstance("", "")).commit()
    }
}
