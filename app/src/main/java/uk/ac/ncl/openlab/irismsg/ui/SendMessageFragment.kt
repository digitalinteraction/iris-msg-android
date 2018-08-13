package uk.ac.ncl.openlab.irismsg.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_send_message.*
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.common.EventBus
import uk.ac.ncl.openlab.irismsg.di.Injectable
import javax.inject.Inject

class SendMessageFragment : Fragment(), Injectable {
    
    private var listener: Listener? = null
    
    @Inject lateinit var irisService: IrisMsgService
    @Inject lateinit var events: EventBus
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) { listener = context }
    }
    
    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        return inflater.inflate(
            R.layout.fragment_send_message,
            container,
            false
        )
    }
    
    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup listeners ...
        message.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            
            override fun beforeTextChanged(s: CharSequence?, start: Int, end: Int, count: Int) {}
            
            override fun onTextChanged(s : CharSequence?, start: Int, end: Int, count: Int) {
                setMessage(s.toString())
            }
        })
    
        setCharLimitLabel("")
    }
    
    override fun onStart() {
        super.onStart()
    
        // Listen for reset events
        events.on(EVENT_RESET, EventBus.Listener { _ ->
            message.text = null
            setMessage("")
        })
    }
    
    override fun onStop() {
        super.onStop()
        
        // Remove all reset even listeners
        events.clear(EVENT_RESET)
    }
    
    private fun setMessage (string: String) {
        listener?.onMessageChange(string)
        setCharLimitLabel(string)
    }
    
    private fun setCharLimitLabel (message: String) {
        char_limit.text = getString(
            R.string.message_char_count,
            message.length,
            140
        )
    }
    
    interface Listener {
        fun onMessageChange (message: String)
    }
    
    companion object {
        const val EVENT_RESET = "send_message_reset"
        
        @JvmStatic
        fun newInstance () = SendMessageFragment().apply { /* .. */ }
    }
}