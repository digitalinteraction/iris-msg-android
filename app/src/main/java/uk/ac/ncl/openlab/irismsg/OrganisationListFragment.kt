package uk.ac.ncl.openlab.irismsg

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment SHOULD implement the
 * [OrganisationListFragment.OnListFragmentInteractionListener] interface.
 */
class OrganisationListFragment : Fragment() {
    
    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var role: MemberRole
    private lateinit var viewModel: OrganisationListViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        viewModel = ViewModelProviders.of(this)
                .get(OrganisationListViewModel::class.java)
        
        arguments?.let {
            role = it.get(ARG_ROLE) as MemberRole
            viewModel.init(it.getString(ARG_USER))
        }
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(
            R.layout.fragment_organisation_list, container,
            false
        )
        
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = OrganisationRecyclerViewAdapter(listOf(), listener)
            }
        }
        
        return view
    }
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
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
     * See the Android Training lesson
     * [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: OrganisationEntity)
    }
    
    companion object {
        
        const val ARG_USER = "user"
        const val ARG_ROLE = "role"
        
        @JvmStatic fun newInstance(role: MemberRole) = OrganisationListFragment()
                .apply { arguments = Bundle().apply { putSerializable(ARG_ROLE , role) } }
    }
}
