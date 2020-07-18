package com.kerjen.vuzixdemo.view.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.kerjen.vuzixdemo.R
import com.kerjen.vuzixdemo.network.WebSocketService
import com.kerjen.vuzixdemo.network.WebSocketState
import com.kerjen.vuzixdemo.network.WebSocketState.CONNECTED
import com.kerjen.vuzixdemo.network.WebSocketState.FAILURE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_ip.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IpFragment : Fragment(R.layout.fragment_ip) {

    @Inject
    lateinit var webSocketService: WebSocketService

    private val callback: (WebSocketState) -> (Unit) = {
        when (it) {
            CONNECTED -> parentFragmentManager.commit {
                replace(R.id.fragmentContainer, ThingsFragment())
            }
            FAILURE -> {
                GlobalScope.launch(Dispatchers.Main) {
                    connectButton.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.connection_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webSocketService.listener.webSocketStateCallback.add(callback)

        connectButton.setOnClickListener {
            connectButton.isEnabled = false
            val ip = ipEditText.text.toString()
            val port = portEditText.text.toString()

            try {
                webSocketService.connect(ip, port)
            } catch (e: IllegalArgumentException) {
                ipEditText.error = getString(R.string.wrong_data)
                portEditText.error = getString(R.string.wrong_data)
            }
        }
    }

    override fun onDestroy() {
        webSocketService.listener.webSocketStateCallback.remove(callback)
        super.onDestroy()
    }
}