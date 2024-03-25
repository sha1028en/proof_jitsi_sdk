package kr.co.citus.proofjitsisdk.main

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import android.widget.ToggleButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.giphy.sdk.ui.themes.Theme
import kr.co.citus.proofjitsisdk.BuildConfig
import kr.co.citus.proofjitsisdk.ui.theme.ProofJitsiSDKTheme

import org.jitsi.meet.sdk.BroadcastEvent
import org.jitsi.meet.sdk.BroadcastIntentHelper
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo

import timber.log.Timber
import java.net.MalformedURLException
import java.net.URL

class ProofJitsiSdkActivity : ComponentActivity() {
    private lateinit var requestFGSNotificationLauncher: ActivityResultLauncher<String>

    private val jitsiMeetEvtReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { this@ProofJitsiSdkActivity.onJitsiMeetEvtReceived(it) }
        }
    }

    private fun onJitsiMeetEvtReceived(intent: Intent) {
        val event = BroadcastEvent(intent)
        when (event.type) {
            BroadcastEvent.Type.CONFERENCE_JOINED -> Timber.i("Conference Joined with url%s", event.data["url"])
            BroadcastEvent.Type.PARTICIPANT_JOINED -> Timber.i("Participant joined%s", event.data["name"])
            else -> Timber.i("Received event: %s", event.type)
        }
    }

    private fun registerJitsiMeetReceiver() {
        val intentFilter = IntentFilter()

        for (type in BroadcastEvent.Type.entries) {
            intentFilter.addAction(type.action)
        }
        LocalBroadcastManager.getInstance(this@ProofJitsiSdkActivity).registerReceiver(this.jitsiMeetEvtReceiver, intentFilter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProofJitsiSDKTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BuildJitsiControlPanel()
                }
            }
        }

        val serverUrlInstance = try {
            URL(BuildConfig.DEFAULT_SERVER_URL)

        } catch (e: MalformedURLException) {
            if (BuildConfig.DEBUG) e.printStackTrace()
            throw RuntimeException("default server url is INVALID")
        }

        // init JitSi Meet SDK
        val userInfo = JitsiMeetUserInfo().also { it.displayName = "Eu Yun soO" }
        val jitsiDefaultOption = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverUrlInstance)
            .setFeatureFlag("welcomepage.enabled", false)
            .setUserInfo(userInfo)
            .build()
        JitsiMeet.setDefaultConferenceOptions(jitsiDefaultOption)

        this.registerJitsiMeetReceiver()

        // for Push FGS Notification
        this.requestFGSNotificationLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            this.connect(this@ProofJitsiSdkActivity)
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.jitsiMeetEvtReceiver)
        super.onDestroy()
    }

    private fun connect(context: Context) {
        JitsiMeetActivity.launch(context,
            JitsiMeetConferenceOptions.Builder()
                .setRoom(BuildConfig.DEFAULT_ROOM_NAME)
                .build())
    }
}

@Composable
private fun BuildJitsiControlPanel() {
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Text(text = "Hell JitSi-Meet Custom SDK \nwith \"Compose\" UI",
            modifier = Modifier,
            textAlign = TextAlign.Center)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp), contentAlignment = Alignment.BottomCenter) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row (modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(), Arrangement.SpaceEvenly) {

                BuildButton("Toggle Cam", onClickEvent = {
                    toggleCam(it)
                })

                BuildButton("Mute Mic", onClickEvent = {
                    setAudioMute(it, true)
                })
            }

            Row (modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(), Arrangement.SpaceEvenly) {

                BuildButton("Connect", onClickEvent = {
                    connect(it)
                })

                BuildButton("Disconnect", onClickEvent = {
                    disconnect(it)
                })
            }
            Box(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun BuildButton(msg: String, onClickEvent: (localContext: Context) -> Unit) {
    val localContext = LocalContext.current

    Button(modifier = Modifier, onClick = {
        onClickEvent(localContext)

    }) {
        Text(text = msg, modifier = Modifier)
    }
}

@Composable
private fun BuildToggleButton(msg: String, state: MutableState<Boolean>, onClickEvent: (localContext: Context) -> Unit) {
    val localContext = LocalContext.current

    Button(modifier = Modifier, onClick = {
        onClickEvent(localContext)

    }) {

        Text(text = msg, modifier = Modifier)
    }
}

@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    ProofJitsiSDKTheme {
        BuildJitsiControlPanel()
    }
}

private fun connect(context: Context) {
    JitsiMeetActivity.launch(context,
        JitsiMeetConferenceOptions.Builder()
            .setRoom(BuildConfig.DEFAULT_ROOM_NAME)
            .build())
}

// Exit Jitsi Meet
// Example for sending actions to JitsiMeetSDK
private fun disconnect(context: Context) {
    val hangupBroadcastIntent: Intent = BroadcastIntentHelper.buildHangUpIntent()
    LocalBroadcastManager.getInstance(context).sendBroadcast(hangupBroadcastIntent)
}

private fun toggleCam(context: Context) {
    val toggleCamBroadcastIntent = BroadcastIntentHelper.buildToggleCameraIntent()
    LocalBroadcastManager.getInstance(context).sendBroadcast(toggleCamBroadcastIntent)
}

private fun setAudioMute(context: Context, isMute: Boolean) {
    val toggleAudioMute = BroadcastIntentHelper.buildSetAudioMutedIntent(isMute)
    LocalBroadcastManager.getInstance(context).sendBroadcast(toggleAudioMute)
}