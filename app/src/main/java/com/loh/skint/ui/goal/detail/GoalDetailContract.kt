package com.loh.skint.ui.goal.detail

import android.support.annotation.StringRes
import com.loh.skint.ui.base.AccountView
import com.loh.skint.ui.base.presenter.MvpPresenter
import java.util.*

interface View : AccountView {
    fun displayName(name: String)
    fun displayIcon(iconResId: Int)
    fun displayNote(note: String)
    fun getGoalUUID(): UUID
    fun displayProgress(progress: Int, savedAmount: String)
    fun displayTargetAmount(targetAmount: String)
    fun displayTargetDate(targetDate: String)
    fun displayLastAddedAmount(amount: String)
    fun navigateBackToGoals()
    fun displayStatus(@StringRes status: Int)
}

interface Presenter : MvpPresenter<View> {
    fun loadGoal()
    fun deleteGoal()
    fun addAmount(amount: String)
}