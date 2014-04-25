package io.github.mpao.magicsquare;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableRow;
import android.widget.TextView;

public class Casella extends TextView implements OnClickListener{

	public Casella(Context context, int tag) {
		
		super(context);
		this.setTag(tag);
		this.setText("");
		setOnClickListener(this); //diventa cliccabile: ATTENZIONE

	}
	public Casella(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public Casella(Context context, AttributeSet attrs, int defStyle) {
		/* bello eh questo costruttore, peccato che non prenda
		 * l'iD dello stile maremma gatta 
		 * http://stackoverflow.com/questions/3142067/android-set-style-in-code
		 * */
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	    int width = MeasureSpec.getSize(widthMeasureSpec);
	    int height = MeasureSpec.getSize(heightMeasureSpec);
	    int size = width > height ? height : width;
	    setMeasuredDimension(size, size);
	}
	@Override
	public void onClick(View v) {
		/* le caselle sulla scacchiera sono numerate da 1 a 100
		 * partendo dall'angolo in alto a destra. in questo modo
		 * sono identificabili. il gioco
		 * consiste nel muoversi attraverso la scacchiera secondo lo 
		 * schema:
		 		*	*	*	15	*	*	*
		 		*	23	*	*	*	27	*
		 		*	*	*	*	*	*	*
		 		42	*	*	X	*	*	48
		 		*	*	*	*	*	*	*
		 		*	63	*	*	*	67	*
		 		*	*	*	75	*	*	*		 
		 * Dalla posizione X posso raggiungere le caselle
		 * x-30, x-22, x-18, x-3, x+3, x+18, x+22, x+30 */            		
		SquareLayout parentId = (SquareLayout)((TableRow) v.getParent()).getParent();
		parentId.increaseResult();
		this.enableNextClick(v, (Integer) v.getTag(),parentId);
		this.setText(parentId.getResult().toString());
		this.setBackgroundResource(R.color.whereIam);
		    	/* Cosa fare ora ?
    	 * 1. Se è il primo click del gioco tutte le caselle sono cliccabili ed alla loro creazione lo sono
    	 * 2. successivamente bloccare tutte tranne quelle in cui posso saltare
    	 * 3. gestione del numero progressivo
    	 * 4. se aiuto è attivo, evidenzio caselle in cui posso saltare */
	}
	private void enableNextClick(View v, int whereIClick,SquareLayout parentId){
		/* per utilizzare con successo findViewWithTag ho bisogno
		 * di sapere chi è il genitore della view. Infatti nella
		 * documentazione si dice che: 
		 * Look for a _child view_ with the given id. 
		 * If this view has the given id, return this view.
		 * Ho bisogno di riferirmi a BOARD per cambiare riga,
		 * e quindi salgo di due livelli (il primo è TableRow) */		
		
		for(int i=0;i<100;i++){
			parentId.findViewWithTag(i).setClickable(false);
			parentId.findViewWithTag(i).setBackgroundResource(R.color.casella);
		}
		//le variabili definite qui sotto sono solo per semplificare la scrittura/lettura del codice
		int colonna = (Integer)v.getTag() % 10; //il numero della colonna è dato dal MOD di 10 =)
		/* da dove ho cliccato mi posso muovere nelle caselle seguenti*/
		int posizioni[] =  {whereIClick-30,whereIClick-22,whereIClick-18,whereIClick-3,whereIClick+3,whereIClick+18,whereIClick+22,whereIClick+30};
		//per ogni posizione definita dell'array
		for(int i=0;i<8;i++){
			/* se esiste la casella con quella posizione, e la distanda di colonna  <=3 e (lazy and, se arriva qui vuol dire
			 * che la prima condizione  stata giudicata vera e quindi la casella esiste e perci˜ posso applicarci metodi ) 
			 * tale casella non ha ancora un valore all'interno, allora...*/
			if(
					parentId.findViewWithTag(posizioni[i])!=null 
					& Math.abs(colonna-(posizioni[i])%10)<=3 
					&& ((Casella)parentId.findViewWithTag(posizioni[i])).getText()==""
				){
				/*... allora tale casella diventa cliccabile */
				parentId.findViewWithTag(posizioni[i]).setClickable(true); 
				/* e se l'help  attivo allora evidenzio anche dove pu˜ saltare nella mossa successiva*/
				if(parentId.isHelpActive())
					parentId.findViewWithTag(posizioni[i]).setBackgroundResource(R.color.nextMoveHelper);
			}
		}
	}
}










