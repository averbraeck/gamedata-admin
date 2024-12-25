package nl.gamedata.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
    }

    private void handleRecordEdit(final HttpServletRequest request, final HttpServletResponse response, final String click,
            final AdminData data, final int recordId) throws IOException
    {
        String menu = data.getMenuChoice();
        String tab = data.getTabChoice(menu);
        System.err.println("RECORD choice: " + click + " with recordId: " + recordId);
        Menus.edit(data, request, click, recordId);
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
