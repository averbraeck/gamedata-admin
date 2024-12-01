package nl.gamedata.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.UserRecord;

/**
 * MaintainUser takes care of the user screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class MaintainUser
{
    private static final String tableHead = """
            <table class="table">
              <thead>
                <tr>
                  <th scope="col">Name</th>
                  <th scope="col">Email</th>
                  <th scope="col">Super Admin</th>
                </tr>
              </thead>
              <tbody>
            """;

    private static final String tableRowStart = """
                <tr>
            """;

    private static final String tableRow = """
                  <td>%s</td>
            """;

    private static final String tableRowEnd = """
                </tr>
            """;

    private static final String tableFoot = """
              </tbody>
            </table>
            """;

    public static void handleMenu(final AdminData data, final HttpServletRequest request, final String menuChoice,
            final int recordNr)
    {
        StringBuilder s = new StringBuilder();
        s.append(tableHead);
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<UserRecord> userRecords = dslContext.selectFrom(Tables.USER).fetch();
        for (var user : userRecords)
        {
            s.append(tableRowStart);
            s.append(tableRow.formatted(user.getName()));
            s.append(tableRow.formatted(user.getEmail()));
            s.append(tableRow.formatted(user.getSuperAdmin() == 1 ? "Y" : "N"));
            s.append(tableRowEnd);
        }
        s.append(tableFoot);
        data.setContent(s.toString());
    }
}
