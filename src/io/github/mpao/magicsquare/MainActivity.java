package io.github.mpao.magicsquare;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SquareLayout board = new SquareLayout(this);
		board.setBackgroundResource(R.color.board);
		
		/* http://developer.android.com/reference/android/view/LayoutInflater.html
		 * Carico e scompatto il layout principale */
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mainLayout = inflater.inflate(R.layout.activity_main, null);
		/* Attenzione al CAST: nel main uso LinearLayout, dopo di che, aggiungo la board */
		((LinearLayout) mainLayout).addView(board);
		setContentView(mainLayout);
	}
}
