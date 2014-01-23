package com.example.kassascanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity {
	private Button scanBtn;
	private TextView formatTxt, contentTxt;

	private static String SERVER_IP = "145.37.63.17";
	private String EAN;
	private Context context;
	private ClientSender clientSender;
    private Socket socket;
    private Activity act;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		act = this;
		
		context = this.getApplicationContext();

		scanBtn = (Button) findViewById(R.id.scan_button);
		formatTxt = (TextView) findViewById(R.id.scan_format);
		contentTxt = (TextView) findViewById(R.id.scan_content);
		
		//context = this.getApplicationContext();
        //clientSender = new ClientSender(context);
        socket = null;

		scanBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	System.out.println("Server messages");
            	
            	String messageToSend = "HOI";

            	//messageToSend = messageText.getText().toString() + System.getProperty("line.separator");
        	    new ClientSender(MainActivity.this).execute(messageToSend);
                
            	IntentIntegrator.initiateScan(act);
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*@Override
	public void onClick(View v) {
		if (v.getId() == R.id.scan_button) {
			IntentIntegrator.initiateScan(this);
		}
	}*/

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);

		if (scanningResult != null) {
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();

			formatTxt.setText("FORMAT: " + scanFormat);
			contentTxt.setText("CONTENT: " + scanContent);
			EAN = scanContent;
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private class ClientSender extends AsyncTask<String, Void, Socket> {
        private Socket socket;
        private String answer;
        private Context context;
        private DataOutputStream out;
        private DataInputStream in;

        public ClientSender(Context context) {
            this.context = context;
            socket = null;
            out = null;
            in = null;
        }

        @Override
        protected Socket doInBackground(String... params) {
            try {
                if (socket == null) {
                    socket = new Socket(SERVER_IP, 8888);
                    
                    System.out.println("Server message: new socket");

                    out = new DataOutputStream(
    						socket.getOutputStream());
                    in = new DataInputStream(socket.getInputStream());
                }
                
                System.out.println("Server message: " + params[0] );
                
                out.writeUTF(params[0]);
                out.flush();

                //answer = in.readLine() + System.getProperty("line.separator");
                
                answer = in.readUTF();

                return socket;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return socket;
        }

        protected void onPostExecute(Socket socket) {
            if (socket != null) {
                Toast.makeText(context, answer, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(context, "Can't connect to server!",
                        Toast.LENGTH_LONG).show();
            }

        }

    }

}
