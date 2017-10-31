package com.example.arthur.ballsensor.scoresList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arthur.ballsensor.R;

import java.util.ArrayList;

public class ScoresArrayAdapter extends ArrayAdapter<Score> {

	private Integer selected =null;

	public ScoresArrayAdapter( Context context, ArrayList<Score> values) {
		super(context, R.layout.cell_layout, values);
	}

	public void setSelected(Integer selected){
		this.selected = selected;
	}

	public View getView( int position, View convertView, ViewGroup parent)
	{
		View cellView = convertView;
		if (cellView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE);
			cellView = inflater.inflate(R.layout.cell_layout, parent, false);
		}
		if( selected != null && position == selected ){
			cellView.setBackgroundColor( Color.parseColor( "#B2DFEE" ) ); // lightblue
		}

		TextView rankView = (TextView)cellView.findViewById(R.id.rank );
		ImageView imageView = (ImageView)cellView.findViewById(R.id.image);
		TextView scoreView = (TextView)cellView.findViewById(R.id.score);
		Score score = getItem(position);
		int rank = score.getRank();
		int value = score.getValue();
		rankView.setText( String.valueOf(rank) );
		scoreView.setText( String.valueOf( value ) );
		switch(rank) {
			case 1:
				imageView.setImageResource(R.drawable.top0 );
				break;
			case 2:
				imageView.setImageResource(R.drawable.top1 );
				break;
			case 3:
				imageView.setImageResource(R.drawable.top2 );
				break;
			default:
				imageView.setImageResource(R.drawable.top3 );
				break;
		}
		return cellView;
	} // fin de la méthode getView
}