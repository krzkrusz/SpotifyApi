package pl.edu.pg.spotify.util;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;

public class DeviceUtil {

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


    public static String getCurrentRAMUsage(final Object systemService) {
        final long bytesInMegabyte = 1048576L;
        final ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        final ActivityManager activityManager = (ActivityManager) systemService;
        activityManager.getMemoryInfo(mi);
        return (mi.totalMem - mi.availMem) / bytesInMegabyte + "MB";
    }

    public static void getNetworkInfo(final Context ctx) {
        final ConnectivityManager cm = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // np. MOBILE lub WIFI
            final String networkType = activeNetwork.getTypeName();
            // np. LTE
            final String subTypeName = activeNetwork.getSubtypeName();
        } else {
            // not connected to the internet
        }
    }

}
