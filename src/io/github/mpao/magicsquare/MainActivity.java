package io.github.mpao.magicsquare;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SquareLayout board = new SquareLayout(this);

		board.setBackgroundColor(Color.GRAY);
		setContentView(board);
	}
}
