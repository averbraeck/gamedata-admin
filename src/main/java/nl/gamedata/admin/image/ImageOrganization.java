package nl.gamedata.admin.image;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.SessionUtils;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.OrganizationRecord;

/**
 * ImageOrganization.java.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
@WebServlet("/imageOrganization")
public class ImageOrganization extends HttpServlet
{
    /** */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        Integer organizationId = Integer.valueOf(request.getParameter("id").toString());
        AdminData data = SessionUtils.getData(request.getSession());
        OrganizationRecord organization = SqlUtils.readRecordFromId(data, Tables.ORGANIZATION, organizationId);
        if (organization == null || organization.getLogo() == null)
            ImageUtil.makeResponse(response, ImageUtil.getNoImage());
        else
            ImageUtil.makeResponse(response, organization.getLogo());
    }
}
