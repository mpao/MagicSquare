/*******************************************************************************
 * This software is distributed under the following BSD license:
 *
 * Copyright (c) 2014, Marco Paoletti <mpao@me.com>, http://mpao.github.io
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package io.github.mpao.magicsquare;

import io.github.mpao.magicsquare.DB_Contract.Classifica;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Scores extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getActionBar().hide(); // nascondo l'action bar, non mi serve in questa app
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
        /* istanzio l'oggetto dbHelper che mi serve sia in scrittura che in lettura */
        DB_Helper dbHelper = new DB_Helper(this.getBaseContext());
        if(punteggio>0){
        /* se il punteggio è maggiore di zero, vuol dire arrivo in questa activity da una
         * partita appena finita. Elimino quindi subito tutti i salvataggi, in modo che non
         * possa essere più ripresa e che il tasto RESUME appaia disabilitato */
    		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.save_file_key), Context.MODE_PRIVATE);
        	SharedPreferences.Editor editor = sharedPref.edit();	
        	editor.clear(); // metodo clear, rimuove tutte le chiavi !
        	editor.commit();
        	/* a questo punto mi connetto al database e utilizzando ContentValues passo i 
        	 * valori alle rispettive colonne */
        	SQLiteDatabase db = dbHelper.getWritableDatabase();
        	ContentValues values = new ContentValues();
        	values.put(Classifica.COLUMN_NAME_PUNTEGGIO, punteggio);
        	values.put(Classifica.COLUMN_NAME_PUNTI, calcolaPunti(punteggio, tempo));
        	values.put(Classifica.COLUMN_NAME_TEMPO, tempo);
        	values.put(Classifica.COLUMN_NAME_HELP, true);
        	values.put(Classifica.COLUMN_NAME_TYPE, "Basic");
        	/* ed inserisco il nuovo record */
        	db.insert(Classifica.TABLE_NAME,null,values);
        }
        /* questa porzione di codice viene eseguita sempre, sia quando ho appena finito una partita
         * sia quando dal menù, clicco il pulsante dei punteggi. Istanzio SQLiteDatabase in modalità
         * lettura ed eseguo una select per ottenere i primi 10 risultati*/
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery("select * from Classifica order by punti desc limit 10 ", null);
        String classifica = "";
        /* il ciclo for lo utilizzo solo per inserire i numeri da 1 a 10, potrei farne a meno
         * visto che la query mi ha già restituito 10 o meno record. */
        for(int i=1;i<=10;i++){
        	/* moveToNext: leggiti la javadoc dei metodi di Cursor! In poche parole, restituisce FALSE
        	 * se il cursore ha passato l'ultimo record disponibile ... */
        	if(result.moveToNext()){
        		/* ... quindi se esiste un record al cursore, scrivi i dati del record stesso*/
        		classifica = classifica + String.format(Locale.getDefault(),"%d. %d - %d - %s\n",i, result.getInt(2), result.getInt(1),timeToString(result.getLong(3))); 
        	}else
        		/* altrimenti scrivi qualcos'altro */
        		classifica = classifica + String.format(Locale.getDefault(),"%d. --- - --- - ---\n",i);
        }
        /* parte di output da scrivere per intero. Per ora va bene così */
        TextView message = (TextView)findViewById(R.id.scoreMessage);
		String test = "Punteggio = "+ punteggio.toString() +"\n";
        test += "tempo = "+ timeToString(tempo)+"\n";
        test += "tempo_ms = "+ tempo.toString() +"\n";
        test += "punti = " + calcolaPunti(punteggio, tempo)+"\n";
        message.setText(test);
        /* view con i migliori 10 punteggi.*/
        TextView scores = (TextView)findViewById(R.id.scoresList);
        scores.setText(classifica);
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
		 * Utilizzo string.format passando il parametro %02d cioè: inserisci il parametro inserendolo
		 * con due cifre, di cui la prima è 0 se minore di 10 ( 3 cifre per i millesimi ) */
		return String.format(Locale.getDefault(),"%02d'%02d''%03d",minuti, secondi, millisec);
	}
}
