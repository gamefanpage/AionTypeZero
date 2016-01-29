package ai.instance.shugoImperialTomb;

import java.util.concurrent.Future;

import ai.AggressiveNpcAI2;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;


@AIName("defensetower")
public class DefenseTowerAI2 extends AggressiveNpcAI2
{

	private Future<?> task;

	@Override
	public boolean canThink()
	{
		return false;
	}

	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage)
	{
		if (hpPercentage > 50 && hpPercentage <= 100)
		SkillEngine.getInstance().applyEffectDirectly(21097, getOwner(), getOwner(), 0);
		if (hpPercentage > 25 && hpPercentage <= 50)
			SkillEngine.getInstance().applyEffectDirectly(21098, getOwner(), getOwner(), 0);
		if (hpPercentage >= 0 && hpPercentage <= 25)
			SkillEngine.getInstance().applyEffectDirectly(21099, getOwner(), getOwner(), 0);
	}

	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		SkillEngine.getInstance().applyEffectDirectly(21097, getOwner(), getOwner(), 0);
		task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				AI2Actions.useSkill(DefenseTowerAI2.this, 20954);
			}
		}, 2000, 2000);
	}

	@Override
	public void handleDespawned()
	{
		task.cancel(true);
		super.handleDespawned();
	}

	@Override
	public void handleBackHome()
	{
		return;
	}

	@Override
	public int modifyDamage(int damage)
	{
		return 1;
	}

	@Override
	protected AIAnswer pollInstance(AIQuestion question)
	{
		switch (question)
		{
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
