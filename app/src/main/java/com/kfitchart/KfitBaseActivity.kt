package com.kfitchart

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.R

/**
 * @author hanjun.Kim
 */
abstract class KfitBaseActivity : AppCompatActivity() {
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prepareActionBar()
    }

    private fun prepareActionBar() {
        supportActionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayHomeAsUpEnabled(false)
        }
    }

    fun showLoading() {
        dialog?.let {
            it.show()
        }
    }

    fun hideLoading() {
        dialog?.let {
            it.dismiss()
        }
    }

    protected fun addFragment(containerViewId: Int, fragment: Fragment) {
        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
        fragmentTransaction.add(containerViewId, fragment)
        fragmentTransaction.commit()
    }

    fun alertDialogConfirmYesOrNo(
        title: Any?,
        message: Any?,
        positiveButtonText: String?,
        positiveListener: DialogInterface.OnClickListener?,
        negativeButtonText: String?,
        negativeListener: DialogInterface.OnClickListener?,
        isCancel: Boolean,
    ) {
        AlertDialog.Builder(this, R.style.Theme_MaterialComponents_Light_Dialog_Alert).run {
            if (title is String) {
                this.setTitle(title)
            } else if (title is Int) {
                this.setTitle(title)
            }

            if (message is String) {
                this.setMessage(message)
            } else if (message is Int) {
                this.setMessage(message)
            }

            if (positiveButtonText != null) {
                this.setPositiveButton(
                    positiveButtonText,
                    positiveListener,
                )
            }
            if (negativeButtonText != null) {
                this.setNegativeButton(
                    negativeButtonText,
                    negativeListener,
                )
            }

            setCancelable(isCancel)
            create().show()
        }
    }
}
