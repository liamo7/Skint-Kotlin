package com.loh.skint.ui.goal.detail

import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
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

    private val savedAmountDialog: MaterialDialog by lazy {
        MaterialDialog.Builder(this)
                .title(R.string.goal_dialog_title)
                .content(R.string.goal_dialog_content)
                .inputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .input("10.00", null, false, { _, input -> presenter.addAmount(input.toString()) })
                .build()
    }

    private val deleteGoalDialog: MaterialDialog by lazy {
        MaterialDialog.Builder(this)
                .title(R.string.dialog_delete_goal)
                .content(R.string.dialog_delete_goal_message)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .onPositive({ _, _ -> presenter.deleteGoal() })
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBackToolbar(toolbar)
        presenter.attach(this)
        fab_add_amount.setOnClickListener { savedAmountDialog.show() }
    }

    override fun onResume() {
        super.onResume()
        presenter.loadGoal()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_goal_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete_goal) {
            deleteGoalDialog.show()
            return true
        }
        return super.onOptionsItemSelected(item)
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

    override fun displayNote(note: String) {
        goal_note.text = note
    }

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

    override fun displayStatus(status: Int) {
        goal_status.setText(status)
    }

    override fun navigateBackToGoals() {
        finish()
    }
}