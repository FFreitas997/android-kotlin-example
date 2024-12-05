package com.example.happyplaces.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happyplaces.HappyPlaceApplication
import com.example.happyplaces.adapter.HappyPlacesAdapter
import com.example.happyplaces.adapter.OnItemClickListener
import com.example.happyplaces.adapter.utils.SwipeToDeleteCallback
import com.example.happyplaces.database.HappyPlaceEntity
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.repository.DefaultHappyPlaceRepository
import com.example.happyplaces.repository.HappyPlacesRepository
import com.example.happyplaces.utils.Constants.EXTRA_PLACE_DETAILS
import com.example.happyplaces.utils.Constants.REQUEST_CODE_ADD_PLACE
import com.example.happyplaces.adapter.utils.SwipeToEditCallback
import com.example.happyplaces.utils.HappyPlaceMapper
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var layout: ActivityMainBinding? = null
    private lateinit var repository: HappyPlacesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        layout = ActivityMainBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(layout?.root!!) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        layout?.fab?.setOnClickListener {
            Intent(this, CreateHappyPlaceActivity::class.java)
                .also { startActivity(it) }
        }

        val application = application as HappyPlaceApplication
        repository = DefaultHappyPlaceRepository(application.db.happyPlaceDao())

        lifecycleScope.launch {
            repository
                .readHappyPlaces()
                .collect { handleResultFromDB(it) }
        }
    }

    private fun handleResultFromDB(list: List<HappyPlaceEntity>) {
        layout?.emptyView?.visibility = View.GONE
        layout?.recyclerView?.visibility = View.VISIBLE
        if (list.isEmpty()) {
            layout?.emptyView?.visibility = View.VISIBLE
            layout?.recyclerView?.visibility = View.GONE
            return
        }
        val adapter = HappyPlacesAdapter(list.toMutableList())
        layout?.recyclerView?.adapter = adapter
        layout?.recyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onClick(position: Int) {
                if (position < 0 || list.isEmpty()) return

                val itemID = list[position].id ?: return

                Intent(this@MainActivity, HappyPlaceDetails::class.java)
                    .also { it.putExtra(EXTRA_PLACE_DETAILS, itemID) }
                    .also { startActivity(it) }
            }
        })

        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(position: Int) {
                val rvAdapter = layout?.recyclerView?.adapter as HappyPlacesAdapter
                rvAdapter.notifyEditItem(this@MainActivity, position, REQUEST_CODE_ADD_PLACE)
            }
        }

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(position: Int) {
                val rvAdapter = layout?.recyclerView?.adapter as HappyPlacesAdapter
                val itemDeleted = HappyPlaceMapper.mapEntityToModel(rvAdapter.removeAt(position))
                lifecycleScope
                    .launch {
                        repository.deleteHappyPlace(itemDeleted)
                        repository
                            .readHappyPlaces()
                            .collect { handleResultFromDB(it) }
                    }
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(layout?.recyclerView)
        deleteItemTouchHelper.attachToRecyclerView(layout?.recyclerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        layout = null
    }
}