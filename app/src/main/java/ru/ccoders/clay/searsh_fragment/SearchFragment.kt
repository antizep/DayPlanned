package ru.ccoders.clay.searsh_fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import ru.ccoders.clay.R
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.ccoders.clay.adapter.ScheduleCastomAdapter
import ru.ccoders.clay.databinding.FragmentSearshBinding
import ru.ccoders.clay.main_activity.MainActivityViewModel
import ru.ccoders.clay.model.SearchModel
import ru.ccoders.clay.rest.TaskRest
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var searchLiveData: MutableLiveData<SearchModel>
    lateinit var ctx:Context
    private lateinit var fragmentSearshBinding: FragmentSearshBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        fragmentSearshBinding = FragmentSearshBinding.inflate(layoutInflater)
        ctx = requireContext();
        val provider: SearchFragmentViewModel by lazy {
            ViewModelProvider(this).get(SearchFragmentViewModel::class.java)
        }

        provider.loadTask()
        searchLiveData = provider.profileLiveData;
        fragmentSearshBinding.searchRecycleView.layoutManager = LinearLayoutManager(ctx)
        searchLiveData.observe(this,{
            Log.d(tag,"it:"+it.previous)
            val adapter = ScheduleCastomAdapter(it.scheduleAndProfile,ctx,true)

            fragmentSearshBinding.searchRecycleView.adapter = adapter

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return fragmentSearshBinding.root


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearshFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
