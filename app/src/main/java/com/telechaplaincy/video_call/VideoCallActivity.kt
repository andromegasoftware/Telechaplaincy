package com.telechaplaincy.video_call

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import kotlinx.android.synthetic.main.activity_patient_future_appointment_detail.*
import kotlinx.android.synthetic.main.activity_video_call.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class VideoCallActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private var chaplainProfileFieldUserId: String = ""

    // Kotlin
    // Fill the App ID of your project generated on Agora Console.
    private val APP_ID = "afb5ee413d864417bbcf32ba55b40dac"

    // Fill the channel name.
    private val CHANNEL = "appointment"

    // Fill the temp token generated on Agora Console.
    private var TOKEN =
        "006afb5ee413d864417bbcf32ba55b40dacIAC9Tpc2k2Rbs/yuqEIM57L+Ci862D4pLLRv+rzrrTcc7ET4OP4AAAAAEAD1z9KPIJn6YQEAAQAfmfph"

    private var mRtcEngine: RtcEngine? = null

    private lateinit var localContainer: FrameLayout
    private lateinit var remoteContainer: FrameLayout
    private lateinit var timer: CountDownTimer

    private var chaplainName = ""
    private var uniqueUserUidLocal: Int = 0
    private var uniqueUserUidRemote: Int = 0
    private var chaplainProfileImageLink = ""
    private var isMicMuted = false
    private var isCameraOpen = true
    private var isRemoteSoundOpen = true

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote user joining the channel to get the uid of the user.
        override fun onUserJoined(uniqueUserUidRemote: Int, elapsed: Int) {
            runOnUiThread {
                // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
                setupRemoteVideo(uniqueUserUidRemote)

                //hide remote user profile image
                video_chat_remote_user_image_view.visibility = View.GONE
                video_page_name_textView.visibility = View.GONE
                timer()
                markChaplainPaymentWait()
            }
        }

        override fun onUserMuteVideo(uniqueUserUidRemote: Int, muted: Boolean) {
            runOnUiThread {
                video_chat_remote_user_image_view.visibility = View.VISIBLE
                video_page_name_textView.visibility = View.VISIBLE
            }
        }

        override fun onUserOffline(uniqueUserUidRemote: Int, reason: Int) {
            runOnUiThread {
                video_chat_remote_user_image_view.visibility = View.VISIBLE
                video_page_name_textView.visibility = View.VISIBLE
                remoteContainer.removeAllViews()
                timer.cancel()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        localContainer = findViewById<FrameLayout>(R.id.local_video_view_container)
        remoteContainer = findViewById<FrameLayout>(R.id.remote_video_view_container)

        chaplainProfileFieldUserId = intent.getStringExtra("chaplainProfileFieldUserId").toString()

        chaplainName = intent.getStringExtra("chaplain_name").toString()
        val userId = intent.getStringExtra("chaplainUniqueUserId").toString()
        chaplainProfileImageLink = intent.getStringExtra("chaplainProfileImageLink").toString()
        uniqueUserUidRemote = userId.toInt(10)
        video_page_name_textView.text = chaplainName
        if (chaplainProfileImageLink != "") {
            Picasso.get().load(chaplainProfileImageLink).into(video_chat_remote_user_image_view)
        }

        getToken()
        //initializeAndJoinChannel()

        video_page_mic_imageButton.setOnClickListener {
            if (!isMicMuted) {
                mRtcEngine?.muteLocalAudioStream(true)
                isMicMuted = true
                video_page_mic_imageButton.setImageResource(R.drawable.ic_baseline_mic_off_24)
            } else {
                mRtcEngine?.muteLocalAudioStream(false)
                isMicMuted = false
                video_page_mic_imageButton.setImageResource(R.drawable.ic_baseline_mic_24)

            }
        }

        video_page_camera_imageButton.setOnClickListener {
            if (isCameraOpen){
                mRtcEngine?.disableVideo()
                video_page_camera_imageButton.setImageResource(R.drawable.ic_baseline_videocam_off_24)
                isCameraOpen = false
            }else{
                mRtcEngine?.enableVideo()
                video_page_camera_imageButton.setImageResource(R.drawable.ic_baseline_videocam_24)
                isCameraOpen = true
            }
        }

        video_page_sound_imageButton.setOnClickListener {
            if (isRemoteSoundOpen){
                mRtcEngine?.muteAllRemoteAudioStreams(true)
                video_page_sound_imageButton.setImageResource(R.drawable.ic_baseline_volume_off_24)
                isRemoteSoundOpen = false
            }else{
                mRtcEngine?.muteAllRemoteAudioStreams(false)
                video_page_sound_imageButton.setImageResource(R.drawable.ic_baseline_volume_up_24)
                isRemoteSoundOpen = true
            }
        }

        video_page_finnish_call_imageButton.setOnClickListener {
            mRtcEngine?.stopPreview()
            mRtcEngine?.leaveChannel()
            RtcEngine.destroy()
            finish()
        }

        video_page_camera_change_imageButton.setOnClickListener {
            mRtcEngine?.switchCamera()
        }

    }

    private fun getToken() {
        //getting token info from rest api
        val retrofit = Retrofit.Builder()
            .baseUrl("https://kadir.webprogrammer.fi/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(TokenApiInterface::class.java)

        //this part is not clear. which uid should be used remote uid or local uid
        api.fetchAllData(uid = uniqueUserUidRemote.toString())
            .enqueue(object : Callback<TokenModelClass> {
                override fun onResponse(
                    call: Call<TokenModelClass>,
                    response: Response<TokenModelClass>
                ) {
                    TOKEN = response.body()?.token ?: TOKEN
                    Log.e("TOKEN_1: ", TOKEN)
                    Log.e("TOKEN_2: ", uniqueUserUidRemote.toString())
                    initializeAndJoinChannel(TOKEN)
                }

                override fun onFailure(call: Call<TokenModelClass>, t: Throwable) {
                    Log.e("TOKEN: ", t.message.toString())
                }

            })

    }

    private fun timer(){
        var meetingTotalTime:Long = 0
        timer = object: CountDownTimer(1800000, 1000) {
            override fun onTick(timeRemain: Long) {
                val dateFormatLocalZone = SimpleDateFormat("mm:ss")
                dateFormatLocalZone.timeZone = TimeZone.getTimeZone("UTC")
                val appointmentDate = dateFormatLocalZone.format(Date(meetingTotalTime))
                video_page_time_textView.text = appointmentDate
                meetingTotalTime += 1000
            }

            override fun onFinish() {
                future_appointment_call_button.isClickable = true
            }
        }.start()
    }

    private fun initializeAndJoinChannel(TOKEN: String) {
        try {
            mRtcEngine = RtcEngine.create(baseContext, APP_ID, mRtcEventHandler)
        } catch (e: Exception) {

        }

        // By default, video is disabled, and you need to call enableVideo to start a video stream.
        mRtcEngine!!.enableVideo()
        // Call CreateRendererView to create a SurfaceView object and add it as a child to the FrameLayout.
        val localFrame = RtcEngine.CreateRendererView(baseContext)
        localFrame.setZOrderMediaOverlay(true)
        localContainer.addView(localFrame)
        // Pass the SurfaceView object to Agora so that it renders the local video.
        mRtcEngine!!.setupLocalVideo(VideoCanvas(localFrame, VideoCanvas.RENDER_MODE_FIT, 0))
        //this uid is the local user uid, not the remote user uid

        // Join the channel with a token.
        mRtcEngine!!.joinChannel(TOKEN, CHANNEL, "", 0)

    }

    private fun setupRemoteVideo(uniqueUserUidRemote: Int) {

        val remoteFrame = RtcEngine.CreateRendererView(baseContext)
        //remoteFrame.setZOrderMediaOverlay(true)
        remoteContainer.addView(remoteFrame)
        mRtcEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteFrame,
                VideoCanvas.RENDER_MODE_FIT,
                uniqueUserUidRemote
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        mRtcEngine?.stopPreview()
        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
    }

    private fun markChaplainPaymentWait() {
        db.collection("chaplains").document(chaplainProfileFieldUserId)
            .update("isPaymentWait", true)
            .addOnSuccessListener {

            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }
}