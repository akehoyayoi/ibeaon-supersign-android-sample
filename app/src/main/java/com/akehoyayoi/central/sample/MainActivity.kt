package com.akehoyayoi.central.sample

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.altbeacon.beacon.*

class MainActivity : AppCompatActivity(), IActivityLifeCycle, BeaconConsumer {

    private val TAG: String
        get() = MainActivity::class.java.simpleName

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }

    private val mLifeCycle: ActivityLifeCycle = ActivityLifeCycle(this)

    private lateinit var mBeaconManager: BeaconManager

    private val uuidString: String = "01122334-4556-6778-899A-ABBCCDDEEFF0"
    private val uuid = Identifier.parse(uuidString)
    private fun mRegion() = Region(packageName, uuid, null, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycle.addObserver(mLifeCycle)

        val syncButton = findViewById<Button>(R.id.syncButton)
        syncButton.setOnClickListener { view ->
            Toast.makeText(this@MainActivity, "Tapped", Toast.LENGTH_SHORT).show()
            
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(mLifeCycle)
    }

    override fun onCreated() {
        // BeaconManagerを取得する
        mBeaconManager = BeaconManager.getInstanceForApplication(this)
        // iBeaconの受信設定：iBeaconのフォーマットを登録する
        mBeaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconUtil.IBEACON_FORMAT))
    }

    /**
     * フォアグラウンド移行時
     */
    override fun onConnected() {
        // Beaconのイベント設定
        mBeaconManager.bind(this@MainActivity)
    }

    /**
     * バックグラウンド移行時
     */
    override fun onDisconnect() {
        // Beaconのイベント解除
        mBeaconManager.unbind(this@MainActivity)
    }

    override fun onBeaconServiceConnect() {

        // Scan間隔を調整
        mBeaconManager.foregroundBetweenScanPeriod = 1000L

        //Beacon領域の入退場を検知するイベント設定
        mBeaconManager.addMonitorNotifier(mMonitorNotifier)

        // レンジングのイベント設定
        mBeaconManager.addRangeNotifier(mRangeNotifier)

        try {
            // 入退場検知イベントの登録
            mBeaconManager.startMonitoringBeaconsInRegion(mRegion())
        } catch (e: Throwable) {
            Log.e(TAG, "Exception", e)
        }
    }

    private val mMonitorNotifier = object : MonitorNotifier {
        override fun didEnterRegion(region: Region) {
            //レンジングの開始
            mBeaconManager.startRangingBeaconsInRegion(mRegion());
        }

        override fun didExitRegion(region: Region) {
            //レンジングの終了
            mBeaconManager.stopRangingBeaconsInRegion(mRegion());
        }

        override fun didDetermineStateForRegion(i: Int, region: Region) {
        }
    }

    private val mRangeNotifier = RangeNotifier { beacons, region ->
        val kuusha = findViewById<TextView>(R.id.kuussha)
        val geisha = findViewById<TextView>(R.id.geisha)
        val jissya = findViewById<TextView>(R.id.jissya)
        val shiharai = findViewById<TextView>(R.id.shiharai)
        val warimashi = findViewById<TextView>(R.id.warimashi)
        val bleDebug = findViewById<TextView>(R.id.bleDebugView)

        // 検出したビーコンの情報を全部Logに書き出す
        for (beacon in beacons) {
            // TODO : データの取得(取得後にデータがある場合に表示を切り替える)

            if(beacon.id1 == uuid) {
                kuusha.visibility = android.view.View.INVISIBLE
                geisha.visibility = android.view.View.INVISIBLE
                jissya.visibility = android.view.View.INVISIBLE
                shiharai.visibility = android.view.View.INVISIBLE
                warimashi.visibility = android.view.View.INVISIBLE
                when(beacon.id3.toString()) {
                    "1" -> kuusha.visibility = android.view.View.VISIBLE
                    "2" -> jissya.visibility = android.view.View.VISIBLE
                    "4" -> warimashi.visibility = android.view.View.VISIBLE
                    "8" -> geisha.visibility = android.view.View.VISIBLE
                    "16" -> shiharai.visibility = android.view.View.VISIBLE
                }
                bleDebug.text = "UUID:" + beacon.id1 + ", major:" + beacon.id2 +
                        ", minor:" + beacon.id3 + ", Distance:" + beacon.distance +
                        ",RSSI" + beacon.rssi + ", TxPower" + beacon.txPower
            }
        }
    }
}
