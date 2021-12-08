package com.telechaplaincy.video_call

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.telechaplaincy.R
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.SurfaceView;
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout;
import com.squareup.picasso.Picasso
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import kotlinx.android.synthetic.main.activity_video_call.*

class VideoCallActivity : AppCompatActivity() {
    // Kotlin
    // Fill the App ID of your project generated on Agora Console.
    private val APP_ID = "afb5ee413d864417bbcf32ba55b40dac"
    // Fill the channel name.
    private val CHANNEL = "appointment"
    // Fill the temp token generated on Agora Console.
    private val TOKEN = "006afb5ee413d864417bbcf32ba55b40dacIABueFjC1nOzzguRBS/or1AmQnRDq6sLVmPjLprAVHXYz0T4OP4AAAAAEAAyrb7jtc6xYQEAAQC1zrFh"

    private var mRtcEngine: RtcEngine ?= null

    private lateinit var localContainer: FrameLayout
    private lateinit var remoteContainer: FrameLayout

    private var chaplainName = ""
    private var uniqueUserId:Int = 0
    private var chaplainProfileImageLink = ""
    private var isMicMuted = false
    private var isCameraOpen = true
    private var isRemoteSoundOpen = true

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote user joining the channel to get the uid of the user.
        override fun onUserJoined(uniqueUserId: Int, elapsed: Int) {
            runOnUiThread {
                // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
                setupRemoteVideo(uniqueUserId)

                //hide remote user profile image
                video_chat_remote_user_image_view.visibility = View.GONE
                video_page_name_textView.visibility = View.GONE
            }
        }

        override fun onUserMuteVideo(uid: Int, muted: Boolean) {
            runOnUiThread {
                video_chat_remote_user_image_view.visibility = View.VISIBLE
                video_page_name_textView.visibility = View.VISIBLE
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                video_chat_remote_user_image_view.visibility = View.VISIBLE
                video_page_name_textView.visibility = View.VISIBLE
                remoteContainer.removeAllViews()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        localContainer = findViewById<FrameLayout>(R.id.local_video_view_container)
        remoteContainer = findViewById<FrameLayout>(R.id.remote_video_view_container)

        chaplainName = intent.getStringExtra("chaplain_name").toString()
        val userId = intent.getStringExtra("chaplainUniqueUserId").toString()
        chaplainProfileImageLink = intent.getStringExtra("chaplainProfileImageLink").toString()
        uniqueUserId = userId.toInt(10)
        video_page_name_textView.text = chaplainName
        if (chaplainProfileImageLink != ""){
            Picasso.get().load(chaplainProfileImageLink).into(video_chat_remote_user_image_view)
        }

        initializeAndJoinChannel()

        video_page_mic_imageButton.setOnClickListener {
            if (!isMicMuted){
                mRtcEngine?.muteLocalAudioStream(true)
                isMicMuted = true
                video_page_mic_imageButton.setImageResource(R.drawable.ic_baseline_mic_off_24)
            }else{
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

    private fun initializeAndJoinChannel() {
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

        // Join the channel with a token.
        mRtcEngine!!.joinChannel(TOKEN, CHANNEL, "", 0)

    }

    private fun setupRemoteVideo(uniqueUserId: Int) {

        val remoteFrame = RtcEngine.CreateRendererView(baseContext)
        //remoteFrame.setZOrderMediaOverlay(true)
        remoteContainer.addView(remoteFrame)
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(remoteFrame, VideoCanvas.RENDER_MODE_FIT, uniqueUserId))
    }

    override fun onDestroy() {
        super.onDestroy()
        mRtcEngine?.stopPreview()
        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
    }
}