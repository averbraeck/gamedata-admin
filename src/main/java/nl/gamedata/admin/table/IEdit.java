package nl.gamedata.admin.table;

import javax.servlet.http.HttpServletRequest;

import nl.gamedata.admin.AdminData;

/**
 * Maintain.java.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
@FunctionalInterface
public interface IEdit
{
    public void edit(AdminData data, HttpServletRequest request, String click, int recordId);
}
