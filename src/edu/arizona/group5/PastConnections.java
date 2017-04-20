package edu.arizona.group5;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Creates a list of previously saved connections for easy connecting.
 * 
 * @author Jordan Smith
 * 
 */
public class PastConnections extends ListActivity {
    private DataHelper dh;
    private ArrayList<Connection> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	dh = new DataHelper(this);
	list = (ArrayList<Connection>) dh.selectAllConnections();
	setListAdapter(new ArrayAdapter<Connection>(this,
		android.R.layout.simple_list_item_1, list));
	ListView lv = getListView();
	lv.setTextFilterEnabled(true);
	lv.setOnItemClickListener(new OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View view,
		    int position, long id) {
		Connection conn = list.get(position);
		Bundle b = new Bundle();
		b.putInt("port", conn.getPort());
		b.putString("host", conn.getHost());
		Intent intent = new Intent(PastConnections.this,
			SplashActivity.class);
		intent.putExtras(b);
		startActivity(intent);
	    }
	});
    }
}
