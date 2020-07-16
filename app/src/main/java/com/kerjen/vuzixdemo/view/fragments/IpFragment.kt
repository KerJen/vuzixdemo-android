package com.kerjen.vuzixdemo.view.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kerjen.vuzixdemo.R
import com.kerjen.vuzixdemo.network.WebSocketService
import com.kerjen.vuzixdemo.network.WebSocketState
import com.kerjen.vuzixdemo.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_ip.*
import javax.inject.Inject

@AndroidEntryPoint
class IpFragment : Fragment(R.layout.fragment_ip) {

    @Inject
    lateinit var webSocketService: WebSocketService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webSocketService.listener.webSocketStateCallback.add {
            when (it) {
                WebSocketState.CONNECTED -> {
                    (activity as MainActivity).openThingsFragment()
                }
                else -> {
                    //TODO: поправить костыль
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(), getString(R.string.lost_internet_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        connectButton.setOnClickListener {
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
}