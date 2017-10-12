package com.example.arthur.ballsensor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoresArrayAdapter extends ArrayAdapter<Score> {

	private final Context context;
	private final ArrayList<Score> values;

	public ScoresArrayAdapter( Context context, ArrayList<Score> values) {
		super(context, R.layout.cell_layout, values);
		this.context = context;
		this.values = values;
	}

	/*
	@Override
	protected void onCreate( @Nullable Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		String[] systems = getResources().getStringArray(R.array.systems);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, systems));
	}

	@Override
	protected void onListItemClick( ListView l, View v, int position, long id ) {
		super.onListItemClick( l, v, position, id );
		Toast.makeText(this, "item " + id + " clicked", Toast.LENGTH_SHORT).show();
	} */

	public View getView( int position, View convertView, ViewGroup parent)
	{
		View cellView = convertView;
		if (cellView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
			cellView = inflater.inflate(R.layout.cell_layout, parent, false);
		}

		TextView labelView = (TextView)cellView.findViewById(R.id.label);
		ImageView imageView = (ImageView)cellView.findViewById(R.id.image);
		TextView scoreView = (TextView)cellView.findViewById(R.id.score);
		Score t = getItem(position);
		String s = t.getName();
		int v = t.getValue();
		labelView.setText(s);
		scoreView.setText( String.valueOf( v ) );
		System.out.println(s);
		switch(t.getRank()) {
			case 1:
				imageView.setImageResource(R.drawable.top0 );
				break;
			case 2:
				imageView.setImageResource(R.drawable.top1 );
				break;
			case 3:
				imageView.setImageResource(R.drawable.top2 );
				break;
			case 4:
				imageView.setImageResource(R.drawable.top3 );
				break;
			default:
				break;
		}

		return cellView;
	} // fin de la méthode getView
}