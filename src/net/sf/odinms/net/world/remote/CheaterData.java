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

package net.sf.odinms.net.world.remote;

import java.io.Serializable;

/**
 *
 * @author Matze
 */
public class CheaterData implements Serializable, Comparable<CheaterData> {
	private static final long serialVersionUID = -8733673311051249885L;
	
	private int points;
	private String info;

	public CheaterData(int points, String info) {
		this.points = points;
		this.info = info;
	}

	public String getInfo() {
		return info;
	}

	public int getPoints() {
		return points;
	}

	public int compareTo(CheaterData o) {
		int thisVal = getPoints();
		int anotherVal = o.getPoints();
		return (thisVal<anotherVal ? 1 : (thisVal==anotherVal ? 0 : -1));
	}
}
