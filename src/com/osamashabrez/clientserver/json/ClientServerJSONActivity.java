package com.osamashabrez.clientserver.json;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class ClientServerJSONActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        buildref = (EditText) findViewById(R.id.editTextbuild);
        buildref.setFocusable(false);buildref.setClickable(false);
        recvdref = (EditText) findViewById(R.id.editTextrecvd);
        recvdref.setFocusable(false);recvdref.setClickable(false);
        JSONObject jsonobj; // declared locally so that it destroys after serving its purpose
        jsonobj = new JSONObject();
        try {
        	// adding some keys
        	jsonobj.put("key", "value");
        	jsonobj.put("weburl", "hashincludetechnology.com");
        	
        	// lets add some headers (nested headers)
        	JSONObject header = new JSONObject();
        	header.put("devicemodel", android.os.Build.MODEL); // Device model
        	header.put("deviceVersion", android.os.Build.VERSION.RELEASE); // Device OS version
        	header.put("language", Locale.getDefault().getISO3Language()); // Language
        	jsonobj.put("header", header);
        	// Display the contents of the JSON objects
        	buildref.setText(jsonobj.toString(2));
        } catch (JSONException ex) {
        	buildref.setText("Error Occurred while building JSON");
        	ex.printStackTrace();
        }
        // Now lets begin with the server part
        try {
        	DefaultHttpClient httpclient = new DefaultHttpClient();
        	HttpPost httppostreq = new HttpPost(wurl);
        	StringEntity se = new StringEntity(jsonobj.toString());
        	//se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        	se.setContentType("application/json;charset=UTF-8");
        	se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
        	httppostreq.setEntity(se);
//        	httppostreq.setHeader("Accept", "application/json");
//        	httppostreq.setHeader("Content-type", "application/json");
//        	httppostreq.setHeader("User-Agent", "android");
        	HttpResponse httpresponse = httpclient.execute(httppostreq);
        	HttpEntity resultentity = httpresponse.getEntity();
        	if(resultentity != null) {
        		InputStream inputstream = resultentity.getContent();
        		Header contentencoding = httpresponse.getFirstHeader("Content-Encoding");
        		if(contentencoding != null && contentencoding.getValue().equalsIgnoreCase("gzip")) {
        			inputstream = new GZIPInputStream(inputstream);
        		}
        		
        		String resultstring = convertStreamToString(inputstream);
        		inputstream.close();
        		resultstring = resultstring.substring(1,resultstring.length()-1);
        		recvdref.setText(resultstring + "\n\n" + httppostreq.toString().getBytes());
//        		JSONObject recvdjson = new JSONObject(resultstring);
//            	recvdref.setText(recvdjson.toString(2));
        	}
        } catch (Exception e) {
        	recvdref.setText("Error Occurred while processing JSON");
        	recvdref.setText(e.getMessage());
		}
    }

    private String convertStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Stream Exception", Toast.LENGTH_SHORT).show();
        }
        return total.toString();
    }
    public EditText buildref;
    public EditText recvdref;
    public static final String wurl = "http://192.168.100.4/testmysql.php?test=true";
}