package com.vendingmachine.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vendingmachine.R
import com.vendingmachine.productResponse.Record


class ProductsAdapter(private val mList: List<Record>, private val onMachinesClick: OnProductsClick)
    :  RecyclerView.Adapter<ProductsAdapter.ViewHolder>()  {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_adapter, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = mList[position]

        holder.productId.text = "Product id :  ${product.product_id[0]}"
        holder.name.text = product.product_id[1].toString()
        holder.quantity.text = "Quantity :  ${product.quantity.toInt()}"
        holder.id.text = "id :  ${product.id}"



    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView), View.OnClickListener{
        val name: TextView = itemView.findViewById(R.id.name)
        val id: TextView = itemView.findViewById(R.id.id)
        val productId: TextView = itemView.findViewById(R.id.product_id)
        val quantity: TextView = itemView.findViewById(R.id.quantity)
        val price: TextView = itemView.findViewById(R.id.price)
        private val edit: Button = itemView.findViewById(R.id.edit)
        init {
            edit.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {

            onMachinesClick.onProductClick(mList[adapterPosition])
        }
    }


    interface OnProductsClick{

        fun onProductClick(product : Record)
    }

}