package ai.instance.theRunadium;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import java.util.concurrent.Future;


/**
 * @author Romanz
 *
 */
@AIName("witch_graendal_storm")
public class WitchGrendalStormAI2 extends NpcAI2 {

	private Future<?> task;

  @Override
  protected void handleSpawned() {
  	super.handleSpawned();
		task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run()	{
					AI2Actions.useSkill(WitchGrendalStormAI2.this, 21180);
				}
			},0, 1000);
  	despawn();
  }

  private void despawn() {
  	ThreadPoolManager.getInstance().schedule(new Runnable() {

  		@Override
  		public void run() {
  			getOwner().getController().onDelete();
  		}
  	}, 5000);
  }

	@Override
	public void handleDespawned() {
		task.cancel(true);
		super.handleDespawned();
	}

  @Override
  protected AIAnswer pollInstance(AIQuestion question) {
  	switch (question) {
      case SHOULD_DECAY:
          return AIAnswers.NEGATIVE;
      case SHOULD_RESPAWN:
          return AIAnswers.NEGATIVE;
      case SHOULD_REWARD:
          return AIAnswers.NEGATIVE;
      default:
          return null;
  	}
  }
}


