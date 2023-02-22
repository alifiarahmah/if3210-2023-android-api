package com.example.majika

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.majika.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_camera,
                R.id.navigation_branch,
                R.id.navigation_menu,
                R.id.navigation_cart
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
        //cek apakah sudah di generate fragmentnya, biar gak rerender mulu
//        navView.setOnNavigationItemSelectedListener {menuItem->
//            val fragment = when(menuItem.itemId){
//                R.id.navigation_camera->{
//                    //cek apakah udah ada kalau gak ada bikin baru
//                    supportFragmentManager.findFragmentByTag("camera")?:
//                    CameraFragment().also{
//                        //daftarin
//                        supportFragmentManager.beginTransaction()
//                            .add(R.id.nav_host_fragment_activity_main,it,"camera")
//                        //    .hide(it)
//                            .commit()
//                    }
//                }
//
//                R.id.navigation_branch->{
//                    supportFragmentManager.findFragmentByTag("branch")?:
//                    BranchFragment().also{
//                        //daftarin
//                        supportFragmentManager.beginTransaction()
//                            .add(R.id.nav_host_fragment_activity_main,it,"branch")
//                        //    .hide(it)
//                            .commit()
//                    }
//                }
//                R.id.navigation_menu->{
//                    supportFragmentManager.findFragmentByTag("menu")?:
//                    MenuFragment().also{
//                        //daftarin
//                        supportFragmentManager.beginTransaction()
//                            .add(R.id.nav_host_fragment_activity_main,it,"menu")
//                        //    .hide(it)
//                            .commit()
//                    }
//                }
//                R.id.navigation_cart ->{
//                    supportFragmentManager.findFragmentByTag("cart")?:
//                    CartFragment().also{
//                        //daftarin
//                        supportFragmentManager.beginTransaction()
//                            .add(R.id.nav_host_fragment_activity_main,it,"cart")
//                        //    .hide(it)
//                            .commit()
//                    }
//                }
//                else->null
//            }
//            Log.d("FRAGMENT",fragment.toString());
//            //aktifin fragment yang dipilih
//            fragment?.let {
//                //hide fragment lama
//                Log.d("FRAGMENT",supportFragmentManager.fragments.toString());
//                supportFragmentManager.fragments.forEach { fragment_->
//                    if(fragment_!=fragment && fragment !is NavHostFragment){
//                        //selain yang mau ditambahin, maka dihide
//                        supportFragmentManager.beginTransaction()
//                        //    .hide(fragment_)
//                            .commit()
//                    }
//                }
//                //tampilin fragment baru
//                supportFragmentManager.beginTransaction()
//                    .show(it)
//                    .commit()
//                true
//            }?:false
//        }
//    }
}