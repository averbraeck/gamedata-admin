package nl.gamedata.admin.column;

import nl.gamedata.admin.form.table.TableForm;

public class FormColumn extends AbstractColumn
{

    private TableForm form;

    private String htmlContents;

    public FormColumn(String width, String defaultHeader)
    {
        super(width, defaultHeader);
        clearForm();
    }

    public String getContent()
    {
        if (this.form != null)
            return this.form.process();
        if (this.htmlContents != null && this.htmlContents.length() > 0)
            return this.htmlContents;
        return "";
    }

    public TableForm getForm()
    {
        return form;
    }

    public void setForm(TableForm form)
    {
        this.form = form;
    }

    public void setHeaderForm(String header, TableForm form)
    {
        setHeader(header);
        setForm(form);
    }

    public void clearForm()
    {
        this.form = null;
    }

    public String getHtmlContents()
    {
        return htmlContents;
    }

    public void setHtmlContents(String htmlContents)
    {
        this.htmlContents = htmlContents;
    }

}
