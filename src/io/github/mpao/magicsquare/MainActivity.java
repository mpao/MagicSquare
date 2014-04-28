package io.github.mpao.magicsquare;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* creo l'oggetto che conterrà le mie caselle */
		SquareLayout board = new SquareLayout(this);
		/* e ne imposto il colore come definito in color.xml*/
		board.setBackgroundResource(R.color.board);
		
		/* http://developer.android.com/reference/android/view/LayoutInflater.html
		 * Carico e scompatto il layout principale */
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mainLayout = inflater.inflate(R.layout.activity_main, null);
		/* Attenzione al CAST: nel main uso LinearLayout, dopo di che, aggiungo la board */
		/* ADDVIEW: Parameters: child the child view to add - index the position at which to add the child
		 * aggiungendo un index quindi posso inserire la view in qualunque posizione del layout */
		((LinearLayout) mainLayout).addView(board); //inserire un indice quando il layout sarà completo
		/* assegno il layout all'activity */
		setContentView(mainLayout);
	}
}

/*
 * Da approfondire attraverso la documentazione: 
 * 
 * OnClickListener
 * Context
 * onMeasure
 * ContextThemeWrapper
 * Log
 * 
 * Non impazzire se i messaggi di Log non funzionano, � colpa di Eclipse
 * abd da shell funziona perfettamente (adb è stato spostato nella directory "platform-tools") 
 * e un riavvio di Eclipse risolve il problema anche nell'editor. Per vedere invece che valore 
 * assumono le variabili inserire breakpoint e mandare in debug con la relativa perspective
 * */
 