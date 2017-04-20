package edu.arizona.group5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Screen for selecting shapes to be sent to the server application.
 * 
 * @author William Snider, Jordan Smith
 */
public class SplashActivity extends Activity {
    private int Port;
    private String Host;
    private Spinner spinner1;
    private Button set_shape;
    private Bundle bundle = new Bundle();
    private Intent myintent;
    private TextView conn;
    private EditText setText;
    private String shape = "NONE";
    private Context toastTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_splash);
	setText = (EditText) findViewById(R.id.set_text);
	setText.setVisibility(View.INVISIBLE);
	conn = (TextView) findViewById(R.id.lat);
	set_shape = (Button) findViewById(R.id.set_shape);
	myintent = new Intent(SplashActivity.this, ClientActivity.class);
	Bundle b = getIntent().getExtras();
	Port = b.getInt("port");
	Host = b.getString("host");
	conn.setText("Connecting to: " + Host + ":" + Port);

	addListenerOnSpinnerItemSelection();

	toastTo = this;
	set_shape.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
		if (shape.equals("NONE")) {
		    Toast.makeText(toastTo, "Cannot leave shape unselected!",
			    Toast.LENGTH_LONG).show();
		    return;
		}

		if (shape.equals("Text")) {
		    if (setText.getText().toString().equals("")) {

			Toast.makeText(toastTo, "Text feild cannot be empty!",
				Toast.LENGTH_LONG).show();
			return;
		    } else {
			bundle.putString("Text", setText.getText().toString());
		    }
		}

		bundle.putString("Shape", shape);
		bundle.putString("HOST", Host);
		bundle.putInt("PORT", Port);
		myintent.putExtras(bundle);

		Toast.makeText(toastTo, "Shape: " + shape, Toast.LENGTH_SHORT)
			.show();

		SplashActivity.this.startActivity(myintent);
	    }
	});
    }

    public void addListenerOnSpinnerItemSelection() {
	spinner1 = (Spinner) findViewById(R.id.spinner1);
	spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
	    @Override
	    public void onItemSelected(AdapterView<?> parent, View view,
		    int pos, long id) {
		switch (parent.getItemAtPosition(pos).toString()) {
		case "Square":
		    setText.setVisibility(View.INVISIBLE);
		    shape = "Square";
		    break;
		case "Circle":
		    setText.setVisibility(View.INVISIBLE);
		    shape = "Circle";
		    break;
		case "Solid Square":
		    setText.setVisibility(View.INVISIBLE);
		    shape = "Solid Square";
		    break;
		case "Solid Circle":
		    setText.setVisibility(View.INVISIBLE);
		    shape = "Solid Circle";
		    break;
		case "Text":
		    setText.setVisibility(View.VISIBLE);
		    shape = "Text";
		    break;
		default:
		    shape = "NONE";
		    break;
		}
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> arg0) {
		shape = "NONE";
	    }

	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.splash, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// Handle action bar item clicks here. The action bar will
	// automatically handle clicks on the Home/Up button, so long
	// as you specify a parent activity in AndroidManifest.xml.
	int id = item.getItemId();
	if (id == R.id.action_settings) {
	    return true;
	}
	return super.onOptionsItemSelected(item);
    }
}
