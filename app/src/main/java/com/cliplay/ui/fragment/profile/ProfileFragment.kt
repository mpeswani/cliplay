package com.cliplay.ui.fragment.profile

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.cliplay.R

import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.user_info.*
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomMarginImageView = activity!!.resources.getDimension(R.dimen._11sdp)
        viewPagerImages.adapter = ImageAdapter(activity!!)
        val parentThatHasBottomSheetBehavior = userInfo.parent.parent as RelativeLayout
        val rect = Rect()
        BottomSheetBehavior.from(parentThatHasBottomSheetBehavior)?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
                cardViewProfile.getGlobalVisibleRect(rect)
                viewPagerImages.post {
                    val params = viewPagerImages.layoutParams
                    params.height = (rect.top + bottomMarginImageView).toInt()
                    viewPagerImages.layoutParams = params
                }
            }
        })
        follow.setOnClickListener {
            follow.startAnimation()
            Handler().postDelayed({
                val bitmap = BitmapFactory.decodeResource(activity!!.resources, R.drawable.ic_action_info)
                follow.doneLoadingAnimation(ContextCompat.getColor(activity!!, R.color.colorPrimaryLight), bitmap)
            }, 2000)
        }

        postsClick.setOnClickListener {
            Toast.makeText(activity!!, "posts", Toast.LENGTH_SHORT).show()
        }
        followersClick.setOnClickListener {
            Toast.makeText(activity!!, "followers", Toast.LENGTH_SHORT).show()
        }
        followingClick.setOnClickListener {
            Toast.makeText(activity!!, "following", Toast.LENGTH_SHORT).show()
        }
        viewPagerImages.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val ran = Random()
                followers_count.text = ran.nextInt(1000).toString()
                posts_count.text = ran.nextInt(1000).toString()
                following_count.text = ran.nextInt(1000).toString()
            }
        })
        description.setText(R.string.temp_desc)
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
