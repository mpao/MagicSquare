package io.github.mpao.magicsquare;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

public class SquareLayout extends TableLayout {
	
	Casella[] arrBoard = new Casella[100];
	int[] movesStack   = new int[100];
	
	public SquareLayout(Context context) {
		super(context);
		TableRow row = new TableRow(getContext());

		TableRow.LayoutParams params = new TableRow.LayoutParams();
		params.setMargins(3, 3, 3, 3);
		params.weight = 1;

		for(int i=0; i<100; i++ ){
			if(i%10==0) { row = new TableRow(getContext()); }
			/* il costruttore di TextView con lo stile non funziona 
			 * questa è una soluzione proposta su stackoverflow, ma che
			 * cosa è ContextThemeWrapper ? Funziona, ma non per i margini */
			arrBoard[i] = new Casella(new ContextThemeWrapper(getContext(), R.style.casella));
			arrBoard[i].setLayoutParams(params);
			arrBoard[i].setText(String.valueOf(i));
			arrBoard[i].setClickable(true);
			arrBoard[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                	v.setBackgroundColor(Color.CYAN);
                 }
                });
			row.addView(arrBoard[i]);
			if(i%10==0) {this.addView(row);}
		}
	}
	public SquareLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		/* al momento non uso questo costruttore, potrei farlo se avessi 
		 * dei parametri interessanti, come ad esempio il numero di 
		 * caselle nella scacchiera. */ 
		
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
