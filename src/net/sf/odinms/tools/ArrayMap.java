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

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ArrayMap<K, V> extends AbstractMap<K, V> {
	static class Entry<K, V> implements Map.Entry<K, V> {
		protected K key;
		protected V value;

		public Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(V newValue) {
			V oldValue = value;
			value = newValue;
			return oldValue;
		}

		@SuppressWarnings("unchecked")
		public boolean equals(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			Map.Entry e = (Map.Entry) o;
			return (key == null ? e.getKey() == null : key.equals(e.getKey())) &&
				(value == null ? e.getValue() == null : value.equals(e.getValue()));
		}

		public int hashCode() {
			int keyHash = (key == null ? 0 : key.hashCode());
			int valueHash = (value == null ? 0 : value.hashCode());
			return keyHash ^ valueHash;
		}

		public String toString() {
			return key + "=" + value;
		}
	}

	private Set<? extends java.util.Map.Entry<K, V>> entries = null;

	private ArrayList<Entry<K, V>> list;

	public ArrayMap() {
		list = new ArrayList<Entry<K, V>>();
	}

	public ArrayMap(Map<K, V> map) {
		list = new ArrayList<Entry<K, V>>();
		putAll(map);
	}

	public ArrayMap(int initialCapacity) {
		list = new ArrayList<Entry<K, V>>(initialCapacity);
	}

	@SuppressWarnings("unchecked")
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		if (entries == null) {
			entries = new AbstractSet<Entry<K, V>>() {
				public void clear() {
					throw new UnsupportedOperationException();
				}

				public Iterator<Entry<K, V>> iterator() {
					return (Iterator<Entry<K, V>>) list.iterator();
				}

				public int size() {
					return list.size();
				}
			};
		}
		return (Set<java.util.Map.Entry<K,V>>) entries;
	}

	public V put(K key, V value) {
		int size = list.size();
		Entry<K, V> entry = null;
		int i;
		if (key == null) {
			for (i = 0; i < size; i++) {
				entry = (Entry<K, V>) (list.get(i));
				if (entry.getKey() == null) {
					break;
				}
			}
		} else {
			for (i = 0; i < size; i++) {
				entry = (Entry<K, V>) (list.get(i));
				if (key.equals(entry.getKey())) {
					break;
				}
			}
		}
		V oldValue = null;
		if (i < size) {
			oldValue = entry.getValue();
			entry.setValue(value);
		} else {
			list.add(new Entry<K, V>(key, value));
		}
		return oldValue;
	}
}
