package clipay.com.clipay.ui.activity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import clipay.com.clipay.R
import clipay.com.clipay.ui.adapter.ColorPickerAdapter
import clipay.com.clipay.util.GradientFileParser
import kotlinx.android.synthetic.main.fragment_blank.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BlankFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val gradients = GradientFileParser.getGradientDrawables(activity!!.applicationContext)
        val adapter = ColorPickerAdapter(gradients)
        val manager = LinearLayoutManager(activity)
        manager.orientation = LinearLayout.HORIZONTAL
        color_picker_list.layoutManager = manager
        color_picker_list.adapter = adapter
        adapter.setOnItemClickListener { _, _, id ->
            edit_query.background = adapter.getItem(id)
            val bitmap = getBitmapFromView(edit_query)
            createPaletteAsync(bitmap)

        }
        super.onViewCreated(view, savedInstanceState)
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null)
            bgDrawable.draw(canvas)
        else
            canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

    private fun createPaletteAsync(bitmap: Bitmap) {
        Palette.from(bitmap).generate { palette ->
            val vibrant = palette?.vibrantSwatch
            val titleColor = vibrant?.bodyTextColor
            if (titleColor != null) {
                edit_query.setTextColor(titleColor)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                BlankFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    fun getColors(): IntArray {
        return intArrayOf(Color.parseColor("#1BFFFF"), Color.parseColor("#2E3192"), Color.parseColor("#ED1E79"), Color.parseColor("#009E00"), Color.parseColor("#FBB03B"), Color.parseColor("#D4145A"), Color.parseColor("#3AA17E"), Color.parseColor("#00537E"))
    }

}
