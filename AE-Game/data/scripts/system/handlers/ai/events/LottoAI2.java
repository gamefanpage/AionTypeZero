package ai.events;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.joda.time.DateTime;

/**
 * @author Romanz
 */
@AIName("lotto")
public class LottoAI2 extends GeneralNpcAI2 {

	@Override
	protected void handleSpawned() {
		DateTime now = DateTime.now();
		int currentDay = now.getDayOfWeek();
		switch (getNpcId()) {
			case 831855:
			case 831856:
			case 831791:  {
				if (currentDay == 7)
					super.handleSpawned();
				else
					if (!isAlreadyDead())
						getOwner().getController().onDelete();
				break;
			}
		}
	}
}
