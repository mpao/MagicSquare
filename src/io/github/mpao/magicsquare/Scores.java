package io.github.mpao.magicsquare;

import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Scores extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide(); // nascondo l'action bar, non mi serve in questa app
		/* Questa activity viene creata in due occasioni:
		 * 1. quando finisce una partita
		 * 2. dal menù iniziale, cliccando sul pulsante per vedere la classifica.
		 * Nel primo caso, viene passato un intent che contiene il punteggio e il tempo della
		 * partita e con questi valori va calcolato il punteggio finale e inserito nella
		 * classifica generale se è il caso.
		 * Nel secondo caso, devo mostrare semplicemente la classifica e il pulsante BACK */
		Intent intent = getIntent();
		/* utilizzo gli oggetti invece che i tipi primitivi per poter usare toString() senza 
		 * troppi cast. getIntExtra e getLongExtra, hanno come parametro 0 e 1 (tempo sta a denominatore), 
		 * che è il valore di default che assumono punteggio e tempo se l'intent è vuoto. */
        Integer punteggio = intent.getIntExtra(MainActivity.MESSAGE_PUNTEGGIO, 0);
        Long tempo 		  = intent.getLongExtra(MainActivity.MESSAGE_TEMPO, 1);
        if(punteggio>0){
        /* se il punteggio è maggiore di zero, vuol dire arrivo in questa activity da una
         * partita appena finita. Elimino quindi subito tutti i salvataggi, in modo che non
         * possa essere più ripresa e che il tasto RESUME appaia disabilitato */
    		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.save_file_key), Context.MODE_PRIVATE);
        	SharedPreferences.Editor editor = sharedPref.edit();	
        	editor.clear(); // metodo clear, rimuove tutte le chiavi !
        	editor.commit();
        /* TODO
         * Devo salvare nel database le seguenti informazioni, ordine per il quale voglio anche 
         * che venga successivamente rappresentata anche la classifica:
         * 1. Punti				calcolaPunti(punteggio, tempo)
         * 2. Punteggio			arriva con intent
         * 3. Tempo impiegato	timeToString(tempo)
         * Inoltre devo inserire le modalità in cui è stato ottenuto il punteggio, ovvero se l'help era attivo
         * o meno e se era una partita basic o expert */
        }
        // rappresentazione provvisoria
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		/* utilizzo ancora LayoutInflater invece che un layout statico in XML per inserire il messaggio,
		 * ma credo che in layout definitivo possa trovare posto normalmente */
		ViewGroup root = (ViewGroup)findViewById(R.layout.scores);
		View mainLayout = inflater.inflate(R.layout.scores, root);

        TextView textView = new TextView(this);
        textView.setTextSize(40);
        String test = "Punteggio = "+ punteggio.toString() + " tempo = "+ timeToString(tempo)+"\n";
        test = test + "punti = " + calcolaPunti(punteggio, tempo);
        textView.setText(test);           
        ((LinearLayout) mainLayout).addView(textView,0);		
		setContentView(mainLayout);
	}
	
	public void tornaAlMenu(View v){
		/* metodo del pulsante per chiudere l'activity e tornare al menù */
		startActivity(new Intent(this, Start_Menu.class));
	}
	
	public Integer calcolaPunti(int p, long t){
		/* ho giocato con l'help attivo ? */
		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
		boolean help = sharedPref.getBoolean("Help", true);
		int	punti = 0;
		final long 	ORDINEGRANDEZZA	= 1000000000000L;
		final int 	BONUS 			= 500;
		/* formula per il calcolo de punteggio: uso il float e poi moltiplico per
		 * ordine di grandezza, per non perdermi i decimali che mi servono per meglio
		 * definire il punteggio. Il tutto poi torna ad essere un int.
		 * CONSIDERAZIONI: la formula deve essere inversamente proporzionale al tempo,
		 * ovvero più ci impieghi e meno punti ti assegno. Inoltre, voglio che anche un
		 * miglioramento di qualche millesimo di secondo sia da premiare al raggiungimento
		 * dello stesso punteggio, quindi diventa inversamente proporzionale al quadrato
		 * del tempo: stesso punteggio, ma un secondo migliore ha un gain notevole!
		 * Così facendo però i numeri sono di diversi ordini di grandezza differenti */
		punti = (int)((float)(p * ORDINEGRANDEZZA / (t*t) ));
		// bonus se raggiungi 100
		if(p==100) punti += BONUS;
		// malus per utilizzo dell'help ( disattivato con diviso 1)
		if(help) punti /= 1; 
		// per fare tanti punti devi avere almeno un punteggio decente dai :)
		if(p<70) punti /= 10000 ;
		return punti;
	}
	public String timeToString(Long l){
		//restituisce una stringa formattata come 1'21"046 in base al tempo parametro
		long minuti   = TimeUnit.MILLISECONDS.toMinutes(l);
		long secondi  = TimeUnit.MILLISECONDS.toSeconds(l);
		long millisec = TimeUnit.MILLISECONDS.toMillis(l/10);
		return String.format("%d'%d''%d", minuti, secondi, millisec);
	}
}
