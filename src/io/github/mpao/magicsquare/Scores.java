package io.github.mpao.magicsquare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;

public class Scores extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide(); // nascondo l'action bar, non mi serve in questa app
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup root = (ViewGroup)findViewById(R.layout.scores);
		View mainLayout = inflater.inflate(R.layout.scores, root);

		Intent intent = getIntent();
        String punteggio = intent.getStringExtra(MainActivity.MESSAGE_PUNTEGGIO);
        String tempo = intent.getStringExtra(MainActivity.MESSAGE_TEMPO);
        TextView textView = new TextView(this);
        
        if(Integer.parseInt(punteggio)>0){
        	/* test: qui funziona o.0, cazzo cazzo cazzo. */
    		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.save_file_key), Context.MODE_PRIVATE);
        	SharedPreferences.Editor editor = sharedPref.edit();	
        	editor.clear(); // metodo clear, rimuove tutte le chiavi !
        	editor.commit();        	
        }
        
        textView.setTextSize(40);
        String test = "Punteggio = "+ punteggio + " tempo = "+ tempo;
        textView.setText(test);           
        ((LinearLayout) mainLayout).addView(textView,0);		
		
		setContentView(mainLayout);
	}
	
	public void tornaAlMenu(View v){
		/* metodo del pulsante per chiudere l'activity e tornare al menù */
		startActivity(new Intent(this, Start_Menu.class));
	}
}
