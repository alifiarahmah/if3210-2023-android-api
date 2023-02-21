package com.example.majika.ui.menu

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.daftarmenu.MenuAPI
import com.example.majika.daftarmenu.MenuAdapter
import com.example.majika.daftarmenu.MenuClient
import com.example.majika.data.*
import com.example.majika.data.MenuItem
import com.example.majika.databinding.FragmentMenuBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuFragment : Fragment(),SensorEventListener {

    private var _binding: FragmentMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //api clinet
    private var API_service: MenuAPI? = null
    //  private var menus:ArrayList<MenuItem> = ArrayList()

    private var adapter: MenuAdapter? = null

    private val foodParent = MenuSection(title = "Makanan")
    private val drinkParent = MenuSection(title = "Minuman")

    //sensor
    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null

    private var temperature: Float = 0.0f

    private var mMenu: Menu? = null

    private var sections =  ArrayList<MenuSection>()
    private lateinit var filteredSections:ArrayList<MenuSection>
    private lateinit var filteredFood:ArrayList<MenuSection>
    private lateinit var filteredDrink:ArrayList<MenuSection>

    //state
    private var recyclerViewState:Parcelable? = null

    private lateinit var recyclerView:RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
      //  clearView()
        //enable action menu (topbar)
        setHasOptionsMenu(true)
        val menuViewModel =
            ViewModelProvider(this)[MenuViewModel::class.java]

        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView = binding.recyclerListMenu
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        //set api service
        API_service = MenuClient.getInstance().create(MenuAPI::class.java)
        Log.d("SECTIONS","Kerender ulang gak lu? ${sections.size}")
        //list section
        if(sections.size<2){
            Log.d("SECTIONS","Napa gak 2 jir? ${sections.size}")
            sections.clear()
            sections.add(foodParent)
            sections.add(drinkParent)
        }
        //set food menu
        adapter = MenuAdapter(container!!.context, sections)

        recyclerView.adapter = adapter
        Log.d("JUMLAH ITEM","sebelum food fetcg: ${foodParent.datas.size}")
        //fetch data jika kosong
        if(foodParent.datas.size==0 && drinkParent.datas.size==0){
            fetchData()
        }
        Log.d("JUMLAH ITEM","sesudah food fetcg: ${foodParent.datas.size}")

        //set search bar listener
        binding.searchBar.setEndIconOnClickListener {
            println(binding.searchBar.editText?.text.toString())
            val text = binding.searchBar.editText?.text.toString()
           // clearView()
            if(text.length>0){
                searchMenu(text)
            }
            else{
                fetchData()
            }
        }
        //setting sensor manager
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return root
    }

    private fun clearView() {
        foodParent.datas.clear()
        drinkParent.datas.clear()
        if(adapter!=null) {
            adapter?.clear()
        }
    }

    private fun searchMenu(query: String) {
        //        nembak ulang

        //data makanan
        Log.d("SEARCH",query)
        val call = API_service!!.getMenu()
        call.enqueue(object : Callback<MenuList> {
            override fun onResponse(call: Call<MenuList>, response: Response<MenuList>) {
                val menus = response.body()
                if (menus != null) {
                    //hapus yang lama
                    if(adapter!=null){
                        adapter?.clear()
                    }
                    sections.add(foodParent)
                    sections.add(drinkParent)
                    //filter data
                    val tempArray = menus.data.filter { menu->menu.name?.lowercase()!!.contains(query.lowercase()) }
                    Log.v("ITEM",tempArray.size.toString())
                    //kosongin dulu
                    foodParent.datas.clear()
                    drinkParent.datas.clear()
                    //masukin ke tempat yang sesuai
                    for(menu in tempArray){
                        if(menu.type==MenuType.Food){
                            foodParent.datas.add(menu)
                        }
                        else if(menu.type==MenuType.Drink){
                            drinkParent.datas.add(menu)
                        }
                    }
                    Log.d("JALAN", "harusnya mah dah kefilter makanannya")
                }
            }

            override fun onFailure(call: Call<MenuList>, t: Throwable) {
                Log.e("ERROR", "Request gagal:" + t.localizedMessage)
            }

        })
        notifyChange()
    }

    private fun fetchData() {
        Log.d("FETCG","NGEFETCH MANING")
        //data makanan
        val call = API_service!!.getFood()
        call.enqueue(object : Callback<MenuList> {
            override fun onResponse(call: Call<MenuList>, response: Response<MenuList>) {
                val foods = response.body()
                if (foods != null) {
                    Log.d("JUMLAH ITEM","sebelum food fetcg: ${foodParent.datas.size}")
                    foodParent.datas.addAll(foods.data)
                    Log.d("JUMLAH ITEM","sesudah food fetcg: ${foodParent.datas.size}")
                    Log.d("JUMLAH ITEM","dqta: ${foods.data.size }")
                    Log.d("JALAN", "harusnya mah jalan ieu")
                }
            }

            override fun onFailure(call: Call<MenuList>, t: Throwable) {
                Log.e("ERROR", "Request gagal:" + t.localizedMessage)
            }

        })
        //data minuman
        val call_drink = API_service!!.getDrink()
        call_drink.enqueue(object : Callback<MenuList> {
            override fun onResponse(call: Call<MenuList>, response: Response<MenuList>) {
                val drinks = response.body()
                if (drinks != null) {
                    drinkParent.datas.addAll(drinks.data)
                    Log.d("JALAN", "Dapetin minuman")
                }
            }

            override fun onFailure(call: Call<MenuList>, t: Throwable) {
                Log.e("ERROR", "Request gagal:" + t.localizedMessage)
            }

        })
        notifyChange()

    }

    private fun notifyChange() {
        adapter!!.notifyDataSetChanged()
    }

    private fun getTemperature() {
        //dapetin sensor
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        if (sensor == null) {
            //gak support
            Log.e("ERROR", "Sensor suhu tidak tersedia!")
            //show error
            //https://stackoverflow.com/questions/14400298/best-practice-for-displaying-error-messages
            Toast.makeText(context,"Sensor Suhu Tidak Tersedia di Perangkat!",Toast.LENGTH_SHORT).show()
        } else {
            //daftarin sensor
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //credit: https://jtmuller5.medium.com/a-quick-take-on-reading-sensor-values-in-android-studio-98e47343d42e
    //kalau di pause stop sensor
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    //lanjutin sensor kalau continue
    override fun onResume() {
        super.onResume()
        //restore state
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
        getTemperature()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //nilainya berubah
        try {
            temperature = event!!.values[0]
         //   Log.d("SENSOR", "Temparature: $temperature")
            try {
                //reset temperature di top bar
                activity?.invalidateOptionsMenu()

            } catch (e: java.lang.Error) {
                Log.e("SENSOR", e.localizedMessage)
            }

        } catch (e: java.lang.Error) {
            Log.e("SENSOR", "Ada kesalahan saat mendapatkan data sensor")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("SENSOR", "Akurasi berubah")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    //    Log.d("SENSOR", "kebentuk harusnya")
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        if (mMenu == null) {
            mMenu = menu
        }
        //dapetin item dari top nya
        val item = menu?.findItem(R.id.top_main_menu)
        //cari text viewnya
        val textView = item?.actionView as TextView
        //set text
        textView.text = "$temperature \u2103"
        //set size
        textView.textSize = 20.0f
    //    Log.d("SENSOR", "keganti cuk harusnya")
    }

    //simpan state kalau ganti halaman
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("RECYCLER_VIEW_STATE",recyclerView.layoutManager?.onSaveInstanceState())
        //update warna
    }

    //load state saat kembali
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        recyclerViewState = savedInstanceState?.getParcelable("RECYCLER_VIEW_STATE")
    }

}