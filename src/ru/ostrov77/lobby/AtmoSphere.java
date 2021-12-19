package ru.ostrov77.lobby;

import java.util.Calendar;

import ru.komiss77.Ostrov;

public class AtmoSphere {
	
	private final Calendar cal;
	private final boolean isWinter;
	private final float mcf;
	
	public AtmoSphere() {
		cal = Ostrov.calendar;
		switch (cal.get(Calendar.MONTH)) {
		case 0:
		case 1:
		case 2:
			mcf = cal.get(Calendar.DAY_OF_YEAR) / 91.5f;
			isWinter = true;
			break;
		case 9:
		case 10:
		case 11:
			mcf = (365f - cal.get(Calendar.DAY_OF_YEAR)) / 91.5f;
			isWinter = true;
			break;
		case 3:
		case 4:
		case 5:
			mcf = (183 - cal.get(Calendar.DAY_OF_YEAR)) / 91.5f;
			isWinter = false;
			break;
		case 6:
		case 7:
		case 8:
			mcf = (cal.get(Calendar.DAY_OF_YEAR) - 183f) / 91.5f;
			isWinter = false;
			break;
		default:
			isWinter = true;
			mcf = 0f;
		}
	}
	
	public int getMCTime() {
		final int tm = (int) (cal.get(Calendar.HOUR_OF_DAY) * 1000f + (cal.get(Calendar.MINUTE) * 16.66f) + (cal.get(Calendar.SECOND) * 0.1666f));
		
		if (isWinter) {
			switch (tm / 1000) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				return 18000 + (int) (tm * (0.75f + (0.25f * mcf) ) );
			case 8:
			case 9:
			case 10:
			case 11:
				return 6000 - (int) ( (12000 - tm) * (1.5f - (0.5f * mcf) ) );
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
				return 6000 + (int) ( (tm - 12000) * (1.5f - (0.5f * mcf) ) );
			case 20:
			case 21:
			case 22:
			case 23:
				return 18000 - (int) ( (24000 - tm) * (0.75f + (0.25f * mcf) ) );
			default:
				return 6000;
			}
		} else {
			switch (tm / 1000) {
			case 0:
			case 1:
			case 2:
			case 3:
				return 18000 + (int) (tm * (1.5f - (0.5f * mcf) ) );
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
				return 6000 - (int) ( (12000 - tm) * (0.75f + (0.25f * mcf) ) );
			case 12:
			case 13:
			case 14:
			case 15:
				return 6000 + (int) ( (tm - 12000) * (0.75f + (0.25f * mcf) ) );
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				return 18000 - (int) ( (24000 - tm) * (1.5f - (0.5f * mcf) ) );
			default:
				return 6000;
			}
		}
	}
	
}
