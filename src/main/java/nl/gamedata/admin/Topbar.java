package nl.gamedata.admin;

import java.util.ArrayList;
import java.util.List;

/**
 * Topbar.java.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Topbar
{
    private static String topbarStart = """
            <!-- Topbar -->
            <div id="main-topbar" class="gd-topbar">
              <div class="gd-topbar-logo">
                <img src="images/header.png">
              </div>
              <div class="gd-topbar-buttons-left">
            """;

    private static String topbarButton = """
                <div class="gd-button">
                  <button type="button" class="btn btn-primary" onclick="clickMenu('%s')">%s</button>
                </div>
            """;

    private static String topbarTitle = """
              </div>
                <div class="gd-title"><h3>%s</h3></div>
              <div class="gd-topbar-buttons-right">
            """;

    private static String topbarEnd = """
              </div>
            </div>
            <!-- Topbar -->
            """;

    /** Button data. */
    public record Button(String buttonText, String click)
    {
    }

    private List<Button> leftButtonList = new ArrayList<>();

    private List<Button> rightButtonList = new ArrayList<>();

    private String title = "";

    public String makeTopbar(final AdminData data)
    {
        StringBuilder s = new StringBuilder();
        s.append(topbarStart);
        for (Button button : this.leftButtonList)
            s.append(topbarButton.formatted(button.click, button.buttonText));
        s.append(topbarTitle.formatted(this.title));
        for (Button button : this.rightButtonList)
            s.append(topbarButton.formatted(button.click, button.buttonText));
        s.append(topbarEnd);
        return s.toString();
    }

    public void clear()
    {
        this.leftButtonList.clear();
        this.rightButtonList.clear();
        this.title = "";
    }

    public void addLeftButton(final String buttonText, final String click)
    {
        this.leftButtonList.add(new Button(buttonText, click));
    }

    public void addRightButton(final String buttonText, final String click)
    {
        this.rightButtonList.add(new Button(buttonText, click));
    }

    public void addNewButton()
    {
        addLeftButton("New", "record-new");
    }

    public void addImportButton()
    {
        addRightButton("Import", "table-import");
    }

    public void addExportButton()
    {
        addRightButton("Export", "table-export");
    }

    public List<Button> getLeftButtonList()
    {
        return this.leftButtonList;
    }

    public void setLeftButtonList(final List<Button> leftButtonList)
    {
        this.leftButtonList = leftButtonList;
    }

    public List<Button> getRightButtonList()
    {
        return this.rightButtonList;
    }

    public void setRightButtonList(final List<Button> rightButtonList)
    {
        this.rightButtonList = rightButtonList;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setTitle(final String title)
    {
        this.title = title;
    }


}
