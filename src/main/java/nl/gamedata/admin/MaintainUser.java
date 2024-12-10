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
            <div class="gd-table-caption">
              <div class="gd-table-title"><h3>User</h3></div>
              <div class="gd-button">
                <button type="button" class="btn btn-primary" onclick="clickMenu('record-new')">New</button>
              </div>
            </div>

            <table class="table">
              <thead>
                <tr>
                  <th class="gd-col-icon" scope="col"><i class="fas fa-square fa-fw"></i></th>
                  <th class="gd-col-icon" scope="col"><i class="far fa-eye fa-fw"></i></th>
                  <th class="gd-col-icon" scope="col"><i class="fas fa-pencil fa-fw"></i></th>
                  <th class="gd-col-icon" scope="col"><i class="far fa-trash-can fa-fw"></i></th>
                  <th class="gd-col-icon" scope="col">&nbsp;</th>
                  <th scope="col">
                    Name &nbsp;
                    <a href="#" onclick="clickMenu('az-name')">
                      <i class="fas fa-arrow-down-z-a fa-fw"></i>
                    </a>
                  </th>
                  <th scope="col">
                    Email &nbsp;
                    <a href="#" onclick="clickMenu('az-email')">
                      <i class="fas fa-arrow-down-z-a fa-fw"></i>
                    </a>
                  </th>
                  <th scope="col">
                    Super User &nbsp;
                    <a href="#" onclick="clickMenu('az-super_admin')">
                      <i class="fas fa-arrow-down-z-a fa-fw"></i>
                    </a>
                  </th>
                </tr>
              </thead>
              <tbody>
            """;

    private static final String tableRowStart = """
                <tr>
                  <td class="gd-col-icon" scope="col">
                    <a href="#" onclick="clickRecordId('record-select', %d)">
                      <i class="far fa-square fa-fw"></i>
                    </a>
                  </td>
                  <td class="gd-col-icon" scope="col">
                    <a href="#" onclick="clickRecordId('record-view', %d)">
                      <i class="far fa-eye fa-fw"></i>
                    </a>
                  </td>
                  <td class="gd-col-icon" scope="col">
                    <a href="#" onclick="clickRecordId('record-edit', %d)">
                      <i class="fas fa-pencil fa-fw"></i></td>
                    </a>
                  <td class="gd-col-icon" scope="col">
                    <a href="#" onclick="clickRecordId('record-delete', %d)">
                      <i class="far fa-trash-can fa-fw"></i></td>
                    </a>
                  <td class="gd-col-icon" scope="col">&nbsp;</td>
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
            for (var organizationRole : data.getOrganizationRoles())
            {
                if (organizationRole.getAdmin() != 0)
                    orgIdAdminSet.add(organizationRole.getOrganizationId());
            }
            if (orgIdAdminSet.size() != 0)
            {
                // all users with a role with these organizations
                // note you cannot have any other organizational role unless you are a member of the organization first
                Set<Integer> userSet = new HashSet<>();
                for (int orgId : orgIdAdminSet)
                {
                    List<OrganizationRoleRecord> organizationRoleList = dslContext.selectFrom(Tables.ORGANIZATION_ROLE)
                            .where(Tables.ORGANIZATION_ROLE.ORGANIZATION_ID.eq(orgId)).fetch();
                    for (var organizationRole : organizationRoleList)
                        userSet.add(organizationRole.getUserId());
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
            s.append(tableRowStart.formatted(user.getId(), user.getId(), user.getId(), user.getId()));
            s.append(tableCell.formatted(user.getName()));
            s.append(tableCell.formatted(user.getEmail()));
            s.append(tableCell.formatted(user.getSuperAdmin() == 1 ? "Y" : "N"));
            s.append(tableRowEnd);
        }
        s.append(tableEnd);
        data.setContent(s.toString());
    }
}
