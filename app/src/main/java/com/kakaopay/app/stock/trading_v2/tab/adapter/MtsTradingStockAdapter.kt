package com.kakaopay.app.stock.trading_v2.tab.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kakaopay.app.stock.trading_v2.model.MtsTradingStockKey
import com.kakaopay.feature.stock.common.presentation.databinding.MtsTradingStockItemBinding

class MtsTradingStockAdapter(
    private val items: List<MtsTradingStockKey>,
    private val onItemClick: (MtsTradingStockKey) -> Unit
) : RecyclerView.Adapter<MtsTradingStockViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MtsTradingStockViewHolder {
        return MtsTradingStockViewHolder(
            MtsTradingStockItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.count()
    override fun onBindViewHolder(holder: MtsTradingStockViewHolder, position: Int) {
        items.getOrNull(position)?.run { holder.bind(this, onItemClick) }
    }
}

class MtsTradingStockViewHolder(
    private val binding: MtsTradingStockItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: MtsTradingStockKey,
        onItemClick: (MtsTradingStockKey) -> Unit
    ) {

        with(binding) {
            stockName.text = item.getId()
            ticker.text = item.key

            container.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}