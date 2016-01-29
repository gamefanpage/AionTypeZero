/*
 * Copyright (c) 2015, TypeZero Engine (game.developpers.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of TypeZero Engine nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.typezero.gameserver.world.knownlist;

import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.configs.main.SecurityConfig;
import org.typezero.gameserver.model.gameobjects.AionObject;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.world.MapRegion;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * KnownList.
 *
 * @author -Nemesiss-
 * @modified kosyachok
 */
public class KnownList {

	private static final Logger log = LoggerFactory.getLogger(KnownList.class);

	/**
	 * Owner of this KnownList.
	 */
	protected final VisibleObject owner;

	/**
	 * List of objects that this KnownList owner known
	 */
	protected final FastMap<Integer, VisibleObject> knownObjects = new FastMap<Integer, VisibleObject>().shared();

	/**
	 * List of player that this KnownList owner known
	 */
	protected volatile FastMap<Integer, Player> knownPlayers;

	/**
	 * List of objects that this KnownList owner known
	 */
	protected final FastMap<Integer, VisibleObject> visualObjects = new FastMap<Integer, VisibleObject>().shared();

	/**
	 * List of player that this KnownList owner known
	 */
	protected volatile FastMap<Integer, Player> visualPlayers;

	private ReentrantLock lock = new ReentrantLock();

	/**
	 * @param owner
	 */
	public KnownList(VisibleObject owner) {
		this.owner = owner;
	}

	/**
	 * Do KnownList update.
	 */
	public void doUpdate() {
		lock.lock();
		try {
			forgetObjects();
			findVisibleObjects();
		}
		finally {
			lock.unlock();
		}
	}

	/**
	 * Clear known list. Used when object is despawned.
	 */
	public void clear() {
		for (VisibleObject object : knownObjects.values()) {
			object.getKnownList().del(owner, false);
		}
		knownObjects.clear();
		if (knownPlayers != null) {
			knownPlayers.clear();
		}
		visualObjects.clear();
		if (visualPlayers != null) {
			visualPlayers.clear();
		}
	}

	/**
	 * Check if object is known
	 *
	 * @param object
	 * @return true if object is known
	 */
	public boolean knowns(AionObject object) {
		return knownObjects.containsKey(object.getObjectId());
	}

	/**
	 * Add VisibleObject to this KnownList.
	 *
	 * @param object
	 */
	protected boolean add(VisibleObject object) {
		if (!isAwareOf(object))
			return false;

		if (knownObjects.put(object.getObjectId(), object) == null) {
			if (object instanceof Player) {
				checkKnownPlayersInitialized();
				knownPlayers.put(object.getObjectId(), (Player) object);
			}

			addVisualObject(object);
			return true;
		}

		return false;
	}

	public void addVisualObject(VisibleObject object) {
		if (object instanceof Creature) {
			if (SecurityConfig.INVIS && object instanceof Player) {
				if (!owner.canSee((Player) object)) {
					return;
				}
			}

			if (visualObjects.put(object.getObjectId(), object) == null) {
				if (object instanceof Player) {
					checkVisiblePlayersInitialized();
					visualPlayers.put(object.getObjectId(), (Player) object);
				}
				owner.getController().see(object);
			}
		}
		else if (visualObjects.put(object.getObjectId(), object) == null) {
			owner.getController().see(object);
		}
	}

	/**
	 * Delete VisibleObject from this KnownList.
	 *
	 * @param object
	 */
	private void del(VisibleObject object, boolean isOutOfRange) {
		/**
		 * object was known.
		 */
		if (knownObjects.remove(object.getObjectId()) != null) {
			if (knownPlayers != null)
				knownPlayers.remove(object.getObjectId());
			delVisualObject(object, isOutOfRange);
		}
	}

	public void delVisualObject(VisibleObject object, boolean isOutOfRange) {
		if (visualObjects.remove(object.getObjectId()) != null) {
			if (visualPlayers != null)
				visualPlayers.remove(object.getObjectId());
			owner.getController().notSee(object, isOutOfRange);
		}
	}

	/**
	 * forget out of distance objects.
	 */
	private void forgetObjects() {
		for (VisibleObject object : knownObjects.values()) {
			if (!checkObjectInRange(object) && !object.getKnownList().checkReversedObjectInRange(owner)) {
				del(object, true);
				object.getKnownList().del(owner, true);
			}
		}
	}

	/**
	 * Find objects that are in visibility range.
	 */
	protected void findVisibleObjects() {
		if (owner == null || !owner.isSpawned())
			return;

		MapRegion[] regions = owner.getActiveRegion().getNeighbours();
		for (int i = 0; i < regions.length; i++) {
			MapRegion r = regions[i];
			FastMap<Integer, VisibleObject> objects = r.getObjects();
			for (FastMap.Entry<Integer, VisibleObject> e = objects.head(), mapEnd = objects.tail(); (e = e.getNext()) != mapEnd;) {
				VisibleObject newObject = e.getValue();
				if (newObject == owner || newObject == null)
					continue;

				if (!isAwareOf(newObject)) {
					continue;
				}
				if (knownObjects.containsKey(newObject.getObjectId()))
					continue;

				if (!checkObjectInRange(newObject) && !newObject.getKnownList().checkReversedObjectInRange(owner))
					continue;

				/**
				 * New object is not known.
				 */
				if (add(newObject)) {
					newObject.getKnownList().add(owner);
				}
			}
		}
	}

	/**
	 * Whether knownlist owner aware of found object (should be kept in
	 * knownlist)
	 *
	 * @param newObject
	 * @return
	 */
	protected boolean isAwareOf(VisibleObject newObject) {
		return true;
	}

	protected boolean checkObjectInRange(VisibleObject newObject) {
		// check if Z distance is greater than maxZvisibleDistance
		if (Math.abs(owner.getZ() - newObject.getZ()) > owner.getMaxZVisibleDistance())
			return false;

		return MathUtil.isInRange(owner, newObject, owner.getVisibilityDistance());
	}

	/**
	 * Check can be overriden if new object has different known range and that
	 * value should be used
	 *
	 * @param newObject
	 * @return
	 */
	protected boolean checkReversedObjectInRange(VisibleObject newObject) {
		return false;
	}

	public void doOnAllNpcs(Visitor<Npc> visitor) {
		doOnAllNpcs(visitor, Integer.MAX_VALUE);
	}

	public int doOnAllNpcs(Visitor<Npc> visitor, int iterationLimit) {
		int counter = 0;
		try {
			for (FastMap.Entry<Integer, VisibleObject> e = knownObjects.head(), mapEnd = knownObjects.tail(); (e = e.getNext()) != mapEnd;) {
				VisibleObject newObject = e.getValue();
				if (newObject instanceof Npc) {
					if ((++counter) == iterationLimit)
						break;
					visitor.visit((Npc) newObject);
				}
			}
		}
		catch (Exception ex) {
			log.error("Exception when running visitor on all npcs" + ex);
		}
		return counter;
	}

	public void doOnAllNpcsWithOwner(VisitorWithOwner<Npc, VisibleObject> visitor) {
		doOnAllNpcsWithOwner(visitor, Integer.MAX_VALUE);
	}

	public int doOnAllNpcsWithOwner(VisitorWithOwner<Npc, VisibleObject> visitor, int iterationLimit) {
		int counter = 0;
		try {
			for (FastMap.Entry<Integer, VisibleObject> e = knownObjects.head(), mapEnd = knownObjects.tail(); (e = e.getNext()) != mapEnd;) {
				VisibleObject newObject = e.getValue();
				if (newObject instanceof Npc) {
					if ((++counter) == iterationLimit)
						break;
					visitor.visit((Npc) newObject, owner);
				}
			}
		}
		catch (Exception ex) {
			log.error("Exception when running visitor on all npcs" + ex);
		}
		return counter;
	}

	public void doOnAllPlayers(Visitor<Player> visitor) {
		if (knownPlayers == null) {
			return;
		}
		try {
			for (FastMap.Entry<Integer, Player> e = knownPlayers.head(), mapEnd = knownPlayers.tail(); (e = e.getNext()) != mapEnd;) {
				Player player = e.getValue();
				if (player != null) {
					visitor.visit(player);
				}
			}
		}
		catch (Exception ex) {
			log.error("Exception when running visitor on all players" + ex);
		}
	}

	public void doOnAllObjects(Visitor<VisibleObject> visitor) {
		try {
			for (FastMap.Entry<Integer, VisibleObject> e = knownObjects.head(), mapEnd = knownObjects.tail(); (e = e.getNext()) != mapEnd;) {
				VisibleObject newObject = e.getValue();
				if (newObject != null) {
					visitor.visit(newObject);
				}
			}
		}
		catch (Exception ex) {
			log.error("Exception when running visitor on all objects" + ex);
		}
	}

	public Map<Integer, VisibleObject> getKnownObjects() {
		return knownObjects;
	}

	public Map<Integer, VisibleObject> getVisibleObjects() {
		return visualObjects;
	}

	public Map<Integer, Player> getKnownPlayers() {
		return knownPlayers != null ? knownPlayers : Collections.<Integer, Player>emptyMap();
	}

	public Map<Integer, Player> getVisiblePlayers() {
		return visualPlayers != null ? visualPlayers : Collections.<Integer, Player>emptyMap();
	}

	final void checkKnownPlayersInitialized() {
		if (knownPlayers == null) {
			synchronized (this) {
				if (knownPlayers == null) {
					knownPlayers = new FastMap<Integer, Player>().shared();
				}
			}
		}
	}

	final void checkVisiblePlayersInitialized() {
		if (visualPlayers == null) {
			synchronized (this) {
				if (visualPlayers == null) {
					visualPlayers = new FastMap<Integer, Player>().shared();
				}
			}
		}
	}

	public VisibleObject getObject(int targetObjectId) {
		return this.knownObjects.get(targetObjectId);
	}

}
