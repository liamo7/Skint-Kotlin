package com.loh.skint.ui.record.create

import com.loh.skint.data.entity.TransferType
import com.loh.skint.domain.model.Category
import com.loh.skint.domain.model.Record
import com.loh.skint.domain.usecase.record.AddRecord
import com.loh.skint.injection.scope.ActivityScoped
import com.loh.skint.ui.base.presenter.BasePresenter
import com.loh.skint.util.LONG_DATE_FORMAT
import io.reactivex.observers.DisposableCompletableObserver
import org.threeten.bp.LocalDate
import timber.log.Timber
import java.io.Serializable
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

@ActivityScoped
class RecordCreatePresenter @Inject constructor(private val addRecord: AddRecord) : BasePresenter<View>(), Presenter {

    // we will save our custom states. Edit texts', etc will store their own and we will pull from there
    private var selectedCategory: Category? = null
    private var selectedTransferType: TransferType = TransferType.INCOME
    private var selectedDate = LocalDate.now()

    override fun onTransferTypeClicked() {
        // show the transfer type selector
        getView().showTransferTypeSelector()
    }

    override fun onDateClicked() {
        // show date selector
        getView().showDateSelector(selectedDate)
    }

    override fun onLocationClicked() {
        // show location selector
        getView().showLocationSelector()
    }

    override fun onCategoryIconClicked() {
        // show the category selector
        getView().showCategorySelector()
    }

    override fun onCategorySelected(categoryId: Int) {
        if (categoryId == Category.UNINITALISED_CATEGORY.id) return

        // find the category via its id
        val category = Category.findCategoryById(categoryId)

        // set the category icon
        getView().setCategoryIcon(category.iconRes)

        // save the category to state
        selectedCategory = category
    }

    override fun onDateSelected(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        // build date
        selectedDate = LocalDate.of(year, monthOfYear, dayOfMonth)

        // format and set date
        val prettyDate = selectedDate.format(LONG_DATE_FORMAT)
        getView().setDate(prettyDate)
    }

    override fun onTransferTypeSelected(itemIndex: Int) {
        // determine transfer type
        val transferType = determineTransferType(itemIndex)

        // set transfer type in view
        val prettyType = transferType.name.toLowerCase().capitalize()
        getView().setTransferType(prettyType)

        // save transfer type to state
        selectedTransferType = transferType
    }

    override fun saveRecord() {
        // gather input
        val accountUUID = getView().getAccountUUID()

        val amount = getView().getAmount()
        val note = getView().getNote()

        // validate input
        if (accountUUID == null) return
        if (selectedCategory == null) return

        val amountRegex = "^[0-9]+(\\.[0-9]{1,2})?$"
        if (amount.isBlank()) return
        if (!amount.matches(Regex(amountRegex))) return

        // build record
        val record = Record(
                UUID.randomUUID(),
                selectedTransferType,
                BigDecimal(amount),
                selectedDate,
                selectedCategory!!,
                accountUUID
        )

        // build params
        val params = AddRecord.Params(accountUUID, record)

        // pass to usecase
        addRecord.execute(Observer(), params)
    }

    override fun onSaveState(): State {
        return State(selectedCategory?.id ?: Category.UNINITALISED_CATEGORY.id,
                selectedTransferType,
                selectedDate)
    }

    override fun onRestoreState(state: State) {
        onCategorySelected(state.categoryId)
        onDateSelected(state.date.year, state.date.monthValue, state.date.dayOfMonth)
        onTransferTypeSelected(state.transferType.ordinal)
        Timber.d("State: ${state.categoryId}, ${state.transferType}, ${state.date}")
    }

    private fun determineTransferType(index: Int): TransferType {
        return if (index == 0) TransferType.INCOME else TransferType.EXPENSE
    }

    override fun cleanUp() {
        addRecord.dispose()
    }

    inner class Observer : DisposableCompletableObserver() {
        override fun onComplete() {
            Timber.d("Completed Successfully")
        }

        override fun onError(e: Throwable) {
            Timber.e(e.message)
        }
    }

    data class State(val categoryId: Int,
                     val transferType: TransferType,
                     val date: LocalDate) : Serializable
}