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

package org.typezero.gameserver.network.loginserver.clientpackets;

import org.typezero.gameserver.configs.network.NetworkConfig;
import org.typezero.gameserver.network.loginserver.LsClientPacket;
import org.typezero.gameserver.services.transfers.PlayerTransferService;

/**
 * @author KID
 */
public class CM_PTRANSFER_RESPONSE extends LsClientPacket {
	public CM_PTRANSFER_RESPONSE(int opCode) {
		super(opCode);
	}

	@Override
	protected void readImpl() {
		int actionId = this.readD();
		switch(actionId)
		{
			case 20: //send info
				{
					int targetAccount = readD();
					int taskId = readD();
					String name = readS();
					String account = readS();
					int len = readD();
					byte[] db = this.readB(len);
					PlayerTransferService.getInstance().cloneCharacter(taskId, targetAccount, name, account, db);
				}
				break;
			case 21://ok
				{
					int taskId = readD();
					PlayerTransferService.getInstance().onOk(taskId);
				}
				break;
			case 22://error
				{
					int taskId = readD();
					String reason = readS();
					PlayerTransferService.getInstance().onError(taskId, reason);
				}
				break;
			case 23:
				{
					byte serverId = readSC();
					if(NetworkConfig.GAMESERVER_ID != serverId) {
						try {
							throw new Exception("Requesting player transfer for server id "+serverId+" but this is "+NetworkConfig.GAMESERVER_ID+" omgshit!");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else {
						byte targetServerId = readSC();
						int account = readD();
						int targetAccount = readD();
						int playerId = readD();
						int taskId = readD();
						PlayerTransferService.getInstance().startTransfer(account, targetAccount, playerId, targetServerId, taskId);
					}
				}
				break;
		}
	}

	@Override
	protected void runImpl() {

	}
}
