package nl.gamedata.admin;

import java.util.NavigableSet;
import java.util.TreeMap;

/**
 * Table.java.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class AdminTable
{
    /** 1 = Title. */
    private static final String tableTitleNew = """
            <div class="gd-table-caption">
              <div class="gd-table-title"><h3>%s</h3></div>
              <div class="gd-button">
                <button type="button" class="btn btn-primary" onclick="clickMenu('record-new')">New</button>
              </div>
            </div>
            """;

    /** 1 = Title. */
    private static final String tableTitleNoNew = """
            <div class="gd-table-caption">
              <div class="gd-table-title"><h3>%s</h3></div>
            </div>
            """;

    /** No args for now. */
    private static final String tableHeaderTop = """
            <table class="gd-table">
              <thead>
                <tr>
                  """;

    /** 1 = icon. */
    private static final String tableheaderIcon = """
                  <th class="gd-col-icon" scope="col"><i class="fas %s fa-fw"></i></th>
            """;

    /** 1 = name of column, 2 = name of clickMenu for a-z, 3=arrow to use for a-z. */
    private static final String tableHeaderCol = """
                  <th scope="col">
                    %s &nbsp;
                    <a href="#" onclick="clickMenu('%s')">
                      <i class="fas %s fa-fw"></i>
                    </a>
                  </th>
            """;

    /** No args. */
    private static final String tableHeaderSpacing = """
                  <th class="gd-col-icon" scope="col">&nbsp;</th>
            """;

    /** No args for now. */
    private static final String tableHeaderBottom = """
                  </th>
                </tr>
              </thead>
              <tbody>
            """;

    /** 1 = record nr. */
    private static final String tableRowStart = """
                <tr>
            """;

    /** 1 = function, 2 = record nr, 3 = icon */
    private static final String tableRowIcon = """
                  <td class="gd-col-icon" scope="col">
                    <a href="#" onclick="clickRecordId('%s', %d)">
                      <i class="fas %s fa-fw"></i>
                    </a>
                  </td>
            """;

    /** No args. */
    private static final String tableRowSpacing = """
                  <td class="gd-col-icon" scope="col">&nbsp;</td>
            """;

    /** 1 = cell content. */
    private static final String tableCell = """
                  <td>%s</td>
            """;

    /** No args for now. */
    private static final String tableRowEnd = """
                </tr>
            """;

    /** No args for now. */
    private static final String tableEnd = """
              </tbody>
            </table>
              """;

    private final AdminData data;

    private final String title;

    private boolean newButton = false;

    private String[] header;

    private String sortColumn;

    private boolean az;

    private int sortFieldIndex = 0;

    TreeMap<String, Row> rows = new TreeMap<>();

    private record Row(int recordId, boolean select, boolean edit, boolean delete, String... cells)
    {
    }

    public AdminTable(final AdminData data, final String title, final String defaultSortField)
    {
        this.data = data;
        this.title = title;
        if (data.getTableColumnSort() == null)
            data.selectTableColumnSort(defaultSortField);
        this.sortColumn = this.data.getTableColumnSort().fieldName();
        this.az = this.data.getTableColumnSort().az();
    }

    public void setNewButton(final boolean newButton)
    {
        this.newButton = newButton;
    }

    public void setHeader(final String... header)
    {
        this.header = header;
        for (int i = 0; i < header.length; i++)
        {
            if (header[i].toLowerCase().replace(' ', '-').equals(this.sortColumn))
                this.sortFieldIndex = i;
        }
    }

    public void addRow(final int recordId, final boolean select, final boolean edit, final boolean delete,
            final String... cells)
    {
        Row row = new Row(recordId, select, edit, delete, cells);
        this.rows.put(cells[this.sortFieldIndex] + String.valueOf(this.rows.size()), row);
    }

    public void process()
    {
        // TABLE START
        StringBuilder s = new StringBuilder();
        if (this.newButton)
            s.append(tableTitleNew.formatted(this.title));
        else
            s.append(tableTitleNoNew.formatted(this.title));
        s.append(tableHeaderTop);
        s.append(tableheaderIcon.formatted("fa-pencil"));
        s.append(tableHeaderSpacing);
        for (String h : this.header)
        {
            String sort = "fa-sort";
            if (this.sortColumn.equals(h.toLowerCase().replace(' ', '-')))
                sort = this.az ? "fa-arrow-down-a-z" : "fa-arrow-up-z-a";
            s.append(tableHeaderCol.formatted(h, "az-" + h.toLowerCase().replace(' ', '-'), sort));
        }
        if (this.newButton)
            s.append(tableheaderIcon.formatted("fa-trash-can"));
        s.append(tableHeaderBottom);

        // ROWS
        NavigableSet<String> keys = this.az ? this.rows.navigableKeySet() : this.rows.descendingKeySet();
        for (String key : keys)
        {
            Row row = this.rows.get(key);
            s.append(tableRowStart);
            if (row.edit)
                s.append(tableRowIcon.formatted("record-edit", row.recordId(), "fa-pencil"));
            else
                s.append(tableRowIcon.formatted("record-view", row.recordId(), "fa-eye"));
            s.append(tableRowSpacing);
            for (String c : row.cells())
            {
                s.append(tableCell.formatted(c));
            }
            if (this.newButton)
                s.append(tableRowIcon.formatted("record-delete", row.recordId(), "fa-trash-can"));
            s.append(tableRowEnd);
        }

        // TABLE END
        s.append(tableEnd);

        this.data.setContent(s.toString());
    }
}
