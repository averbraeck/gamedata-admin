package nl.gamedata.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
            response.sendRedirect("/housinggame-admin/login");
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

        data.setShowModalWindow(0);
        data.setModalWindowHtml("");

        switch (click)
        {
            // Language - LanguageGroup (non-heriarchic) - Label
            case "language":
            case "viewLanguage":
            case "editLanguage":
            case "saveLanguage":
            case "deleteLanguage":
            case "deleteLanguageOk":
            case "newLanguage":

            case "viewLanguageGroup":
            case "editLanguageGroup":
            case "saveLanguageGroup":
            case "deleteLanguageGroup":
            case "deleteLanguageGroupOk":
            case "newLanguageGroup":

            case "viewLabel":
            case "editLabel":
            case "saveLabel":
            case "deleteLabel":
            case "deleteLabelOk":
            case "newLabel":
                data.setMenuChoice("language");
                // MaintainLanguage.handleMenu(request, click, recordNr);
                break;

            default:
                System.err.println("Unknown menu choice: " + click);
                break;
        }

        response.sendRedirect("jsp/admin/admin.jsp");
    }

    public static void makeColumnContent(final AdminData data)
    {
        StringBuilder s = new StringBuilder();
        s.append("<table width=\"100%\">\n");
        s.append("  <tr>");
        for (int i = 0; i < data.getNrColumns(); i++)
        {
            s.append("    <td width=\"");
            s.append(data.getColumn(i).getWidth());
            s.append("\">\n");
            s.append("      <div class=\"gd-admin-line-header\">");
            s.append(data.getColumn(i).getHeader());
            s.append("</div>\n");
            s.append(data.getColumn(i).getContent());
            s.append("    </td>\n");
        }
        if (data.getFormColumn() != null)
        {
            s.append("    <td width=\"");
            s.append(data.getFormColumn().getWidth());
            s.append("\">\n");
            s.append("      <div class=\"gd-admin-line-header\">");
            s.append(data.getFormColumn().getHeader());
            s.append("</div>\n");
            s.append(data.getFormColumn().getContent());
            s.append("    </td>\n");
        }
        s.append("  </tr>");
        s.append("</table>\n");
        data.setContentHtml(s.toString());
    }

    public static String getTopMenu(final AdminData data)
    {
        StringBuilder s = new StringBuilder();
        // Organization
        topmenu(data, s, "organization", "Organization", "#008000");
        // Role
        topmenu(data, s, "role", "Role", "#ff8000");
        // Game - GameSession - GameMission
        topmenu(data, s, "game", "Game", "#ff8000");
        // (Game) - DashboardSettings
        topmenu(data, s, "dashboard", "Dashboard", "#008000");

        return s.toString();
    }

    private static final String br = "          <div class=\"gd-admin-menu-button-red\"";

    private static void topmenu(final AdminData data, final StringBuilder s, final String key, final String text,
            final String color)
    {
        String bn = "          <div class=\"gd-admin-menu-button\" style=\"background-color: " + color + "\"";
        s.append(key.equals(data.getMenuChoice()) ? br : bn);
        s.append(" onclick=\"clickMenu('");
        s.append(key);
        s.append("')\">");
        s.append(text);
        s.append("</div>\n");
    }
}
