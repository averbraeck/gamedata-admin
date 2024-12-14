package nl.gamedata.admin.form.table;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import nl.gamedata.admin.AdminData;

public class TableEntryPickRecord extends AbstractTableEntry<TableEntryPickRecord, Integer>
{

    /** Entries alphabetically sorted on pick name. */
    private SortedMap<String, Integer> records = new TreeMap<>();

    public TableEntryPickRecord(final TableField<?, Integer> tableField)
    {
        super(tableField);
    }

    @Override
    public String codeForEdit(final Integer value)
    {
        if (value != null)
            return value.toString();
        return "0";
    }

    @Override
    public Integer codeForType(final String s)
    {
        if (s == null || s.length() == 0 || s.equals("null") || s.equals("0"))
            return null;
        return Integer.valueOf(s);
    }

    public TableEntryPickRecord setPickTable(final AdminData data, final Table<?> table, final TableField<?, Integer> id,
            final TableField<?, ?> name)
    {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<? extends Record> tableRecords = dslContext.selectFrom(table).fetch();
        if (!isRequired())
            this.records.put("", null);
        for (Record record : tableRecords)
        {
            this.records.put(record.get(name).toString(), record.get(id).intValue());
        }
        return this;
    }

    public TableEntryPickRecord setPickTable(final AdminData data, final Table<?> table, final TableField<?, Integer> id,
            final TableField<?, String> name, final Condition condition)
    {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<? extends Record> tableRecords = dslContext.selectFrom(table).where(condition).fetch();
        if (!isRequired())
            this.records.put("", null);
        for (Record record : tableRecords)
        {
            this.records.put(record.get(name), record.get(id).intValue());
        }
        return this;
    }

    public TableEntryPickRecord setPickTable(final AdminData data, final List<? extends Record> tableRecords,
            final TableField<?, ?> id, final TableField<?, String> name)
    {
        if (!isRequired())
            this.records.put("", null);
        for (Record record : tableRecords)
        {
            if (record.get(id) instanceof UInteger)
                this.records.put(record.get(name), ((UInteger) record.get(id)).intValue());
            else
                this.records.put(record.get(name), ((Integer) record.get(id)).intValue());
        }
        return this;
    }

    @Override
    public String makeHtml()
    {
        StringBuilder s = new StringBuilder();
        s.append("    <tr>\n");
        String labelLength = getForm() == null ? "25%" : getForm().getLabelLength();
        s.append("      <td width=\"" + labelLength + "\">");
        s.append(getLabel());
        if (isRequired())
            s.append(" *");
        s.append("      </td>");
        String fieldLength = getForm() == null ? "75%" : getForm().getFieldLength();
        s.append("      <td width=\"" + fieldLength + "\">");
        s.append("        <select ");
        if (isRequired())
            s.append(" required name=\"");
        else
            s.append(" name=\"");
        s.append(getTableField().getName());
        if (isReadOnly() || !getForm().isEdit())
            s.append("\" style=\"pointer-events: none;\">\n");
        else
            s.append("\">\n");
        for (String name : this.records.keySet())
        {
            Integer id = this.records.get(name); // TODO: int is not possible here -- could be null?
            s.append("        <option value=\"");
            s.append(id);
            s.append("\"");
            // System.out.println(getLastEnteredValue());
            if (codeForEdit(id).equals(getLastEnteredValue()))
            {
                s.append(" selected");
            }
            s.append(">");
            s.append(name);
            s.append("</option>\n");
        }
        s.append("        </select>\n");

        if (getTableField().getDataType().nullable())
        {
            s.append("&nbsp;&nbsp;<input type=\"checkbox\" name=\"");
            s.append(getTableField().getName() + "-null\" value=\"null\"");
            s.append(getLastEnteredValue() == null ? " checked" : "");
            if (isReadOnly() || !getForm().isEdit())
                s.append(" readonly />");
            else
                s.append(" />");
        }

        s.append("      </td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

}
