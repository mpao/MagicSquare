package io.github.mpao.magicsquare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Start_Menu extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide(); // nascondo l'action bar, non mi serve in questa app
		setContentView(R.layout.start__menu);
	}
	@Override
	public void onResume(){
		/* una volta creata l'activity, al resume controllo se esiste il punteggio>0*/
		Button resume = (Button) findViewById(R.id.resume);
		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.save_file_key), Context.MODE_PRIVATE);
		/* il tasto resume è abilitato o disabilitato a seconda di cosa restituisce l'espressione
		 * all'interno delle parentesi, ovvero se punteggio è > 0, restituisce TRUE e quindi il 
		 * tasto è abilitato */
		Log.i("punteggio"," = " + sharedPref.getInt("punteggio", 0));
		resume.setEnabled(sharedPref.getInt("punteggio", 0)>0);
		super.onStart();
	}
	public void startGame(View view) {
		/*  se clicco su start un dialog mi deve avvertire se esiste una partita in corso
			- Una partita in corso esiste, se esiste SharedPreferences e result > 0 
			- SharedPreferences esiste se una partita iniziata ha result > 0 e non è finita
			- Una partita è finita quando non ho più mosse o sono arrivato a 100 */
		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.save_file_key), Context.MODE_PRIVATE);
		/* non occorre controllare se esiste o meno l'entry "punteggio", in quanto se non esiste, viene utilizzato il
		 * valore di default, che ho impostato a 0 e quindi, se non esiste punteggio è uguale a 0
		 * e quindi non occorre avvisare che c'è una partita in corso. */
		if(sharedPref.getInt("punteggio", 0)>0){
			/* creo l'oggetto per un Alert */
			AlertDialog.Builder newGameAlert = new AlertDialog.Builder(this);
			/* ne imposto solo il testo, il titolo */
			newGameAlert.setMessage(R.string.newGameMessageAlert);
			/* pulsante YES */
			newGameAlert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				/* devo implementare il metodo onClick */
		        public void onClick(DialogInterface dialog, int which) {
		        	/* cancello ogni entry di punteggio */
		        	SharedPreferences sharedPref = getBaseContext().getSharedPreferences(getString(R.string.save_file_key), Context.MODE_PRIVATE);
		        	SharedPreferences.Editor editor = sharedPref.edit();
		        	editor.putInt("punteggio", 0);
		        	editor.commit();
		        	/* Creo l'intent, ci abbino il messaggio che è una nuova partita(0) 
		        	 * e lancio l'activity MainActivity. */
					Intent intent = new Intent( getBaseContext(), MainActivity.class );
					intent.putExtra(MESSAGE, 0);			
					startActivity(intent);
		        }
			});
			/* pulsante NO */
			newGameAlert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {/* Click su no, non faccio nulla*/}
		    });
			/* mando a video il messaggio di alert */
			newGameAlert.create().show();
		}else{
			/* punteggio è = 0 o non esiste, quindi lancio l'intent normalmente.
			 * devo istanziare nuovamente l'oggetto intent, poichè mi è servito all'interno
			 * del metodo onClick ma lì rimane */
			Intent intent = new Intent( getBaseContext() , MainActivity.class);
			intent.putExtra(MESSAGE, 0);			
			startActivity(intent);
		}
	}	
	public void resumeGame(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(MESSAGE, 1);
	    startActivity(intent);
	}	
	public final static String MESSAGE = "io.github.mpao.MagicSquare.GAME";

}
