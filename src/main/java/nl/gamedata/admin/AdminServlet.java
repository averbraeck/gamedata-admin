package nl.gamedata.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.gamedata.admin.organization.MaintainOrganization;
import nl.gamedata.admin.user.MaintainUser;

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

        int recordNr = 0;
        if (request.getParameter("recordNr") != null)
            recordNr = Integer.parseInt(request.getParameter("recordNr"));
        else if (request.getParameter("editRecordNr") != null)
            recordNr = Integer.parseInt(request.getParameter("editRecordNr"));

        System.out.println("Clicked: " + click);

        data.setMenuChoice(click);
        data.setShowModalWindow(false);
        data.setModalWindowHtml("");

        // state machine
        if (click.startsWith("menu"))
        {
            data.setMenuChoice(click);
            switch (click)
            {
                // logoff
                case "menu-logoff":
                    response.sendRedirect("jsp/admin/login.jsp");
                    return;

                // user - gamerole
                case "menu-user":
                    data.setTabChoice("tab-user#user");
                    MaintainUser.handleMenu(data, request, click, recordNr);
                    break;

                // user - gamerole
                case "menu-organization":
                    data.setTabChoice("tab-organization#organization");
                    MaintainOrganization.handleMenu(data, request, click, recordNr);
                    break;

                default:
                    System.err.println("Unknown menu choice: " + click + " with recordNr: " + recordNr);
                    break;
            }
        }

        else if (click.startsWith("tab"))

        {
            data.setTabChoice(click);
            System.err.println("TAB choice: " + click + " with recordNr: " + recordNr);
        }

        else

        {
            System.err.println("Unknown menu choice: " + click + " with recordNr: " + recordNr);
        }

        response.sendRedirect("jsp/admin/admin.jsp");
    }

}
