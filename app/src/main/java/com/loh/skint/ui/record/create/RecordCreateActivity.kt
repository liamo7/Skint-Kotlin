package com.loh.skint.ui.record.create

import android.content.Intent
import android.os.Bundle
import android.support.annotation.DrawableRes
import com.afollestad.materialdialogs.MaterialDialog
import com.loh.skint.R
import com.loh.skint.injection.component.ActivityComponent
import com.loh.skint.ui.base.activity.BaseActivity
import com.loh.skint.ui.category.list.CategoryListActivity.Companion.ARG_SELECTED_CATEGORY
import com.loh.skint.ui.category.list.CategoryListActivity.Companion.INTENT_REQUEST_CODE
import com.loh.skint.util.INTENT_ACCOUNT_ID
import com.loh.skint.util.categoryListActivity
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.activity_record_create.*
import org.threeten.bp.LocalDate
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class RecordCreateActivity : BaseActivity(), View, DatePickerDialog.OnDateSetListener {

    @Inject lateinit var presenter: Presenter

    private val transferTypeDialog: MaterialDialog by lazy {
        MaterialDialog.Builder(this)
                .title(R.string.title_transfer_type)
                .items(R.array.transfer_types)
                .itemsCallbackSingleChoice(-1, { _, _, which, _ ->
                    presenter.onTransferTypeSelected(which)
                    true
                })
                .positiveText(R.string.choose)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBackToolbar(toolbar, R.drawable.ic_arrow_back)
        presenter.attach(this)


        if (savedInstanceState != null && savedInstanceState["STATE"] != null) {
            presenter.onRestoreState(savedInstanceState.getSerializable("STATE") as RecordCreatePresenter.State)
            Timber.d("Activity State: ${savedInstanceState.getSerializable("STATE") as RecordCreatePresenter.State}")
        }

        // disable entry
        record_create_transfer_type_input.keyListener = null
        record_create_date_input.keyListener = null
        record_create_location_input.keyListener = null

        // setup click listeners for actions
        record_create_transfer_type_input.setOnClickListener { presenter.onTransferTypeClicked() }
        record_create_transfer_type_action.setOnClickListener { presenter.onTransferTypeClicked() }

        record_create_date_input.setOnClickListener { presenter.onDateClicked() }
        record_create_date_action.setOnClickListener { presenter.onDateClicked() }

        record_create_location_input.setOnClickListener { presenter.onLocationClicked() }
        record_create_location_action.setOnClickListener { presenter.onLocationClicked() }

        record_create_icon.setOnClickListener { presenter.onCategoryIconClicked() }

        fab_save_record.setOnClickListener { presenter.saveRecord() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INTENT_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.extras?.getInt(ARG_SELECTED_CATEGORY)?.let { presenter.onCategorySelected(it) }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable("STATE", presenter.onSaveState())
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    override fun getLayoutRes(): Int = R.layout.activity_record_create

    override fun inject(component: ActivityComponent) = component.inject(this)

    override fun getAccountUUID(): UUID? = intent.getSerializableExtra(INTENT_ACCOUNT_ID) as UUID

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        presenter.onDateSelected(year, monthOfYear, dayOfMonth)
    }

    override fun showCategorySelector() {
        startActivityForResult(categoryListActivity(), INTENT_REQUEST_CODE)
    }

    override fun showTransferTypeSelector() {
        if (!transferTypeDialog.isShowing) transferTypeDialog.show()
    }

    override fun showDateSelector(date: LocalDate) {
        val dpd = DatePickerDialog.newInstance(this, date.year, date.monthValue, date.dayOfMonth)
        dpd.show(fragmentManager, "dpd")
    }

    override fun showLocationSelector() {

    }

    override fun setCategoryIcon(@DrawableRes iconRes: Int) {
        record_create_icon.setImageResource(iconRes)
    }

    override fun setTransferType(transferType: String) {
        record_create_transfer_type_input.setText(transferType)
    }

    override fun setDate(date: String) {
        record_create_date_input.setText(date)
    }

    override fun getAmount(): String {
        return record_create_amount.text.toString()
    }

    override fun getNote(): String {
        return record_create_note_input.text.toString()
    }
}