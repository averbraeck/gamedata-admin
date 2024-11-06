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
                MaintainLanguage.handleMenu(request, click, recordNr);
                break;

            // ScenarioParameters
            case "parameters":
            case "viewParameters":
            case "editParameters":
            case "saveParameters":
            case "deleteParameters":
            case "deleteParametersOk":
            case "newParameters":
            case "cloneParameters":
                data.setMenuChoice("parameters");
                MaintainParameters.handleMenu(request, click, recordNr);
                break;

            // GameVersion - Scenario
            case "scenario":
            case "viewGameVersion":
            case "editGameVersion":
            case "saveGameVersion":
            case "deleteGameVersion":
            case "deleteGameVersionOk":
            case "newGameVersion":
            case "cloneGameVersion":
            case "destroyGameVersion":
            case "destroyGameVersionOk":

            case "viewScenario":
            case "editScenario":
            case "saveScenario":
            case "deleteScenario":
            case "deleteScenarioOk":
            case "newScenario":
            case "cloneScenario":
            case "destroyScenario":
            case "destroyScenarioOk":
                data.setMenuChoice("scenario");
                MaintainScenario.handleMenu(request, click, recordNr);
                break;

            // (GameVersion) - (Scenario) - NewsItem - NewsEffects
            case "news":
            case "viewNewsGameVersion":
            case "viewNewsScenario":

            case "viewNewsItem":
            case "editNewsItem":
            case "saveNewsItem":
            case "deleteNewsItem":
            case "deleteNewsItemOk":
            case "newNewsItem":

            case "viewNewsEffects":
            case "editNewsEffects":
            case "saveNewsEffects":
            case "deleteNewsEffects":
            case "deleteNewsEffectsOk":
            case "newNewsEffects":
                data.setMenuChoice("news");
                MaintainNews.handleMenu(request, click, recordNr);
                break;

            // (GameVersion) - (Scenario) - WelfareType
            case "welfaretype":
            case "viewWelfareTypeGameVersion":
            case "viewWelfareTypeScenario":

            case "viewWelfareType":
            case "editWelfareType":
            case "saveWelfareType":
            case "deleteWelfareType":
            case "deleteWelfareTypeOk":
            case "newWelfareType":
                data.setMenuChoice("welfaretype");
                MaintainWelfareType.handleMenu(request, click, recordNr);
                break;

            // (GameVersion) - MeasureCategory - MeasureType
            case "measuretype":
            case "viewMeasureTypeGameVersion":
            case "viewMeasureTypeScenario":

            case "measurecategory":
            case "viewMeasureCategory":
            case "editMeasureCategory":
            case "saveMeasureCategory":
            case "deleteMeasureCategory":
            case "deleteMeasureCategoryOk":
            case "newMeasureCategory":

            case "viewMeasureType":
            case "editMeasureType":
            case "saveMeasureType":
            case "deleteMeasureType":
            case "deleteMeasureTypeOk":
            case "newMeasureType":
                data.setMenuChoice("measuretype");
                MaintainMeasureType.handleMenu(request, click, recordNr);
                break;

            // (GameVersion) - MovingReason
            case "movingreason":
            case "viewMovingReasonGameVersion":

            case "viewMovingReason":
            case "editMovingReason":
            case "saveMovingReason":
            case "deleteMovingReason":
            case "deleteMovingReasonOk":
            case "newMovingReason":
                data.setMenuChoice("movingreason");
                MaintainMovingReason.handleMenu(request, click, recordNr);
                break;

            // (GameVersion) - (Scenario) - Question - QuestionItem
            case "question":
            case "viewQuestionGameVersion":
            case "viewQuestionScenario":

            case "viewQuestion":
            case "editQuestion":
            case "saveQuestion":
            case "deleteQuestion":
            case "deleteQuestionOk":
            case "newQuestion":

            case "viewQuestionItem":
            case "editQuestionItem":
            case "saveQuestionItem":
            case "deleteQuestionItem":
            case "deleteQuestionItemOk":
            case "newQuestionItem":
                data.setMenuChoice("question");
                MaintainQuestion.handleMenu(request, click, recordNr);
                break;

            // (GameVersion) - Community - Tax
            case "community":
            case "viewCommunityGameVersion":

            case "viewCommunity":
            case "editCommunity":
            case "saveCommunity":
            case "deleteCommunity":
            case "deleteCommunityOk":
            case "newCommunity":

            case "viewTax":
            case "editTax":
            case "saveTax":
            case "deleteTax":
            case "deleteTaxOk":
            case "newTax":
                data.setMenuChoice("community");
                MaintainCommunity.handleMenu(request, click, recordNr);
                break;

            // (GameVersion) - (Community) - House - (Scenario) - InitialHouseMeasure
            case "house":
            case "viewHouseGameVersion":
            case "viewHouseCommunity":

            case "viewHouse":
            case "editHouse":
            case "saveHouse":
            case "deleteHouse":
            case "deleteHouseOk":
            case "newHouse":

            case "viewHouseScenario":

            case "viewInitialHouseMeasure":
            case "editInitialHouseMeasure":
            case "saveInitialHouseMeasure":
            case "deleteInitialHouseMeasure":
            case "deleteInitialHouseMeasureOk":
            case "newInitialHouseMeasure":
                data.setMenuChoice("house");
                MaintainHouse.handleMenu(request, click, recordNr);
                break;

            // User
            case "user":
            case "viewUser":
            case "editUser":
            case "saveUser":
            case "deleteUser":
            case "deleteUserOk":
            case "newUser":
                data.setMenuChoice("user");
                MaintainUser.handleMenu(request, click, recordNr);
                break;

            // (GameVersion) - GameSession - Group - Player
            case "gamesession":
            case "viewGameSessionGameVersion":

            case "viewGameSession":
            case "editGameSession":
            case "saveGameSession":
            case "deleteGameSession":
            case "deleteGameSessionOk":
            case "newGameSession":
            case "destroyGameSession":
            case "destroyGameSessionOk":

            case "viewGroup":
            case "editGroup":
            case "saveGroup":
            case "deleteGroup":
            case "deleteGroupOk":
            case "newGroup":
            case "generateGroups":
            case "generateGroupsParams":
            case "destroyGroup":
            case "destroyGroupOk":

            case "viewPlayer":
            case "editPlayer":
            case "savePlayer":
            case "deletePlayer":
            case "deletePlayerOk":
            case "newPlayer":
            case "destroyPlayer":
            case "destroyPlayerOk":
            case "destroyPlayPlayer":
            case "destroyPlayPlayerOk":
                data.setMenuChoice("gamesession");
                MaintainGameSession.handleMenu(request, click, recordNr);
                break;

            // (GameSession) - (Group) - GroupRound - GroupState
            case "play-group":
            case "viewPlayGroupGameSession":
            case "viewPlayGroupGroup":
            case "destroyGamePlayGroup":
            case "destroyGamePlayGroupOk":

            case "viewPlayGroupGroupRound":
            case "editPlayGroupGroupRound":
            case "savePlayGroupGroupRound":
            case "deletePlayGroupGroupRound":
            case "deletePlayGroupGroupRoundOk":
            case "newPlayGroupGroupRound":

            case "viewPlayGroupState":
            case "editPlayGroupState":
            case "savePlayGroupState":
            case "deletePlayGroupState":
            case "deletePlayGroupStateOk":
            case "newPlayGroupState":
                data.setMenuChoice("play-group");
                MaintainPlayGroup.handleMenu(request, click, recordNr);
                break;

            // (GameSession) - (Group) - (GroupRound) - (Player) - PlayerRound - PlayerState
            case "play-player":
            case "viewPlayGameSession":
            case "viewPlayGroup":
            case "viewPlayGroupRound":
            case "viewPlayPlayer":

            case "viewPlayPlayerRound":
            case "editPlayPlayerRound":
            case "savePlayPlayerRound":
            case "deletePlayPlayerRound":
            case "deletePlayPlayerRoundOk":
            case "newPlayPlayerRound":

            case "viewPlayPlayerState":
            case "editPlayPlayerState":
            case "savePlayPlayerState":
            case "deletePlayPlayerState":
            case "deletePlayPlayerStateOk":
            case "newPlayPlayerState":
                data.setMenuChoice("play-player");
                MaintainPlayPlayer.handleMenu(request, click, recordNr);
                break;

            // (GameSession) - (Group) - (GroupRound) - (Player) - (PlayerRound) - QuestionScore
            case "play-question":
            case "viewPlayQuestionGameSession":
            case "viewPlayQuestionGroup":
            case "viewPlayQuestionGroupRound":
            case "viewPlayQuestionPlayer":
            case "viewPlayQuestionPlayerRound":

            case "viewPlayQuestion":
            case "editPlayQuestion":
            case "savePlayQuestion":
            case "deletePlayQuestion":
            case "deletePlayQuestionOk":
            case "newPlayQuestion":
                data.setMenuChoice("play-question");
                MaintainPlayQuestion.handleMenu(request, click, recordNr);
                break;

            // (GameSession) - (Group) - (GroupRound) - (Player) - (PlayerRound) - PersonalMeasure
            case "play-measure":
            case "viewPlayMeasureGameSession":
            case "viewPlayMeasureGroup":
            case "viewPlayMeasureGroupRound":
            case "viewPlayMeasurePlayer":
            case "viewPlayMeasurePlayerRound":

            case "viewPersonalMeasure":
            case "editPersonalMeasure":
            case "savePersonalMeasure":
            case "deletePersonalMeasure":
            case "deletePersonalMeasureOk":
            case "newPersonalMeasure":
                data.setMenuChoice("play-measure");
                MaintainPlayMeasure.handleMenu(request, click, recordNr);
                break;

            // (GameSession) - (Group) - HouseGroup - HouseMeasure - HouseTransaction
            case "housemeasure":
            case "viewMeasureGameSession":
            case "viewMeasureGroup":

            case "viewHouseGroup":
            case "editHouseGroup":
            case "saveHouseGroup":
            case "deleteHouseGroup":
            case "deleteHouseGroupOk":
            case "newHouseGroup":

            case "viewHouseMeasure":
            case "editHouseMeasure":
            case "saveHouseMeasure":
            case "deleteHouseMeasure":
            case "deleteHouseMeasureOk":
            case "newHouseMeasure":

            case "viewHouseTransaction":
            case "editHouseTransaction":
            case "saveHouseTransaction":
            case "deleteHouseTransaction":
            case "deleteHouseTransactionOk":
            case "newHouseTransaction":
                data.setMenuChoice("housemeasure");
                MaintainHouseMeasure.handleMenu(request, click, recordNr);
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

    public static String getTopMenu1(final AdminData data)
    {
        StringBuilder s = new StringBuilder();
        // Language - LanguageGroup (non-heriarchic) - Label
        topmenu(data, s, "language", "Language", "#ff8000");
        // ScenarioParameters
        topmenu(data, s, "parameters", "Parameters", "#ff8000");
        // GameVersion - Scenario
        topmenu(data, s, "scenario", "Scenario", "#008000");
        // (GameVersion) - (Scenario) - WelfareType
        topmenu(data, s, "welfaretype", "WelfareType", "#008000");
        // (GameVersion) - (Scenario) - MeasureCategory - MeasureType
        topmenu(data, s, "measuretype", "MeasureType", "#008000");
        // (GameVersion) - MovingReason
        topmenu(data, s, "movingreason", "MovingReason", "#008000");
        // (GameVersion) - Community - Tax
        topmenu(data, s, "community", "Community-Tax", "#008000");
        // (GameVersion) - (Community) - House - (Scenario) - InitialHouseMeasure
        topmenu(data, s, "house", "House", "#008000");
        // (GameVersion) - (Scenario) - NewsItem - NewsEffects
        topmenu(data, s, "news", "News-Effects", "#008000");
        // (GameVersion) - (Scenario) - Question - QuestionItem
        topmenu(data, s, "question", "Question-Item", "#008000");
        return s.toString();
    }

    public static String getTopMenu2(final AdminData data)
    {
        StringBuilder s = new StringBuilder();
        // User
        topmenu(data, s, "user", "User", "#0040ff");
        // (GameVersion) - GameSession - Group - Player
        topmenu(data, s, "gamesession", "Session", "#0040ff");
        // (GameSession) - (Group) - GroupRound - GroupState
        topmenu(data, s, "play-group", "Play-Group", "#0040ff");
        // (GameSession) - (Group) - GroupRound - (Player) - PlayerRound - PlayerState
        topmenu(data, s, "play-player", "Play-Player", "#0040ff");
        // (GameSession) - (Group) - (GroupRound) - (Player) - (PlayerRound) - QuestionScore
        topmenu(data, s, "play-question", "Play-Question", "#0040ff");
        // (GameSession) - (Group) - (GroupRound) - (Player) - (PlayerRound) - PersonalMeasure
        topmenu(data, s, "play-measure", "Play-Measure", "#0040ff");
        // (GameSession) - (Group) - HouseGroup - Measure - HouseTransaction
        topmenu(data, s, "housemeasure", "House-Measure", "#0040ff");
        //
        topmenu(data, s, "results", "Results", "#ff00ff");
        //
        topmenu(data, s, "grouptimeline", "GroupTimeline", "#ff00ff");
        //
        topmenu(data, s, "playertimeline", "PlayerTimeline", "#ff00ff");
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
