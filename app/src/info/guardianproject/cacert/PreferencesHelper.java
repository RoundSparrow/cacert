package info.guardianproject.cacert;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by adminsag on 4/22/14.
 */
public class PreferencesHelper {

    public static boolean getLoadKeyStoreFile(Context inContext)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(inContext);
        return settings.getBoolean("loadKeyStoreFile", true);
    }

    public static String getFileKeyStorePath(Context inContext)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(inContext);
        return settings.getString("fileKeyStorePath", "/system/etc/security/cacerts.bks");
    }

    public static String getPasswordKeyStore(Context inContext)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(inContext);
        return settings.getString("passwordKeyStore", "changeit");
    }

    public static String getTypeKeyStore(Context inContext)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(inContext);
        return settings.getString("typeKeyStore", "BKS");
    }
}
