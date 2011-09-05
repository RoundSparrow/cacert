package info.guardianproject.cacert;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Enumeration;

import java.security.cert.Certificate;

public class CACertManager {

    KeyStore ksCACert;
    private final static String KEYSTORE_TYPE = "BKS";
    
    private final static String CMD_REMOUNT_RW = "grep \" /system \" /proc/mounts | awk '{system(\"mount -o rw,remount -t \"$3\" \"$1\" \"$2)}'";
    private final static String CMD_REMOUNT_RO = "grep \" /system \" /proc/mounts | awk '{system(\"mount -o ro,remount -t \"$3\" \"$1\" \"$2)}'";
    
    private final static String CMD_CHANGE_PERMS = "chmod 777";
    
    public void load (String path, String password) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException
    {
    	ksCACert = KeyStore.getInstance(KEYSTORE_TYPE);
    
    	InputStream trustStoreStream = new FileInputStream(new File(path));
    	ksCACert.load(trustStoreStream, password.toCharArray());
    }
    
    public Enumeration<String> getCertificateAliases () throws KeyStoreException
    {
    	return ksCACert.aliases();
    	
    	
    }
    
    public int size ()  throws KeyStoreException
    {
    	return ksCACert.size();
    }
  
    public Certificate getCertificate (String alias) throws KeyStoreException
    {
    	return ksCACert.getCertificate(alias);
    	
    }
    
    public Certificate[] getCertificateChain (String alias) throws KeyStoreException
    {
    	return ksCACert.getCertificateChain(alias);
    }

    public void addCertificate (String alias, Certificate cert) throws KeyStoreException
    {
    	ksCACert.setCertificateEntry(alias, cert);
    }
    
    public void delete(String alias)  throws KeyStoreException
    {
    	ksCACert.deleteEntry(alias);
    	
    }
    
    public void delete(Certificate cert)  throws KeyStoreException
    {
    	ksCACert.deleteEntry(ksCACert.getCertificateAlias(cert));
    	
    }
    
    public void save (String targetPath, String password) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException
    {
    	if (!new File(targetPath).canWrite())
    		throw new FileNotFoundException("Cannot write to: " + targetPath);
    	
    	OutputStream trustStoreStream = new FileOutputStream(new File(targetPath));
    	ksCACert.store(trustStoreStream, password.toCharArray());
    }
    
    public void remountAndCopy (String srcPath, String targetPath) throws IOException
    {
    
    //	String cmd = "mount -o rw,remount -t ext3 /dev/block/mmcblk1p21 /system";
    	
    	Process p = Runtime.getRuntime().exec("su");
    	DataOutputStream os = new DataOutputStream(p.getOutputStream());
    	os.writeBytes(CMD_REMOUNT_RW + "\n");    
    	os.writeBytes(CMD_CHANGE_PERMS + ' ' + targetPath + '\n');
    	
    	if (!new File(targetPath).canWrite())
    		throw new FileNotFoundException("Cannot write to: " + targetPath);
    	
    	os.writeBytes("cp " + srcPath + ' ' + targetPath + '\n');
    	os.writeBytes("exit\n");  
    	os.flush();
    }
    
    //mount -o rw,remount -t yaffs2 /dev/block/mtdblock3 /system
    
    public void remountSystemRO () throws IOException
    {
    	Process p = Runtime.getRuntime().exec("su");
    	DataOutputStream os = new DataOutputStream(p.getOutputStream());
    	os.writeBytes(CMD_REMOUNT_RO + "\n");          
    	os.writeBytes("exit\n");  
    	os.flush();
    	
    }
}
