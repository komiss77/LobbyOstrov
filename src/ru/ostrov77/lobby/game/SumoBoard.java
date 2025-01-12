package ru.ostrov77.lobby.game;

import ru.komiss77.modules.scores.ScoreDis;
import ru.komiss77.modules.world.WXYZ;

public class SumoBoard extends ScoreDis {

	public SumoBoard(final String name, final WXYZ loc) {
		super(name, loc, 5, true);
	}
	
	//@Override
	//public String toDisplay(final Integer amt) {
	//	return amt == null ? "--" : String.valueOf(amt);
	//}

}
