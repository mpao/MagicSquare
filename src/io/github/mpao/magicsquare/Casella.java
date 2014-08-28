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
/*
 * 
 * IMPLEMENTAZIONE DELLA CLASSE Casella
 * 
 */
package io.github.mpao.magicsquare;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableRow;
import android.widget.TextView;

public class Casella extends TextView implements OnClickListener{
/* mi creo la classe Casella perché mi serve una textview quadrata
 * ma che prenda le dimensioni dinamicamente, cioé a seconda del 
 * dispositivo in cui gira. Per fare ciò occorre riscrivere il metodo
 * onMeasure. In più, la mia classe Casella deve essere cliccabili, e
 * pertanto implementare l'interfaccia OnClickListener che richiede la
 * scrittura di un metodo onClick */
	public Casella(Context context, int tag) {
		/* uso questo costruttore, il classico di TextView a cui però ho 
		 * aggiunto un parametro di tipo int. Il prototipo:
		 * Casella casella = new Casella(context, int); mi permette di creare
		 * l'oggetto con un tag (non ID) univoco da 1 a 100 che identifica la 
		 * casella sulla board. Pertanto: */ 
		
		/* 1. creo l'oggetto invocando il costruttore della superclasse */
		 super(context);
		/* 2. gli assegno il TAG univoco importante per l'identificazione della 
		 * casella. Perché non utilizzare ID ? Ho la necessità che ogni casella
		 * sia identificabile con un numero da 1 a 100 per l'algoritmo di movimento
		 * che ho creato, e setID non mi garantisce l'univocità.*/ 
		this.setTag(tag);
		/* 3. gli assegno una stringa nulla (appena creata la casella non deve 
		 * contenere alcun valore ) */
		this.setText("");
		/* 4. ed infine rendo Casella cliccabile. Se viene
		 * invocato il costruttore, significa che il gioco è appena iniziato, quindi
		 * tutte le caselle create devono poter essere cliccabili */
		setOnClickListener(this);
	}
	public Casella(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Costruttore non personalizzato, ma necessario per ereditarietà
	}
	public Casella(Context context, AttributeSet attrs, int defStyle) {
		/* bello eh questo costruttore, peccato che non prenda
		 * l'iD dello stile maremma gatta, altrimenti era proprio quello di cui
		 * avevo bisogno. Lo stile della casella invece tocca assegnarlo in altro modo 
		 * http://stackoverflow.com/questions/3142067/android-set-style-in-code */
		super(context, attrs, defStyle);
		// Costruttore non personalizzato, ma necessario per ereditarietà
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// http://developer.android.com/reference/android/widget/TextView.html#onMeasure(int,%20int)
	    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	    int width = MeasureSpec.getSize(widthMeasureSpec);
	    int height = MeasureSpec.getSize(heightMeasureSpec);
	    int size = width > height ? height : width;
	    setMeasuredDimension(size, size);
	}
	@Override
	public void onClick(View v) {
        /* implementare l'interfaccia OnClickListener significa dover implementare il metodo
         * onClick con il prototipo di cui sopra, senza poter aggiungere altri parametri. 
         * Ho quindi a disposizione la View che è stata cliccata e devo farmela bastare.
         * La View che arriva è di tipo Casella che devo fare ?
         * L'utente ha cliccato una casella all'interno della board:
         * 1. Aumentare il contatore dei numeri
         * 2. Scriverlo all'interno della casella appena cliccata
         * 3. Abilitare il salto alla casella successiva.
         * La cosa più complicata è il punto 1, perché una Casella non può avere memoria del
         * punteggio e a che punto siamo della partita. Tale prerogativa ce l'ha la Board in 
         * cui la Casella è inserita. Fortunatamente con il metodo getParent di View ottengo
         * il riferimento alla View gerarchicamente genitrice della View che sta eseguendo il
         * metodo. Per ottenere il riferimento alla Board, devo far due salti indietro, perché
         * c'è di mezzo TableRow*/   		
		SquareLayout parentId = (SquareLayout)((TableRow) v.getParent()).getParent();
		/* A questo punto, avendo un riferimento alla board, posso invocarne il metodo scritto
		 * da me che consente di aumentare il punteggio */
		parentId.increaseResult();
		/* Sempre con un metodo personalizzato, ottengo da board il numero che ho appena raggiunto. Il
		 * metodo getResult restituisce un Integer, e con il metodo toString lo faccio diventare
		 * un testo da poter inserire all'interno della casella che ho cliccato, attraverso il metodo setText*/
		this.setText(parentId.getResult().toString());
		/* Sono pronto ora per un nuovo click, e mi preparo con il metodo enableNextClick di Casella, che prende
		 * in ingresso la View che sto cliccando. Il metodo ritorna vero o falso a seconda se ho ancora mosse
		 * disponibili o meno, quindi se vero il gioco prosegue, se falso si interrompe. */
		if(this.enableNextClick(v)){
			// il gioco prosegue, evidenzio la casella che ho appena cliccato
			this.setBackgroundResource(R.color.whereIam);
		}else{
			/* gioco finito mi avvio alla conclusione. Ci pensa la board con il suo
			 * metodo gameEnded alla gestione. Casella non fa altro che alzare la bandierina
			 * dicendo che il gioco è concluso con il punteggio di parentId.getResult(). Se
			 * questo è uguale a 100 o minore, lo controlla la board */
			parentId.gameEnded(parentId.getResult());
		}
	}
	protected boolean enableNextClick(View v){
		/* questo metodo deve stabilire se una partita è conclusa o meno, cioè se ho
		 * ancora mosse disponibili. Definisco un array booleano che contiene il valore
		 * che assumono le caselle per il salto */
		boolean continua[] = {false,false,false,false,false,false,false,false};
		/* ALGORITMO:
		 * le caselle sulla scacchiera sono numerate da 1 a 100
		 * partendo dall'angolo in alto a destra. in questo modo
		 * sono identificabili (vedi costruttore della Classe). il gioco
		 * consiste nel muoversi attraverso la scacchiera secondo lo 
		 * schema:
		 		*	*	*	15	*	*	*
		 		*	23	*	*	*	27	*
		 		*	*	*	*	*	*	*
		 		42	*	*	X	*	*	48
		 		*	*	*	*	*	*	*
		 		*	63	*	*	*	67	*
		 		*	*	*	75	*	*	*		 
		 * Dalla posizione X posso quindi raggiungere le caselle in posizione e con TAG
		 * x-30, x-22, x-18, x-3, x+3, x+18, x+22, x+30 
		 * per cui mi occorre sapere che casella ho appena cliccato e abilitare 
		 * di conseguenza le casella alle posizioni sopra riportare. Questo avviene attraverso
		 * il TAG inserito dal costruttore. */ 
		
		/* per utilizzare con successo findViewWithTag ho bisogno di sapere chi è il genitore della view */
		SquareLayout parentId = (SquareLayout)((TableRow) v.getParent()).getParent();
		/* per semplificare il codice nella lettura, salvo alcune variabili le informazioni che mi servono come
		 * il TAG della casella cliccata */
		int whereIClick = (Integer) v.getTag();
		/* la colonna in cui sta tale casella (perché ? te lo dico tra poco) */
		int colonna =  whereIClick % 10; //il numero della colonna è dato dal MOD di 10 =)
		/* e le posizioni in cui mi posso muovere a partire da dove ho cliccato, definite in un array per 
		 * poter eseguire una serie di operazioni all'interno di un ciclo for */
		int posizioni[] =  {whereIClick-30,whereIClick-22,whereIClick-18,whereIClick-3,
							whereIClick+3,whereIClick+18,whereIClick+22,whereIClick+30};
		/* Avendo battezzato la Caselle con un TAg da 1 a 100, oltre ad avere un algoritmo di movimento,
		 * posso attraverso un ciclo for operare su tutte le Caselle che necessito.*/
		for(int i=0;i<100;i++){
			/* Qui le rendo tutte NON cliccabili, e tutte con il colore di default definito
			 * come risorsa esterna. Facendo questa operazione TUTTE, le caselle non sono 
			 * più cliccabili ed hanno lo stesso colore, MA come visto nel metodo onCLick
			 * la Casella in oggetto prende il colore solo dopo che è stata eseguita questa 
			 * operazione. A questo punto siamo con tutte le caselle non cliccabili e colorate
			 * uguali, tranne la casella appena cliccata, che è si non cliccabile, ma ha un suo
			 * proprio colore */
			parentId.findViewWithTag(i).setClickable(false);
			parentId.findViewWithTag(i).setBackgroundResource(R.color.casella);
		}
		/* Avendo definito l'array posizioni posso scrivere solo questo blocco di codice, altrimenti avrei dovuto scrivere un
		 * IF per ognuna delle otto posizioni. Insomma posizioni[] esiste solo per comodità e chiarezza in lettura. Qui mi serve
		 * anche la variabile "colonna": la pecca dell'algoritmo che utilizzo è che se una casella è vicino ai bordi, aggiungendo
		 * o sottraendo un valore potrei "uscire" dalla board e abilitare una casella che non dovrebbe esserlo. L'alternativa
		 * era usare una matrice X,Y ma perché complicarsi la vita :) ? */
		for(int i=0;i<8;i++){
			/* se esiste la casella con quella posizione, e la distanda di colonna è <=3 e (lazy and, se arriva qui vuol dire
			 * che la prima condizione è stata giudicata vera e quindi la casella esiste e perciò posso applicarci metodi ) 
			 * tale casella non ha ancora un valore all'interno, allora...*/
			if(
					parentId.findViewWithTag(posizioni[i])!=null 
					& Math.abs(colonna-(posizioni[i])%10)<=3 
					&& ((Casella)parentId.findViewWithTag(posizioni[i])).getText()==""
				){
				/*... allora tale casella, che cerco con il metodo findViewWithTag, diventa cliccabile */
				parentId.findViewWithTag(posizioni[i]).setClickable(true); 
				/* e se l'help della board è attivo...*/
				if(parentId.isHelpActive())
					/* allora evidenzio anche dove può saltare nella mossa successiva sempre con un colore
					 * definito con risorsa esterna nel file color.xml */
					parentId.findViewWithTag(posizioni[i]).setBackgroundResource(R.color.nextMoveHelper);
				// questa casella è disponibile per il prossimo salto
				continua[i] = true;
			}else{
				// questa casella NON è più disponibile
				continua[i] = false;
			}
		}
		/* se tutte le caselle ritornano FALSE, allora la partita è finita. Basta solo una casella
		 * che ritorna TRUE e l'espressione qui sotto ritorna TRUE proseguendo nel gioco. */
		return (continua[0] | continua[1] | continua[2] | continua[3]
				 | continua[4] | continua[5] | continua[6] | continua[7]);
	}
}










