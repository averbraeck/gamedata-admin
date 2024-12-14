package nl.gamedata.admin.organization;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.Table;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.OrganizationRecord;

/**
 * MaintainOrganization takes care of the organization screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class MaintainOrganization
{
    public static void handleMenu(final AdminData data, final HttpServletRequest request, final String menuChoice,
            final int recordNr)
    {
        StringBuilder s = new StringBuilder();
        Table.tableStart(s, "Organization", new String[] {"Code", "Name"}, true, "Code", true);
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<OrganizationRecord> organizationRecords = dslContext.selectFrom(Tables.ORGANIZATION).fetch();
        for (var organization : organizationRecords)
        {
            Table.tableRow(s, recordNr, new String[] {organization.getCode(), organization.getName()});
        }
        Table.tableEnd(s);
        data.setContent(s.toString());
    }
}
