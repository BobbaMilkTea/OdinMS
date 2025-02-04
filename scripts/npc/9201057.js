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

/**
 Bell (NLC Dude)
**/

var status = 0;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (status == 0) {
		if (cm.getChar().getMapId() == 103000100) {
			cm.sendYesNo("Do you wish to visit New Leaf City on the continent of Masteria?");
		} else {
			cm.sendYesNo("Do you wish to go to back to Kerning City?");
		}
		status++;
	} else {
		if ((status == 1 && type == 1 && selection == -1 && mode == 0) || mode == -1) {
			cm.dispose();
		} else {
			if (status == 1) {
					cm.sendNext ("Alright, see you next time. Take care.");
					status++
			} else if (status == 2) {
					if (cm.getChar().getMapId() == 103000100) {
						cm.warp(600010001, 0);
					} else {
						cm.warp(103000100, 0);
					}
					cm.dispose();
			}
		}
	}
}
