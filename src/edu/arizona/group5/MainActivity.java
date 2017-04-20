package edu.arizona.group5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Initial screen for getting host and port for connecting to the server.
 * 
 * @author Jordan Smith
 */
public class MainActivity extends Activity {
    private DataHelper dh;
    private Button connectionButton;
    private EditText host_ip;
    private EditText host_port;
    private String host_main;
    private String port_main;
    private Button recent_conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	dh = new DataHelper(this);
	host_ip = (EditText) findViewById(R.id.host_ip);
	host_port = (EditText) findViewById(R.id.host_port);
	recent_conn = (Button) findViewById(R.id.rec_conn);

	recent_conn.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		Intent myintent = new Intent(MainActivity.this,
			PastConnections.class);
		startActivity(myintent);
	    }
	});

	connectionButton = (Button) findViewById(R.id.connect);

	connectionButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		host_main = host_ip.getText().toString();
		port_main = host_port.getText().toString();

		if (!host_main.equals("") && !port_main.equals("")) {
		    Intent myintent = new Intent(MainActivity.this,
			    SplashActivity.class);
		    Bundle bundle = new Bundle();
		    bundle.putString("host", host_main);
		    bundle.putInt("port", Integer.parseInt(port_main));
		    myintent.putExtras(bundle);
		    dh.insertConnection(host_main, port_main);
		    startActivity(myintent);
		}
	    }
	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	int op = item.getItemId();
	if (op == R.id.pastConnections) {
	    Intent myintent = new Intent(MainActivity.this,
		    PastConnections.class);
	    startActivity(myintent);
	    return true;
	}
	return super.onOptionsItemSelected(item);
    }
}
