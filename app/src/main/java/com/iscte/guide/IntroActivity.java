package com.iscte.guide;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.proximity.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.proximity.ProximityAttachment;
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;
import com.estimote.proximity_sdk.proximity.ProximityZone;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class IntroActivity extends AppCompatActivity {

    private String CHANNEL_ID = "notification_channelID";

    private String notificationMuseum = "Welcome to museumID!";
    private String notificationExhibition = "Welcome to the exhibition exhibitionID!";
    private String notificationZone = "Welcome to our zoneID zone!";
    private String notificationItem = "Here you can find itemID!";

    public static final String PREFS_NAME = "MyPrefsFile";

    private ProximityObserver proximityObserver;
    private ProximityObserver.Handler proximityHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        String setupBeacons = SetupBeacons.getInstance().getSetupBeacons();

        if (setupBeacons.equals("True")) {

            setupBeacons();

            SetupBeacons.getInstance().setSetupBeacons("False");
        }

    }

    public void museumsButton_clicked(View view){
        Intent intent = new Intent(this,MuseumsActivity.class);
        startActivity(intent);
    }

    public void lastVisitsButton_clicked(View view){
    }

    private void setupBeacons(){
        EstimoteCloudCredentials cloudCredentials =
                new EstimoteCloudCredentials("guide-3nq", "dc1792305ba1e69f71d87cc29f7dc47e");

        /*final Intent notificationIntent = new Intent(this, ZoneInfo.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ZoneInfo.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        builder.setContentIntent(resultPendingIntent);*/

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        this.proximityObserver =
                new ProximityObserverBuilder(getApplicationContext(), cloudCredentials)
                        .withOnErrorAction(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                Log.e("app_beacons", "proximity observer error: " + throwable);
                                return null;
                            }
                        })
                        .withBalancedPowerMode()
                        .withEstimoteSecureMonitoringDisabled()
                        .withTelemetryReportingDisabled()
                        .build();

        ProximityZone zone1 = this.proximityObserver.zoneBuilder()
                .forAttachmentKeyAndValue("beacon_from", this.getResources().getString(R.string.beacon_id))
                .inNearRange()
                .withOnEnterAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment attachment) {
                        if(attachment.hasAttachment()){
                            SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
                            SharedPreferences.Editor editor=preferences.edit();
                            String mySpace = preferences.getString("current_space", "Default");

                            String museumItem = "";
                            String museumItem_desc = "";
                            String museumZone = "";
                            String museumZone_desc = "";
                            String museumExhibition = "";
                            String museumExhibition_desc = "";
                            String museumId = "";
                            String museumId_desc = "";

                            museumId = attachment.getPayload().get("beacon_museum");
                            museumId_desc = attachment.getPayload().get("beacon_museum_desc");


                            //region Notification
                            Intent notificationIntent = new Intent(getApplicationContext(), Main2Activity.class);
                            notificationIntent.setAction(Intent.ACTION_MAIN);
                            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            notificationIntent.putExtra("limited", false);

                            if(museumId != mySpace) {
                                notificationIntent.putExtra("mySpace",museumId);
                            }

                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                            /*stackBuilder.addParentStack(ZoneInfo.class);
                            stackBuilder.addNextIntent(notificationIntent);

                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                            */

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_stat_name)
                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                                    .setContentTitle(getString(R.string.app_name))
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                                    .setAutoCancel(true);

                            //builder.setContentIntent(resultPendingIntent);

                            //endregion

                            String beacon_info = attachment.getPayload().get("beacon_item");

                            if(!beacon_info.isEmpty()){
                                museumItem = beacon_info;
                                museumItem_desc = attachment.getPayload().get("beacon_item_desc");
                                builder.setContentText(notificationItem.replace("itemID",museumItem_desc));
                                stackBuilder.addParentStack(ItemInfo.class);
                                //notificationIntent = new Intent(getApplicationContext(), Item.class);
                            }else{
                                beacon_info = attachment.getPayload().get("beacon_zone");
                                if(!beacon_info.isEmpty()){
                                    museumZone = beacon_info;
                                    museumZone_desc = attachment.getPayload().get("beacon_zone_desc");
                                    notificationIntent = new Intent(getApplicationContext(), ZoneInfo.class);
                                    stackBuilder.addParentStack(ZoneInfo.class);
                                    builder.setContentText(notificationZone.replace("zoneID",museumZone_desc));
                                }else{
                                    beacon_info = attachment.getPayload().get("beacon_exhibition");
                                    if(!beacon_info.isEmpty()){
                                        museumExhibition = beacon_info;
                                        museumExhibition_desc = attachment.getPayload().get("beacon_exhibition_desc");
                                        notificationIntent = new Intent(getApplicationContext(), ExhibitionInfo.class);
                                        stackBuilder.addParentStack(ExhibitionInfo.class);
                                        builder.setContentText(notificationExhibition.replace("exhibitionID",museumExhibition_desc));
                                    }else{
                                        museumId_desc = attachment.getPayload().get("beacon_museum_desc");
                                        builder.setContentText(notificationMuseum.replace("museumID",museumId_desc));
                                    }
                                }
                            }

                            editor.putString("museumItem",museumItem);
                            editor.putString("museumItem_desc",museumItem_desc);
                            editor.putString("museumZone",museumZone);
                            editor.putString("museumZone_desc",museumZone_desc);
                            editor.putString("museumExhibition",museumExhibition);
                            editor.putString("museumExhibition_desc",museumExhibition_desc);
                            //editor.putBoolean("limited", false);
                            editor.commit();

                            stackBuilder.addNextIntent(notificationIntent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                            builder.setContentIntent(resultPendingIntent);
                            builder.setContentTitle(museumId_desc);

                            notificationManager.notify(64647, builder.build());

                        }

                        return null;
                    }
                })
                .withOnExitAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment attachment) {
                        return null;
                    }
                })
                .create();
        this.proximityObserver.addProximityZone(zone1);

        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        // onRequirementsFulfilled
                        new Function0<Unit>() {
                            @Override public Unit invoke() {
                                Log.d("app_beacons", "requirements fulfilled");
                                proximityHandler = proximityObserver.start();
                                return null;
                            }
                        },
                        // onRequirementsMissing
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override public Unit invoke(List<? extends Requirement> requirements) {
                                Log.e("app_beacons", "requirements missing: " + requirements);
                                return null;
                            }
                        },
                        // onError
                        new Function1<Throwable, Unit>() {
                            @Override public Unit invoke(Throwable throwable) {
                                Log.e("app_beacons", "requirements error: " + throwable);
                                return null;
                            }
                        });
    }
}
