package nl.gamedata.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.OrganizationRoleRecord;
import nl.gamedata.data.tables.records.UserRecord;

/**
 * MaintainUser takes care of the user screen. The following users are shown for editing:
 * <ul>
 * <li>super_admin: all users</li>
 * <li>organization_admin: all users who have some role with the own organization</li>
 * <li>game_admin: only self</li>
 * <li>session_admin: only self</li>
 * <li>other: only self</li>
 * </ul>
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class MaintainUser
{
    private static final String tableStart = """
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

    private static final String tableCell = """
                  <td>%s</td>
            """;

    private static final String tableRowEnd = """
                </tr>
            """;

    private static final String tableEnd = """
              </tbody>
            </table>
            """;

    public static void handleMenu(final AdminData data, final HttpServletRequest request, final String menuChoice,
            final int recordNr)
    {
        StringBuilder s = new StringBuilder();
        s.append(tableStart);
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<UserRecord> userRecords = new ArrayList<>();
        if (data.isSuperAdmin())
        {
            userRecords = dslContext.selectFrom(Tables.USER).fetch();
        }
        else
        {
            // see if there are organization(s) for which this user is organization_admin
            Set<Integer> orgIdAdminSet = new HashSet<>();
            for (var orgRole : data.getOrganizationRoles())
            {
                if (orgRole.getOrganizationAdmin() != 0)
                    orgIdAdminSet.add(orgRole.getOrganizationId());
            }
            if (orgIdAdminSet.size() != 0)
            {
                Set<Integer> userSet = new HashSet<>();
                // all users with a role with these organizations
                for (int orgId : orgIdAdminSet)
                {
                    List<OrganizationRoleRecord> orgRoleList = dslContext.selectFrom(Tables.ORGANIZATION_ROLE)
                            .where(Tables.ORGANIZATION_ROLE.ORGANIZATION_ID.eq(orgId)).fetch();
                    for(var orgRole : orgRoleList)
                        userSet.add(orgRole.getUserId());
                }
                for (int userId : userSet)
                    userRecords.add(SqlUtils.readUserFromUserId(data, userId));
            }
            else
            {
                // only self
                userRecords.add(data.getUser());
            }
        }
        for (var user : userRecords)
        {
            s.append(tableRowStart);
            s.append(tableCell.formatted(user.getName()));
            s.append(tableCell.formatted(user.getEmail()));
            s.append(tableCell.formatted(user.getSuperAdmin() == 1 ? "Y" : "N"));
            s.append(tableRowEnd);
        }
        s.append(tableEnd);
        data.setContent(s.toString());
    }
}
