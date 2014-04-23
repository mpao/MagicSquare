package io.github.mpao.magicsquare;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.TextView;

public class Casella extends TextView {

	public Casella(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
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
	public void Highlight(int selected){
		/* metodo per evidenziare le caselle in cui posso saltare
		 * riceve in ingresso la posizione dell'array della
		 * casella che è stata cliccata secondo il seguente
		 * algoritmo:
		 * le caselle sulla scacchiera sono numerate da 1 a 100
		 * partendo dall'angolo in alto a destra. in questo modo
		 * sono identificabili all'interno di un array. il gioco
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
	}
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	    int width = MeasureSpec.getSize(widthMeasureSpec);
	    int height = MeasureSpec.getSize(heightMeasureSpec);
	    int size = width > height ? height : width;
	    setMeasuredDimension(size, size);
	}	
}










