package nl.gamedata.admin.form;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;

import nl.gamedata.admin.AdminData;

public class FormEntryPickRecord extends AbstractFormEntry<FormEntryPickRecord, Integer>
{

    /** Entries alphabetically sorted on pick name. */
    private SortedMap<String, Integer> records = new TreeMap<>();

    public FormEntryPickRecord(final String label, final String name)
    {
        super(label, name);
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

    public FormEntryPickRecord setPickTable(final AdminData data, final Table<?> table, final TableField<?, Integer> id,
            final TableField<?, String> name)
    {
        List<? extends Record> tableRecords = data.getDSL().selectFrom(table).fetch();
        if (!isRequired())
            this.records.put("", null);
        for (Record record : tableRecords)
        {
            this.records.put(record.get(name), record.get(id));
        }
        return this;
    }

    public FormEntryPickRecord setPickTable(final AdminData data, final Table<?> table, final TableField<?, Integer> id,
            final TableField<?, String> name, final Condition condition)
    {
        List<? extends Record> tableRecords = data.getDSL().selectFrom(table).where(condition).fetch();
        if (!isRequired())
            this.records.put("", null);
        for (Record record : tableRecords)
        {
            this.records.put(record.get(name), record.get(id));
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
        s.append(getName());
        if (isReadOnly())
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
        s.append("      </td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

}
