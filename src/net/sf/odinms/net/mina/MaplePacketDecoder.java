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

package net.sf.odinms.net.mina;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.tools.MapleAESOFB;
import net.sf.odinms.tools.MapleCustomEncryption;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaplePacketDecoder extends CumulativeProtocolDecoder {
	private static final String DECODER_STATE_KEY = MaplePacketDecoder.class.getName() + ".STATE";
	private static Logger log = LoggerFactory.getLogger(MaplePacketDecoder.class);
	
	private static class DecoderState {
		public int packetlength = -1;
	}

	@Override
	protected boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) throws Exception {
		MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
		DecoderState decoderState = (DecoderState) session.getAttribute(DECODER_STATE_KEY);

		if (decoderState == null) {
			decoderState = new DecoderState();
			session.setAttribute(DECODER_STATE_KEY, decoderState);
		}
		
		if (in.remaining() >= 4 && decoderState.packetlength == -1) {
			int packetHeader = in.getInt();
			if (!client.getReceiveCrypto().checkPacket(packetHeader)) {
				log.warn("Client failed packet check -> disconnecting");
				session.close();
				return true;
			}
			decoderState.packetlength = MapleAESOFB.getPacketLength(packetHeader);
		} else if (in.remaining() < 4 && decoderState.packetlength == -1) {
			log.trace("decode... not enough data");
			return false;
		}

		if (in.remaining() >= decoderState.packetlength) {
			byte decryptedPacket[] = new byte[decoderState.packetlength];
			in.get(decryptedPacket, 0, decoderState.packetlength);
			decoderState.packetlength = -1;
			
			client.getReceiveCrypto().crypt(decryptedPacket);
			MapleCustomEncryption.decryptData(decryptedPacket);
			out.write(decryptedPacket);
			
			return true;
		} else {
			log.trace("decode... not enough data to decode (need {})", decoderState.packetlength);
			return false;
		}
	}

}
