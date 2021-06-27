package com.example.todo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.versionedparcelable.ParcelField
import com.example.todo.databinding.ActivityMainBinding
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val PREFS_NAME = "ToDoApp"
    lateinit var sharedPreferences: SharedPreferences

    private val itemList = ArrayList<Item>()
    private val allItems = ArrayList<Item>()
    private var prevMenuItem : MenuItem? = null
    private var currentFilter = -1
    private var currentSorting = 0

    companion object{
        const val addSorting = 0
        const val dateSorting = 1
        const val alphabetSorting = 2
    }

    @Serializable
    class Item constructor(val caption: String, val image: Int, val date: String, val time: String, var picked: Boolean)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.myToolbar)
        binding.recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recycler.adapter = CustomAdapter(itemList)
        touchHelper.attachToRecyclerView(binding.recycler)

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val myString : String? = sharedPreferences.getString("list", null)
        if (myString != null){
            allItems.addAll(Json.decodeFromString(myString))
        }
        filter()
    }

    override fun onStop() {
        val jsonList = Json.encodeToString(allItems)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("list", jsonList)
        editor.commit()
        super.onStop()
    }

    /*override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList("key", allItems)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.run {
            allItems.addAll(getParcelableArrayList<Item>("key") as ArrayList<Item>)
            itemList.addAll(allItems)
            prevMenuItem?.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
            filter()
        }
    }*/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        prevMenuItem = menu?.findItem(R.id.menuAll)

        return true
    }

    val touchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    var position: Int = viewHolder.adapterPosition

                    when (direction) {
                        ItemTouchHelper.RIGHT -> {
                            allItems.remove(itemList[position])
                            itemList.removeAt(position)
                            sort()
                        }
                    }
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addBackgroundColor(Color.parseColor("#000000"))
                            .addActionIcon(R.drawable.deleteicon)
                            .create()
                            .decorate()
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            })

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != R.id.menuSort){
            prevMenuItem?.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            prevMenuItem = item

            when (item.itemId){
                R.id.menuAll -> currentFilter = -1
                R.id.menuImportant -> currentFilter = CreateTask.IMPORTANT
                R.id.menuStudy -> currentFilter = CreateTask.STUDY
                R.id.menuSport -> currentFilter = CreateTask.SPORT
                R.id.menuFree -> currentFilter = CreateTask.FREE
                R.id.menuTrip -> currentFilter = CreateTask.TRIP
                R.id.menuOthers -> currentFilter = CreateTask.OTHER
            }
        }
        else{
            currentSorting = (currentSorting + 1) % 3
            when (currentSorting){
                addSorting -> item.setIcon(R.drawable.add)
                dateSorting -> item.setIcon(R.drawable.date)
                alphabetSorting -> item.setIcon(R.drawable.aa)
            }
        }
        filter()
        return true
    }

    fun click(view: View) {
        val myIntent = Intent(this, CreateTask::class.java)
        startActivityForResult(myIntent, 1)
    }

    fun clickDelete(view: View){
        var i = 0
        while (true){
            if (i == itemList.size){
                break
            }
            if (itemList[i].picked){
                allItems.remove(itemList[i])
                itemList.removeAt(i)
                i--
            }
            i++
        }
        filter()
    }

    fun filter(){
        itemList.clear()
        itemList.addAll(allItems)
        if (currentFilter != -1){
            var i = 0
            while (true){
                if (i == itemList.size){
                    break
                }
                if (itemList[i].image != currentFilter){
                    itemList.removeAt(i)
                    i--
                }
                i++
            }
        }
        sort()
    }

    fun sort(){
        when (currentSorting){
            dateSorting -> {
                Collections.sort(itemList, object : Comparator<Item> {
                    @SuppressLint("SimpleDateFormat")
                    override fun compare(o1: Item, o2: Item): Int {
                        val dateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm")
                        val firstDate = o1.date + " " + o1.time
                        val secDate = o2.date + " " + o2.time
                        System.out.println((dateFormat.parse(firstDate).toString() + " vs " + dateFormat.parse(secDate).toString()))
                        if (dateFormat.parse(firstDate).before(dateFormat.parse(secDate))){
                            return -1
                        }
                        else if (dateFormat.parse(firstDate).equals(dateFormat.parse(secDate))){
                            return o1.time.compareTo(o2.time)
                        }
                        else{
                            return 1
                        }
                    }
                })
            }
            alphabetSorting -> {
                Collections.sort(itemList, object : Comparator<Item> {
                    @SuppressLint("SimpleDateFormat")
                    override fun compare(o1: Item, o2: Item): Int {
                        return o1.caption.compareTo(o2.caption)
                    }
                })
            }
        }
        binding.recycler.adapter?.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val caption = data?.getStringExtra("opis")
        val image = data?.getIntExtra("ikonka", -1)
        val date = data?.getStringExtra("data")
        val time = data?.getStringExtra("godzina")

        if (data != null){
            if (caption == null || image == null || date == null || time == null){
                return
            }
            itemList.add(Item(caption, image, date, time, false))
            allItems.add(Item(caption, image, date, time, false))
            binding.recycler.adapter?.notifyDataSetChanged()
            filter()
        }
    }
}
