package uk.ac.ncl.openlab.irismsg.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.api.JsonWebToken
import uk.ac.ncl.openlab.irismsg.di.Injectable

import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity
import uk.ac.ncl.openlab.irismsg.viewmodel.OrganisationListViewModel
import javax.inject.Inject

/**
 * A fragment representing a list of Organisations, filtered by a MemberRole
 */
class OrganisationListFragment : Fragment(), Injectable {
    
    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var adapter: OrganisationRecyclerViewAdapter
    private lateinit var role: MemberRole
    private lateinit var viewModel: OrganisationListViewModel
    
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(OrganisationListViewModel::class.java)
                .init()

        arguments?.let {
            role = it.get(ARG_ROLE) as MemberRole
        }
    
        val userId = JsonWebToken.load(context!!)?.getUserId() ?: return
        
        viewModel.organisations.observe(this, Observer { orgs ->
            adapter.organisations = (orgs ?: listOf()).filter { org ->
                when (role) {
                    MemberRole.DONOR -> org.members.all { member ->
                        member.role != MemberRole.COORDINATOR || member.userId != userId
                    }
                    else -> org.members.any { member ->
                        member.role == MemberRole.COORDINATOR && member.userId == userId
                    }
                }
            }
        })
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(
            R.layout.fragment_organisation_list, container,
            false
        )

        adapter = OrganisationRecyclerViewAdapter(listener)
        
        // Set the adapter
        if (view is RecyclerView) {
            view.layoutManager = LinearLayoutManager(context)
            view.adapter = adapter
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
        fun onListFragmentInteraction(organisation: OrganisationEntity)
    }
    
    companion object {
        
        const val ARG_ROLE = "role"
        
        @JvmStatic
        fun newInstance(role: MemberRole) = OrganisationListFragment()
                .apply { arguments = Bundle().apply {
                    putSerializable(ARG_ROLE, role)
                } }
    }
}
