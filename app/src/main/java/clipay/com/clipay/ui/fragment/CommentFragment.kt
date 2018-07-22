package clipay.com.clipay.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import clipay.com.clipay.R
import clipay.com.clipay.model.Comment
import clipay.com.clipay.ui.adapter.CommentsAdapter
import clipay.com.clipay.ui.adapter.HomePageAdapter
import kotlinx.android.synthetic.main.fragment_comment.*
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CommentFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CommentFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_comment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val commentsAdapter = CommentsAdapter(getComments())
        val manager = LinearLayoutManager(activity)
        manager.orientation = LinearLayout.VERTICAL
        recyclerViewComment.layoutManager = manager
        recyclerViewComment.adapter = commentsAdapter
        val itemDecor = DividerItemDecoration(activity, manager.orientation)
        recyclerViewComment.addItemDecoration(itemDecor)

    }

    private fun getComments(): List<Comment> {
        val list = LinkedList<Comment>()
        for (i in 0..25) {
            val comment = Comment()
            comment.comment = "Greyhound divisively hello coldly wonderfully marginally far upon excluding."
            comment.likes = "99.1k"
            comment.time = "8h"
            comment.userName = "Kathryn Collins"
            comment.userImage = HomePageAdapter.URL
            list.add(comment)
        }
        return list
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CommentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CommentFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
