package io.github.mpao.magicsquare;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SquareLayout board = new SquareLayout(this);
		board.setBackgroundColor(Color.GRAY);
		
		//LinearLayout mainLayout = (LinearLayout)findViewById(R.id.main);
		//mainLayout.addView(board);
		setContentView(board);
	}
}
