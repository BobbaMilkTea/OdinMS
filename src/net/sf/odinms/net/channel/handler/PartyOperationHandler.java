/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
					   Matthias Butz <matze@odinms.de>
					   Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.sf.odinms.net.channel.handler;

import java.rmi.RemoteException;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.PartyOperation;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class PartyOperationHandler extends AbstractMaplePacketHandler {
	// private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PartyOperationHandler.class);

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		int operation = slea.readByte();
		MapleCharacter player = c.getPlayer();
		WorldChannelInterface wci = ChannelServer.getInstance(c.getChannel()).getWorldInterface();
		MapleParty party = player.getParty();
		MaplePartyCharacter partyplayer = new MaplePartyCharacter(player);

		switch (operation) {
			case 1: { // create
				if (c.getPlayer().getParty() == null) {
					try {
						party = wci.createParty(partyplayer);
						player.setParty(party);
					} catch (RemoteException e) {
						c.getChannelServer().reconnectWorld();
					}
					c.getSession().write(MaplePacketCreator.partyCreated());
				} else {
					c.getSession().write(MaplePacketCreator.serverNotice(5, "You can't create a party as you are already in one"));
				}
				break;
			}
			case 2: { // leave
				if (party != null) { //are we in a party? o.O"
					try {
						if (partyplayer.equals(party.getLeader())) { // disband
							wci.updateParty(party.getId(), PartyOperation.DISBAND, partyplayer);
						} else {
							wci.updateParty(party.getId(), PartyOperation.LEAVE, partyplayer);
						}
					} catch (RemoteException e) {
						c.getChannelServer().reconnectWorld();
					}
					player.setParty(null);
				}
				break;
			}
			case 3: { // accept invitation
				int partyid = slea.readInt();
				if (c.getPlayer().getParty() == null) {
					try {
						party = wci.getParty(partyid);
						if (party != null) {
							if (party.getMembers().size() < 6) {
								wci.updateParty(party.getId(), PartyOperation.JOIN, partyplayer);
								player.receivePartyMemberHP();
								player.updatePartyMemberHP();
							} else {
								c.getSession().write(MaplePacketCreator.partyStatusMessage(17));
							}
						} else {
							c.getSession().write(MaplePacketCreator.serverNotice(5, "The party you are trying to join does not exist"));
						}
					} catch (RemoteException e) {
						c.getChannelServer().reconnectWorld();
					}
				} else {
					c.getSession().write(MaplePacketCreator.serverNotice(5, "You can't join the party as you are already in one"));
				}
				break;
			}
			case 4: { // invite
				//TODO store pending invitations and check against them
				String name = slea.readMapleAsciiString();
				MapleCharacter invited = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
				if (invited != null) {
					if (invited.getParty() == null) {
						if (invited != null && party.getMembers().size() < 6) {
							invited.getClient().getSession().write(MaplePacketCreator.partyInvite(player));
						}
					} else {
						c.getSession().write(MaplePacketCreator.partyStatusMessage(16));
					}
				} else {
					c.getSession().write(MaplePacketCreator.partyStatusMessage(18));
				}
				break;
			}
			case 5: { // expel
				int cid = slea.readInt();
				if (partyplayer.equals(party.getLeader())) {
					MaplePartyCharacter expelled = party.getMemberById(cid);
					if (expelled != null) {
						try {
							wci.updateParty(party.getId(), PartyOperation.EXPEL, expelled);
						} catch (RemoteException e) {
							c.getChannelServer().reconnectWorld();
						}
					}
				}
				break;
			}
		}
	}
}
