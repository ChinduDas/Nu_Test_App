package com.mechine_test.nu_test_app.view.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mechine_test.nu_test_app.R
import com.mechine_test.nu_test_app.databinding.RowDeviceBinding
import com.mechine_test.nu_test_app.models.Device

class DeviceRecAdap(var dataList: ArrayList<Device>, var ctx: Activity) :
    RecyclerView.Adapter<DeviceRecAdap.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(p0.context),
            R.layout.row_device,
            p0,
            false
        )
                as RowDeviceBinding
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList.get(position))
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    inner class ViewHolder(binding: RowDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val bind = binding
        fun bind(
            data: Device,
        ) {
            bind.data = data
            bind.imgStatus.setImageResource( if(data.status=="0") R.drawable.ic_offline else R.drawable.ic_online)
            bind.clytMain.setOnClickListener {
                (ctx as onSelected).onItemSelected()
            }
        }
    }

    interface onSelected {
        fun onItemSelected()
    }

}