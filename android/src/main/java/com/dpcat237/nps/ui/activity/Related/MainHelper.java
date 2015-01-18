package com.dpcat237.nps.ui.activity.Related;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.service.PlayerService;
import com.dpcat237.nps.behavior.task.SyncNewsTask;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.helper.LoginHelper;
import com.dpcat237.nps.helper.NotificationHelper;
import com.dpcat237.nps.ui.activity.AboutActivity;
import com.dpcat237.nps.ui.activity.AddFeedActivity;
import com.dpcat237.nps.ui.activity.CreateLabelActivity;
import com.dpcat237.nps.ui.activity.ManualActivity;
import com.dpcat237.nps.ui.activity.SettingsActivity;

public class MainHelper {
    public static Context mContext;
    public static Activity mActivity;

    public static Boolean OptionsItemSelector(Context context, Activity activity, View mView, MenuItem item) {
        mContext = context;
        mActivity = activity;
        switch (item.getItemId()) {
            case R.id.buttonSync:
                downloadData(mView, item);
                return true;
            case R.id.buttonAddFeed:
                Intent intentFeed = new Intent(activity, AddFeedActivity.class);
                activity.startActivity(intentFeed);
                return true;
            case R.id.buttonCreateLabel:
                Intent intentLabel = new Intent(activity, CreateLabelActivity.class);
                activity.startActivity(intentLabel);
                return true;
            case R.id.actionLogout:
                logoutConfirmation();
                return true;
            case R.id.buttonActionSettings:
                showSettings();
                return true;
            case R.id.buttonAbout:
                showAbout();
                return true;
            case R.id.buttonDictate:
                PlayerService.playpause(mContext, SongConstants.GRABBER_TYPE_DICTATE_ITEM, 0);
                return true;
            case R.id.buttonManual:
                showManual();
                return true;
        }

        return false;
    }

    private static void downloadData(View mView, MenuItem item) {
        if (!ConnectionHelper.hasConnection(mContext)) {
            NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.error_connection));

            return;
        }

        item.setEnabled(false);
        SyncNewsTask task = new SyncNewsTask(mContext, mView);
        task.execute();
    }

    private static void logoutConfirmation() {
        new AlertDialog.Builder(mActivity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(mContext.getString(R.string.cm_logout))
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doLogout();
                    }

                })
                .setNegativeButton(mContext.getString(R.string.no), null)
                .show();
    }

    private static void doLogout() {
        LoginHelper.doLogout(mActivity);
        mActivity.finish();
    }

    private static void showSettings() {
        Intent intent = new Intent(mActivity, SettingsActivity.class);
        mActivity.startActivity(intent);
    }

    private static void showAbout() {
        Intent intent = new Intent(mActivity, AboutActivity.class);
        mActivity.startActivity(intent);
    }

    private static void showManual() {
        Intent intent = new Intent(mActivity, ManualActivity.class);
        mActivity.startActivity(intent);
    }
}
