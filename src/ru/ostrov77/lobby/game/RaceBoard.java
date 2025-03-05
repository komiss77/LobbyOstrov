package ru.ostrov77.lobby.game;

import ru.komiss77.modules.scores.ScoreDis;
import ru.komiss77.modules.world.BVec;
import ru.komiss77.modules.world.WXYZ;

public class RaceBoard extends ScoreDis {

	public RaceBoard(final String name, final BVec loc) {
		super(name, loc, 5, false);
	}
	
	//@Override
	//public String toDisplay(final Integer amt) {
	//	return amt == null ? "--" : TimeUtil.secondToTime(amt);
	//}

}
