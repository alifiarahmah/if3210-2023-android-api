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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.daftarmenu.MenuAPI
import com.example.majika.daftarmenu.MenuAdapter
import com.example.majika.daftarmenu.MenuClient
import com.example.majika.data.MenuList
import com.example.majika.data.MenuSection
import com.example.majika.data.MenuType
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

    //lifedata
    private val foodLiveData:MutableLiveData<List<com.example.majika.data.MenuItem>> = MutableLiveData<List<com.example.majika.data.MenuItem>>()
    private val drinkLiveData:MutableLiveData<List<com.example.majika.data.MenuItem>> = MutableLiveData<List<com.example.majika.data.MenuItem>>()

    //state
    private var recyclerViewState:Parcelable? = null

    private lateinit var recyclerView:RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
      //  clearView()
//        //set defsault value
        if(foodLiveData.value==null){
            foodLiveData.value = ArrayList<com.example.majika.data.MenuItem>()
        }
        if(drinkLiveData.value==null){
            drinkLiveData.value = ArrayList<com.example.majika.data.MenuItem>()
        }
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
            if(foodLiveData.value!!.isNotEmpty()){
                sections.add(foodParent)
            }
            if(drinkLiveData.value!!.isNotEmpty()){
                sections.add(drinkParent)
            }
        }
        //set food menu
        adapter = MenuAdapter(container!!.context, sections)

        recyclerView.adapter = adapter
        //Log.d("JUMLAH ITEM","sebelum food fetcg: ${foodLiveData.value!!.size}")
        //fetch data jika kosong
        if(foodLiveData.value!!.isEmpty() && drinkLiveData.value!!.isEmpty()){
            fetchData()
        }
      //  Log.d("JUMLAH ITEM","sesudah food fetcg: ${foodLiveData.value!!.size}")

        //set search bar listener
        binding.searchBar.setEndIconOnClickListener {
            println(binding.searchBar.editText?.text.toString())
            val text = binding.searchBar.editText?.text.toString()
           // clearView()
            if(text.length>0){
                //clear arraynya dulu
                searchMenu(text)
            }
            else{
                fetchData()
            }
        }
        //set life data
        foodLiveData.observe(viewLifecycleOwner, Observer {foods->
            if(foodParent.datas.isNotEmpty()){
                foodParent.datas.clear()
            }
            foodParent.datas.addAll(foods)
            notifyChange()
        })
        drinkLiveData.observe(viewLifecycleOwner, Observer {drinks->
            if(drinkParent.datas.isNotEmpty()){
                drinkParent.datas.clear()
            }
            drinkParent.datas.addAll(drinks)
            notifyChange()
        })
        //setting sensor manager
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return root
    }


    private fun searchMenu(query: String) {
        //        nembak ulang
        sections.clear()
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
                    //filter data
                    val tempArray = menus.data.filter { menu->menu.name?.lowercase()!!.contains(query.lowercase()) }
                    Log.v("ITEM",tempArray.size.toString())
                    //kosongin dulu
                    foodParent.datas.clear()
                    drinkParent.datas.clear()
                    //masukin ke tempat yang sesuai
                    val tempFood = ArrayList<com.example.majika.data.MenuItem>()
                    val tempDrink = ArrayList<com.example.majika.data.MenuItem>()
                    for(menu in tempArray){
                        if(menu.type==MenuType.Food){
                            tempFood.add(menu)
                      //      foodParent.datas.add(menu)
                        }
                        else if(menu.type==MenuType.Drink){
                         //   drinkParent.datas.add(menu)
                            tempDrink.add(menu)
                        }
                    }
                    //update lifedata
                    foodLiveData.value = tempFood
                    drinkLiveData.value = tempDrink
                    //cek apakah ada
                    if(foodLiveData.value!!.isNotEmpty()){
                        //tampilin
                        sections.add(foodParent)
                    }
                    if(drinkLiveData.value!!.isNotEmpty()){
                        ///tampilin
                        sections.add(drinkParent)
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
        //bersihkan data dulu
        foodParent.datas.clear()
        drinkParent.datas.clear()
        sections.clear()
        //data makanan
        val call = API_service!!.getFood()
        call.enqueue(object : Callback<MenuList> {
            override fun onResponse(call: Call<MenuList>, response: Response<MenuList>) {
                val foods = response.body()
                if (foods != null) {
                    Log.d("JUMLAH ITEM", "sebelum food fetcg: ${foodLiveData.value!!.size}")
                    //   foodParent.datas.addAll(foods.data)
                    foodLiveData.value = foods.data
                    Log.d("JUMLAH ITEM", "sesudah food fetcg: ${foodLiveData.value!!.size}")
                    Log.d("JUMLAH ITEM", "dqta: ${foods.data.size}")
                    Log.d("JALAN", "harusnya mah jalan ieu")
                    //cek apakah ada
                    if (foodLiveData.value!!.isNotEmpty()) {
                        //kalau gak ada gak usah ditampilin
                        sections.add(foodParent)
                    }
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
                //    drinkParent.datas.addAll(drinks.data)
                    drinkLiveData.value = drinks.data
                    Log.d("JALAN", "Dapetin minuman")
                    if(drinkLiveData.value!!.isNotEmpty()){
                        //kalau gak ada gak usah ditampilin
                        sections.add(drinkParent)
                    }
                }
            }

            override fun onFailure(call: Call<MenuList>, t: Throwable) {
                Log.e("ERROR", "Request gagal:" + t.localizedMessage)
            }

        })
        Log.v("SECTIONS","Jumlah segmen : ${sections.size}")
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
    }

    //simpan state kalau ganti halaman
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(recyclerView.layoutManager!=null){
            outState.putParcelable("RECYCLER_VIEW_STATE",recyclerView.layoutManager?.onSaveInstanceState())
            //update warna
        }
    }

    //load state saat kembali
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        recyclerViewState = savedInstanceState?.getParcelable("RECYCLER_VIEW_STATE")
    }

}