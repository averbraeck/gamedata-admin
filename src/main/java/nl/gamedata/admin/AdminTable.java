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
    /** 1 = Title, 2 = visible/hidden for New button. */
    private static final String tableTitle = """
            <div class="gd-table-caption">
              <div class="gd-table-title"><h3>%s</h3></div>
              <div class="gd-button" style="visibility:%s;">
                <button type="button" class="btn btn-primary" onclick="clickMenu('record-new')">New</button>
              </div>
            </div>
            """;

    /** No args for now. */
    private static final String tableHeaderTop = """
            <table class="table">
              <thead>
                <tr>
                  <th class="gd-col-icon" scope="col"><i class="fas fa-square fa-fw"></i></th>
                  <th class="gd-col-icon" scope="col"><i class="far fa-eye fa-fw"></i></th>
                  <th class="gd-col-icon" scope="col"><i class="fas fa-pencil fa-fw"></i></th>
                  <th class="gd-col-icon" scope="col"><i class="far fa-trash-can fa-fw"></i></th>
                  <th class="gd-col-icon" scope="col">&nbsp;</th>
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

    /** No args for now. */
    private static final String tableHeaderBottom = """
                  </th>
                </tr>
              </thead>
              <tbody>
            """;

    /** 1, 2, 3, 4 = record nr. */
    private static final String tableRowStart = """
                <tr>
                  <td class="gd-col-icon" scope="col">
                    <a href="#" onclick="clickRecordId('record-select', %d)">
                      <i class="far fa-square fa-fw"></i>
                    </a>
                  </td>
                  <td class="gd-col-icon" scope="col">
                    <a href="#" onclick="clickRecordId('record-view', %d)">
                      <i class="far fa-eye fa-fw"></i>
                    </a>
                  </td>
                  <td class="gd-col-icon" scope="col">
                    <a href="#" onclick="clickRecordId('record-edit', %d)">
                      <i class="fas fa-pencil fa-fw"></i></td>
                    </a>
                  <td class="gd-col-icon" scope="col">
                    <a href="#" onclick="clickRecordId('record-delete', %d)">
                      <i class="far fa-trash-can fa-fw"></i></td>
                    </a>
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

    public static void tableStart(final StringBuilder s, final String title, final String[] header, final boolean hasNew,
            final String sortColumn, final boolean sortDown)
    {
        s.append(tableTitle.formatted(title, hasNew ? "visible" : "hidden"));
        s.append(tableHeaderTop);
        for (String h : header)
        {
            String sort = "fa-sort";
            if (sortColumn.equals(h))
                sort = sortDown ? "fa-arrow-down-a-z" : "fa-arrow-up-z-a";
            s.append(tableHeaderCol.formatted(h, "az-" + h.toLowerCase().replace(' ', '-'), sort));
        }
        s.append(tableHeaderBottom);
    }

    public static void tableRow(final StringBuilder s, final int recordId, final String[] content)
    {
        s.append(tableRowStart.formatted(recordId, recordId, recordId, recordId));
        for (String c : content)
        {
            s.append(tableCell.formatted(c));
        }
        s.append(tableRowEnd);
    }

    public static void tableEnd(final StringBuilder s)
    {
        s.append(tableEnd);
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
            if (header[i].equals(this.sortColumn))
                this.sortFieldIndex = i;
        }
    }

    public void addRow(final int recordId, final boolean select, final boolean edit, final boolean delete,
            final String... cells)
    {
        Row row = new Row(recordId, select, edit, delete, cells);
        this.rows.put(cells[this.sortFieldIndex], row);
    }

    public void process()
    {
        // TABLE START
        StringBuilder s = new StringBuilder();
        s.append(tableTitle.formatted(this.title, this.newButton ? "visible" : "hidden"));
        s.append(tableHeaderTop);
        for (String h : this.header)
        {
            String sort = "fa-sort";
            if (this.sortColumn.equals(h))
                sort = this.az ? "fa-arrow-down-a-z" : "fa-arrow-up-z-a";
            s.append(tableHeaderCol.formatted(h, "az-" + h.toLowerCase().replace(' ', '-'), sort));
        }
        s.append(tableHeaderBottom);

        // ROWS
        NavigableSet<String> keys = this.az ? this.rows.navigableKeySet() : this.rows.descendingKeySet();
        for (String key : keys)
        {
            Row row = this.rows.get(key);
            s.append(tableRowStart.formatted(row.recordId(), row.recordId(), row.recordId(), row.recordId()));
            for (String c : row.cells())
            {
                s.append(tableCell.formatted(c));
            }
            s.append(tableRowEnd);
        }

        // TABLE END
        s.append(tableEnd);

        this.data.setContent(s.toString());
    }
}
