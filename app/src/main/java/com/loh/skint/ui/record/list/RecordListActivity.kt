package com.loh.skint.ui.record.list

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.loh.skint.R
import com.loh.skint.injection.component.ActivityComponent
import com.loh.skint.ui.account.BaseAccountDrawerActivity
import com.loh.skint.util.DateRange
import com.loh.skint.util.calculateViewpagerPositionFromDateRange
import kotlinx.android.synthetic.main.activity_record_list.*
import org.threeten.bp.LocalDate
import javax.inject.Inject

class RecordListActivity : BaseAccountDrawerActivity() {

    @Inject lateinit var pagerAdapter: RecordListPagerAdapter

    private lateinit var dateRangeDialog: MaterialDialog
    private var date = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewpager.adapter = pagerAdapter
        viewpager.currentItem = date.calculateViewpagerPositionFromDateRange(pagerAdapter.getDateRange())
    }

    override fun getLayoutRes(): Int = R.layout.activity_record_list

    override fun inject(component: ActivityComponent) = component.inject(this)

    override fun getMenuItemRes(): Int = R.id.nav_records

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_record_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_date_range) openDateRangeDialog()
        return super.onOptionsItemSelected(item)
    }

    private fun openDateRangeDialog() {
        dateRangeDialog = MaterialDialog.Builder(this)
                .title(R.string.title_date_range_select)
                .items(R.array.date_ranges)
                .itemsCallbackSingleChoice(pagerAdapter.getDateRange().id, { dialog, itemView, which, text ->
                    // update our newly selected date range
                    pagerAdapter.setDateRange(DateRange.values()[which])
                    // update the pager position to new position of date based on new date range
                    viewpager.currentItem = date.calculateViewpagerPositionFromDateRange(pagerAdapter.getDateRange())
                    true
                })
                .positiveText(R.string.choose)
                .build()

        if (!dateRangeDialog.isShowing) dateRangeDialog.show()
    }
}
