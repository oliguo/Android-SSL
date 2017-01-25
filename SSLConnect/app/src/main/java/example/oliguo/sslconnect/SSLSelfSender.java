package example.oliguo.sslconnect;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public class SSLSelfSender {
    KeyStore trustStore = null;

    public String send(Context context, String urlString){
        //Create ssl context
        SSLContext sslContext = prepareSelfSign(context);
        if(trustStore==null||sslContext==null){
            return null;
        }
        // Tell the URLConnection to use a SocketFactory from our SSLContext
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if(url==null){
            return null;
        }
        try {
            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
            // Use ssl context
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            // Get result
            InputStream is = urlConnection.getInputStream();
            if (is != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte data[] = new byte[256];
                int length = 0, getPer = 0;
                while ((getPer = is.read(data)) != -1) {
                    length += getPer;
                    byteArrayOutputStream.write(data, 0, getPer);
                }
                is.close();
                byteArrayOutputStream.close();
                String utf8 = new String(byteArrayOutputStream.toByteArray(), "UTF-8").trim();
                return utf8;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SSLContext prepareSelfSign(Context context){

        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        if(trustStore==null){
            return null;
        }
        SSLContext sslContext = null;
        InputStream crtInput = null;
        try {
            // 載入憑證檔
            crtInput = context.getAssets().open("google.crt");//export 'DER' type of cert by firebox
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(crtInput);
            Certificate ca = cf.generateCertificate(caInput);
            trustStore.load(null, null);
            trustStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(trustStore);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } finally {
            if(crtInput!=null) {
                try {
                    crtInput.close();
                } catch (IOException e) {
                }
            }
        }
        return sslContext;
    }
}
