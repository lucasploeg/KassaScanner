package com.example.kassascanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity implements OnClickListener {
	private Button scanBtn;
	private TextView formatTxt, contentTxt;

	private static String SERVER_IP = "192.168.42.148";
	private String EAN;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = this.getApplicationContext();

		scanBtn = (Button) findViewById(R.id.scan_button);
		formatTxt = (TextView) findViewById(R.id.scan_format);
		contentTxt = (TextView) findViewById(R.id.scan_content);

		scanBtn.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.scan_button) {
			IntentIntegrator.initiateScan(this);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);

		if (scanningResult != null) {
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();

			formatTxt.setText("FORMAT: " + scanFormat);
			contentTxt.setText("CONTENT: " + scanContent);
			EAN = scanContent;
			try {
				Socket s = new Socket("192.168.42.148", 21111);
				String message = "blablabla";

				PrintWriter outp = null;
				BufferedReader inp = null;
				String serverMsg = null;

				outp = new PrintWriter(s.getOutputStream(), true);
				inp = new BufferedReader(new InputStreamReader(
						s.getInputStream()));
				serverMsg = inp.readLine();

				if (message != null) {
					// convo.append(message + "\n");
					outp.println(message);
					serverMsg = inp.readLine();
					// convo.append(serverMsg + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
}
