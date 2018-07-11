package uk.ac.ncl.openlab.irismsg.ui

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import kotlinx.android.synthetic.main.fragment_organisation.view.*
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.ui.OrganisationListFragment.Listener

import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity

/**
 * A Recycler adapter to show a list of Organisation Entities
 */
class OrganisationRecyclerViewAdapter(
    private val mListener: Listener?
) : RecyclerView.Adapter<OrganisationRecyclerViewAdapter.ViewHolder>() {
    
    private val mOnClickListener: View.OnClickListener
    var organisations = listOf<OrganisationEntity>()
    
    init {
        mOnClickListener = View.OnClickListener { v ->
            mListener?.onOrganisationSelected(v.tag as OrganisationEntity)
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_organisation, parent, false)
        return ViewHolder(view)
    }
    
    override fun getItemCount(): Int = organisations.size
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        organisations[position].let { org ->
            holder.nameView.text = org.name
            holder.infoView.text = org.shortInfo
            holder.view.tag = org
            holder.view.setOnClickListener(mOnClickListener)
        }
    }
    
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.name
        val infoView: TextView = view.info
        
        // override fun toString(): String =super.toString() + " '" + nameView.text + "'"
    }
}
