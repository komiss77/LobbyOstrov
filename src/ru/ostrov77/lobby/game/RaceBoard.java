package ru.ostrov77.lobby.game;

import ru.komiss77.ApiOstrov;
import ru.komiss77.modules.scores.ScoreBoard;
import ru.komiss77.modules.world.WXYZ;

public class RaceBoard extends ScoreBoard {

	public RaceBoard(final String name, final WXYZ loc) {
		super(name, loc, 5, false);
	}
	
	@Override
	public String toDisplay(final Integer amt) {
		return amt == null ? "--" : ApiOstrov.secondToTime(amt);
	}

}
