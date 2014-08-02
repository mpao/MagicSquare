package io.github.mpao.magicsquare;

import java.util.concurrent.TimeUnit;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Scores extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide(); // nascondo l'action bar, non mi serve in questa app
		setContentView(R.layout.scores);
	}
	@Override
	protected void onStart(){
		/* Questa activity viene lanciata in due occasioni:
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
        /* questa view mi serve per scrivere messaggio di congratulazioni o altro quando si arriva 
         * nella activity dopo la partita. Se si arriva qui dal menù che ci metto ? La parola CLASSIFICA ? */
        TextView message = (TextView)findViewById(R.id.scoreMessage);
        message.setText(this.debugPunteggio(punteggio, tempo));
        /* view con i migliori 10 punteggi.*/
        TextView scores = (TextView)findViewById(R.id.scoresList);
		super.onStart();
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
	public String timeToString(Long t){
		/*restituisce una stringa formattata come 1'21"046 in base al tempo parametro.
		 * TimeUnit non è tanto semplice, poteva essere qualcosa di meglio. Tanto valeva 
		 * fare i calcoli sul long: ad esempio 132984 tolgo le ultime 3 cifre che sono i ms
		 * e divido per 1000. Rimane 132 che sono 2 minuti e 12 secondi. TimeUnit è bello 
		 * da vedere e pulito, ma ci si incasina non poco */
		long minuti   = TimeUnit.MILLISECONDS.toMinutes(t);
		long secondi  = TimeUnit.MILLISECONDS.toSeconds(t) - 
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(t));
		long millisec = t-TimeUnit.MILLISECONDS.toSeconds(t)*1000;	
		/* ok funziona, ma il problema è che essendo numeri non hanno un bell'allineamento:
		 * devo trasformarli in stringa e se sono minori di 10 aggiungere uno 0 davanti.
		 * Utilizzo string.format passando il parametro %02d cioè inserisci il parametro inserendolo
		 * con due cifre, di cui la prima è 0 se minore di 10 ( 3 cifre per i millesimi ) */
		return String.format("%s'%s''%s", String.format("%02d", minuti), String.format("%02d", secondi), String.format("%03d", millisec));
	}
	private String debugPunteggio(Integer p, Long t){
		String test = "Punteggio = "+ p.toString() +"\n";
        test += "tempo = "+ timeToString(t)+"\n";
        test += "tempo_ms = "+ t.toString() +"\n";
        test += "punti = " + calcolaPunti(p, t)+"\n";
        return test;
	}
}
