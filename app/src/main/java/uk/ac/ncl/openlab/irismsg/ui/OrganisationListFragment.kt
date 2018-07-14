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
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_organisation_item.view.*
import kotlinx.android.synthetic.main.fragment_organisation_list.*
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import uk.ac.ncl.openlab.irismsg.di.Injectable
import uk.ac.ncl.openlab.irismsg.jwt.JwtService
import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity
import uk.ac.ncl.openlab.irismsg.viewmodel.OrganisationListViewModel
import javax.inject.Inject

/**
 * A fragment representing a list of Organisations, filtered by a MemberRole
 * TODO: Handle loading errors?
 */
class OrganisationListFragment : Fragment(), Injectable {
    
    private var listener: Listener? = null
    private lateinit var adapter: RecyclerAdapter
    private lateinit var viewModel: OrganisationListViewModel
    
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var jwtService: JwtService
    
    private val memberRole: MemberRole
        get () = arguments?.getSerializable(ARG_MEMBER_ROLE) as MemberRole
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) { listener = context }
    }
    
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = RecyclerAdapter(listener)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(
            R.layout.fragment_organisation_list,
            container,
            false
        )
    }
    
    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set the adapter
        org_list.layoutManager = LinearLayoutManager(context)
        org_list.adapter = adapter
    
        // Listen for refresh events
        swipe_refresh.setOnRefreshListener {
            swipe_refresh.isRefreshing = true
            viewModel.reload()
        }
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        // Get a ViewModel to manage the data
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)
                .get(OrganisationListViewModel::class.java)
                .init()
        
        // Grab the user's id from our jwt
        val userId = jwtService.getUserId() ?: return
        
        // Listen for organisations
        viewModel.organisations.observe(this, Observer { orgs ->
            
            // Stop the refresh animation
            swipe_refresh.isRefreshing = false
            
            // Do nothing more if there are no organisations
            // TODO: Handle this error
            if (orgs == null) return@Observer
            
            // Filter the organisations based on our memberRole
            adapter.organisations = when (memberRole) {
                MemberRole.COORDINATOR -> orgs.filter { it.isCoordinator(userId) }
                else -> orgs.filter { !it.isCoordinator(userId) }
            }
        })
    }
    
    override fun onResume() {
        super.onResume()
        
        viewModel.reloadFromCache()
    }
    
    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    
    companion object {
        
        const val ARG_MEMBER_ROLE = "memberRole"
        
        @JvmStatic
        fun newInstance(role: MemberRole) = OrganisationListFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_MEMBER_ROLE, role)
            }
        }
    }
    
    inner class RecyclerAdapter (private val listener: Listener?)
        : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
        
        private val onClickListener: View.OnClickListener
        
        var organisations: List<OrganisationEntity> = listOf()
            set (newValue) { field = newValue; notifyDataSetChanged() }
        
        init {
            onClickListener = View.OnClickListener { view ->
                listener?.onOrganisationSelected(view.tag as OrganisationEntity)
            }
        }
    
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_organisation_item, parent, false)
            return ViewHolder(view)
        }
        
        override fun getItemCount(): Int = organisations.size
    
        override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
            organisations[pos].let { org ->
                holder.nameView.text = org.name
                holder.infoView.text = org.shortInfo
                jwtService.getUserId()?.let { userId ->
                    holder.rolesView.text = org.primaryMembership(userId)?.role?.humanized ?: ""
                }
                holder.view.tag = org
                holder.view.setOnClickListener(onClickListener)
            }
        }
        
        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val nameView: TextView = view.name
            val infoView: TextView = view.organisation_info
            val rolesView: TextView = view.roles
        }
    }
    
    interface Listener {
        fun onOrganisationSelected(organisation: OrganisationEntity)
    }
}
