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
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Scores extends Activity {
	private static Integer NUMRECORD = 10;
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
        /* Voglio limitare le dimensioni del DB salvando solo gli N risultati che poi mostro ?
         * Invece che punteggio>0, posso prendermi il N°esimo record e confrontarlo con punteggio.
         * Istanzio SQLiteDatabase in modalità lettura */
        SQLiteDatabase db_r = dbHelper.getReadableDatabase();
        /* eleguo la query che mi restituisce i risultati */
        Cursor result = db_r.rawQuery(String.format(Locale.getDefault(), "select * from Classifica order by punti desc limit %s ", NUMRECORD.toString()), null);
        /* decimoPosto prende il valore di: moveToPosition restituisce true, vuol dire
         * che esiste il record alla posizione N, quindi mi prendo il valore della colonna punteggio.
         * Se restituisce false, basta che il punteggio sia maggiore di 0, ovvero ho finito una partita*/
        int decimoPosto = result.moveToPosition(NUMRECORD) ? result.getInt(2) : 0;
        if(punteggio > decimoPosto){
        /* se il punteggio è maggiore di zero, vuol dire arrivo in questa activity da una
         * partita appena finita. Elimino quindi subito tutti i salvataggi, in modo che non
         * possa essere più ripresa e che il tasto RESUME appaia disabilitato */
    		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.save_file_key), Context.MODE_PRIVATE);
        	SharedPreferences.Editor editor = sharedPref.edit();	
        	editor.clear(); // metodo clear, rimuove tutte le chiavi !
        	editor.commit();
        	/* a questo punto mi connetto al database e utilizzando ContentValues passo i 
        	 * valori alle rispettive colonne */
        	SQLiteDatabase db_w = dbHelper.getWritableDatabase();
        	ContentValues values = new ContentValues();
        	values.put(Classifica.COLUMN_NAME_PUNTEGGIO, punteggio);
        	values.put(Classifica.COLUMN_NAME_PUNTI, calcolaPunti(punteggio, tempo));
        	values.put(Classifica.COLUMN_NAME_TEMPO, tempo);
        	values.put(Classifica.COLUMN_NAME_HELP, true);
        	values.put(Classifica.COLUMN_NAME_TYPE, "Basic");
        	/* ed inserisco il nuovo record */
        	db_w.insert(Classifica.TABLE_NAME,null,values);
        }
        /* questa porzione di codice viene eseguita sempre, sia quando ho appena finito una partita
         * sia quando dal menù, clicco il pulsante dei punteggi. 
         * Devo rieseguire la query, non basta spostare il cursore, poichè un nuovo record è entrato.*/
        result = db_r.rawQuery(String.format(Locale.getDefault(), "select * from Classifica order by punti desc limit %s ", NUMRECORD.toString()), null);
        /* il ciclo for lo utilizzo solo per inserire i numeri da 1 a 10, potrei farne a meno
         * visto che la query mi ha già restituito 10 o meno record. Metto tutto in una tabella.
         * Perchè non utilizzo ListView ? perchè i dati sono limitati e perpetui, cioè non cambiano
         * mai, cambia solo il loro contenuto. potrei anche inserire tutti i N record nel file XML
         * ma visto che è una cosa ripetitiva e che N è configurabile, preferisco farlo all'interno del codice*/
        TableLayout scores = (TableLayout)findViewById(R.id.scoresList);  
        for(int i=1;i<=NUMRECORD;i++){
        	/* moveToNext: leggiti la javadoc dei metodi di Cursor! In poche parole, restituisce FALSE
        	 * se il cursore ha passato l'ultimo record disponibile ... 
        	 * Creo le mie caselle di testo per ogni riga*/
        	TableRow row = new TableRow(getBaseContext());
        	scores.addView(row);
        	/* indice da 1 a N */
        	TextView index = new TextView(getBaseContext()); 			row.addView(index);
        	index.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
        	/* il punteggio ottenuto */
        	TextView tv_punteggio = new TextView(getBaseContext());		row.addView(tv_punteggio);
        	tv_punteggio.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 3f));
        	tv_punteggio.setTextAppearance(getBaseContext(), R.style.testoClassifica);
        	/* hai usato l'help ?*/
            TextView tv_help = new TextView(getBaseContext());			row.addView(tv_help);
            tv_help.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
            /* che tipo di partita era ? basic o expert ?*/
            TextView tv_type = new TextView(getBaseContext());			row.addView(tv_type);
            tv_type.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
            /* a che numero sei arrivato ?*/
        	TextView tv_punti = new TextView(getBaseContext());			row.addView(tv_punti);
        	tv_punti.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 3f));
        	/* in quanto tempo ?*/
            TextView tv_tempo = new TextView(getBaseContext());			row.addView(tv_tempo);
            tv_tempo.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 4f));
        	if(result.moveToNext()){
        		/* se esiste un record, scrivo i valori */
        		index.setText(String.format("%d",i));
        		tv_punteggio.setText(String.format("%d",result.getInt(2)));
        		tv_help.setText("Y"); // ture or false
        		tv_type.setText("B"); // basic or expert
        		tv_punti.setText(String.format("%d",result.getInt(1)));
        		tv_tempo.setText(timeToString(result.getLong(3)));
        	}else{
        		index.setText(String.format("%d",i));
        		tv_punteggio.setText("---");
        		tv_help.setText(" "); // ture or false
        		tv_type.setText(" "); // basic or expert
        		tv_punti.setText("---");
        		tv_tempo.setText("---");
        	}
        }
        /* parte di output da scrivere per intero. Per ora va bene così */
        TextView message = (TextView)findViewById(R.id.scoreMessage);
		String test = "Punteggio = "+ punteggio.toString() +"\n";
        test += "tempo = "+ timeToString(tempo)+"\n";
        test += "tempo_ms = "+ tempo.toString() +"\n";
        test += "punti = " + calcolaPunti(punteggio, tempo)+"\n";
        message.setText(test);

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
		/* restituisce una stringa formattata come 1'21"046 in base al tempo parametro.
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
