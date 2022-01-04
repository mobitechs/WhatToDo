package com.wahttodo.app.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wahttodo.app.R
import com.wahttodo.app.adapter.DecisionListCardStackAdapter
import com.wahttodo.app.adapter.DecisionShortListedAdapter
import com.wahttodo.app.model.DumpedMoviesList
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.viewModel.UserListViewModel
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.fragment_decision_listing.view.*

class DecisionListingFragment : Fragment(), CardStackListener {

    lateinit var listAdapter: DecisionListCardStackAdapter
    val listItems = ArrayList<DumpedMoviesList>()
    private lateinit var manager: CardStackLayoutManager
    lateinit var rootView: View
    lateinit var viewModelUser: UserListViewModel
    lateinit var mLayoutManager: LinearLayoutManager
    var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_decision_listing, container, false)
        intView()
        return rootView
    }

    private fun intView() {

        userId = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()

//        setupRecyclerView()
        setupCardStackView()
        getDecisionSelectedList()
    }

    private fun setupRecyclerView() {
    }

    private fun setupCardStackView() {
        manager = CardStackLayoutManager(requireActivity(), this)
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.Manual)
        manager.setOverlayInterpolator(LinearInterpolator())
        rootView.cardStackView.layoutManager = manager
        listAdapter = DecisionListCardStackAdapter(requireActivity())
        rootView.cardStackView.adapter = listAdapter
        rootView.cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction?.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction?) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View?, position: Int) {
        Log.d("CardStackView", "onCardAppeared: ($position)")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        Log.d("CardStackView", "onCardDisappeared: ($position)")
    }

    private fun getDecisionSelectedList() {
        listItems.clear()
        listItems.add(DumpedMoviesList("https://upload.wikimedia.org/wikipedia/en/thumb/c/cc/K.G.F_Chapter_1_poster.jpg/220px-K.G.F_Chapter_1_poster.jpg", "KGF", "5", "Best Movie of south", 0))
        listItems.add(DumpedMoviesList("https://m.media-amazon.com/images/M/MV5BNDExMTBlZTYtZWMzYi00NmEwLWEzZGYtOTA1MDhmNTc0ODZkXkEyXkFqcGdeQXVyODE5NzE3OTE@._V1_.jpg", "Hera Pheri", "4", "Best comedy movie", 0))
        listItems.add(DumpedMoviesList("https://m.media-amazon.com/images/M/MV5BNTEwMWJlMWUtNGI3ZC00NzhmLWI1M2ItNGE1NTBiMjk5NmYyXkEyXkFqcGdeQXVyNDUzOTQ5MjY@._V1_.jpg", "Kasoor", "2", "Old Hindi Movie", 0))
        listItems.add(DumpedMoviesList("https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg", "Joker", "5", "Best English Movie", 0))
        listItems.add(DumpedMoviesList("https://m.media-amazon.com/images/M/MV5BNTkyOGVjMGEtNmQzZi00NzFlLTlhOWQtODYyMDc2ZGJmYzFhXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_.jpg", "3 Idiots", "5", "Best youth movie", 0))
        listAdapter.updateListItems(listItems)
    }
}