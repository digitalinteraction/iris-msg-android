package uk.ac.ncl.openlab.irismsg.activity

import android.app.PendingIntent
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_donate.*
import kotlinx.android.synthetic.main.fragment_donation_item.view.*
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.api.MessageAttemptUpdate
import uk.ac.ncl.openlab.irismsg.api.UpdateMessageAttemptsRequest
import uk.ac.ncl.openlab.irismsg.common.DateUtils
import uk.ac.ncl.openlab.irismsg.common.MessageAttemptState
import uk.ac.ncl.openlab.irismsg.common.ViewsUtil
import uk.ac.ncl.openlab.irismsg.model.PendingMessageEntity
import uk.ac.ncl.openlab.irismsg.receiver.SmsDeliveredReceiver
import uk.ac.ncl.openlab.irismsg.receiver.SmsSentReceiver
import uk.ac.ncl.openlab.irismsg.viewmodel.PendingMessageListViewModel
import javax.inject.Inject

class DonateActivity : AppCompatActivity(), HasSupportFragmentInjector {
    
    @Inject lateinit var dispatchingAndroidInjector : DispatchingAndroidInjector<Fragment>
    @Inject lateinit var viewModelFactory : ViewModelProvider.Factory
    @Inject lateinit var viewsUtil : ViewsUtil
    @Inject lateinit var irisService : IrisMsgService
    
    override fun supportFragmentInjector() = dispatchingAndroidInjector
    
    private lateinit var viewModel : PendingMessageListViewModel
    private var currentState : State = State.INITIAL
    private val recyclerAdapter = RecyclerAdapter()
    
    private var toSend : MutableMap<String, Int> = mutableMapOf()
    
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup the view
        setContentView(R.layout.activity_donate)
        setSupportActionBar(toolbar)
        
        // Setup ViewModel
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PendingMessageListViewModel::class.java)
                .init()
        
        // Listen for donate clicks
        donate_button.setOnClickListener { _ ->
            val pending = viewModel.messages.value ?: return@setOnClickListener
            performDonations(pending, toSend)
        }
        
        // Listen for refreshes
        swipe_refresh.setOnRefreshListener {
            swipe_refresh.isRefreshing = true
            viewModel.reload()
        }
        
        // Setup the recycler
        donation_list.adapter = recyclerAdapter
        
        // Enter initial state
        listOf(no_donations, donations, api_progress).forEach { it.visibility = View.GONE }
        
        // Observer donations
        viewModel.messages.observe(this, Observer { messages ->
            swipe_refresh.isRefreshing = false
            
            if (messages != null && messages.isNotEmpty()) {
                messages.forEach { toSend[it.id] = it.attempts.size }
                enterState(State.HAS_DONATIONS)
                recyclerAdapter.donations = messages
            } else {
                enterState(State.NO_DONATIONS)
                recyclerAdapter.donations = listOf()
            }
        })
    }
    
    private fun performDonations(pendingMessages : List<PendingMessageEntity>, counts : Map<String, Int>) {
        enterState(State.WORKING)
        
        val updates = mutableListOf<PotentialAttemptUpdate>()
        
        pendingMessages.forEach { message ->
            
            val count = counts[message.id]!!
            
            message.attempts.forEachIndexed { i, attempt ->
                updates.add(PotentialAttemptUpdate(
                    attempt.id,
                    if (i < count) MessageAttemptState.PENDING
                    else MessageAttemptState.REJECTED,
                    message.content,
                    attempt.phoneNumber
                )
                )
            }
        }
        
        updates.filter { it.state == MessageAttemptState.PENDING }
                .forEach { sendSms(it) }
        
        val toUpdate = updates.filter { it.state != MessageAttemptState.PENDING }
                .map { it.forApi() }
        
        val body = UpdateMessageAttemptsRequest(toUpdate)
        irisService.updateMessageAttempts(body).enqueue(ApiCallback({ res ->
            if (res.success) {
                viewModel.reload()
                Snackbar.make(
                    main_content,
                    getString(R.string.body_donations_sent),
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                Snackbar.make(
                    main_content,
                    res.messages.joinToString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }, { _ ->
            TODO("Handle messages.attempts.update error")
        }))
        
    }
    
    private fun sendSms(message : PotentialAttemptUpdate) {
        
        val sentIntent = Intent(this, SmsSentReceiver::class.java)
                .putExtra(SmsSentReceiver.EXTRAS_ATTEMPT_ID, message.attemptId)
        
        val deliveredIntent = Intent(this, SmsDeliveredReceiver::class.java)
                .putExtra(SmsDeliveredReceiver.EXTRAS_ATTEMPT_ID, message.attemptId)
        
        // Create a pending intent to trigger the sent receiver
        val sentPending = PendingIntent.getBroadcast(
            applicationContext,
            SmsSentReceiver.REQUEST_API_UPDATE,
            sentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Create a pending intent to trigger the broadcast receiver
        val deliveredPending = PendingIntent.getBroadcast(
            applicationContext,
            SmsDeliveredReceiver.REQUEST_API_UPDATE,
            deliveredIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Send the sms
        SmsManager.getDefault().sendTextMessage(
            message.phoneNumber,
            null,
            message.content,
            sentPending,
            deliveredPending
        )
    }
    
    private fun enterState(state : State) {
        
        // Transition out
        viewsUtil.toggleElem(when (currentState) {
            State.NO_DONATIONS -> no_donations
            State.HAS_DONATIONS -> donations
            State.WORKING -> api_progress
            else -> null
        }, false
        )
        
        // Set state
        currentState = state
        
        // Transition in
        viewsUtil.toggleElem(when (currentState) {
            State.NO_DONATIONS -> no_donations
            State.HAS_DONATIONS -> donations
            State.WORKING -> api_progress
            else -> null
        }, true
        )
    }
    
    data class PotentialAttemptUpdate(
        val attemptId : String,
        val state : MessageAttemptState,
        val content : String,
        val phoneNumber : String
    ) {
        fun forApi() = MessageAttemptUpdate(attemptId, state)
    }
    
    enum class State {
        NO_DONATIONS, HAS_DONATIONS, WORKING, INITIAL
    }
    
    inner class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
        
        var donations : List<PendingMessageEntity> = listOf()
            set (newValue) {
                field = newValue; notifyDataSetChanged()
            }
        
        override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_donation_item, parent, false)
            return ViewHolder(view)
        }
        
        override fun getItemCount() : Int = donations.size
        
        override fun onBindViewHolder(holder : ViewHolder, pos : Int) {
            donations[pos].let { donation ->
                holder.orgView.text = donation.organisationId
                holder.dateView.text = DateUtils.timeSince(donation.createdAt, true)
                holder.messageView.text = donation.content
                
                holder.seekView.progress = toSend[donation.id]!!
                holder.seekView.max = donation.attempts.size
                holder.seekView.tag = donation
                
                updateCountLabel(holder.countView, donation.attempts.size, donation.attempts.size)
                
                holder.seekView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(bar : SeekBar, value : Int, flag : Boolean) {
                        (bar.tag as? PendingMessageEntity)?.let { donation ->
                            toSend[donation.id] = value
                            updateCountLabel(holder.countView, value, donation.attempts.size)
                        }
                    }
                    
                    override fun onStartTrackingTouch(bar : SeekBar?) {}
                    override fun onStopTrackingTouch(bar : SeekBar?) {}
                })
            }
        }
        
        private fun updateCountLabel(countLabel : TextView, current : Int, max : Int) {
            countLabel.text = getString(
                R.string.label_fraction, current, max
            )
        }
        
        inner class ViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
            val orgView : TextView = view.organisation
            val dateView : TextView = view.date
            val messageView : TextView = view.message
            val seekView : SeekBar = view.seeker
            val countView : TextView = view.counter
        }
    }
}
