package nl.gamedata.admin.image;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.SessionUtils;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.GameRecord;

/**
 * ImageGame.java.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
@WebServlet("/imageGame")
public class ImageGame extends HttpServlet
{
    /** */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        Integer gameId = Integer.valueOf(request.getParameter("id").toString());
        AdminData data = SessionUtils.getData(request.getSession());
        GameRecord game = SqlUtils.readRecordFromId(data, Tables.GAME, gameId);
        if (game == null || game.getLogo() == null)
            ImageUtil.makeResponse(response, ImageUtil.getNoImage());
        else
            ImageUtil.makeResponse(response, game.getLogo());
    }
}
