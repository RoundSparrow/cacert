/* Copyright (c) 2011, Nathan Freitas,/ The Guardian Project - https://guardianproject.info */
/* See LICENSE for licensing information */

package info.guardianproject.cacert;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;

import java.security.cert.Certificate;

import android.content.Context;
import android.os.Build;
import android.util.Log;

public class CACertManager {

    private final static String TAG = "CACert";

    KeyStore ksCACert;
    //private final static String KEYSTORE_TYPE = "BKS";
    Process superUserTestProcess = null;
    Context androidContext;

    public CACertManager(Context inContext) {
        try {
            androidContext = inContext;
            superUserTestProcess = Runtime.getRuntime().exec("su");
            // ToDo: caller should check that this worked - SU is available on this system
        } catch (IOException e) {
            Log.e(TAG, "Exception in CACertManager ", e);
        }
    }

    public void load(String path, String password, String keystoreType) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        ksCACert = KeyStore.getInstance(keystoreType);

        // code taken from GPL library AndroidPinning
        if (Build.VERSION.SDK_INT >= 14) {
            if (PreferencesHelper.getLoadKeyStoreFile(androidContext)) {
                File fileKeyStore = new File(path);
                if (fileKeyStore.exists()) {
                    Log.i(TAG, "file exists in CACertManager " + path);
                    InputStream trustStoreStream = new FileInputStream(fileKeyStore);
                    ksCACert.load(trustStoreStream, password.toCharArray());
                }
                else
                {
                    Log.e(TAG, "Can't read file in CACertManager " + path);
                }
            }
            else
            {
                ksCACert = KeyStore.getInstance("AndroidCAStore");
                ksCACert.load(null, null);
            }
        } else {
            InputStream trustStoreStream = new FileInputStream(new File(path));
            ksCACert.load(trustStoreStream, password.toCharArray());
        }
    }

    public Enumeration<String> getCertificateAliases() throws KeyStoreException {
        return ksCACert.aliases();
    }

    public int size() throws KeyStoreException {
        return ksCACert.size();
    }

    public Certificate getCertificate(String alias) throws KeyStoreException {
        return ksCACert.getCertificate(alias);
    }

    public Certificate[] getCertificateChain(String alias) throws KeyStoreException {
        return ksCACert.getCertificateChain(alias);
    }

    public void addCertificate(String alias, Certificate cert) throws KeyStoreException {
        ksCACert.setCertificateEntry(alias, cert);
    }

    public void delete(String alias) throws KeyStoreException {
        ksCACert.deleteEntry(alias);
    }

    public void delete(Certificate cert) throws KeyStoreException {
        ksCACert.deleteEntry(ksCACert.getCertificateAlias(cert));

    }

    public void save(String targetPath, String password) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        File fileNew = new File(targetPath);

        if (fileNew.exists() && (!fileNew.canWrite()))
            throw new FileNotFoundException("Cannot write to: " + targetPath);
        else if (fileNew.getParentFile().exists() && (!fileNew.getParentFile().canWrite()))
            throw new FileNotFoundException("Cannot write to: " + targetPath);

        OutputStream trustStoreStream = new FileOutputStream(new File(targetPath));
        ksCACert.store(trustStoreStream, password.toCharArray());
        trustStoreStream.close();
    }
}
