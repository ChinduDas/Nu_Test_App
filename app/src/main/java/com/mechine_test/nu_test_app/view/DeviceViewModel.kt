package com.mechine_test.nu_test_app.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeviceViewModel : ViewModel() {

    var datasNew = MutableLiveData<DeviceDetailsActivity.IpDetails>()
}