package nl.gamedata.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.gamedata.admin.table.MaintainGame;
import nl.gamedata.admin.table.MaintainOrganization;
import nl.gamedata.admin.table.MaintainUser;

@WebServlet("/admin")
@MultipartConfig
public class AdminServlet extends HttpServlet
{

    /** */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession session = request.getSession();

        AdminData data = SessionUtils.getData(session);
        if (data == null)
        {
            response.sendRedirect("/gamedata-admin/login");
            return;
        }

        String click = "";
        if (request.getParameter("click") != null)
            click = request.getParameter("click").toString();
        else if (request.getParameter("editClick") != null)
            click = request.getParameter("editClick").toString();

        int recordId = 0;
        if (request.getParameter("recordId") != null)
            recordId = Integer.parseInt(request.getParameter("recordId"));
        else if (request.getParameter("editRecordId") != null)
            recordId = Integer.parseInt(request.getParameter("editRecordId"));

        System.out.println("Clicked: " + click);

        data.setShowModalWindow(false);
        data.setModalWindowHtml("");

        // state machine
        if (click.equals("menu-logoff"))
        {
            response.sendRedirect("jsp/admin/login.jsp");
            return;
        }

        if (click.startsWith("menu"))
            handleMenu(request, response, click, data, recordId);
        else if (click.startsWith("tab"))
            handleTab(request, response, click, data, recordId);
        else if (click.equals("record-new") || click.equals("record-view") || click.equals("record-edit"))
            handleRecordEdit(request, response, click, data, recordId);
        else if (click.equals("record-save"))
            handleRecordSave(request, response, click, data, recordId);
        else if (click.equals("record-cancel"))
            handleRecordCancel(request, response, click, data, recordId);
        else if (click.equals("record-delete"))
            handleRecordDelete(request, response, click, data, recordId);
        else if (click.startsWith("az"))
            handleSort(request, response, click, data, recordId);
        else
            System.err.println("Unknown menu choice: " + click + " with recordId: " + recordId);

        response.sendRedirect("jsp/admin/admin.jsp");
    }

    private void handleMenu(final HttpServletRequest request, final HttpServletResponse response, final String click,
            final AdminData data, final int recordId) throws IOException
    {
        String menuChoice = click.replace("menu-", "");
        data.setMenuChoice(menuChoice);
        String tabChoice = "tab-" + data.getTabChoice(menuChoice);
        handleTab(request, response, tabChoice, data, recordId);
    }

    private void handleTab(final HttpServletRequest request, final HttpServletResponse response, final String click,
            final AdminData data, final int recordId) throws IOException
    {
        String tab = click.replace("tab-", "");
        data.putTabChoice(data.getMenuChoice(), tab);
        String menu = data.getMenuChoice();
        System.err.println("TAB choice: " + click + " with recordId: " + recordId);
        Menus.table(data, request, click);

        /*-
        switch (menu)
        {
            // accessible for OrganizationAdmin and SuperAdmin
            case "organization" ->
            {
                if (Menus.showMenu(data, menu))
                    switch (tab)
                    {
                        case "organization" -> MaintainOrganization.table(data, request, click);
                        case "user" -> MaintainUser.table(data, request, click);
                        case "user-role" -> MaintainOrganization.table(data, request, click);
                        case "game" -> MaintainGame.table(data, request, click);
                        case "game-access" -> MaintainOrganization.table(data, request, click);
                        case "access-token" -> MaintainOrganization.table(data, request, click);
                        case "game-session" -> MaintainOrganization.table(data, request, click);
                        default -> System.err.println("Unexpected tab value: " + tab);
                    }
                break;
            }
            case "user" ->
            {
                if (Menus.showMenu(data, menu))
                    switch (tab)
                    {
                        case "user" -> MaintainUser.table(data, request, click);
                        case "user-role" -> MaintainUser.table(data, request, click);
                        case "game" -> MaintainGame.table(data, request, click);
                        case "game-role" -> MaintainUser.table(data, request, click);
                        default -> System.err.println("Unexpected tab value: " + tab);
                    }
                break;
            }
            case "game" ->
            {
                if (Menus.showMenu(data, menu))
                    switch (tab)
                    {
                        case "game" -> MaintainGame.table(data, request, click);
                        case "game-version" -> MaintainGame.table(data, request, click);
                        case "game-mission" -> MaintainGame.table(data, request, click);
                        case "scale" -> MaintainGame.table(data, request, click);
                        case "learning-goal" -> MaintainGame.table(data, request, click);
                        case "player-objective" -> MaintainGame.table(data, request, click);
                        case "group-objective" -> MaintainGame.table(data, request, click);
                        default -> System.err.println("Unexpected tab value: " + tab);
                    }
                break;
            }
            case "game-control" ->
            {
                if (Menus.showMenu(data, menu))
                    switch (tab)
                    {
                        case "game" -> MaintainGame.table(data, request, click);
                        case "game-access" -> MaintainGame.table(data, request, click);
                        case "game-token" -> MaintainGame.table(data, request, click);
                        default -> System.err.println("Unexpected tab value: " + tab);
                    }
                break;
            }
            case "game-session" ->
            {
                if (Menus.showMenu(data, menu))
                    switch (tab)
                    {
                        case "game" -> MaintainGame.table(data, request, click);
                        case "game-version" -> MaintainGame.table(data, request, click);
                        case "game-session" -> MaintainGame.table(data, request, click);
                        default -> System.err.println("Unexpected tab value: " + tab);
                    }
                break;
            }
            case "data-session" ->
            {
                if (Menus.showMenu(data, menu))
                    switch (tab)
                    {
                        case "game" -> MaintainGame.table(data, request, click);
                        case "game-version" -> MaintainGame.table(data, request, click);
                        case "game-session" -> MaintainGame.table(data, request, click);
                        case "game-mission" -> MaintainGame.table(data, request, click);
                        case "mission-event" -> MaintainGame.table(data, request, click);
                        default -> System.err.println("Unexpected tab value: " + tab);
                    }
                break;
            }
            case "data-player" ->
            {
                if (Menus.showMenu(data, menu))
                    switch (tab)
                    {
                        case "game" -> MaintainGame.table(data, request, click);
                        case "game-version" -> MaintainGame.table(data, request, click);
                        case "game-session" -> MaintainGame.table(data, request, click);
                        case "player" -> MaintainGame.table(data, request, click);
                        case "player-attempt" -> MaintainGame.table(data, request, click);
                        case "player-score" -> MaintainGame.table(data, request, click);
                        case "player-event" -> MaintainGame.table(data, request, click);
                        case "player-group-role" -> MaintainGame.table(data, request, click);
                        default -> System.err.println("Unexpected tab value: " + tab);
                    }
                break;
            }
            case "data-group" ->
            {
                if (Menus.showMenu(data, menu))
                    switch (tab)
                    {
                        case "game" -> MaintainGame.table(data, request, click);
                        case "game-version" -> MaintainGame.table(data, request, click);
                        case "game-session" -> MaintainGame.table(data, request, click);
                        case "group" -> MaintainGame.table(data, request, click);
                        case "group-player" -> MaintainGame.table(data, request, click);
                        case "group-attempt" -> MaintainGame.table(data, request, click);
                        case "group-score" -> MaintainGame.table(data, request, click);
                        case "group-event" -> MaintainGame.table(data, request, click);
                        default -> System.err.println("Unexpected tab value: " + tab);
                    }
                break;
            }
            default -> System.err.println("Unexpected menu value: " + menu);
        }
        */
    }

    private void handleRecordEdit(final HttpServletRequest request, final HttpServletResponse response, final String click,
            final AdminData data, final int recordId) throws IOException
    {
        String menu = data.getMenuChoice();
        String tab = data.getTabChoice(menu);
        System.err.println("RECORD choice: " + click + " with recordId: " + recordId);
        Menus.edit(data, request, click, recordId);

        /*-
        switch (menu)
        {
            case "organization" ->
            {
                switch (tab)
                {
                    case "organization" -> MaintainOrganization.edit(data, request, click, recordId);
                    case "user" -> MaintainOrganization.edit(data, request, click, recordId);
                    case "user-role" -> MaintainOrganization.edit(data, request, click, recordId);
                    case "game" -> MaintainOrganization.edit(data, request, click, recordId);
                    case "game-access" -> MaintainOrganization.edit(data, request, click, recordId);
                    case "access-token" -> MaintainOrganization.edit(data, request, click, recordId);
                    case "game-session" -> MaintainOrganization.edit(data, request, click, recordId);
                    default -> System.err.println("Unexpected tab value: " + tab);
                }
                break;
            }
            case "user" ->
            {
                switch (tab)
                {
                    case "user" -> MaintainUser.edit(data, request, click, recordId);
                    case "user-role" -> MaintainUser.edit(data, request, click, recordId);
                    case "game" -> MaintainGame.edit(data, request, click, recordId);
                    case "game-role" -> MaintainGame.edit(data, request, click, recordId);
                    default -> System.err.println("Unexpected tab value: " + tab);
                }
                break;
            }
            case "game" ->
            {
                switch (tab)
                {
                    case "game" -> MaintainGame.edit(data, request, click, recordId);
                    case "game-version" -> MaintainGame.edit(data, request, click, recordId);
                    case "game-mission" -> MaintainGame.edit(data, request, click, recordId);
                    case "scale" -> MaintainGame.edit(data, request, click, recordId);
                    case "learning-goal" -> MaintainGame.edit(data, request, click, recordId);
                    case "player-objective" -> MaintainGame.edit(data, request, click, recordId);
                    case "group-objective" -> MaintainGame.edit(data, request, click, recordId);
                    default -> System.err.println("Unexpected tab value: " + tab);
                }
                break;
            }
            case "game-control" ->
            {
                switch (tab)
                {
                    case "game" -> MaintainGame.edit(data, request, click, recordId);
                    case "game-access" -> MaintainGame.edit(data, request, click, recordId);
                    case "game-token" -> MaintainGame.edit(data, request, click, recordId);
                    default -> System.err.println("Unexpected tab value: " + tab);
                }
                break;
            }
            case "game-session" ->
            {
                switch (tab)
                {
                    case "game" -> MaintainGame.edit(data, request, click, recordId);
                    case "game-version" -> MaintainGame.edit(data, request, click, recordId);
                    case "game-session" -> MaintainGame.edit(data, request, click, recordId);
                    default -> System.err.println("Unexpected tab value: " + tab);
                }
                break;
            }
            case "data-session" ->
            {
                switch (tab)
                {
                    case "game" -> MaintainGame.edit(data, request, click, recordId);
                    case "game-version" -> MaintainGame.edit(data, request, click, recordId);
                    case "game-session" -> MaintainGame.edit(data, request, click, recordId);
                    case "game-mission" -> MaintainGame.edit(data, request, click, recordId);
                    case "mission-event" -> MaintainGame.edit(data, request, click, recordId);
                    default -> System.err.println("Unexpected tab value: " + tab);
                }
                break;
            }
            case "data-player" ->
            {
                switch (tab)
                {
                    case "game" -> MaintainGame.edit(data, request, click, recordId);
                    case "game-version" -> MaintainGame.edit(data, request, click, recordId);
                    case "game-session" -> MaintainGame.edit(data, request, click, recordId);
                    case "player" -> MaintainGame.edit(data, request, click, recordId);
                    case "player-attempt" -> MaintainGame.edit(data, request, click, recordId);
                    case "player-score" -> MaintainGame.edit(data, request, click, recordId);
                    case "player-event" -> MaintainGame.edit(data, request, click, recordId);
                    case "player-group-role" -> MaintainGame.edit(data, request, click, recordId);
                    default -> System.err.println("Unexpected tab value: " + tab);
                }
                break;
            }
            case "data-group" ->
            {
                switch (tab)
                {
                    case "game" -> MaintainGame.edit(data, request, click, recordId);
                    case "game-version" -> MaintainGame.edit(data, request, click, recordId);
                    case "game-session" -> MaintainGame.edit(data, request, click, recordId);
                    case "group" -> MaintainGame.edit(data, request, click, recordId);
                    case "group-player" -> MaintainGame.edit(data, request, click, recordId);
                    case "group-attempt" -> MaintainGame.edit(data, request, click, recordId);
                    case "group-score" -> MaintainGame.edit(data, request, click, recordId);
                    case "group-event" -> MaintainGame.edit(data, request, click, recordId);
                    default -> System.err.println("Unexpected tab value: " + tab);
                }
                break;
            }
            default -> System.err.println("Unexpected menu value: " + menu);
        }
        */
    }

    private void handleRecordSave(final HttpServletRequest request, final HttpServletResponse response, final String click,
            final AdminData data, final int recordId) throws IOException
    {
        System.err.println("RECORD SAVE: " + click + " with recordId: " + recordId);
        if (data.getEditRecord().getTable().getName().toLowerCase().equals("user"))
        {
            MaintainUser.saveUser(request, data, recordId);
        }
        else
        {
            data.saveRecord(request, recordId);
        }

        // TODO: check, popup for errors -> repair/discard

        // TODO: save, popup if error during saving -> repair/discard

        handleTab(request, response, "tab-" + data.getTabChoice(data.getMenuChoice()), data, 0);
    }

    private void handleRecordCancel(final HttpServletRequest request, final HttpServletResponse response, final String click,
            final AdminData data, final int recordId) throws IOException
    {
        System.err.println("RECORD CANCEL: " + click + " with recordId: " + recordId);

        // TODO: check for changes -> continue edit/discard

        handleTab(request, response, "tab-" + data.getTabChoice(data.getMenuChoice()), data, 0);
    }

    private void handleRecordDelete(final HttpServletRequest request, final HttpServletResponse response, final String click,
            final AdminData data, final int recordId) throws IOException
    {
        System.err.println("RECORD DELETE: " + click + " with recordId: " + recordId);

        // TODO: check for ok -> delete/cancel

        handleTab(request, response, "tab-" + data.getTabChoice(data.getMenuChoice()), data, 0);
    }

    private void handleSort(final HttpServletRequest request, final HttpServletResponse response, final String click,
            final AdminData data, final int recordId) throws IOException
    {
        System.err.println("AZ choice: " + click + " with recordId: " + recordId);
    }

}
