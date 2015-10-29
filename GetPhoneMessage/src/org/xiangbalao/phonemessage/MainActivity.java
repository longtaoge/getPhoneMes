package org.xiangbalao.phonemessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.xiangbalao.phongmessage.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * 【小尛龙工作室】作品秀 http://www.apkbus.com/android-143303-1-1.html
 * 
 * Android开发技术交流群：剩者为王 （群号：314447894），欢迎您的加入。
 * 
 * @author 小尛龙
 */
public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.button3).setOnClickListener(this);
		findViewById(R.id.button4).setOnClickListener(this);
		findViewById(R.id.button5).setOnClickListener(this);
		findViewById(R.id.button6).setOnClickListener(this);
		findViewById(R.id.button7).setOnClickListener(this);
		findViewById(R.id.button8).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 手机定位
		case R.id.button1:
			setCriteria();
			break;
		// IP和MAC地址
		case R.id.button2:
			Toast.makeText(
					MainActivity.this,
					"hostIP:" + getLocalIpAddress2() + "  macAddr:"
							+ getLocalMacAddress(), Toast.LENGTH_LONG).show();
			break;
		// SD卡存储信息
		case R.id.button3:
			getSDCardInfo();
			break;
		// 手机分辨率
		case R.id.button4:
			getDisplayMetrics();
			break;
		// 网络是否连接
		case R.id.button5:
			isNetConnecting();
			break;
		// 手机内存
		case R.id.button6:
			getSystemMemory();
			break;
		// 手机CUP
		case R.id.button7:
			getCpuInfo();
			break;
		// 手机IMEI
		case R.id.button8:
			getImei();
			break;

		default:
			break;
		}

	}

	/**
	 * 手机定位start
	 */
	private void setCriteria() {
		LocationManager locationManager;
		String serviceName = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) getSystemService(serviceName);
		// String provider = LocationManager.GPS_PROVIDER;

		Criteria criteria = new Criteria();
		// 设置定位的精度
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);// 获取大体的位置
		criteria.setAltitudeRequired(false);// 海拔信息
		criteria.setBearingRequired(false);// 海拔信息
		criteria.setCostAllowed(true);// 允许产生费用
		criteria.setPowerRequirement(Criteria.POWER_LOW);// 低功耗
		String provider = locationManager.getBestProvider(criteria, true);// 获取一个最符合查询条件的位置提供者

		Location location = locationManager.getLastKnownLocation(provider);
		updateWithNewLocation(location);
		locationManager.requestLocationUpdates(provider, 2000, 10,
				locationListener);// 注册 位置改变的监听器
	}

	private final LocationListener locationListener = new LocationListener() {
		// 用户位置改变的时候 的回调方法
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		// 关闭
		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		// gps ,打开
		public void onProviderEnabled(String provider) {
		}

		// 状态改变
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private void updateWithNewLocation(Location location) {
		String latLongString;
		// 获取到用户的纬度
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			latLongString = "纬度:" + lat + "\n" + "经度:" + lng;
		} else {
			latLongString = "无法获取地理信息";
		}
		Toast.makeText(MainActivity.this, latLongString, Toast.LENGTH_LONG)
				.show();
	}

	/**
	 * 获取手机ip start
	 */
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
			// Log.e("ifo", ex.toString());
		}
		return "";
	}

	// 获取手机ip method-2
	public String getLocalIpAddress2() {
		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);
		return ip;
	}

	private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

	/**
	 * mac地址 start
	 */
	public String getLocalMacAddress() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();

		return info.getMacAddress();
	}

	/**
	 * 获取Android手机中SD卡存储信息 获取剩余空间
	 */
	public void getSDCardInfo() {
		// 需要判断手机上面SD卡是否插好，如果有SD卡的情况下，我们才可以访问得到并获取到它的相关信息，当然以下这个语句需要用if做判断
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 取得sdcard文件路径
			File path = Environment.getExternalStorageDirectory();
			StatFs statfs = new StatFs(path.getPath());
			// 获取block的SIZE
			long blocSize = statfs.getBlockSize();
			// 获取BLOCK数量
			long totalBlocks = statfs.getBlockCount();
			// 空闲的Block的数量
			long availaBlock = statfs.getAvailableBlocks();
			// 计算总空间大小和空闲的空间大小
			// 存储空间大小跟空闲的存储空间大小就被计算出来了。
			long availableSize = blocSize * availaBlock;
			// (availableBlocks * blockSize)/1024 KIB 单位
			// (availableBlocks * blockSize)/1024 /1024 MIB单位
			long allSize = blocSize * totalBlocks;
			Toast.makeText(
					MainActivity.this,
					"可用：" + availableSize / 1024 / 1024 / 1024 + "GB" + "  总共："
							+ allSize / 1024 / 1024 / 1024 + "GB",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(MainActivity.this, "SD卡不可用", Toast.LENGTH_LONG)
					.show();
		}

	}

	public void getDisplayMetrics() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Toast.makeText(
				MainActivity.this,
				"分辨率：" + displayMetrics.widthPixels + "x"
						+ displayMetrics.heightPixels, Toast.LENGTH_LONG)
				.show();
	}

	/**
	 * 当前网络是否连接
	 * 
	 */

	public void isNetConnecting() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnected()) {
			// info.setConnected(false);
			Toast.makeText(MainActivity.this, "当前无网络", Toast.LENGTH_LONG)
					.show();
		} else {
			// info.setConnected(true);
			Toast.makeText(MainActivity.this, "当前网络正常", Toast.LENGTH_LONG)
					.show();
		}

	}

	/**
	 * 获取手机可用内存和总内存
	 * 
	 */
	private void getSystemMemory() {
		String availMemory = getAvailMemory();
		String totalMemory = getTotalMemory();

		Toast.makeText(MainActivity.this,
				"可用内存:" + availMemory + "\n" + "总内存:" + totalMemory,
				Toast.LENGTH_LONG).show();

	}

	// 手机的内存信息主要在/proc/meminfo文件中，其中第一行是总内存，而剩余内存可通过ActivityManager.MemoryInfo得到。

	private String getAvailMemory() {// 获取android当前可用内存大小
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内存
		return Formatter.formatFileSize(getBaseContext(), mi.availMem);// 将获取的内存大小规格化
	}

	private String getTotalMemory() {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}

			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();

		} catch (IOException e) {
		}
		return Formatter.formatFileSize(getBaseContext(), initial_memory);// Byte转换为KB或者MB，内存大小规格化
	}

	/**
	 * 获取手机CPU信息
	 */

	private void getCpuInfo() {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		String[] cpuInfo = { "", "" }; // 1-cpu型号 //2-cpu频率
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
			}
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			cpuInfo[1] += arrayOfString[2];
			localBufferedReader.close();
		} catch (IOException e) {
		}
		Toast.makeText(MainActivity.this,
				"cpu型号:" + cpuInfo[0] + "\n" + "cpu频率:" + cpuInfo[1],
				Toast.LENGTH_LONG).show();
	}

	/**
	 * 手机IMEI
	 */
	private void getImei() {
		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imei = mTelephonyMgr.getDeviceId();
		Toast.makeText(MainActivity.this, "imei:" + imei, Toast.LENGTH_LONG)
				.show();
	}

}
