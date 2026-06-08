package com.jdm.alarmlocation.presentation.ui.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.slider.Slider
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.databinding.ActivitySearchLocationBinding
import com.jdm.alarmlocation.domain.model.Place
import com.jdm.alarmlocation.domain.toLatLng
import com.jdm.alarmlocation.domain.toPlace
import com.jdm.alarmlocation.presentation.dialog.PermissionDialog
import com.jdm.alarmlocation.presentation.dialog.PlaceDialog
import com.jdm.alarmlocation.presentation.ui.routine.CreateRoutineActivity
import com.jdm.alarmlocation.presentation.ui.util.hasPermissions
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchLocationActivity : AppCompatActivity(), OnMapReadyCallback {
    private val viewModel: SearchLocationViewModel by viewModels()
    lateinit var binding: ActivitySearchLocationBinding
    private lateinit var locationSource: FusedLocationSource
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private val marker = Marker()
    private val circle = CircleOverlay()

    // FusedLocationProviderClient 선언
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null // 위치 업데이트를 위한 콜백

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsResult ->
            if (permissionsResult.all { it.value }) { // 모든 권한이 허용되었는지 확인
                permissionSuccessProcess()
            } else {
                // 하나 이상의 권한이 거부된 경우
                // 사용자가 "다시 묻지 않음"을 선택했는지 여부에 따라 다른 처리 가능
                if (permissions.any {
                        !shouldShowRequestPermissionRationale(it) && !hasPermissions(
                            arrayOf(it)
                        )
                    }) {
                    // "다시 묻지 않음"을 선택하고 권한이 없는 경우 설정으로 유도
                    showPermissionDialog(getString(R.string.str_app_permission_popup_setting_desc)) {
                        goToSystemSetting()
                    }
                } else {
                    // 일반적인 권한 거부
                    showPermissionDialog(getString(R.string.str_app_permission_storage_popup_desc)) {
                        requestPermission() // 다시 권한 요청 시도 또는 다른 처리
                    }
                }
            }
        }

    private val systemSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (hasPermissions(permissions)) {
                permissionSuccessProcess()
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                // 설정에서 돌아왔는데도 권한이 없으면 사용자에게 알림
            }
        }
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_location)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val insets =
                insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.updatePadding(insets.left, insets.top, insets.right, insets.bottom)

            WindowInsetsCompat.CONSUMED
        }
        mapView = findViewById<MapView>(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        initState()
    }

    fun initView() {
        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }


    private fun initState() {
        initView()
        initEvent()
        subscribe()
        initData()
    }

    private fun permissionSuccessProcess() {
        // 권한이 성공적으로 부여되면 현재 위치를 가져옵니다.
        getCurrentLocation()
    }

    // 메시지를 파라미터로 받도록 수정
    private fun showPermissionDialog(message: String, rightClick: () -> Unit) {
        PermissionDialog(
            context = this@SearchLocationActivity,
            msg = message,
            icon = R.drawable.ic_pin_black,
            permissionName = getString(R.string.str_location),
            rightClick = {
                rightClick()
            }
        ).show(supportFragmentManager, PermissionDialog.TAG)
    }


    private fun initEvent() {
        binding.ivLocSearch.setOnClickListener {
            viewModel.searchKeyword(binding.etLocationSearch.text.toString())
        }

        binding.slSearchLocation.addOnChangeListener(object : Slider.OnChangeListener {
            override fun onValueChange(
                slider: Slider,
                value: Float,
                fromUser: Boolean
            ) {
                viewModel.rangeData.value = value.toInt()
            }
        })
        binding.llSearchLocationOut.setOnClickListener {
            viewModel.isInData.value = false
        }
        binding.llSearchLocationIn.setOnClickListener {
            viewModel.isInData.value = true
        }
        binding.btSearchCancel.setOnClickListener {
            finish()
        }
        binding.btSearchComplete.setOnClickListener {
            val place = viewModel.currentPlace.value
            if (place == null) {
                Toast.makeText(this, "장소를 선택해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (viewModel.rangeData.value == null) {
                Toast.makeText(this, "범위를 선택해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (viewModel.isInData.value == null) {
                Toast.makeText(this, "방향를 선택해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = getFinishIntent(place, viewModel.rangeData.value!!, viewModel.isInData.value!!)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun subscribe() {
        viewModel.currentPlace.observe(this) {
            createMarker(it, viewModel.rangeData.value)
        }
        viewModel.toastMsg.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.placeData.observe(this) {
            PlaceDialog(
                this,
                it,
                this::onClickPlace
            ).show(
                supportFragmentManager,
                PlaceDialog.TAG
            )
        }
        viewModel.rangeData.observe(this) { range ->
            binding.tvSearchLocationRange.text = "${range}"
            viewModel.currentPlace.value?.let { place ->
                createMarker(place, range)
            }
            binding.slSearchLocation.value = range.toFloat()
        }
        viewModel.isInData.observe(this) {
            binding.ivSearchLocationIn.isSelected = false
            binding.ivSearchLocationOut.isSelected = false
            if (it) {
                binding.ivSearchLocationIn.isSelected = true
            } else {
                binding.ivSearchLocationOut.isSelected = true
            }
        }
    }

    private fun initData() {
        val alarm = intent.getParcelableExtra<Place>(PARAM_PLACE)
        val range: Int = intent.getIntExtra(PARAM_RANGE, 50) ?: 50
        val isIn: Boolean = intent.getBooleanExtra(PARAM_DIRECTION, false)
        viewModel.rangeData.value = range
        viewModel.isInData.value = isIn
        Log.e("alarmdinit", "${alarm}")
        viewModel.emitAlarm(alarm)
    }

    private fun onClickPlace(place: Place) {
        viewModel.currentPlace.value = place
    }

    private fun requestPermission() {
        permissionLauncher.launch(permissions)
    }

    // 위치 정보를 가져오기 전에 권한을 확인하는 통합 함수
    private fun checkPermissionsAndGetLocation() {
        if (hasPermissions(permissions)) {
            getCurrentLocation()
        } else {
            // 권한 요청 로직 (기존 checkPermissions 함수와 유사하게 처리)
            // 사용자가 이전에 권한 요청을 명시적으로 거부했고 "다시 묻지 않음"을 선택하지 않은 경우
            if (permissions.all { shouldShowRequestPermissionRationale(it) }) {
                showPermissionDialog(getString(R.string.str_app_permission_storage_popup_desc)) {
                    requestPermission()
                }
            } else {
                // 처음 권한을 요청하거나, 사용자가 "다시 묻지 않음"을 선택한 경우
                requestPermission() // 바로 시스템 권한 요청 팝업을 띄우거나,
                // "다시 묻지 않음" 선택 시 설정으로 유도하는 로직은 permissionLauncher 콜백에서 처리
            }
        }
    }


    @SuppressLint("MissingPermission") // 권한 체크는 이미 checkPermissionsAndGetLocation 에서 수행
    private fun getCurrentLocation() {
        // 1. 마지막으로 알려진 위치 가져오기 (빠르고, 없을 수 있음)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d(
                        "LocationLog",
                        "Last known location: ${location.latitude}, ${location.longitude}"
                    )
                    // 필요한 경우 추가 처리
                } else {
                    Log.d("LocationLog", "Last known location is null. Requesting new location.")
                    // 마지막 위치가 없으면 새 위치 요청
                    requestNewLocationData()
                }
            }
            .addOnFailureListener { e ->
                Log.e("LocationLog", "Error getting last known location", e)
                // 오류 발생 시 새 위치 요청 시도 또는 사용자에게 알림
                requestNewLocationData()
            }
    }

    @SuppressLint("MissingPermission") // 권한 체크는 이미 checkPermissionsAndGetLocation 에서 수행
    private fun requestNewLocationData() {
        // LocationRequest 설정
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000) // 10초 간격
                .setWaitForAccurateLocation(false) // 정확한 위치를 기다릴지 여부 (true면 더 오래 걸릴 수 있음)
                .setMinUpdateIntervalMillis(5000) // 최소 업데이트 간격 (5초)
                .setMaxUpdateDelayMillis(15000) // 최대 업데이트 지연 시간 (15초) - 배치 업데이트 시
                .build()

        // LocationCallback 정의
        if (locationCallback == null) { // 콜백이 중복 등록되지 않도록
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val lastLocation: Location? = locationResult.lastLocation
                    if (lastLocation != null) {
                        Log.d(
                            "LocationLog",
                            "New location received: ${lastLocation.latitude}, ${lastLocation.longitude}"
                        )

                        // 일회성 위치 정보가 필요한 경우, 위치 업데이트 중단
                        stopLocationUpdates()
                    } else {
                        Log.d("LocationLog", "New location is null in onLocationResult.")
                    }
                }

                override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                    if (!locationAvailability.isLocationAvailable) {
                        Log.w("LocationLog", "Location is not available.")
                        // 위치를 사용할 수 없는 경우 사용자에게 알림 (예: GPS 꺼짐)
                        Toast.makeText(
                            this@SearchLocationActivity,
                            "위치를 사용할 수 없습니다. GPS가 켜져 있는지 확인하세요.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        // 위치 업데이트 시작
        // Looper.myLooper()가 null이 아닌지 확인하여 제공
        val looper = Looper.myLooper()
        if (looper != null) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, looper)
            Log.d("LocationLog", "Requested location updates.")
        } else {
            // 기본 메인 Looper 사용 (백그라운드 스레드에서 호출 시 주의)
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
            Log.d("LocationLog", "Requested location updates on main looper as myLooper was null.")
        }
    }

    // 위치 업데이트 중단
    private fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null // 콜백 참조 제거
            Log.d("LocationLog", "Stopped location updates.")
        }
    }


    private fun goToSystemSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        systemSettingLauncher.launch(intent)
    }

    private fun createMarker(place: Place, range: Int? = null) {
        marker.map = null
        val latLong = place.toLatLng()
        marker.position = latLong
        marker.icon = OverlayImage.fromResource(R.drawable.ic_loc_blue400)
        marker.map = naverMap
        val cameraUpdate = CameraUpdate.scrollTo(latLong)
        naverMap.moveCamera(cameraUpdate)

        circle.map = null
        circle.center = latLong
        circle.outlineWidth = 10
        circle.color = Color.TRANSPARENT
        circle.outlineColor = Color.parseColor("#276EF1")
        val notNullRange = range ?: 50
        circle.radius = notNullRange.toDouble()
        circle.map = this.naverMap

        if (place.title.isNotEmpty()) {
            binding.llSearchLocationAddress.visibility = View.VISIBLE
            binding.tvSearchLocationAddress.text = "${place.title}"
        } else {
            binding.llSearchLocationAddress.visibility = View.GONE
            binding.tvSearchLocationAddress.text = ""
        }


    }


    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        this.naverMap.locationSource = locationSource
        viewModel.emitMapReadyFlow()
        this.naverMap.setOnMapClickListener { point, coord ->
            binding.llSearchLocationAddress.visibility = View.GONE
            binding.tvSearchLocationAddress.text = ""
            viewModel.currentPlace.value = coord.toPlace()
        }
        this.naverMap.setOnSymbolClickListener { symbol ->
            if (!symbol.caption.isNullOrEmpty()) {
                viewModel.currentPlace.value = symbol.position.toPlace(title = symbol.caption)
                binding.llSearchLocationAddress.visibility = View.VISIBLE
                binding.tvSearchLocationAddress.text = "${symbol.caption}"
            } else {
                binding.llSearchLocationAddress.visibility = View.GONE
                binding.tvSearchLocationAddress.text = ""
            }
            true
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (
            locationSource.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
        // 필요하다면 onResume에서 다시 위치 권한을 확인하고 위치를 가져올 수 있습니다.
        // 예: if (필요한_조건 && hasPermissions(permissions)) { getCurrentLocation() }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        // 액티비티가 백그라운드로 갈 때 위치 업데이트 중단 (배터리 절약)
        // 지속적인 위치 추적이 필요하지 않은 경우에만
        // stopLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    fun getFinishIntent(place: Place?, range: Int, isIn: Boolean): Intent {
        val intent = Intent()
        intent.putExtra(PARAM_PLACE, place)
        intent.putExtra(PARAM_RANGE, range)
        intent.putExtra(PARAM_DIRECTION, isIn)
        return intent
    }


    companion object {
        const val PARAM_PLACE = "place"
        const val PARAM_RANGE = "range"
        const val PARAM_DIRECTION = "direction"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        fun getIntent(context: Context, place: Place?, range: Int, isIn: Boolean): Intent {
            val intent = Intent(context, SearchLocationActivity::class.java)
            intent.putExtra(PARAM_PLACE, place)
            intent.putExtra(PARAM_RANGE, range)
            intent.putExtra(PARAM_DIRECTION, isIn)
            return intent
        }


    }

}