package io.github.mpao.magicsquare;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SquareLayout board = new SquareLayout(this);

		board.setBackgroundColor(Color.GRAY);
		setContentView(R.layout.activity_main);
	}
	public void test(View v){
		
		View t = findViewById(R.id.board);
		Casella casella = (Casella) findViewById(R.id.c0);
		View c = t.findViewWithTag(casella.getTag());
		
		c.setBackgroundColor(Color.BLUE);
	}
}
