package io.github.mpao.magicsquare;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;


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
		/* non permetto lo screenshot nelle app recenti, in modo da non far sbirciare durante la pausa. */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
		/* la board viene creata sempre vuota, poichè onCreate viene invocato o a inizio gioco, oppure
		 * solo esclusivamente se questa activity è stata distrutta dall'utente o dal sistema. Ho bisogno
		 * invece, che il controllo del gioco interrotto avvenga anche dopo onStop */
		setContentView(mainLayout);
	}
	@Override
	protected void onStop() {
		/* creo oggetto SharedPreferences e mi preparo a modificarlo */
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		/* mi creo un riferimento all'oggetto board creato attraverso onCreate e lo trovo
		 * mediante il metodo findViewById */
		SquareLayout board = (SquareLayout)this.findViewById(1000);
		/* salvo le caselle cliccate con il valore che hanno assunto */
		for(int i=0;i<100;i++){
			/* per ogni casella, devo utilizzare il metodo putInt che prende come attributi una coppia di
			 * valori fatta come Stringa = valore . Mi devo inventare quindi una stringa che identifichi 
			 * ogni casella, e posso utilizzare i ( che corrisponde a tag) convertendola appunto da int a stringa */
			String name = ((Integer)i).toString();
			/* recupero il valore che ha la casella nella posizione i con alcuni cast per passare da array di char 
			 * a oggetto string, a oggetto integer ed infine ad int. Mi sono complicato la vita e 
			 * se la casella è vuota, non posso trasformarla in INT */
			int value;
			try{
				value = Integer.parseInt(((Casella)board.findViewWithTag(i)).getText().toString());
			}catch(NumberFormatException casellaVuota){
				value = 0;
			}
			/* inserisco la coppia chiave-valore */
			editor.putInt(name, value);
		}
		/* inserisco il punteggio a cui ero rimasto */
		int punteggio = board.getResult();
		editor.putInt("punteggio", punteggio);
		/* ora salvo la posizione in cui tale punteggio è stato raggiunto, che vuol dire
		 * che è l'ultima casella che ho cliccato.*/
		editor.putInt("posizione", board.getPosition(punteggio));
		/* salvo tutte le modifiche fatte */
		editor.commit();
		/* richiamo il metodo onStop della superclasse */
	    super.onStop();  
	}
	@Override
	protected void onStart() {
		/* onStart avviene dopo on stop e onCreate ovvero sempre dalla activity del menù: lì ho definito due
		 * pulsanti che passano un parametro come intent in modo che MainActivity possa riconoscere quando si
		 * vuole iniziare un nuovo gioco e quando caricarne uno esistente*/
        int game = getIntent().getIntExtra(Start_Menu.MESSAGE, 0);
        /* se game è =1 identifico il caso RESUME e quindi devo ripristinare la partita precedente */
        if(game==1){
        	int defaultValue = 0;
        	/* solito riferimento a board utilizzato anche in precedenza */
			SquareLayout board = (SquareLayout)this.findViewById(1000);
			/* e riferimento all'oggetto SharedPreferences da cui leggere le informazioni salvate */
			SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
			/* leggo tutte le caselle e, come sopra, le nomino attraverso i */
			for(int i=0;i<100;i++){
				String name = ((Integer)i).toString();
				/* assegno il valore salvato alla casella corrispondende. defaultValue serve al metodo
				 * getInt, niente di particolare. Se la casella al momento del salvataggio era vuota, l'ho
				 * salvata con all'interno il valore 0, devo tenerne conto nel ripristino */
				if(sharedPref.getInt(name, defaultValue)!=0)
					((Casella)board.findViewWithTag(i)).setText(((Integer)sharedPref.getInt(name, defaultValue)).toString());
			}
			/* riprendo il punteggio */
			board.setResult(sharedPref.getInt("punteggio", defaultValue));
			/* dove ero rimasto ? devo ripartire esattamente dalla casella che ho lasciato, evidenziarla
			 * e se l'help è attivo evidenziare le altre, bloccare quelle che non devono essere cliccate 
			 * TRICK: se vuoi arrivare a 100 per debug, invece di completare il gioco, commenta 
			 * il ripristino della posizione. Così puoi ripartire da qualunque casella, senza regole*/
			int posizione = sharedPref.getInt("posizione", defaultValue);
			((Casella)board.findViewWithTag(posizione)).enableNextClick(((Casella)board.findViewWithTag(posizione)));
			((Casella)board.findViewWithTag(posizione)).setBackgroundResource(R.color.whereIam);
        }
        /* in ogni caso, sia una nuova partita, oppure un resume, invoco la superclasse */
        super.onStart();  
	}
	@Override
	protected void onPause(){
		/* interrompo il cronometro*/
	    super.onPause();
	}
	@Override
	protected void onResume(){
		/* riparte il cronometro */
	    super.onResume();
	}
}

 