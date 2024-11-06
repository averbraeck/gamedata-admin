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
        if (data != null)
            data.setError(false);
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

    /*-
    public static void showGames(HttpSession session, AdminData data, int selectedGameRecordNr, String showText,
            String showMethod) {
        StringBuilder s = new StringBuilder();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameRecord> gameRecords = dslContext.selectFrom(Tables.GAME).fetch();

        s.append(AdminTable.startTable());
        for (GameRecord game : gameRecords) {
            TableRow tableRow = new TableRow(game.getId(), selectedGameRecordNr,
                    game.getCode() + " : " + game.getName(), showMethod);
            tableRow.addButton(showText, showMethod);
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        data.getColumn(0).setSelectedRecordNr(selectedGameRecordNr);
        data.getColumn(0).setContent(s.toString());
    }
    */

}
