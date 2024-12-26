package nl.gamedata.admin.table;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import jakarta.xml.bind.DatatypeConverter;
import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.ModalWindowUtils;
import nl.gamedata.admin.form.table.TableEntryBoolean;
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.Access;
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
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        StringBuilder s = new StringBuilder();
        boolean newButton = data.isSuperAdmin() || data.isGameAdmin() || data.hasOrganizationAccess(Access.ADMIN);
        AdminTable.tableStart(s, "User", new String[] {"Name", "Email", "Super Admin", "Game Admin"}, newButton, "Name", true);

        List<UserRecord> userRecords = new ArrayList<>();
        if (data.isSuperAdmin())
        {
            userRecords = dslContext.selectFrom(Tables.USER).fetch();
        }
        else
        {
            // all users with a role in the organizations to which this user has admin access
            for (var entry : data.getOrganizationAccess().entrySet())
            {
                if (entry.getValue().admin())
                {
                    List<OrganizationRoleRecord> organizationRoleList = dslContext.selectFrom(Tables.ORGANIZATION_ROLE)
                            .where(Tables.ORGANIZATION_ROLE.ORGANIZATION_ID.eq(entry.getKey())).fetch();
                    for (var organizationRole : organizationRoleList)
                    {
                        var user = SqlUtils.readUserFromUserId(data, organizationRole.getUserId());
                        if (!userRecords.contains(data.getUser()))
                            userRecords.add(user);
                    }
                }
            }
        }
        // always self
        if (!userRecords.contains(data.getUser()))
            userRecords.add(data.getUser());

        for (var user : userRecords)
        {
            AdminTable.tableRow(s, user.getId(), new String[] {user.getName(), user.getEmail(),
                    user.getSuperAdmin() == 1 ? "Y" : "N", user.getGameAdmin() == 1 ? "Y" : "N"});
        }
        AdminTable.tableEnd(s);
        data.setContent(s.toString());
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        UserRecord user = recordId == 0 ? Tables.USER.newRecord() : SqlUtils.readRecordFromId(data, Tables.USER, recordId);
        String salt = recordId == 0 ? UUID.randomUUID().toString() : user.getSalt();
        data.setEditRecord(user);
        TableForm form = new TableForm(data);
        form.startForm();
        form.setHeader("User", click, recordId);
        form.addEntry(new TableEntryString(Tables.USER.NAME, user).setMinLength(2));
        form.addEntry(new TableEntryString(Tables.USER.EMAIL, user));
        form.addEntry(new TableEntryString(Tables.USER.PASSWORD, user).setInitialValue("").setRequired(recordId == 0)
                .setMinLength(recordId == 0 ? 8 : 0));
        form.addEntry(new TableEntryString(Tables.USER.SALT, user).setInitialValue(salt).setHidden());
        if (data.isSuperAdmin())
        {
            form.addEntry(new TableEntryBoolean(Tables.USER.SUPER_ADMIN, user));
            form.addEntry(new TableEntryBoolean(Tables.USER.GAME_ADMIN, user));
        }
        form.endForm();
        data.setContent(form.process());
    }

    public static int saveUser(final HttpServletRequest request, final AdminData data, final int userId)
    {
        String backToMenu = "clickMenu('menu-" + data.getMenuChoice() + "')";
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        UserRecord user = userId == 0 ? dslContext.newRecord(Tables.USER)
                : dslContext.selectFrom(Tables.USER).where(Tables.USER.ID.eq(userId)).fetchOne();
        String hashedPassword = userId == 0 ? "" : user.getPassword(); // has to be BEFORE setFields
        String errors = data.getEditForm().setFields(user, request, data);
        if (errors.length() > 0)
        {
            ModalWindowUtils.popup(data, "Error in user entries for user", errors, backToMenu);
            System.err.println("Error in user entries for user - " + errors);
            return -1;
        }
        else
        {
            if (user.getPassword().length() > 0)
            {
                MessageDigest md;
                try
                {
                    // https://www.baeldung.com/java-md5
                    md = MessageDigest.getInstance("MD5");
                    String saltedPassword = user.getPassword() + user.getSalt();
                    md.update(saltedPassword.getBytes());
                    byte[] digest = md.digest();
                    hashedPassword = DatatypeConverter.printHexBinary(digest).toLowerCase();
                }
                catch (NoSuchAlgorithmException e1)
                {
                    ModalWindowUtils.popup(data, "Error storing user record (MD5 not found)", "<p>" + e1.getMessage() + "</p>",
                            backToMenu);
                    System.err.println("Error storing user record (MD5 not found) - " + e1.getMessage());
                    return -1;
                }
            }
            user.set(Tables.USER.PASSWORD, hashedPassword); // restore old password if not changed

            try
            {
                user.store();
            }
            catch (DataAccessException exception)
            {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>", backToMenu);
                System.err.println("Error storing record - " + exception.getMessage());
                return -1;
            }
            return user.getId();
        }
    }

}
