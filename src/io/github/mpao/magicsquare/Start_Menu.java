package io.github.mpao.magicsquare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Start_Menu extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide(); // nascondo l'action bar, non mi serve in questa app
		setContentView(R.layout.start__menu);
	}
	public void startGame(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(MESSAGE, 0);
		startActivity(intent);
	}	
	public void resumeGame(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(MESSAGE, 1);
	    startActivity(intent);
	}	
	public final static String MESSAGE = "io.github.mpao.MagicSquare.GAME";

}
