package com.example.zipextectorapp.Activities

import android.content.DialogInterface
import android.content.Entity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.zipextectorapp.Models.AppModel
import com.example.zipextectorapp.Utills.*

import com.example.zipextectorapp.ViewModels.MainViewModel
import com.example.zipextectorapp.databinding.ActivityMainBinding
import com.example.zipextractor.model.MainEntity
import com.example.zipfilemanager.pageradapter.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pagerAdapter: FragmentPagerAdapter
    private var galleryViewModel: MainViewModel? = null
    private var fragmentPosition:Int=0
//    private var adapter = TestAdapter<AppModel> { binding, entity, position ->

//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        supportActionBar?.setDisplayShowTitleEnabled(false)
        setSupportActionBar(binding.toolbarHome)

        // setSupportActionBar(binding.toolbarHome)
        galleryViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        initTabLayout()
        // setupToolbar()

    }

    private fun initTabLayout() {
        with(binding) {
            val fragmentManager = supportFragmentManager
            pagerAdapter = FragmentPagerAdapter(fragmentManager, lifecycle)
            viewPager2Id.adapter = pagerAdapter
            viewPager2Id.offscreenPageLimit = 7
            tabLayoutId.addTab(tabLayoutId.newTab().setText("Images"))
            tabLayoutId.addTab(tabLayoutId.newTab().setText("Videos"))
            tabLayoutId.addTab(tabLayoutId.newTab().setText("Music"))
            tabLayoutId.addTab(tabLayoutId.newTab().setText("Apps"))
            tabLayoutId.addTab(tabLayoutId.newTab().setText("Compressed"))
            tabLayoutId.addTab(tabLayoutId.newTab().setText("Documents"))
            tabLayoutId.addTab(tabLayoutId.newTab().setText("General"))


            tabLayoutId.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewPager2Id.currentItem = tab!!.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })

            viewPager2Id.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabLayoutId.selectTab(tabLayoutId.getTabAt(position))
                    if (position == 0) {
                        fragmentPosition=0
                        binding.toolbarHome.title = "Images"
                    } else if (position == 1) {
                        fragmentPosition=1
                        binding.toolbarHome.title = "Videos"
                    } else if (position == 2) {
                        fragmentPosition=2
                        binding.toolbarHome.title = "Music"
                    } else if (position == 3) {
                        fragmentPosition=3
                        binding.toolbarHome.title = "Apps"
                    } else if (position == 4) {
                        fragmentPosition=4
                        binding.toolbarHome.title = "Compressed"
                    } else if (position == 5) {
                        fragmentPosition=5
                        binding.toolbarHome.title = "Documents"
                    } else if (position == 6) {
                        fragmentPosition=6
                        binding.toolbarHome.title = "Downloads"
                    }

                }
            })
        }
    }

    internal fun changeTitle(title: String) {
        binding.toolbarHome.title = title
    }

    private fun setupToolbar() {
        binding.toolbarHome.inflateMenu(com.example.zipextectorapp.R.menu.toolbarselection_nav_menu)
        try {
            setSupportActionBar(binding.toolbarHome)
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(com.example.zipextectorapp.R.menu.toolbarselection_nav_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.example.zipextectorapp.R.id.zipMenu_id -> {
                if (fragmentPosition==0){
                    openZipDialog?.invoke()
                }
                else if (fragmentPosition==1){

                }
                else if (fragmentPosition==2){

                }
                else if (fragmentPosition==3){

                }
                else if (fragmentPosition==4){
                       openZipDialogZipFragment?.invoke()
                }
                else if (fragmentPosition==5){
                    openZipDialog?.invoke()
                }
                else if (fragmentPosition==6){

                }

                return true
            }
            com.example.zipextectorapp.R.id.checkBox_Id -> {
                if (fragmentPosition==0){
                    checkBoxSelection?.invoke()
                }
                else if (fragmentPosition==1){

                }
                else if (fragmentPosition==2){

                }
                else if (fragmentPosition==3){

                }
                else if (fragmentPosition==4){
                    checkBoxSelectionZipFragment?.invoke()
                }
                else if (fragmentPosition==5){
                    checkBoxSelection?.invoke()
                }
                else if (fragmentPosition==6){

                }

                return true
            }
            com.example.zipextectorapp.R.id.crossAll_id -> {
                if (fragmentPosition==0){
                    unSelectAll?.invoke()
                }
                else if (fragmentPosition==1){

                }
                else if (fragmentPosition==2){

                }
                else if (fragmentPosition==3){

                }
                else if (fragmentPosition==4){
                    unSelectAllZipFragment?.invoke()
                }
                else if (fragmentPosition==5){
                    unSelectAll?.invoke()
                }
                else if (fragmentPosition==6){

                }

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    fun exitDialog() {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("EXIT APP ! ")
            .setMessage("Are you sure you want to close this App?")
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, which -> finishAffinity() })
            .setNegativeButton("No", null)
            .show()
    }

}