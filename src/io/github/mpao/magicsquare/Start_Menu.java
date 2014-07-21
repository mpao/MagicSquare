package io.github.mpao.magicsquare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
		/*  se clicco su start un dialog mi deve avvertire se esiste una partita in corso
			- Una partita in corso esiste, se esiste SharedPreferences e result > 0 
			- SharedPreferences esiste se una partita iniziata ha result > 0 e non è finita
			- Una partita è finita quando non ho più mosse o sono arrivato a 100 */
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		if(sharedPref.contains("punteggio") && sharedPref.getInt("punteggio", 0)>0){
			/* se esiste "punteggio" e (lazy) se esiste è maggiore di 0 
			 * allora mostrami un alert che mi dice se sono sicuro di perdere i 
			 * progressi dell'altra partita */
		}
		startActivity(intent);
	}	
	public void resumeGame(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(MESSAGE, 1);
	    startActivity(intent);
	}	
	public final static String MESSAGE = "io.github.mpao.MagicSquare.GAME";

}
