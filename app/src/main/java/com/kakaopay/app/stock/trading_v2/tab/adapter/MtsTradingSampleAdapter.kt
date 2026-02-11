package com.kakaopay.app.stock.trading_v2.tab.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kakaopay.feature.stock.common.presentation.R

class MtsTradingSampleAdapter : RecyclerView.Adapter<MtsTradingSampleViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MtsTradingSampleViewHolder {
        return MtsTradingSampleViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.mts_trading_sample_item, parent, false)
        )
    }

    override fun getItemCount() = 20
    override fun onBindViewHolder(holder: MtsTradingSampleViewHolder, position: Int) {}
}

class MtsTradingSampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)