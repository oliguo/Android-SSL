package example.oliguo.sslconnect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView log_text;
    String res="";
    private static final int COMPLETED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        log_text=(TextView)findViewById(R.id.Log_text);
        new WorkThread().start();
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                updateOutput("completed");
                updateOutput(res);
            }
        }
    };

    private class WorkThread extends Thread {
        @Override
        public void run() {
            res=doPost();
            Message msg = new Message();
            msg.what = COMPLETED;
            handler.sendMessage(msg);
        }
    }

    private void updateOutput(String text) {
        log_text.setText(log_text.getText() + "\n\n" + text);
    }

    private String doPost(){
        SSLSelfSender https = new SSLSelfSender();
        String resp = https.send(this, "https://google.com.hk");
        return resp;
//        URL url = null;
//        try {
//            url = new URL("https://google.com.hk");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        URLConnection urlConnection = null;
//        try {
//            urlConnection = url.openConnection();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        InputStream in = null;
//        try {
//            in = urlConnection.getInputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        byte data[] = new byte[256];
//        int length = 0, getPer = 0;
//        try {
//            while ((getPer = in.read(data)) != -1) {
//                length += getPer;
//                byteArrayOutputStream.write(data, 0, getPer);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            in.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            byteArrayOutputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String utf8 = null;
//        try {
//            utf8 = new String(byteArrayOutputStream.toByteArray(), "UTF-8").trim();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return utf8;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
