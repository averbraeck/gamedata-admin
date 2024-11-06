package nl.gamedata.admin;

import java.util.ArrayList;
import java.util.List;

public class TableRow
{

    private final int id;

    private final int selectedId;

    private final String name;

    private final String viewMethod;

    private List<String> editButtons = new ArrayList<>();

    private List<String> editMethods = new ArrayList<>();

    public TableRow(final int id, final int selectedId, final String name, final String viewMethod)
    {
        this.id = id;
        this.selectedId = selectedId;
        this.name = name;
        this.viewMethod = viewMethod;
    }

    public void addButton(final String buttonText, final String buttonMethod)
    {
        this.editButtons.add(buttonText);
        this.editMethods.add(buttonMethod);
    }

    public String process()
    {
        StringBuilder s = new StringBuilder();
        if (this.id == this.selectedId)
            s.append("        <div class=\"gd-admin-line-selected\">\n");
        else
            s.append("        <div class=\"gd-admin-line\">\n");
        s.append("            <div class=\"gd-admin-line-field\">");
        s.append("<a href=\"#\" onClick=\"clickRecordId('");
        s.append(this.viewMethod);
        s.append("',");
        s.append(this.id);
        s.append("); return false;\">");
        s.append(this.name);
        s.append("</a></div>\n"); // gd-admin-line-field
        for (int i = 0; i < this.editButtons.size(); i++)
        {
            s.append("            <div class=\"gd-admin-line-click\"><a href=\"#\" onClick=\"clickRecordId('");
            s.append(this.editMethods.get(i));
            s.append("',");
            s.append(this.id);
            s.append("); return false;\">");
            s.append(this.editButtons.get(i));
            s.append("</a></div>\n");
        }
        s.append("          </div>\n"); // gd-admin-line
        return s.toString();
    }

}
