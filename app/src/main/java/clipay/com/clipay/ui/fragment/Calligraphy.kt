package clipay.com.clipay.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import clipay.com.clipay.R
import clipay.com.clipay.ui.adapter.ColorPickerAdapter
import clipay.com.clipay.ui.adapter.TextEditorFunAdapter
import clipay.com.clipay.util.GradientFileParser
import com.clipay.texteditor.KnifeText
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_blank.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Calligraphy : Fragment() {
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
        gradients.add(0, ContextCompat.getDrawable(activity!!, R.drawable.ic_keyboard_arrow_right_white_24dp)!!)
        val colorPickerAdapter = ColorPickerAdapter(gradients)
        val manager = LinearLayoutManager(activity)
        manager.orientation = LinearLayout.HORIZONTAL
        color_picker_list.layoutManager = manager
        color_picker_list.adapter = colorPickerAdapter
        colorPickerAdapter.setOnItemClickListener { adapter, _, id ->
            when (id) {
                0 -> {
                    if (adapter.data.size == 1) {
                        adapter.setNewData(gradients)
//                        color_picker_list.setBackgroundResource(R.drawable.recyclerview_color_picker_background)
                    } else {
                        val arrow = arrayListOf(ContextCompat.getDrawable(activity!!, R.drawable.ic_keyboard_arrow_left_white_24dp)!!)
//                        color_picker_list.setBackgroundResource(R.drawable.recycler_view_side_background)
                        adapter.setNewData(arrow)
                    }
                }
                else -> {
                    edit_query.background = adapter.getItem(id) as Drawable
                    val bitmap = getBitmapFromView(edit_query)
                    createPaletteAsync(bitmap)
                }
            }
        }
        val arrayOfIcons = intArrayOf(R.drawable.ic_keyboard_arrow_right_white_24dp, R.drawable.ic_format_bold, R.drawable.ic_format_italic,
                R.drawable.ic_format_underline, R.drawable.ic_format_strikethrough,
                R.drawable.ic_format_bullet, R.drawable.ic_format_quote,
                R.drawable.ic_insert_link, R.drawable.ic_format_clear, R.drawable.ic_color_lens_black_24dp)
        val textEditorFunAdapter = TextEditorFunAdapter(arrayOfIcons.toList())
        val manager2 = LinearLayoutManager(activity)
        manager2.orientation = LinearLayout.HORIZONTAL
        text_editor_fun.layoutManager = manager2
        text_editor_fun.adapter = textEditorFunAdapter
        textEditorFunAdapter.setOnItemClickListener { adapter, _, position ->
            when (position) {
                0 -> {
                    if (adapter.data.size == 1) {
                        adapter.setNewData(arrayOfIcons.toList())
//                        text_editor_fun.setBackgroundResource(R.drawable.recyclerview_color_picker_background)
                    } else {
                        val a = intArrayOf(R.drawable.ic_keyboard_arrow_left_white_24dp)
//                        text_editor_fun.setBackgroundResource(R.drawable.recycler_view_side_background)
                        adapter.setNewData(a.toList())
                    }
                }
                1 -> {
                    edit_query.bold(!edit_query.contains(KnifeText.FORMAT_BOLD))
                }
                2 -> {
                    edit_query.italic(!edit_query.contains(KnifeText.FORMAT_ITALIC))
                }
                3 -> {
                    edit_query.underline(!edit_query.contains(KnifeText.FORMAT_UNDERLINED))
                }
                4 -> {
                    edit_query.strikethrough(!edit_query.contains(KnifeText.FORMAT_STRIKETHROUGH))
                }
                5 -> {
                    edit_query.bullet(!edit_query.contains(KnifeText.FORMAT_BULLET))
                }
                6 -> {
                    edit_query.quote(!edit_query.contains(KnifeText.FORMAT_QUOTE))
                }
                7 -> {
                    showLinkDialog()
                }
                8 -> {
                    edit_query.clearFormats()
                }
                9 -> {
                    ColorPickerDialogBuilder
                            .with(context)
                            .setTitle("Choose color")
                            .initialColor(edit_query.currentTextColor)
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
//                            .setOnColorSelectedListener { selectedColor ->  }
                            .setPositiveButton("ok") { dialog, selectedColor, allColors -> edit_query.setTextColor(selectedColor) }
                            .setNegativeButton("cancel") { dialog, which -> }
                            .build()
                            .show()
                }

            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun showLinkDialog() {
        val start = edit_query.selectionStart
        val end = edit_query.selectionEnd

        val builder = AlertDialog.Builder(activity)
        builder.setCancelable(false)

        val view = layoutInflater.inflate(R.layout.dialog_link, null, false)
        val editText = view.findViewById(R.id.edit) as EditText
        builder.setView(view)
        builder.setTitle(R.string.dialog_title)

        builder.setPositiveButton(R.string.dialog_button_ok, DialogInterface.OnClickListener { dialog, which ->
            val link = editText.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(link)) {
                return@OnClickListener
            }

            // When KnifeText lose focus, use this method
            edit_query.link(link, start, end)
        })

        builder.setNegativeButton(R.string.dialog_button_cancel) { dialog, which ->
            // DO NOTHING HERE
        }

        builder.create().show()
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

    private fun getBitmapFromView(view: View): Bitmap {
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
         * @return A new instance of fragment Calligraphy.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                Calligraphy().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }


}
