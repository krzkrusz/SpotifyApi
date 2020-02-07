package pl.edu.pg.spotify.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class DeviceUtil {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static String getCurrentCPUusage() {
        ProcessBuilder processBuilder;
        String holder = "";
        String[] command = {"sh","-c", "top -n 1"};
        InputStream inputStream;
        Process process;
        byte[] byteArry;
        byteArry = new byte[1024];
        try{
            processBuilder = new ProcessBuilder(command);
            process = processBuilder.start();
            inputStream = process.getInputStream();
            while(inputStream.read(byteArry) != -1){
                holder = holder + new String(byteArry);
            }
            inputStream.close();
        } catch(IOException ex){
            ex.printStackTrace();
        }
        final String clearedFromNewLines = holder.replace("\n", "");
        final int onlyCPUusageLastIndex = clearedFromNewLines.indexOf("IOW");
        return clearedFromNewLines.substring(0, onlyCPUusageLastIndex - 1);
    }

    /**
     * Pass (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE) here in main actvity
     */
    public static String getGsmCellInfo(final TelephonyManager telephony) {
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            try {
                final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
                if (location != null) {
                    return "ID: " + location.getCid() + ", Location area codee: " + location.getLac();
                }
            } catch (SecurityException exc) {
                Log.d("MAIN ACTIVITY", exc.getMessage());
            }
        }
        return "No GSM info.";
    }


    public static String getCurrentRAMUsage(final Object systemService) {
        final long bytesInMegabyte = 1048576L;
        final ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        final ActivityManager activityManager = (ActivityManager) systemService;
        activityManager.getMemoryInfo(mi);
        return (mi.totalMem - mi.availMem) / bytesInMegabyte + "MB";
    }

    public static String getNetworkInfo(final Context ctx) {
        final ConnectivityManager cm = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // np. MOBILE lub WIFI
            final String networkType = activeNetwork.getTypeName();
            // np. LTE
            final String subTypeName = activeNetwork.getSubtypeName();
            return networkType + " - " + subTypeName;
        } else {
            return "Not connected";
        }
    }

    /**
     * FIXME Passed window object has 0 width and height!
     */
    public static void takeScreenshot(final Activity activity, final Window window) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            verifyStoragePermissions(activity);
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
            View v1 = window.getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            Log.d("SCREENSHOT", e.getMessage());
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
