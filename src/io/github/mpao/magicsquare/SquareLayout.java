/*
 * 
 * IMPLEMENTAZIONE DELLA CLASSE SquareLayout
 * 
 */
package io.github.mpao.magicsquare;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.TableLayout;
import android.widget.TableRow;

public class SquareLayout extends TableLayout {
	/* questa è la classe che rappresenta la Board, avrebbe potuto essere un TableLayout,
	 * ma tocca estendere tale Layout perché voglio che sia quadrata e devo riscrivere il 
	 * metodo onMeasure */
	/* definisco due campi, due variabili di istanza che mi servono per:*/
	private Integer result; // sapere quale numero sto gestendo
	private boolean helpMe; // sapere se l'help è attivo o meno
	public SquareLayout(Context context) {
		/* costruttore:*/
		super(context);
		/* inizializzo a zero il risultato*/
		result = 0;
		/* di base l'aiuto è attivo, ma l'utente può scegliere di disabilitarlo */
		helpMe = true;
		/* creo una riga all'interno di SquareLayout che ti ricordo essere un TableLayout*/
		TableRow row = new TableRow(getContext());
		/* e come parametri mi creo dei margini per distanziare un filo le caselle una
		 * dall'altra, caselle che avranno weight pari a 1 e quindi tutte e 10 occuperanno
		 * il medesimo spazio orizzontale all'interno della riga */
		TableRow.LayoutParams params = new TableRow.LayoutParams();
		params.setMargins(3, 3, 3, 3);
		params.weight = 1;
		/* a questo punto riempio la board con le 100 caselle che servono per il gioco */
		for(int i=0; i<100; i++ ){
			/* se sto per disegnare una casella che come TAG ha 0,10,20,... vuol dire che 
			 * devo cambiare riga, e quindi la creo*/
			if(i%10==0) { row = new TableRow(getContext()); }
			/* il costruttore di TextView con lo stile non funziona 
			 * questa è una soluzione proposta su stackoverflow, ma che
			 * cosa è ContextThemeWrapper ? Funziona, ma non per i margini */
			/* Costruisco la casella assegnandogli uno stile grafico e il TAG i che va quindi
			 * da 0 a 99, partendo dall'angolo in alto a sinistra */
			Casella casella = new Casella(new ContextThemeWrapper(getContext(), R.style.casella), i);
			/* alla casella appena creata assegno i parametri di layout definiti in precedenza...*/
			casella.setLayoutParams(params);
			/* ...e la aggiungo alla TableRow su cui sto lavorando */
			row.addView(casella);
			/* Se, come sopra, sono alla prima colonna, una volta riempita la riga di caselle, devo
			 * aggiungere tale riga alla board, SquareLayout*/
			if(i%10==0) {this.addView(row);}
		}
	}
	public SquareLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		/* al momento non uso questo costruttore, potrei farlo se avessi 
		 * dei parametri interessanti, come ad esempio il numero di caselle nella scacchiera. */ 	
	}

	public Integer getResult(){
		/* metodo getter per il campo result, mi restituisce a che punto sono
		 * della partita */
		return result;
	}
	public void increaseResult(){
		/* metodo setter per il campo result, incrementa il risultato di uno.
		 * utilizzato ad ogni click su una casella per aumentare il punteggio */
		result++;
	}
	public boolean isHelpActive(){
		/* metodo getter per il campo helpMe, mi dice se l'help è attivo o meno */
		return helpMe;
	}
	public void setHelp(boolean b){
		/* metodo setter per il campo helpMe, riceve in ingresso true o false e imposta
		 * l'help come dettato dall'argomento in ingresso */
		helpMe = b;
	}
	/*
	 * http://stackoverflow.com/questions/16748124/custom-square-linearlayout-how
	 * http://developer.android.com/guide/topics/ui/custom-components.html
	 * http://developer.android.com/reference/android/view/View.MeasureSpec.html
	 * http://stackoverflow.com/questions/19697102/android-create-square-cell-table
	 * */
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	    int width = MeasureSpec.getSize(widthMeasureSpec);
	    int height = MeasureSpec.getSize(heightMeasureSpec);
	    int size = width > height ? height : width;
	    setMeasuredDimension(size, size);
	}
}
