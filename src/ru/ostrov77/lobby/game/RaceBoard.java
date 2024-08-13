package ru.ostrov77.lobby.game;

import ru.komiss77.modules.scores.ScoreDis;
import ru.komiss77.modules.world.WXYZ;
import ru.komiss77.utils.TimeUtil;

public class RaceBoard extends ScoreDis {

	public RaceBoard(final String name, final WXYZ loc) {
		super(name, loc, 5, false);
	}
	
	@Override
	public String toDisplay(final Integer amt) {
		return amt == null ? "--" : TimeUtil.secondToTime(amt);
	}

}
