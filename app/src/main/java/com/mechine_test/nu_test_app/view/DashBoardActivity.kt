package com.mechine_test.nu_test_app.view

import android.content.Context
import android.content.Intent
import android.net.nsd.NsdManager
import android.net.nsd.NsdManager.ResolveListener
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mechine_test.nu_test_app.databinding.ActivityDashBoardBinding
import com.mechine_test.nu_test_app.db.DBHelper
import com.mechine_test.nu_test_app.models.Device
import com.mechine_test.nu_test_app.view.adapter.DeviceRecAdap

lateinit var nsdManager: NsdManager

class DashBoardActivity : AppCompatActivity(), DeviceRecAdap.onSelected, IDoUpdate {

    lateinit var _binding: ActivityDashBoardBinding
    lateinit var mContext: Context
    private val SERVICE_TYPE = "_services._dns-sd._udp."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDashBoardBinding.inflate(layoutInflater)
        mContext = this
        nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        DBHelper(mContext, null).updateDevices()
        _binding.adapter = DeviceRecAdap(getFromDb()!!, this)
        setContentView(_binding.root)
    }

    private val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onDiscoveryStarted(regType: String) {
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            val discoverClildLstener = DiscoveryListenerChildNew()
            discoverClildLstener.setContext(mContext)
            nsdManager.discoverServices(
                service.serviceName + "._tcp.",
                NsdManager.PROTOCOL_DNS_SD,
                discoverClildLstener
            )
        }

        override fun onServiceLost(service: NsdServiceInfo) {
        }

        override fun onDiscoveryStopped(serviceType: String) {
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            nsdManager.stopServiceDiscovery(this)
        }
    }

    private class MyResolveListener : ResolveListener {

        lateinit var mContext: Context

        fun setContext(ctx: Context) {
            mContext = ctx
        }

        fun saveToDb(device: Device, ctx: Context) {
            if (DBHelper(mContext, null).saveDevice(device)) {
                Log.d("SAVED", "SUCCESS")
            }
        }

        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            try {
                val listener = MyResolveListener()
                listener.setContext(mContext)
                nsdManager.resolveService(serviceInfo, listener)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
            saveToDb(Device(serviceInfo.serviceName, serviceInfo.host.hostAddress, "1"), mContext)
            (mContext as IDoUpdate).updateView()
        }
    }

    private class DiscoveryListenerChildNew : NsdManager.DiscoveryListener {
        lateinit var mContext: Context

        fun setContext(ctx: Context) {
            mContext = ctx
        }

        override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
        }

        override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
        }

        override fun onDiscoveryStarted(serviceType: String?) {
        }

        override fun onDiscoveryStopped(serviceType: String?) {
        }

        override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
            val listener = MyResolveListener()
            listener.setContext(mContext)
            nsdManager.resolveService(serviceInfo, listener)
        }

        override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
        }
    }

    fun getFromDb() = DBHelper(mContext, null).getDevices()


    override fun onItemSelected() {
         startActivity(Intent(this,DeviceDetailsActivity::class.java))
    }

    override fun updateView() {
        _binding.adapter = DeviceRecAdap(getFromDb()!!, this)
    }
}

interface IDoUpdate {
    fun updateView()
}



