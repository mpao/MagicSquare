package io.github.mpao.magicsquare;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide(); // nascondo l'action bar, non mi serve in questa app
		SquareLayout board = new SquareLayout(this);
		board.setBackgroundResource(R.color.board);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mainLayout = inflater.inflate(R.layout.activity_main, board);
		/*assegno un ID a board per poterla identificare all'interno del metodo onStart*/
		board.setId(1000); 
		/* la board viene creata sempre vuota, poichè onCreate viene invocato o a inizio gioco, oppure
		 * solo esclusivamente se questa activity è stata distrutta dall'utente o dal sistema. Ho bisogno
		 * invece, che il controllo del gioco interrotto avvenga anche dopo onStop */
		setContentView(mainLayout);
		
	}
	@Override
	protected void onStop() {
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("Test", 56);
		editor.commit();
	    super.onStop();  
	}
	@Override
	protected void onStart() {
		
        int game = getIntent().getIntExtra(Start_Menu.MESSAGE, 0);
        if(game==1){
        	int defaultValue = 0;
			SquareLayout board = (SquareLayout)this.findViewById(1000);
			SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
			((Casella)board.findViewWithTag(5)).setText(((Integer)sharedPref.getInt("Test", defaultValue)).toString());
        }
        super.onStart();  
	}
}

 