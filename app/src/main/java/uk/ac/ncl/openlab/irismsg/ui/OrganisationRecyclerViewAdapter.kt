package uk.ac.ncl.openlab.irismsg.ui

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import kotlinx.android.synthetic.main.fragment_organisation.view.*
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.ui.OrganisationListFragment.OnListFragmentInteractionListener

import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity

/**
 * A Recycler adapter to show a list of Organisation Entities
 */
class OrganisationRecyclerViewAdapter(
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<OrganisationRecyclerViewAdapter.ViewHolder>() {
    
    private val mOnClickListener: View.OnClickListener
    var organisations = listOf<OrganisationEntity>()
    
    init {
        mOnClickListener = View.OnClickListener { v ->
            mListener?.onListFragmentInteraction(v.tag as OrganisationEntity)
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_organisation, parent, false)
        return ViewHolder(view)
    }
    
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        organisations[position].let { org ->
            holder.mNameView.text = org.name
            holder.mInfoView.text = org.shortInfo
            holder.mView.tag = org
            holder.mView.setOnClickListener(mOnClickListener)
        }
    }
    
    override fun getItemCount(): Int = organisations.size
    
    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mNameView: TextView = mView.name
        val mInfoView: TextView = mView.info
        
        override fun toString(): String {
            return super.toString() + " '" + mNameView.text + "'"
        }
    }
}
