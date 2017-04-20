package edu.arizona.group5;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import model.commands.Command;
import model.commands.DisconnectFromServer;
import model.commands.DrawToScreen;
import model.commands.Drawing;
import model.commands.HideCommand;
import model.commands.MovePointCommand;
import model.commands.ServerInterface;
import model.extra.ColorPoint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Handles the server input as well as sends message to the server about
 * move/hide data.
 * 
 * @author William Snider, Jordan Smith
 */
public class ClientActivity extends Activity implements SensorEventListener {
    private ServerHandler serverHandler = null;
    private static String HOST;
    private int PORT;
    private View client_color;
    private float X;
    private float Y;
    private float Z;
    private boolean DRAW = false;
    private SensorManager sensorManager;
    private Button disconnectButton;
    private Button draw;
    private TextView xCoor; // declare X axis object
    private TextView yCoor; // declare Y axis object
    private TextView zCoor; // declare Z axis object
    private TextView ID;
    private TextView connection;
    private Button connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_client);

	// Buttons
	connect = (Button) findViewById(R.id.client_connect);
	draw = (Button) findViewById(R.id.draw_button);
	draw.setVisibility(View.INVISIBLE);
	disconnectButton = (Button) findViewById(R.id.disconnect_button);
	disconnectButton.setVisibility(View.INVISIBLE);

	// TextViews
	xCoor = (TextView) findViewById(R.id.xcoor); // create X axis object
	yCoor = (TextView) findViewById(R.id.ycoor); // create Y axis object
	zCoor = (TextView) findViewById(R.id.zcoor); // create Z axis object
	ID = (TextView) findViewById(R.id.client_id);
	connection = (TextView) findViewById(R.id.textView1);

	// Color box
	client_color = (View) findViewById(R.id.view1);

	Bundle b = getIntent().getExtras();
	HOST = b.getString("HOST");
	PORT = b.getInt("PORT");
	connection.setText(HOST + ":" + PORT);

	sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	sensorManager.registerListener(this,
		sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		SensorManager.SENSOR_DELAY_GAME);

	connect.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		serverHandler = new ServerHandler(HOST, PORT);
		serverHandler.start();
	    }
	});

	draw.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {

		if (DRAW == true) {
		    serverHandler.send(new HideCommand<ServerHandler>(ID
			    .toString(), true));
		    DRAW = false;
		} else {
		    serverHandler.send(new HideCommand<ServerHandler>(ID
			    .toString(), false));
		    DRAW = true;
		}
	    }
	});

	disconnectButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		onDestroy();
		Intent myintent = new Intent(ClientActivity.this,
			MainActivity.class);
		startActivity(myintent);
	    }
	});

    }

    public void assign_shape() {
	Bundle b = getIntent().getExtras();
	String my_shape = b.getString("Shape");
	switch (my_shape) {
	case "Square":
	    serverHandler.send(new DrawToScreen<ServerHandler>(ID.toString(),
		    Drawing.RECTANGLE, 80, 80, null));
	    break;
	case "Circle":
	    serverHandler.send(new DrawToScreen<ServerHandler>(ID.toString(),
		    Drawing.CIRCLE, 80, 80, null));
	    break;
	case "Solid Square":
	    serverHandler.send(new DrawToScreen<ServerHandler>(ID.toString(),
		    Drawing.RECTANLE_FILLED, 80, 80, null));
	    break;
	case "Solid Circle":
	    serverHandler.send(new DrawToScreen<ServerHandler>(ID.toString(),
		    Drawing.CIRCLE_FILLED, 80, 80, null));
	    break;
	case "Text":
	    serverHandler.send(new DrawToScreen<ServerHandler>(ID.toString(),
		    Drawing.TEXT, 80, 80, b.getString("Text")));
	    break;
	}
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {
	// check sensor type
	if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	    // assign directions
	    X = event.values[0];
	    Y = event.values[1];
	    Z = event.values[2];
	    if (DRAW == true) {
		serverHandler.movePointer(-X, -Y);
	    }
	    xCoor.setText("X: " + X);
	    yCoor.setText("Y: " + Y);
	    zCoor.setText("Z: " + Z);
	}
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	if (serverHandler != null) {
	    serverHandler.disconnect(null);
	}
    }

    /**
     * 
     * Handles server input and responds the it. Also helps client forward
     * messages to the server.
     * 
     * @author William Snider
     * 
     */
    private class ServerHandler extends Thread implements ServerInterface {
	private String id;
	private ColorPoint color_point;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private Socket client;
	private String host;
	private int port;

	public ServerHandler(String host, int port) {
	    this.host = host;
	    this.port = port;
	}

	@Override
	public void run() {
	    try {
		client = new Socket(host, port);

		input = new ObjectInputStream(client.getInputStream());
		output = new ObjectOutputStream(client.getOutputStream());

		id = (String) input.readObject();
		color_point = (ColorPoint) input.readObject();

		// Letting UI thread mess with ui
		ClientActivity.this.runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
			ID.setText("ID: " + id);
			client_color.setBackgroundColor(Color.rgb(
				color_point.getRed(), color_point.getGreen(),
				color_point.getBlue()));
			connect.setVisibility(View.INVISIBLE);
			draw.setVisibility(View.VISIBLE);
			disconnectButton.setVisibility(View.VISIBLE);
		    }
		});

		assign_shape();

		while (true) {
		    Object object = input.readObject();
		    if (object instanceof Command<?>) {
			@SuppressWarnings("unchecked")
			Command<ServerHandler> command = (Command<ServerHandler>) object;
			command.execute(this);
		    }
		}
	    } catch (SocketException e) {
		e.printStackTrace();
	    } catch (UnknownHostException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    } catch (ClassNotFoundException e) {
		e.printStackTrace();
	    }
	}

	@Override
	public void disconnect(String ignored) {
	    try {
		output.writeObject(new DisconnectFromServer<ServerInterface>(id));
		output.flush();
		output.close();
		input.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	@Override
	public void send(Command<?> cmd) {
	    try {
		cmd.setOwner(id);
		output.writeObject(cmd);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	public void movePointer(float x, float y) {
	    if (id != null) {

		move(id, x, y);
	    }
	}

	@Override
	public void move(String id, float x, float y) {
	    try {
		output.writeObject(new MovePointCommand<ServerInterface>(id, x,
			y));
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	@Override
	public void draw(String id, Drawing draw, int width, int height,
		String extra) {
	    try {
		output.writeObject(new DrawToScreen<ServerHandler>(id, draw,
			width, height, null));
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	}

	@Override
	public void erase(String id, boolean hide) {
	    // Not supported on client, server only method
	}

	@Override
	public void changeColor(String id, int red, int green, int blue) {
	    // Not supported on client, server only method
	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	int op = item.getItemId();
	if (op == R.id.action_settings) {
	    return true;
	}
	return super.onOptionsItemSelected(item);
    }
}
