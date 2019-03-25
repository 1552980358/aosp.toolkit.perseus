package aosp.toolkit.perseus

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import aosp.toolkit.perseus.base.BaseManager
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.fragment_ready.*
import kotlinx.android.synthetic.main.fragment_welcome.*
import java.lang.Exception

/*
 * OsToolkit - Kotlin
 *
 * Date : 31/12/2018
 *
 * By   : 1552980358
 *
 */

/*
 * Modify
 *
 * 9/1/2019
 *
 * Re-write
 * 25 Mar 2019
 *
 */
class WelcomeActivity : AppCompatActivity() {
    private var t: Thread? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        BaseManager.getInstance().setWelcomeActivity(this)

        val option = (SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.decorView.systemUiVisibility = option

        val tabList = listOf(getString(R.string.welcome_tab_welcome), getString(R.string.welcome_tab_ready))
        val fragmentList = listOf(WelcomeFragment(), ReadyFragment())

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, tabList, fragmentList)
        tabLayout.setupWithViewPager(viewPager)

        t = Thread {
            val colors = listOf(
                ContextCompat.getColor(this, R.color.red),
                ContextCompat.getColor(this, R.color.orange),
                ContextCompat.getColor(this, R.color.yellow),
                ContextCompat.getColor(this, R.color.green),
                ContextCompat.getColor(this, R.color.cyan),
                ContextCompat.getColor(this, R.color.blue),
                ContextCompat.getColor(this, R.color.purple)
            )
            var i = 0
            while (true) {
                if (i != 6) {
                    runOnUiThread { root.setBackgroundColor(colors[i]) }
                    i++
                } else {
                    runOnUiThread { root.setBackgroundColor(colors[0]) }
                    i = 1
                }
                try {
                    Thread.sleep(2000)
                } catch (e: Exception) {
                    //
                }
            }
        }
        t!!.start()
    }

    override fun finish() {
        t!!.interrupt()
        super.finish()
    }

    class ViewPagerAdapter(
        fragmentManager: FragmentManager,
        private val tavList: List<String>,
        private val fragmentList: List<Fragment>
    ) : FragmentPagerAdapter(fragmentManager) {
        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(p0: Int): Fragment {
            return fragmentList[p0]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return tavList[position]
        }
    }

    class WelcomeFragment: Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_welcome, container, false)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            val array = resources.getStringArray(R.array.version)
            version.text = array[0]
            version.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        }
    }

    class ReadyFragment: Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_ready, container, false)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            root_S.setOnClickListener { startActivity(Intent(context!!, MainActivity::class.java)) }
        }
    }
}