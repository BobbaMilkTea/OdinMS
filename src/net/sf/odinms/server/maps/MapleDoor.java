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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.server.maps;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Matze
 */
public class MapleDoor extends AbstractMapleMapObject {

	private MapleCharacter owner;
	private MapleMap town;
	private MaplePortal townPortal;
	private MapleMap target;
	private Point targetPosition;
	
	public MapleDoor(MapleCharacter owner, Point targetPosition) {
		super();
		this.owner = owner;
		this.target = owner.getMap();
		this.targetPosition = targetPosition;
		setPosition(this.targetPosition);
		this.town = this.target.getReturnMap();
		this.townPortal = getFreePortal();
	}
	
	public MapleDoor(MapleDoor origDoor) {
		super();
		this.owner = origDoor.owner;
		this.town = origDoor.town;
		this.townPortal = origDoor.townPortal;
		this.target = origDoor.target;
		this.targetPosition = origDoor.targetPosition;
		this.townPortal = origDoor.townPortal;
		setPosition(this.townPortal.getPosition());
	}
	
	private MaplePortal getFreePortal() {
		List<MaplePortal> freePortals = new ArrayList<MaplePortal>();
		
		for (MaplePortal port : town.getPortals()) {
			if (port.getType() == 6) {
				freePortals.add(port);
			}
		}
		Collections.sort(freePortals, new Comparator<MaplePortal>() {

			public int compare(MaplePortal o1, MaplePortal o2) {
				if (o1.getId() < o2.getId())
					return -1;
				else if (o1.getId() == o2.getId())
					return 0;
				else
					return 1;
			}
			
		});
		for (MapleMapObject obj : town.getMapObjects()) {
			if (obj instanceof MapleDoor) {
				MapleDoor door = (MapleDoor) obj;
				if (door.getOwner().getParty() != null &&
					owner.getParty().containsMembers(new MaplePartyCharacter(door.getOwner()))) {
					freePortals.remove(door.getTownPortal());
				}
			}
		}
		return freePortals.iterator().next();
	}
	
	public void sendSpawnData(MapleClient client) {
		if (target.getId() == client.getPlayer().getMapId() || 
			owner == client.getPlayer() && owner.getParty() == null) {
			client.getSession().write(MaplePacketCreator.spawnDoor(owner.getId(), 
				town.getId() == client.getPlayer().getMapId() ? townPortal.getPosition() : targetPosition, 
				true));
			if (owner.getParty() != null && (owner == client.getPlayer() || owner.getParty().containsMembers(new MaplePartyCharacter(client.getPlayer())))) {
				client.getSession().write(MaplePacketCreator.partyPortal(town.getId(), target.getId(), targetPosition));
			}
			client.getSession().write(MaplePacketCreator.spawnPortal(town.getId(), target.getId(), targetPosition));
		}
	}

	public void sendDestroyData(MapleClient client) {
		if (target.getId() == client.getPlayer().getMapId() || owner == client.getPlayer() || 
			owner.getParty() != null && owner.getParty().containsMembers(new MaplePartyCharacter(client.getPlayer()))) {
			if (owner.getParty() != null && (owner == client.getPlayer() || owner.getParty().containsMembers(new MaplePartyCharacter(client.getPlayer())))) {
				client.getSession().write(MaplePacketCreator.partyPortal(999999999, 999999999, new Point(-1, -1)));
			}
			client.getSession().write(MaplePacketCreator.removeDoor(owner.getId(), 
				false));
			client.getSession().write(MaplePacketCreator.removeDoor(owner.getId(), 
				true));
		}
	}
	
	public void warp(MapleCharacter chr) {
		if (chr == owner || 
			owner.getParty() != null && owner.getParty().containsMembers(new MaplePartyCharacter(chr))) {
			if (chr.getMapId() == town.getId()) {
				chr.changeMap(target, targetPosition);
			} else if (chr.getMapId() == target.getId()) {
				chr.changeMap(town, townPortal);
			} else {
				// wtf.
			}
		} else {
			chr.getClient().getSession().write(MaplePacketCreator.enableActions());
		}
	}

	public MapleCharacter getOwner() {
		return owner;
	}

	public MapleMap getTown() {
		return town;
	}

	public MaplePortal getTownPortal() {
		return townPortal;
	}

	public MapleMap getTarget() {
		return target;
	}

	public Point getTargetPosition() {
		return targetPosition;
	}

	@Override
	public MapleMapObjectType getType() {
		return MapleMapObjectType.DOOR;
	}

}
