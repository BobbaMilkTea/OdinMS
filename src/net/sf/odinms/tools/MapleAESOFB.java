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

package net.sf.odinms.tools;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class MapleAESOFB {
	private byte iv[];
	private Cipher cipher;
	private short mapleVersion;
	private static final byte[] funnyBytes = new byte[] {
		(byte) 0xEC, 0x3F, 0x77, (byte) 0xA4, 0x45, (byte) 0xD0, 0x71,
		(byte) 0xBF, (byte) 0xB7, (byte) 0x98, 0x20, (byte) 0xFC, 0x4B,
		(byte) 0xE9, (byte) 0xB3, (byte) 0xE1, 0x5C, 0x22, (byte) 0xF7, 0x0C,
		0x44, 0x1B, (byte) 0x81, (byte) 0xBD, 0x63, (byte) 0x8D, (byte) 0xD4,
		(byte) 0xC3, (byte) 0xF2, 0x10, 0x19, (byte) 0xE0, (byte) 0xFB,
		(byte) 0xA1, 0x6E, 0x66, (byte) 0xEA, (byte) 0xAE, (byte) 0xD6,
		(byte) 0xCE, 0x06, 0x18, 0x4E, (byte) 0xEB, 0x78, (byte) 0x95,
		(byte) 0xDB, (byte) 0xBA, (byte) 0xB6, 0x42, 0x7A, 0x2A, (byte) 0x83,
		0x0B, 0x54, 0x67, 0x6D, (byte) 0xE8, 0x65, (byte) 0xE7, 0x2F, 0x07,
		(byte) 0xF3, (byte) 0xAA, 0x27, 0x7B, (byte) 0x85, (byte) 0xB0, 0x26,
		(byte) 0xFD, (byte) 0x8B, (byte) 0xA9, (byte) 0xFA, (byte) 0xBE,
		(byte) 0xA8, (byte) 0xD7, (byte) 0xCB, (byte) 0xCC, (byte) 0x92,
		(byte) 0xDA, (byte) 0xF9, (byte) 0x93, 0x60, 0x2D, (byte) 0xDD,
		(byte) 0xD2, (byte) 0xA2, (byte) 0x9B, 0x39, 0x5F, (byte) 0x82, 0x21,
		0x4C, 0x69, (byte) 0xF8, 0x31, (byte) 0x87, (byte) 0xEE, (byte) 0x8E,
		(byte) 0xAD, (byte) 0x8C, 0x6A, (byte) 0xBC, (byte) 0xB5, 0x6B, 0x59,
		0x13, (byte) 0xF1, 0x04, 0x00, (byte) 0xF6, 0x5A, 0x35, 0x79, 0x48,
		(byte) 0x8F, 0x15, (byte) 0xCD, (byte) 0x97, 0x57, 0x12, 0x3E, 0x37,
		(byte) 0xFF, (byte) 0x9D, 0x4F, 0x51, (byte) 0xF5, (byte) 0xA3, 0x70,
		(byte) 0xBB, 0x14, 0x75, (byte) 0xC2, (byte) 0xB8, 0x72, (byte) 0xC0,
		(byte) 0xED, 0x7D, 0x68, (byte) 0xC9, 0x2E, 0x0D, 0x62, 0x46, 0x17,
		0x11, 0x4D, 0x6C, (byte) 0xC4, 0x7E, 0x53, (byte) 0xC1, 0x25,
		(byte) 0xC7, (byte) 0x9A, 0x1C, (byte) 0x88, 0x58, 0x2C, (byte) 0x89,
		(byte) 0xDC, 0x02, 0x64, 0x40, 0x01, 0x5D, 0x38, (byte) 0xA5,
		(byte) 0xE2, (byte) 0xAF, 0x55, (byte) 0xD5, (byte) 0xEF, 0x1A, 0x7C,
		(byte) 0xA7, 0x5B, (byte) 0xA6, 0x6F, (byte) 0x86, (byte) 0x9F, 0x73,
		(byte) 0xE6, 0x0A, (byte) 0xDE, 0x2B, (byte) 0x99, 0x4A, 0x47,
		(byte) 0x9C, (byte) 0xDF, 0x09, 0x76, (byte) 0x9E, 0x30, 0x0E,
		(byte) 0xE4, (byte) 0xB2, (byte) 0x94, (byte) 0xA0, 0x3B, 0x34, 0x1D,
		0x28, 0x0F, 0x36, (byte) 0xE3, 0x23, (byte) 0xB4, 0x03, (byte) 0xD8,
		(byte) 0x90, (byte) 0xC8, 0x3C, (byte) 0xFE, 0x5E, 0x32, 0x24, 0x50,
		0x1F, 0x3A, 0x43, (byte) 0x8A, (byte) 0x96, 0x41, 0x74, (byte) 0xAC,
		0x52, 0x33, (byte) 0xF0, (byte) 0xD9, 0x29, (byte) 0x80, (byte) 0xB1,
		0x16, (byte) 0xD3, (byte) 0xAB, (byte) 0x91, (byte) 0xB9, (byte) 0x84,
		0x7F, 0x61, 0x1E, (byte) 0xCF, (byte) 0xC5, (byte) 0xD1, 0x56, 0x3D,
		(byte) 0xCA, (byte) 0xF4, 0x05, (byte) 0xC6, (byte) 0xE5, 0x08, 0x49,
		0x4F, 0x64, 0x69, 0x6E, 0x4D, 0x53, 0x7E, 0x46, 0x72, 0x7A 
};

	/**
	 * @param key the 256 bit key to use
	 * @param iv the IV, 4 byte
	 */
	public MapleAESOFB(byte key[], byte iv[], short mapleVersion) {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

		try {
			cipher = Cipher.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		try {
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}

		this.setIv(iv);
		this.mapleVersion = (short) (((mapleVersion >> 8) & 0xFF) | ((mapleVersion << 8) & 0xFF00));
	}

	private void setIv(byte[] iv) {
		this.iv = iv;
	}

	/**
	 * For debugging/testing purposes only.
	 * 
	 * @return the iv
	 */
	public byte[] getIv() {
		return this.iv;
	}

	public byte[] crypt(byte[] data) {
		int remaining = data.length;
		int llength = 0x5B0;
		int start = 0;
		while (remaining > 0) {
			byte[] myIv = BitTools.multiplyBytes(this.iv, 4, 4);
			if (remaining < llength) {
				llength = remaining;
			}
			for (int x = start; x < (start + llength); x++) {
				if ((x - start) % myIv.length == 0) {
					try {
						byte[] newIv = cipher.doFinal(myIv);
						for (int j = 0; j < myIv.length; j++) {
							myIv[j] = newIv[j];
						}
						// System.out
						// .println("Iv is now " + HexTool.toString(this.iv));
					} catch (IllegalBlockSizeException e) {
						e.printStackTrace();
					} catch (BadPaddingException e) {
						e.printStackTrace();
					}
				}
				data[x] ^= myIv[(x - start) % myIv.length];
			}
			start += llength;
			remaining -= llength;
			llength = 0x5B4;
		}
		updateIv();
		return data;
	}

	private void updateIv() {
		this.iv = getNewIv(this.iv);
	}

	public byte[] getPacketHeader(int length) {
		int iiv = (iv[3]) & 0xFF;
		iiv |= (iv[2] << 8) & 0xFF00;

		iiv ^= mapleVersion;
		int mlength = ((length << 8) & 0xFF00) | (length >>> 8);
		int xoredIv = iiv ^ mlength;

		byte[] ret = new byte[4];
		ret[0] = (byte) ((iiv >>> 8) & 0xFF);
		ret[1] = (byte) (iiv & 0xFF);
		ret[2] = (byte) ((xoredIv >>> 8) & 0xFF);
		ret[3] = (byte) (xoredIv & 0xFF);
		return ret;
	}

	public static int getPacketLength(int packetHeader) {
		int packetLength = ((packetHeader >>> 16) ^ (packetHeader & 0xFFFF));
		packetLength = ((packetLength << 8) & 0xFF00) | ((packetLength >>> 8) & 0xFF); // fix endianness
		return packetLength;
	}

	public boolean checkPacket(byte[] packet) {
		return ((((packet[0] ^ iv[2]) & 0xFF) == ((mapleVersion >> 8) & 0xFF)) && (((packet[1] ^ iv[3]) & 0xFF) == (mapleVersion & 0xFF)));
	}

	public boolean checkPacket(int packetHeader) {
		byte packetHeaderBuf[] = new byte[2];
		packetHeaderBuf[0] = (byte) ((packetHeader >> 24) & 0xFF);
		packetHeaderBuf[1] = (byte) ((packetHeader >> 16) & 0xFF);
		return checkPacket(packetHeaderBuf);
	}

	public static byte[] getNewIv(byte oldIv[]) {
		byte[] in = { (byte) 0xf2, 0x53, (byte) 0x50, (byte) 0xc6 }; // magic
		// ;)
		for (int x = 0; x < 4; x++) {
			funnyShit(oldIv[x], in);
			// System.out.println(HexTool.toString(in));
		}
		return in;
	}

	public String toString() {
		return "IV: " + HexTool.toString(this.iv);
	}

	/**
	 * Does funny shit. oldiv != in must be true. Modifies in and returns it for convenience
	 */
	public static byte[] funnyShit(byte inputByte, byte[] in) {
		byte elina = in[1];
		byte anna = inputByte;
		byte moritz = funnyBytes[(int) elina & 0xFF];
		moritz -= inputByte;
		in[0] += moritz;
		moritz = in[2];
		moritz ^= funnyBytes[(int) anna & 0xFF];
		elina -= (int) moritz & 0xFF;
		in[1] = elina;
		elina = in[3];
		moritz = elina;
		elina -= (int) in[0] & 0xFF;
		moritz = funnyBytes[(int) moritz & 0xFF];
		moritz += inputByte;
		moritz ^= in[2];
		in[2] = moritz;
		elina += (int) funnyBytes[(int) anna & 0xFF] & 0xFF;
		in[3] = elina;

		int merry = ((int) in[0]) & 0xFF;
		merry |= (in[1] << 8) & 0xFF00;
		merry |= (in[2] << 16) & 0xFF0000;
		merry |= (in[3] << 24) & 0xFF000000;
		int ret_value = merry;
		ret_value = ret_value >>> 0x1d;
		merry = merry << 3;
		ret_value = ret_value | merry;

		in[0] = (byte) (ret_value & 0xFF);
		in[1] = (byte) ((ret_value >> 8) & 0xFF);
		in[2] = (byte) ((ret_value >> 16) & 0xFF);
		in[3] = (byte) ((ret_value >> 24) & 0xFF);

		return in;
	}
}