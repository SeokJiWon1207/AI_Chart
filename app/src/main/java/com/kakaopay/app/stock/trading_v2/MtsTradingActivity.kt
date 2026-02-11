package com.kakaopay.app.stock.trading_v2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.kakaopay.feature.stock.common.presentation.R
import com.kakaopay.feature.stock.common.presentation.databinding.ActivityMtsTradingBinding

class MtsTradingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMtsTradingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMtsTradingBinding
            .inflate(layoutInflater)
            .also {
                setContentView(it.root)
            }

        supportFragmentManager.commit(false) {
            replace(R.id.frame, MtsTradingFragment.newInstance())
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MtsTradingActivity::class.java)
        }
    }
}