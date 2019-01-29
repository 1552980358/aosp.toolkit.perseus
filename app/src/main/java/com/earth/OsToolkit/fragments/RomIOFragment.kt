package com.earth.OsToolkit.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.AppCompatSeekBar
import android.util.Log
import android.view.*
import android.widget.SeekBar

import com.earth.OsToolkit.R
import com.earth.OsToolkit.base.BaseIndex.*
import com.earth.OsToolkit.base.BaseManager
import com.earth.OsToolkit.base.BaseKotlinOperation.Companion.checkFilePresent
import com.earth.OsToolkit.base.BaseKotlinOperation.Companion.readFile

import kotlinx.android.synthetic.main.fragment_romio.*
import kotlinx.android.synthetic.main.fragment_romio_emmc.*
import kotlinx.android.synthetic.main.fragment_romio_ufs.*
import java.lang.Exception

/*
 * OsToolkit - Kotlin
 *
 * Date : 19 Jan 2019
 *
 * By   : 1552980358
 *
 */

class RomIOFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseManager.getInstance().setRomIOFragment(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_romio, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val romIOeMMCFragment = RomIOeMMCFragment()
        val romIOUFSFragment = RomIOUFSFragment()

        romIOeMMCFragment.getParent(this)
        romIOUFSFragment.getParent(this)

        val fragments = listOf(romIOeMMCFragment, romIOUFSFragment)
        val tabs = listOf("eMMC", "UFS")
        viewPager.adapter = ViewPagerAdapter(fragmentManager, fragments, tabs)
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //BaseManager.getInstance().restartRomIOFragment()
    }

    class ViewPagerAdapter(fragmentManager: FragmentManager?,
                           private var fragmentList: List<Fragment>,
                           private var tabList: List<String>) : FragmentPagerAdapter(fragmentManager) {
        override fun getItem(p0: Int): Fragment {
            return fragmentList[p0]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return tabList[position]
        }
    }

    class RomIOeMMCFragment : Fragment() {
        private var parent: RomIOFragment? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_romio_emmc, container, false)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            emmc_addrandom.initSwitchCompact(parent, io_random_emmc, type_shell, index_romio, IO_RANDOM_EMMC)
            emmc_iostats.initSwitchCompact(parent, io_iostats_emmc, type_shell, index_romio, IO_IOSTATS_EMMC)
            emmc_nomerges.initSwitchCompact(parent, io_nomerges_emmc, type_shell, index_romio, IO_NOMERGES_EMMC)

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val seekBar : AppCompatSeekBar = view.findViewById(R.id.seekBar)
            if (checkFilePresent("/sys/block/mmcblk0/queue/rq_affinity")) {
                seekBar.progress = if (readFile("/sys/block/mmcblk0/queue/rq_affinity") == "Fail") {1} else {readFile("/sys/block/mmcblk0/queue/rq_affinity").toInt()}
                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        try {
                            val process = Runtime.getRuntime().exec(arrayOf("su", "-c",
                                "echo", "\"" + progress + "\"", ">", "/sys/block/mmcblk0/queue/rq_affinity"))
                            Log.i("rq_affinity", process.waitFor().toString())
                        } catch (e : Exception) {
                            e.printStackTrace()
                        }
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {

                    }
                })
            } else {
                seekBar.isEnabled = false
            }
        }

        fun getParent(parent: RomIOFragment){
            this.parent = parent
        }
    }

    class RomIOUFSFragment : Fragment() {
        private var parent: RomIOFragment? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_romio_ufs, container, false)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            ufs_addrandom.initSwitchCompact(parent, io_random_ufs, type_shell, index_romio, IO_RANDOM_UFS)
            ufs_iostats.initSwitchCompact(parent, io_iostats_ufs, type_shell, index_romio, IO_IOSTATS_UFS)
            ufs_nomerges.initSwitchCompact(parent, io_nomerges_ufs, type_shell, index_romio, IO_NOMERGES_UFS)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val seekBar : AppCompatSeekBar = view.findViewById(R.id.seekBar)
            if (checkFilePresent("/sys/block/sda/queue/rq_affinity")) {
                seekBar.progress = readFile("/sys/block/sda/queue/rq_affinity").toInt()
                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        try {
                            val process = Runtime.getRuntime().exec(arrayOf("su", "-c",
                                "echo", "\"" + progress + "\"", ">", "/sys/block/sda/queue/rq_affinity"))
                            Log.i("rq_affinity", process.waitFor().toString())
                        } catch (e : Exception) {
                            e.printStackTrace()
                        }
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                })
            } else {
                seekBar.isEnabled = false
            }
        }

        fun getParent(parent: RomIOFragment) {
            this.parent = parent
        }
    }
}