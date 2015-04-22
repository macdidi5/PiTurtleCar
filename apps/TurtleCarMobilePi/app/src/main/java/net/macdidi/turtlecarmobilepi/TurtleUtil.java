package net.macdidi.turtlecarmobilepi;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class TurtleUtil {

    private static SharedPreferences sp = null;

    public static final String KEY_BROKER_IP = "BROKER_IP";
    public static final String KEY_BROKER_PORT = "BROKER_PORT";
    public static final String KEY_WEBCAM_IP = "WEBCAM_IP";

    /**
     * Read MQTT broker IP Address
     *
     * @param context Android Context
     * @return MQTT broker IP Address
     */
    public static String getBrokerIP(Context context) {
        return getSharedPreferences(context).getString(KEY_BROKER_IP,
                "192.168.1.51");
    }

    /**
     * Read MQTT broker Port number
     *
     * @param context Android Context
     * @return MQTT broker port number
     */
    public static String getBrokerPort(Context context) {
        return getSharedPreferences(context).getString(KEY_BROKER_PORT,
                "1883");
    }

    public static String getWebcamIP(Context context) {
        return getSharedPreferences(context).getString(KEY_WEBCAM_IP,
                "192.168.1.202");
    }

    /**
     * Save MQTT broker IP Address
     *
     * @param context Android Context
     * @param ip MQTT broker IP Address
     */
    public static void saveBrokerIP(Context context, String ip) {
        SharedPreferences.Editor editor =
                getSharedPreferences(context).edit();
        editor.putString(KEY_BROKER_IP, ip);
        editor.commit();
    }

    /**
     * Save MQTT broker Port number
     *
     * @param context Android Context
     * @return MQTT broker port number
     */
    public static void saveBrokerPort(Context context, String port) {
        SharedPreferences.Editor editor =
                getSharedPreferences(context).edit();
        editor.putString(KEY_BROKER_PORT, port);
        editor.commit();
    }

    public static void saveWebcamIP(Context context, String ip) {
        SharedPreferences.Editor editor =
                getSharedPreferences(context).edit();
        editor.putString(KEY_WEBCAM_IP, ip);
        editor.commit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        if (sp == null) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        }
        else {
            return sp;
        }
    }

    public static boolean checkNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info == null || !info.isConnected()) {
            return false;
        }

        return true;
    }

}
