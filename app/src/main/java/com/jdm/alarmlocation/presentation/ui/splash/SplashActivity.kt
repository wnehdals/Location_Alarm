package com.jdm.alarmlocation.presentation.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdm.alarmlocation.presentation.ui.main.MainActivity
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.ui.theme.AlarmlocationTheme
import com.jdm.core.base.BaseComposeActivity
import com.jdm.designsystem.kit.JPopup
import com.jdm.designsystem.theme.JdsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SplashActivity : BaseComposeActivity() {
    val viewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlarmlocationTheme {
                val splashState by viewModel.viewState.collectAsStateWithLifecycle()
                val context = LocalContext.current
                var isShowPopup by rememberSaveable {
                    mutableStateOf(false)
                }
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LaunchedEffect(key1 = true) {
                        delay(2000L)
                        viewModel.getAppVersion()
                    }
                    LaunchedEffect(key1 = Unit) {
                        viewModel.effect.collectLatest { sideEffect ->
                            when (sideEffect) {
                                is SplashContract.SplashSideEffect.MoveMain -> {
                                    val intent = Intent(context, MainActivity::class.java)
                                    goToActivity(intent)
                                }

                                is SplashContract.SplashSideEffect.SelectVersionUpdate -> {
                                    isShowPopup = true
                                }

                                is SplashContract.SplashSideEffect.ForceVersionUpdate -> {
                                    isShowPopup = true
                                }
                            }
                        }

                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = JdsTheme.colors.purple400),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.splash),
                            contentDescription = "splash"
                        )
                        if (isShowPopup) {
                            JPopup(
                                title = getString(R.string.str_notification_update),
                                message = getString(R.string.str_update_guide),
                                rightText = getString(R.string.str_do_update),
                                rightClick = {

                                },
                                ) {
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun SplashScreen(
    viewModel: SplashViewModel
) {

}