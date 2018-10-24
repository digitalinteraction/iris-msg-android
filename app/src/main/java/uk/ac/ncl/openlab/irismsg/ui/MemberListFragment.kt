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
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_member_item.view.*
import kotlinx.android.synthetic.main.fragment_member_list.*
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import uk.ac.ncl.openlab.irismsg.di.Injectable
import uk.ac.ncl.openlab.irismsg.model.OrganisationMemberEntity
import uk.ac.ncl.openlab.irismsg.model.UserEntity
import uk.ac.ncl.openlab.irismsg.viewmodel.OrganisationMembersViewModel
import javax.inject.Inject


/**
 * Displays a list of Members
 */
class MemberListFragment : Fragment(), Injectable {
    
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    
    private var listener: Listener? = null
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var viewModel : OrganisationMembersViewModel
    
    
    private val organisationId: String
        get () = arguments?.getString(ARG_ORGANISATION_ID)!!
    
    private val memberRole: MemberRole
        get () = arguments?.getSerializable(ARG_MEMBER_ROLE) as MemberRole
    
    
    override fun onAttach(context : Context?) {
        super.onAttach(context)
        if (context is Listener) { listener = context }
    }
    
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerAdapter = RecyclerAdapter(listener)
    }
    
    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        return inflater.inflate(
            R.layout.fragment_member_list,
            container,
            false
        )
    }
    
    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        member_list.layoutManager = LinearLayoutManager(context)
        member_list.adapter = recyclerAdapter
        
        // Set the info text based on the role
        info_text.text = getString(
            if (memberRole === MemberRole.DONOR) R.string.body_donors_description
            else R.string.body_subscribers_description
        )
        
        // Listen for refreshes
        swipe_refresh.setOnRefreshListener {
            swipe_refresh.isRefreshing = true
            viewModel.reload()
        }
    }
    
    override fun onActivityCreated(savedInstanceState : Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        // Get a view model for the members
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)
                .get(OrganisationMembersViewModel::class.java)
                .init(organisationId)
        
        // Observe members changes and re-render accordingly
        viewModel.members.observe(this, Observer { members ->
    
            // Stop the refresh animation
            swipe_refresh.isRefreshing = false
            
            if (members == null) return@Observer
            
            // Get the active members in our role
            recyclerAdapter.members = members.filter { member ->
                member.role == memberRole
            }
            
            // Reload the recycler
            recyclerAdapter.notifyDataSetChanged()
        })
    }
    
    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    
    companion object {
        const val ARG_MEMBER_ROLE = "role"
        const val ARG_ORGANISATION_ID = "org_id"
        
        @JvmStatic
        fun newInstance (role: MemberRole, orgId: String) = MemberListFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_MEMBER_ROLE, role)
                putString(ARG_ORGANISATION_ID, orgId)
            }
        }
    }
    
    interface Listener {
        fun onDeleteMember (memberId: String, role: MemberRole)
    }
    
    inner class RecyclerAdapter (private val listener: Listener?)
        : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    
        private val onDeleteListener : View.OnClickListener
        var members: List<OrganisationMemberEntity> = listOf()
        
        init {
            onDeleteListener = View.OnClickListener { button ->
                (button.tag as OrganisationMemberEntity).let { member ->
                    listener?.onDeleteMember(member.id, memberRole)
                }
            }
        }
        
        override fun getItemCount() = members.size
        
        override fun onCreateViewHolder(parent: ViewGroup, post: Int) : ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_member_item, parent, false
            )
            return ViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
            members[pos].let { member ->
                if (member.userId == UserEntity.current?.id) {
                    holder.phoneNumberView.text = getString(
                        R.string.body_member_phone_number_self, member.phoneNumber
                    )
                } else {
                    holder.phoneNumberView.text = member.phoneNumber
                }
                
                holder.deleteButton.tag = member
                holder.deleteButton.setOnClickListener(onDeleteListener)
            }
        }
    
        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val phoneNumberView: TextView = view.phone_number
            val deleteButton: Button = view.delete_button
        }
    }
}