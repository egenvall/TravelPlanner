package com.egenvall.travelplanner.search


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.egenvall.travelplanner.R
import kotlinx.android.synthetic.main.search_placeholder.view.*

/**
 * The Controller that represents the Search screen in the ViewPager.
 * This Controller has a [childRouter] with a seperate backstack that can change content
 * depending on actions of [SearchController] such as searching for Stops, Displaying trips etc.
 */
class SearchRouterController : Controller(){
    lateinit var childRouter : Router
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.search_placeholder,container,false)
        onViewBound(view)
        return view
    }

    fun onViewBound(view : View){
        childRouter = getChildRouter(view.child_frame, null).setPopsLastView(false)
        if (!childRouter.hasRootController()) {
            childRouter.setRoot(RouterTransaction.with(SearchController()))
        }
    }

}