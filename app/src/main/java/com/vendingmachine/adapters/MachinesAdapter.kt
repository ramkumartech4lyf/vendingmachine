package com.vendingmachine.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vendingmachine.R
import com.vendingmachine.loginParams.Machine
import com.vendingmachine.machinesResponse.Record

class MachinesAdapter(private val mList: List<Record>, private val onMachinesClick: OnMachinesClick)
    :  RecyclerView.Adapter<MachinesAdapter.ViewHolder>()  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.machine_adapter, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val machine = mList[position]
        holder.name.text = machine.name
        holder.id.text = "id :  ${machine.id}"


    }

    override fun getItemCount(): Int {
        return mList.size
    }



   inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView), View.OnClickListener {
                val name: TextView = itemView.findViewById(R.id.name)
                val id: TextView = itemView.findViewById(R.id.id)

       init {
           itemView.setOnClickListener(this)
       }


        override fun onClick(p0: View?) {
            onMachinesClick.onMachineClick(mList[adapterPosition])
        }
    }



    interface OnMachinesClick{

        fun onMachineClick(machine: Record)
    }

}