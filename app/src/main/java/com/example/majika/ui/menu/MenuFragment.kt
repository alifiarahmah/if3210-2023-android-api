package com.example.majika.ui.menu

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.majika.daftarmenu.MenuAPI
import com.example.majika.daftarmenu.MenuAdapter
import com.example.majika.daftarmenu.MenuClient
import com.example.majika.data.MenuItem
import com.example.majika.data.MenuList
import com.example.majika.data.MenuSection
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
    private var API_service:MenuAPI? = null
  //  private var menus:ArrayList<MenuItem> = ArrayList()

    private var adapter:MenuAdapter? = null

    private val foodParent = MenuSection(title="Makanan")
    private val drinkParent = MenuSection(title="Minuman")

    //sensor
    private lateinit var sensorManager:SensorManager
    private lateinit var sensor:Sensor

    private var temperature:Float = 0.0f;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val menuViewModel =
            ViewModelProvider(this)[MenuViewModel::class.java]

        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root:View = binding.root
        val recyclerView = binding.recyclerListMenu
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        //set api service
        API_service = MenuClient.getInstance().create(MenuAPI::class.java)
        //list section
        val sections = ArrayList<MenuSection>()
        sections.add(foodParent)
        sections.add(drinkParent)
        //set food menu
        adapter = MenuAdapter(container!!.context, sections)

        recyclerView.adapter = adapter

        //fetch data
        fetchData()

        //set search bar listener
        binding.searchBar.setEndIconOnClickListener{
            println(binding.searchBar.editText?.text.toString())
        }
        //setting sensor manager
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return root
    }

    private fun fetchData() {
        //data makanan
        val call = API_service!!.getFood()
        call.enqueue(object :Callback<MenuList>{
            override fun onResponse(call: Call<MenuList>, response: Response<MenuList>) {
               val foods = response.body()
                if(foods!=null){
                    foodParent.datas.addAll(foods.data)
                    Log.d("JALAN","harusnya mah jalan ieu")
                }
            }

            override fun onFailure(call: Call<MenuList>, t: Throwable) {
                Log.e("ERROR","Request gagal:"+t.localizedMessage)
            }

        })
        //data minuman
        val call_drink = API_service!!.getDrink()
        call_drink.enqueue(object :Callback<MenuList>{
            override fun onResponse(call: Call<MenuList>, response: Response<MenuList>) {
                val drinks = response.body()
                if(drinks!=null){
                    drinkParent.datas.addAll(drinks.data)
                    Log.d("JALAN","Dapetin minuman")
                }
            }

            override fun onFailure(call: Call<MenuList>, t: Throwable) {
                Log.e("ERROR","Request gagal:"+t.localizedMessage)
            }

        })
        adapter!!.notifyDataSetChanged()
    }

    private fun getTemperature() {
         //dapetin sensor
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        if(sensor==null){
            //gak support
            Log.e("ERROR","Sensor suhu tidak tersedia!")
        }
        else{
            //daftarin sensor
            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL)
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
        getTemperature()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //nilainya berubah
        try{
            temperature = event!!.values[0]
            Log.d("SENSOR","Temparature: ${temperature}")

        }catch(e:java.lang.Error){
            Log.e("SENSOR","Ada kesalahan saat mendapatkan data sensor")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("SENSOR","Akurasi berubah")
    }
}