package uk.ac.ncl.openlab.irismsg

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import kotlinx.android.synthetic.main.fragment_organisation.view.*
import uk.ac.ncl.openlab.irismsg.OrganisationListFragment.OnListFragmentInteractionListener

import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class OrganisationRecyclerViewAdapter(
        private val mValues: List<OrganisationEntity>,
        private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<OrganisationRecyclerViewAdapter.ViewHolder>() {
    
    private val mOnClickListener: View.OnClickListener
    
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
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val org = mValues[position]
        holder.mIdView.text = position.toString()
        holder.mContentView.text = org.name
        
        with(holder.mView) {
            tag = org
            setOnClickListener(mOnClickListener)
        }
    }
    
    override fun getItemCount(): Int = mValues.size
    
    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content
        
        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}