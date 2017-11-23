package com.loh.skint.ui.goal.detail

import android.os.Bundle
import com.loh.skint.R
import com.loh.skint.injection.component.ActivityComponent
import com.loh.skint.ui.base.activity.BaseActivity
import com.loh.skint.util.INTENT_ACCOUNT_ID
import com.loh.skint.util.INTENT_GOAL_UUID
import kotlinx.android.synthetic.main.activity_goal_detail.*
import java.util.*
import javax.inject.Inject

class GoalDetailActivity : BaseActivity(), View {

    @Inject lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBackToolbar(toolbar, R.drawable.ic_arrow_back)
        presenter.attach(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.loadGoal()
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    override fun getLayoutRes(): Int = R.layout.activity_goal_detail

    override fun inject(component: ActivityComponent) = component.inject(this)

    override fun getAccountUUID() = intent.getSerializableExtra(INTENT_ACCOUNT_ID) as UUID

    override fun getGoalUUID() = intent.getSerializableExtra(INTENT_GOAL_UUID) as UUID

    override fun displayName(name: String) {
        goal_name.text = name
    }

    override fun displayNote(note: String) {}

    override fun displayProgress(progress: Int, savedAmount: String) {
        goal_progress.progressValue = progress
        goal_progress.centerTitle = savedAmount
    }

    override fun displayIcon(iconResId: Int) = goal_icon.setImageResource(iconResId)

    override fun displayTargetDate(targetDate: String) {
        goal_target_date.text = targetDate
    }

    override fun displayLastAddedAmount(amount: String) {
        goal_last_amount_added.text = amount
    }

    override fun displayTargetAmount(targetAmount: String) {
        goal_target_amount.text = getString(R.string.goal_target_amount, targetAmount)
    }
}