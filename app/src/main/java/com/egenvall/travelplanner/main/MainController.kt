package com.egenvall.travelplanner.main

import android.graphics.Color
import android.support.annotation.LayoutRes
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.rxlifecycle.RxController
import com.bluelinelabs.conductor.support.ControllerPagerAdapter
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.favourite.FavouriteController
import com.egenvall.travelplanner.search.SearchController
import devlight.io.library.ntb.NavigationTabBar

/**
 * MainController acts like a container that contains a [navigationTabBar] and a
 * [viewPager] which [pagerAdapter] from Conductor has Controllers.
 * as its views.
 */
class MainController : RxController(){
    private val pagerAdapter : ControllerPagerAdapter
    private lateinit var viewPager : ViewPager
    private lateinit var navigationTabBar :  NavigationTabBar
    @LayoutRes val layoutResId: Int = R.layout.screen_main
    private val TAG = "MainController"


    init {
        pagerAdapter = object : ControllerPagerAdapter(this, false) {
            override fun getItem(position: Int): Controller {
                when(position){
                    0 -> return SearchController()
                    1 -> return FavouriteController()
                }
                return FavouriteController()
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(layoutResId,container,false)
        onViewBound(view)
        return view
    }

     fun onViewBound(view: View) {
        navigationTabBar = view.findViewById(R.id.ntb) as NavigationTabBar
        viewPager = view.findViewById(R.id.view_pager) as ViewPager
        viewPager.adapter = pagerAdapter
        initTabBar()
    }

    override fun onDestroyView(view: View) {
        viewPager.adapter = null
        super.onDestroyView(view)
    }



    private fun initTabBar(){
        val models = mutableListOf<NavigationTabBar.Model>()
        val colors = resources?.getStringArray(R.array.white)
        models.add(
                NavigationTabBar.Model.Builder(
                        resources?.getDrawable(R.drawable.ic_search_white_24dp),
                        Color.parseColor(colors!![0])
                )
                        .badgeTitle(resources?.getString(R.string.search))
                        .build()
        )
        models.add(
                NavigationTabBar.Model.Builder(
                        resources?.getDrawable(R.drawable.ic_favorite_border_white_24dp),
                        Color.parseColor(colors[1])
                )
                        .badgeTitle(resources?.getString(R.string.favourites))
                        .build()
        )
        models.add(
                NavigationTabBar.Model.Builder(
                        resources?.getDrawable(R.drawable.ic_settings_white_24dp),
                        Color.parseColor(colors[2])
                )
                        .badgeTitle(resources?.getString(R.string.settings))
                        .build()
        )

        navigationTabBar.models = models
        navigationTabBar.setViewPager(viewPager, 2)
    }
}