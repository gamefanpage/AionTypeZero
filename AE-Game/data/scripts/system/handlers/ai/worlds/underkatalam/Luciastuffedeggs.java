package ai.worlds.kataramunderground;

import java.util.concurrent.Future;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.utils.ThreadPoolManager;

import ai.NoActionAI2;

@AIName("luciastuffedegg")
public class Luciastuffedeggs extends NoActionAI2 {

	private Future<?> spawnTask;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		if (spawnTask == null)
			startTask();
	}

	private void startTask() {

		spawnTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				spawn(284278, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				getOwner().getController().die();
				getOwner().getController().delete();
			}
		}, 30000);
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		if (spawnTask != null)
			spawnTask.cancel(true);
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		if (spawnTask != null)
			spawnTask.cancel(true);
	}
}
