package datenbank.deployer.ui

import java.util.Observable;
import java.util.Observer;

class ConsolePrinter  implements Observer {

	@Override
	public void update(Observable o, Object arg) {
		println o
		
	}

}
