package nl.gamedata.admin;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public final class SessionUtils
{

    private SessionUtils()
    {
        // utility class
    }

    public static AdminData getData(final HttpSession session)
    {
        AdminData data = (AdminData) session.getAttribute("adminData");
        return data;
    }

    public static boolean checkLogin(final HttpServletRequest request, final HttpServletResponse response) throws IOException
    {
        if (request.getSession().getAttribute("userId") == null)
        {
            response.sendRedirect("jsp/admin/login.jsp");
            return false;
        }
        @SuppressWarnings("unchecked")
        Map<Integer, String> idSessionMap = (Map<Integer, String>) request.getServletContext().getAttribute("idSessionMap");
        String storedSessionId = idSessionMap.get(request.getSession().getAttribute("userId"));
        if (!request.getSession().getId().equals(storedSessionId))
        {
            response.sendRedirect("jsp/admin/login-session.jsp"); // TODO: session management
            return false;
        }
        return true;
    }

}
