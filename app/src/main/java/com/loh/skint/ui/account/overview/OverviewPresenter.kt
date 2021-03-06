package com.loh.skint.ui.account.overview

import com.loh.skint.domain.usecase.account.GetOverview
import com.loh.skint.injection.scope.ActivityScoped
import com.loh.skint.ui.base.presenter.BasePresenter
import com.loh.skint.ui.model.OverviewModel
import io.reactivex.observers.DisposableSingleObserver
import timber.log.Timber
import javax.inject.Inject

@ActivityScoped
class OverviewPresenter @Inject constructor(private val getOverview: GetOverview) : BasePresenter<View>(), Presenter {

    override fun loadAccount() {
        val id = getView().getAccountUUID()
        getOverview.execute(Observer(), id)
    }

    override fun onFabClicked() {
        getView().getAccountUUID().let {
            getView().navigateToRecordCreation(it)
        }
    }

    override fun cleanUp() {
        getOverview.dispose()
    }

    inner class Observer : DisposableSingleObserver<OverviewModel>() {
        override fun onSuccess(model: OverviewModel) {
            getView().renderOverviewCollapse(model)
            getView().renderRecentRecords(model.recentRecords)
            getView().renderUpcomingGoals(model.upcomingGoals)
        }

        override fun onError(e: Throwable) {
            Timber.e(e.message)
            getView().handleInvalidAccount()
        }
    }
}