package com.kfitchart

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * @author hanjun.Kim
 */
abstract class KfitBaseFragment : Fragment() {
    protected abstract fun getTitleRes(): Int?
    abstract fun refresh()
    abstract fun launchSelect()

    fun setTitle(title: String) {
        activity?.let {
            (activity as AppCompatActivity).supportActionBar?.title = title
        }
    }

    fun setTitle(@StringRes title: Int?) {
        title?.let {
            setTitle(getString(title))
        }
    }

    fun getTitle(): Int? {
        getTitleRes()?.let { return it } ?: return null
    }

    fun onError(throwable: Throwable?) {
//        val act = activity
//        if(act is BaseActivity) act.onError(throwable)
    }

    fun supportFragmentManager(): FragmentManager? {
        return activity?.supportFragmentManager
    }
}
