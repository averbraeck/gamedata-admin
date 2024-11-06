package nl.gamedata.admin;

import javax.servlet.http.HttpSession;

import nl.gamedata.common.SqlUtils;

public final class AdminUtils extends SqlUtils {

	public static void loadAttributes(final HttpSession session) {
		AdminData data = SessionUtils.getData(session);
		data.setMenuChoice("");
	}

}
