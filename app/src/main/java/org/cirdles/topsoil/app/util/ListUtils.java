package org.cirdles.topsoil.app.util;

import java.util.List;
import java.util.StringJoiner;

public class ListUtils {

	public static String listToString( List<?> list ) {
		StringJoiner joiner = new StringJoiner(", ");
		for (int i = 0; i < list.size(); i++) {
			joiner.add(list.get(i).toString());
		}
		return String.format("{ %s }", joiner.toString());
	}

}
