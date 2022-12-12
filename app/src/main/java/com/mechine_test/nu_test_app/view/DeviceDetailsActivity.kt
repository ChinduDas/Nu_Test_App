package com.mechine_test.nu_test_app.view

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.mechine_test.nu_test_app.databinding.ActivityDeviceDetailsBinding
import com.mechine_test.nu_test_app.util.commonUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL


class DeviceDetailsActivity : AppCompatActivity() {
    lateinit var _binding: ActivityDeviceDetailsBinding
    lateinit var _viewModel: DeviceViewModel
    lateinit var mProgressDialog: ProgressDialog


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDeviceDetailsBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setTitle("Loading")
        mProgressDialog.setMessage("Please wait...")
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()
        if (commonUtils.isNetworkAvailable(this))
            GlobalScope.launch { getMyIp() }
        else
            Snackbar.make(
                _binding.tvDetails,
                "Please check your wifi network!",
                Snackbar.LENGTH_SHORT
            ).show()
        _viewModel = ViewModelProvider(this).get(DeviceViewModel::class.java)
        _viewModel.datasNew.observe(this) {
            mProgressDialog.dismiss()
            _binding.tvDetails.text =
                "Ip - " + it.ip + "\n" + "City - " + it.city + "\n" + "Region - " + it.region + "\n" + "Country - " + it.country + "\n" + "Location - " + it.loc + "\n" + "Organization - " + it.org + "\n"
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getMyIp() {
        val url = URL("https://api.ipify.org?format=json")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET
            println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    println(line)
                    GlobalScope.launch {
                        getIpDetails(
                            Gson().fromJson(
                                line,
                                IpData::class.java
                            ).ip.toString()
                        ).toString()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    fun getIpDetails(ip: String) {
        val url = URL(" https://ipinfo.io/$ip/geo")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET
            println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
            inputStream.bufferedReader().use {
                println(it)
                try {
                    val datas = Gson().fromJson(it, IpDetails::class.java)
                    _viewModel.datasNew.postValue(datas)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    data class IpData(
        var ip: String? = ""
    )

    data class IpDetails(
        var ip: String? = "",
        var city: String? = "",
        var region: String? = "",
        var country: String? = "",
        var loc: String? = "",
        var org: String? = "",
        var timezone: String? = "",
        var readme: String? = ""
    )
}