package jp.ueda.mcpebackup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

/**
 * Created by takec on 2017/04/29.
 */

public class InstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName=intent.getData().getEncodedSchemeSpecificPart();
        if(packageName.equals("com.mojang.minecraftpe")){
            intent.setClass(context, MainActivity.class);
            Bundle bandle = new Bundle();
            bandle.putString("type", "backup");
            intent.putExtras(bandle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
