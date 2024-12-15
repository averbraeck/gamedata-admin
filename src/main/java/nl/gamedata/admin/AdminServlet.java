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
        else if (request.getParameter("editRecordNr") != null)
            recordId = Integer.parseInt(request.getParameter("editRecordNr"));

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
        else if (click.startsWith("record"))
            handleRecord(request, response, click, data, recordId);
        else if (click.startsWith("az"))
            handleSort(request, response, click, data, recordId);
        else
            System.err.println("Unknown menu choice: " + click + " with recordId: " + recordId);

        response.sendRedirect("jsp/admin/admin.jsp");
    }

    private void handleMenu(final HttpServletRequest request, final HttpServletResponse response, final String click,
            final AdminData data, final int recordId) throws IOException
    {
        data.setMenuChoice(click);
        switch (click)
        {
            case "menu-user":
                data.putTabChoice("user", "user");
                MaintainUser.tableUser(data, request, click, recordId);
                break;

            case "menu-organization":
                data.putTabChoice("organization", "organization");
                MaintainOrganization.tableOrganization(data, request, click, recordId);
                break;

            case "menu-game":
                data.putTabChoice("game", "game");
                MaintainGame.tableGame(data, request, click, recordId);
                break;

            default:
                System.err.println("Unknown menu choice: " + click + " with recordId: " + recordId);
                break;
        }
    }

    private void handleTab(final HttpServletRequest request, final HttpServletResponse response, final String click,
            final AdminData data, final int recordId) throws IOException
    {
        data.putTabChoice(data.getMenuChoice(), click.replace("tab-", ""));
        System.err.println("TAB choice: " + click + " with recordId: " + recordId);
    }

    private void handleRecord(final HttpServletRequest request, final HttpServletResponse response, final String click,
            final AdminData data, final int recordId) throws IOException
    {
        String menu = data.getMenuChoice();
        String tab = data.getTabChoice(menu);
        System.err.println("RECORD choice: " + click + " with recordId: " + recordId);
        switch (menu)
        {
            case "organization" ->
            {
                switch (tab)
                {
                    case "organization" -> MaintainOrganization.edit(data, request, click, recordId);
                    case "user" -> MaintainOrganization.edit(data, request, click, recordId);
                    default -> System.err.println("Unexpected tab value: " + tab);
                }
                break;
            }
            case "user" ->
            {
                switch (tab)
                {
                    case "user" -> MaintainUser.edit(data, request, click, recordId);
                    default -> System.err.println("Unexpected tab value: " + tab);
                }
                break;
            }
            default -> System.err.println("Unexpected menu value: " + menu);
        }
    }

    private void handleSort(final HttpServletRequest request, final HttpServletResponse response, final String click,
            final AdminData data, final int recordId) throws IOException
    {
        System.err.println("AZ choice: " + click + " with recordId: " + recordId);
    }

}
