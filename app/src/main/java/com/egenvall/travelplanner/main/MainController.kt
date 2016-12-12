package com.egenvall.travelplanner.main

import android.graphics.Color
import android.support.annotation.LayoutRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewParent
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.support.ControllerPagerAdapter
import com.egenvall.travelplanner.ExampleApplication
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.base.presentation.BaseController
import com.egenvall.travelplanner.common.injection.component.DaggerMainViewComponent
import com.egenvall.travelplanner.common.injection.component.MainViewComponent
import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.extension.showSnackbar
import devlight.io.library.ntb.NavigationTabBar
import javax.inject.Inject


class MainController : BaseController<MainPresenter.View, MainPresenter>(),
        MainPresenter.View {

    private lateinit var mainViewComponent: MainViewComponent
    override val passiveView: MainPresenter.View = this
    @Inject override lateinit var presenter: MainPresenter
    lateinit var pagerAdapter : ControllerPagerAdapter
    lateinit var viewPager : ViewPager
    lateinit var navigationTabBar :  NavigationTabBar

    @LayoutRes override val layoutResId: Int = R.layout.screen_main
    private val TAG = "MainController"


    init {
        pagerAdapter = object : ControllerPagerAdapter(this, false) {
            override fun getItem(position: Int): Controller {
                return ChildController()
            }

            override fun getCount(): Int {
                return 2
            }

            override fun getPageTitle(position: Int): CharSequence {
                return "Page " + position
            }
        }
    }
    //===================================================================================
    // Lifecycle methods and initialization
    //===================================================================================
    override fun onViewBound(view: View) {
        initInjection()
        navigationTabBar = view.findViewById(R.id.ntb) as NavigationTabBar
        viewPager = view.findViewById(R.id.view_pager) as ViewPager
        viewPager.adapter = pagerAdapter
        initTabBar()
    }

    //===================================================================================
    // Dependency injection
    //===================================================================================

    override fun initInjection() {
        val act = activity as AppCompatActivity
        mainViewComponent = DaggerMainViewComponent.builder()
                .appComponent((act.application as ExampleApplication).appComponent)
                .activityModule(ActivityModule(act))
                .build()
        mainViewComponent.inject(this)
    }

    //===================================================================================
    // View methods
    //===================================================================================

    override fun showMessage(str : String) {
        view?.showSnackbar(str)
    }



    fun initTabBar(){

        val models = mutableListOf<NavigationTabBar.Model>()
        val colors = resources?.getStringArray(R.array.red_wine)
        models.add(
                NavigationTabBar.Model.Builder(
                        resources?.getDrawable(R.drawable.ic_first),
                        Color.parseColor(colors!![0])
                )
                        .badgeTitle("NTB")
                        .build()
        )
        models.add(
                NavigationTabBar.Model.Builder(
                        resources?.getDrawable(R.drawable.ic_second),
                        Color.parseColor(colors[1])
                )
                        .badgeTitle("with")
                        .build()
        )
        models.add(
                NavigationTabBar.Model.Builder(
                        resources?.getDrawable(R.drawable.ic_third),
                        Color.parseColor(colors[2])
                )
                        .badgeTitle("state")
                        .build()
        )
        models.add(
                NavigationTabBar.Model.Builder(
                        resources?.getDrawable(R.drawable.ic_fourth),
                        Color.parseColor(colors[3])
                )
                        .badgeTitle("icon")
                        .build()
        )
        models.add(
                NavigationTabBar.Model.Builder(
                        resources?.getDrawable(R.drawable.ic_fifth),
                        Color.parseColor(colors[4])
                )
                        .badgeTitle("777")
                        .build()
        )
        navigationTabBar.models = models
        navigationTabBar.setViewPager(viewPager, 2)
        navigationTabBar.titleMode = NavigationTabBar.TitleMode.ACTIVE
        navigationTabBar.badgeGravity = NavigationTabBar.BadgeGravity.BOTTOM
        navigationTabBar.badgePosition = NavigationTabBar.BadgePosition.CENTER
        navigationTabBar.setTypeface("fonts/custom_font.ttf")
        navigationTabBar.setIsBadged(true)
        navigationTabBar.setIsTinted(true)
        navigationTabBar.setIsBadgeUseTypeface(true)
        navigationTabBar.badgeBgColor = Color.RED
        navigationTabBar.badgeTitleColor = Color.WHITE
        navigationTabBar.setIsSwiped(true)
        navigationTabBar.badgeSize = 10f
        navigationTabBar.titleSize = 10f
        navigationTabBar.iconSizeFraction = 0.5f
    }
}