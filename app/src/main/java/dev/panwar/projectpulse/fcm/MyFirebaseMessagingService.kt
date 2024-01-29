package dev.panwar.projectpulse.fcm


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.activities.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object{
        private const val TAG="MyFirebaseMsgService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options

//        checking from where the message came from
        Log.d(TAG,"FROM: ${remoteMessage.from}")

//        checking message data
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG,"Message data payload: ${remoteMessage.data}")
        }

//        checking if remoteMessage contains Notification
        remoteMessage.notification?.let {
            Log.d(TAG,"Message Notification body: ${it.body}")

        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */

//    when we receive a new token..we save this newly generated token on Device permanently using share Preferences on the Main Activity where person goes after loggin in
    override fun onNewToken(token: String) {
        super.onNewToken(token)

    Log.e(TAG,"Refreshed Token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    sendRegistrationToServer(token)
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */

    private fun sendRegistrationToServer(token: String?){
        // implement
    }

//    generation our own Custom Notification
    private fun sendNotification(messageBody:String){
//        user will be sent to Main Activity once he click's on Notification
        val intent=Intent(this,MainActivity::class.java)
//        putting this intent at top of intent Stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

//        when notification is sent and user is in other application then in that case we use a pending intent. for more read document
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)

        val channelId = this.resources.getString(R.string.default_notification_channel_id)

//        setting up the ringtone for the notification
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

//        finally creating notification
        val notificationBuilder=NotificationCompat.Builder(this,channelId).setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle("Title").setContentText("Message").setAutoCancel(true)
            .setSound(defaultSoundUri).setContentIntent(pendingIntent)

        val notificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//    / Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId, "Channel Project Pulse Title",NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0,notificationBuilder.build())
    }

}