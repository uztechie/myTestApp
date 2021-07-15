package com.example.datepicker

import android.Manifest
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.datepicker.dialogs.BirthYearDialog
import com.example.datepicker.dialogs.GenderDialog
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.util.*
import kotlin.random.Random


@RuntimePermissions
class MainActivity : AppCompatActivity() {
    var TAG: String = "MyTAG"
    var REQUEST_CODE = 11
    var bluetoothAdapter: BluetoothAdapter? = null
    private var cma_address: String? = null

    private val mHandler = Handler()
    private var mScanning = false
    private val SCAN_PERIOD: Long = 5000

    private var mDeviceAddress: String? = null

    var myDialog: Dialog? = null
    var myConnection: Dialog? = null
    var findDeviceDialog: Dialog? = null

    private var btnCancelConnection: Button? = null
    private var btnGo: Button? = null
    var mServiceBound = false


    private val mLeDevices = ArrayList<BLEInfoSet>()
    private var mListAddress: ListView? = null
    private var txtContents: TextView? = null

    private lateinit var genderDialog: GenderDialog
    private lateinit var birthYearDialog: BirthYearDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bluetoothManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        myDialog = Dialog(this)
        findDeviceDialog = Dialog(this)


        genderDialog = GenderDialog(this, object : GenderDialog.Listener{
            override fun onInit() {
                PreferenceHandler.setStrPreferences(
                    this@MainActivity,
                    Constants.USER_GENDER,
                    Constants.GENDER_FEMALE
                )
                var r = java.util.Random()
                val randomNumber = r.nextInt(10000)
                PreferenceHandler.setIntPreferences(
                    this@MainActivity,
                    Constants.USER_LAST_ID,
                    randomNumber
                )
            }

            override fun onChooseFemale() {
                setGender(Constants.GENDER_FEMALE)
            }

            override fun onChooseMale() {
                setGender(Constants.GENDER_MALE)
            }

            private fun setGender(gender:String){
                PreferenceHandler.setStrPreferences(
                    this@MainActivity,
                    Constants.USER_GENDER,
                    gender
                )

                val chosenGender:String = PreferenceHandler.getStrPreferences(this@MainActivity, Constants.USER_GENDER)
                Log.d(TAG, "setGender: ${chosenGender}")
            }

        })
//        genderDialog.show()

        birthYearDialog = BirthYearDialog(this, object : BirthYearDialog.Listner{
            override fun onClickOk(year:Int) {
                Log.d(TAG, "onClickOk: ${year}")
            }

            override fun onCancel() {
                TODO("Not yet implemented")
            }

        })
        birthYearDialog.show()




        askPermissionsWithPermissionCheck()
    }

    override fun onStart() {
        super.onStart()
        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        this.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE)
    }


    @NeedsPermission(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA
    )
    fun askPermissions() {
        Log.d(TAG, "askPermission: ")
//        connectToBle()
    }

    private fun connectToBle() {
        startBluAll()
        scanLeDevice(true)

    }

    private fun scanLeDevice(enable: Boolean) {
        Log.d("TEST", "scanLeDevice: $enable")
        if (!enable) {
            mScanning = false
            bluetoothAdapter!!.stopLeScan(mCallBack)
        }
        else {
            showConnectionPopup()

            mHandler.postDelayed({
                hideConnectionPopup()
                mScanning = false
                bluetoothAdapter!!.stopLeScan(mCallBack)

                if (mLeDevices.size > 0) {
                    var dbm: Int = 1000
                    var selectDevice: BluetoothDevice? = null
                    val boundedDevice: String =
                        PreferenceHandler.getStrPreferences(this, "BONED", "")

                    var isExistBoneded = false

                    for (cma: BLEInfoSet in mLeDevices) {
                        if (dbm > cma.getRssi()) {
                            dbm = cma.getRssi()
                            selectDevice = cma.getDevice()
                            mDeviceAddress = selectDevice!!.address
                            if (boundedDevice.equals(selectDevice.address)) {
                                isExistBoneded = true
                            }
                        }
                    }
                    showPopup()
                    mListAddress!!.setOnItemClickListener { parent, view, position, id ->
                        val device: BluetoothDevice? = mLeDevices[position].getDevice()
                        if (device != null && !mServiceBound){
                            if (mBluetoothLeService!!.connect(device.address)){
                                mDeviceAddress = device.address
                                val address:String = device.address.replace(":", "")
                                cma_address = "PC" + address
                                PreferenceHandler.setStrPreferences(this, "BONED", mDeviceAddress)
                            }
                        }
                        myDialog!!.dismiss()

                    }

                }
                else{
                    showfindDevice(0)
                }

            }, 5000)

            mScanning = true
            bluetoothAdapter!!.startLeScan(mCallBack)


        }
    }

    private fun showfindDevice(type: Int) {
        var messageId = R.string.device_not_found
        if (type == 0){
            messageId = R.string.please_turn_on_bluetooth
        }
        findDeviceDialog!!.setContentView(R.layout.finddevice)
        findDeviceDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        findDeviceDialog!!.setCanceledOnTouchOutside(false)
        findDeviceDialog!!.show()
        txtContents = findDeviceDialog!!.findViewById(R.id.txt_contents)
        txtContents!!.text = getString(messageId)

        btnGo = findDeviceDialog!!.findViewById(R.id.findDevice)
        btnGo!!.setOnClickListener {
            cma_address = null
            connectToBle()
            findDeviceDialog!!.dismiss()
        }

    }

    private var mBluetoothLeService: BluetoothLeService? = null
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            mBluetoothLeService = (service as BluetoothLeService.LocalBinder).getService()
            if (!mBluetoothLeService!!.initialize()) {
            }
            if (mBluetoothLeService!!.connect(mDeviceAddress)) {
                PreferenceHandler.setStrPreferences(this@MainActivity, "BONED", mDeviceAddress)
                Toast.makeText(this@MainActivity, "CONNECTED", Toast.LENGTH_LONG).show()
                mServiceBound = true
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBluetoothLeService = null
            mServiceBound = false
        }
    }

    private fun showPopup() {
        myDialog!!.setContentView(R.layout.bluetooth_popup_window)
        myDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mListAddress = myDialog!!.findViewById(R.id.listview)
        val customAdapter:CustomAdapter = CustomAdapter(this)
        mListAddress!!.adapter = customAdapter
        customAdapter.setDataSetList(mLeDevices)
        myDialog!!.setCanceledOnTouchOutside(false)
        myDialog!!.show()

        btnCancelConnection = myDialog!!.findViewById(R.id.btn_bluetooth_cancel)
        btnCancelConnection!!.setOnClickListener {
            myDialog!!.dismiss()
        }

    }

    private fun hideConnectionPopup() {
        myConnection!!.dismiss()
    }

    private fun showConnectionPopup() {
        myConnection = Dialog(this)
        myConnection!!.setContentView(R.layout.connection_popup_check)
        myConnection!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myConnection!!.setCanceledOnTouchOutside(false)
        myConnection!!.show()

        val imageview: ImageView = myConnection!!.findViewById(R.id.gifImageView)
        imageview.setImageResource(R.drawable.connection)

        btnCancelConnection = myConnection!!.findViewById(R.id.btn_cancel_connection)
        btnCancelConnection!!.setOnClickListener {
            scanLeDevice(false)
            myConnection!!.dismiss()
        }
    }

    private fun startBluAll() {
        if (!bluetoothAdapter!!.isEnabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    private val mCallBack =
        LeScanCallback { device: BluetoothDevice, rssi: Int, scanRecord: ByteArray? ->

            Log.d(TAG, ": scanlist ${device.name}  ${device.address}")

            if (device.name != null && device.name.startsWith("CMA")) {
                val infoset = BLEInfoSet()
                infoset.setDevice(device)
                infoset.setRssi(rssi)
                var checkDevice = false
                for (set in mLeDevices) {
                    if (device.address == set.getDevice()!!.getAddress()) {
                        checkDevice = true
                        continue
                    }
                }
                if (!checkDevice) {
                    mLeDevices.add(infoset)
                }
            }
        }


    @OnPermissionDenied(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
    )
    fun onPermissionsDenied() {
        finish()
        Log.d(TAG, "onPermissionsDenied: denied")
    }

    fun HideConnection(view: View) {}

    internal class CustomAdapter(context: Context?) :
        BaseAdapter() {
        private val mInflater: LayoutInflater
        private var mDataSetList: ArrayList<BLEInfoSet>? = null

        internal inner class ViewHolder {
            var name: TextView? = null
            var image: ImageView? = null
        }

        override fun getCount(): Int {
            return if (mDataSetList != null) {
                mDataSetList!!.size
            } else 0
        }

        override fun getItem(position: Int): Any {
            return (if (mDataSetList != null) {
                mDataSetList!![position]
            } else null)!!
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            var convertView = convertView
            val holder: ViewHolder
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.row_data, null)
                holder = ViewHolder()
                holder.name = convertView.findViewById(R.id.fruits)
                holder.image = convertView.findViewById(R.id.images)
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            val name = mDataSetList!![position].getDevice()!!.name
            holder.name!!.text = name
            holder.image!!.setImageResource(R.drawable.ic_ble_on)
            return convertView
        }

        fun setDataSetList(dataSetList: ArrayList<BLEInfoSet>?) {
            mDataSetList = dataSetList
            notifyDataSetChanged()
        }

        init {
            mInflater = LayoutInflater.from(context)
        }
    }



}