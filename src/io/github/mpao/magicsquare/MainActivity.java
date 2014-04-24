package io.github.mpao.magicsquare;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
<<<<<<< HEAD
import android.widget.LinearLayout;

=======
import android.view.View;
>>>>>>> 48a58639e6960449fb494f1ade0b3600c087ad35

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SquareLayout board = new SquareLayout(this);
		board.setBackgroundColor(Color.GRAY);
<<<<<<< HEAD
		
		//LinearLayout mainLayout = (LinearLayout)findViewById(R.id.main);
		//mainLayout.addView(board);
		setContentView(board);
=======
		setContentView(R.layout.activity_main);
	}
	public void test(View v){
		
		View t = findViewById(R.id.board);
		Casella casella = (Casella) findViewById(R.id.c0);
		View c = t.findViewWithTag(casella.getTag());
		
		c.setBackgroundColor(Color.BLUE);
>>>>>>> 48a58639e6960449fb494f1ade0b3600c087ad35
	}
}
