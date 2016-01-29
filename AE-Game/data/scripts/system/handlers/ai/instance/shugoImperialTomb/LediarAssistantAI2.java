package ai.instance.shugoImperialTomb;


import ai.AggressiveNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.manager.EmoteManager;
import org.typezero.gameserver.model.gameobjects.Npc;

@AIName("lediar_assistant")
public class LediarAssistantAI2 extends AggressiveNpcAI2
{
	private final static int[] npc_ids = {831251, 831250, 831305};

	@Override
	public int modifyOwnerDamage(int damage)
	{
		return damage = 1;
	}

	@Override
	protected void handleSpawned()
	{
		addHate();
		super.handleSpawned();
	}

	private void addHate()
	{
		EmoteManager.emoteStopAttacking(getOwner());
		for (int npc_id : npc_ids)
		{
			Npc tower = getOwner().getPosition().getWorldMapInstance().getNpc(npc_id);
			if (tower != null && !tower.getLifeStats().isAlreadyDead())
			{
				switch (npc_id)
				{
					case 831305:
						getOwner().getAggroList().addHate(tower, 10000);
					case 831250:
						getOwner().getAggroList().addHate(tower, 10000);
					case 831251:
						getOwner().getAggroList().addHate(tower, 10000);
						break;
				}
			}
		}
	}
}
