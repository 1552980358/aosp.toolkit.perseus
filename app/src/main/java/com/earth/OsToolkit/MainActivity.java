package com.earth.OsToolkit;

import android.content.SharedPreferences;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.*;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.Toast;

import com.earth.OsToolkit.Fragment.ChargingFragment;
import com.earth.OsToolkit.Fragment.MainFragment;
import com.earth.OsToolkit.Fragment.UpdateDialogFragment;
import com.earth.OsToolkit.Working.BaseClass.Checking;

import java.lang.Process;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	FragmentManager fragmentManager = getSupportFragmentManager();

	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initUI();
		checkUpdate();
	}

	public void initUI() {
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.main_fragment, new MainFragment()).commit();

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}

	public void checkUpdate() {
		Checking.checkVersion(this);
		String PackageVersionCode = null;
		try {
			PackageVersionCode = getPackageManager().getPackageInfo(getPackageName(),0).versionName;
		}catch (Exception e) {
			e.printStackTrace();
		}

		Log.i("PackageVersionCode",PackageVersionCode);

		SharedPreferences sharedPreferences = getSharedPreferences("UpdateSP",MODE_PRIVATE);
		if (!sharedPreferences.getString("updateVersion","fail")
				.equals(PackageVersionCode)) {
			UpdateDialogFragment updateDialogFragment = new UpdateDialogFragment();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			updateDialogFragment.show(fragmentTransaction, "updateDialogFragment");
		}
	}




	@Override
	public void onBackPressed() {
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	@SuppressWarnings("all")
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		Process process;
		switch (id) {
			case R.id.action_reboot:
				try {
					Toast.makeText(this, getString(R.string.reboot_getRoot),
							Toast.LENGTH_SHORT).show();
					process = Runtime.getRuntime().exec("su -c reboot");
					Log.e("Reboot", "reboot");
				} catch (Exception e) {
					Log.e("Reboot", "reboot");
					Toast.makeText(this, getString(R.string.reboot_fail), Toast.LENGTH_SHORT).show();
				}
				return true;
			case R.id.action_recovery:
				try {
					Toast.makeText(this, getString(R.string.reboot_getRoot),
							Toast.LENGTH_SHORT).show();
					process = Runtime.getRuntime().exec(new String[]{"su -c ", "reboot recovery"});
					Log.e("Reboot", "reboot rec");
				} catch (Exception e) {
					Log.e("Reboot", "reboot rec");
					Toast.makeText(this, getString(R.string.reboot_fail), Toast.LENGTH_SHORT).show();
				}
				return true;
			case R.id.action_soft:
				try {
					Toast.makeText(this, getString(R.string.reboot_getRoot),
							Toast.LENGTH_SHORT).show();
					process = Runtime.getRuntime().exec(new String[]{"su -c", "killall zygote"});
					Log.e("Reboot", "killall zygote");
				} catch (Exception e) {
					Log.e("Reboot", "killall zygote");
					Toast.makeText(this, getString(R.string.reboot_fail), Toast.LENGTH_SHORT).show();
				}
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
		Fragment fragment = new MainFragment();
		int title = R.string.app_name;
		switch (id) {
			case R.id.nav_main:
				fragment = new MainFragment();
				break;
			case R.id.nav_charging:
				title = R.string.nav_charging;
				fragment = new ChargingFragment();
				break;
		}

		toolbar.setTitle(title);
		ft.replace(R.id.main_fragment, fragment).commit();

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

}
