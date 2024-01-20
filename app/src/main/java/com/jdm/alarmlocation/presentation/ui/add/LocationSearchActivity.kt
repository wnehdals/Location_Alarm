package com.jdm.alarmlocation.presentation.ui.add

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.ui.theme.AlarmlocationTheme
import com.jdm.core.base.BaseComposeActivity
import com.jdm.designsystem.kit.JAppbar
import com.jdm.designsystem.kit.JButtonF
import com.jdm.designsystem.kit.LabelM
import com.jdm.designsystem.kit.ParagraphM
import com.jdm.designsystem.kit.PermissionDialog
import com.jdm.designsystem.layout.BaseVerticalScrollLayout
import com.jdm.designsystem.theme.JdsTheme
import com.jdm.model.NameLocation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
@AndroidEntryPoint
class LocationSearchActivity : BaseComposeActivity() {
    private val viewModel: LocationSetViewModel by viewModels()
    private val permission = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
    )
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
            checkPermissions()
        }

    private val systemSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkPermissions()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribe()
        //permissionLauncher.launch(permission)
        setContent {
            AlarmlocationTheme {
                LocationSearchScreen(
                    viewModel = viewModel,
                    permissionDialogClick = {
                        viewModel.setEvent(
                            LocationSetContract.LocationSetEvent.SetDialogVisibility(
                                false
                            )
                        )
                        if (permission.all { shouldShowRequestPermissionRationale(it) }) {
                            permissionLauncher.launch(permission)
                        } else {
                            goToSystemSetting()
                        }
                    },
                    backButtonClick = {
                        onBackPressed()
                    },
                )
            }
        }
    }

    private fun subscribe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.effect.collectLatest {
                    when (it) {
                        is LocationSetContract.LocationSetSideEffect.RequestPermission -> {
                            permissionLauncher.launch(permission)
                        }
                        is LocationSetContract.LocationSetSideEffect.SendActivityResult -> {
                            var intent = Intent()
                            intent.putExtra("location", viewModel.selectedNameLocation)
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    }

                }
            }
        }
    }

    private fun goToSystemSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        systemSettingLauncher.launch(intent)
    }

    private fun checkPermissions() {
        if (!isPermissionAllow(permission)) {
            viewModel.setEvent(LocationSetContract.LocationSetEvent.SetDialogVisibility(true))
        } else {
            viewModel.getLocationInfo(this)
        }
    }

    override fun onDestroy() {
        viewModel.releaseLocation()
        super.onDestroy()
    }
}

@Composable
private fun LocationSearchScreen(
    state: LocationSetContract.LocationSetState,
    permissionDialogClick: () -> Unit,
    backButtonClick: () -> Unit,
    setEvent: (LocationSetContract.LocationSetEvent) -> Unit,
) {
    val scrollState = rememberScrollState()
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = JdsTheme.colors.white)
        ) {
            if (state.isShowPermissionDialog) {
                PermissionDialog(
                    title = stringResource(id = R.string.str_app_permission_mandatory_popup_title),
                    permissionTitle = stringResource(id = R.string.str_location),
                    permissionIcon = R.drawable.ic_pin_black,
                    message = stringResource(id = R.string.str_app_permission_storage_popup_desc),
                    rightText = stringResource(id = R.string.str_confirm),
                    rightClick = permissionDialogClick
                )
            }
            JAppbar(
                title = stringResource(id = R.string.str_search_location),
                color = JdsTheme.colors.white,
                leftIcon = R.drawable.ic_back_black,
                onClickLeftIcon = backButtonClick,
                rightIcon = null
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (state.locationSearchState) {
                    is LocationSearchState.Undefine -> {
                        JButtonF(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            onClick = { setEvent(LocationSetContract.LocationSetEvent.SearchLocationEvent) },
                            text = stringResource(id = R.string.str_select_current_location)
                        )
                    }

                    is LocationSearchState.Loading -> {
                        Loader(
                            modifier = Modifier
                                .size(60.dp)
                        )
                        ParagraphM(
                            modifier = Modifier.padding(vertical = 8.dp),
                            text = stringResource(id = R.string.str_search_location_guide),
                            textColor = JdsTheme.colors.black
                        )
                        LabelM(
                            text = stringResource(id = R.string.str_wait_please),
                            textColor = JdsTheme.colors.black
                        )
                    }

                    is LocationSearchState.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                                .background(
                                    color = JdsTheme.colors.gray200,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ParagraphM(
                                text = state.locationSearchState.address,
                                textColor = JdsTheme.colors.gray400
                            )

                        }
                    }

                    is LocationSearchState.Fail -> {
                        ParagraphM(
                            text = stringResource(id = R.string.str_location_search_fail_desc),
                            textColor = JdsTheme.colors.red400
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        JButtonF(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            onClick = backButtonClick,
                            text = stringResource(id = R.string.str_re_search)
                        )
                    }
                }

            }
            Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 16.dp,
                color = JdsTheme.colors.gray200
            )
            ParagraphM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                text = stringResource(id = R.string.str_recently_search_address), textColor = JdsTheme.colors.black )

            if (state.locationList.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ParagraphM(text = stringResource(id = R.string.str_empty_recently_search_address), textColor = JdsTheme.colors.gray400)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        items = state.locationList,
                        key = { index, location -> location.id }
                    ) { index, location ->
                        LocationItem(
                            location = location,
                            onClick =  {
                                setEvent(LocationSetContract.LocationSetEvent.OnClickRecentlyLocation(it))
                            }
                        )
                    }
                }
            }


        }

    }
}

@Composable
private fun LocationSearchScreen(
    viewModel: LocationSetViewModel,
    permissionDialogClick: () -> Unit,
    backButtonClick: () -> Unit,
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    LocationSearchScreen(state, permissionDialogClick, backButtonClick, viewModel::setEvent)
    viewModel.getLocationList()
}

@Composable
private fun Loader(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.pin_lottie))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 0.4F
    )
    LottieAnimation(
        modifier = modifier,
        composition = composition,
        progress = { progress }
    )
}
@Composable
private fun LocationItem(
    location: NameLocation,
    onClick: (NameLocation) -> Unit
) {
    Column(
        modifier = Modifier.clickable {
            onClick(location)
        },
    ) {
        ParagraphM(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
            text = location.name, textColor = JdsTheme.colors.black )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            thickness = 1.dp,
            color = JdsTheme.colors.gray200
        )
    }

}