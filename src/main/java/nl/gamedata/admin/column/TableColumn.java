package nl.gamedata.admin.column;

public class TableColumn extends AbstractColumn
{

    private String content;

    private int selectedRecordId;

    public TableColumn(final String width, final String defaultHeader)
    {
        super(width, defaultHeader);
        this.content = "";
        this.selectedRecordId = 0;
    }

    public String getContent()
    {
        return this.content;
    }

    public void setContent(final String content)
    {
        this.content = content;
    }

    public void addContent(final String newContent)
    {
        this.content += newContent;
    }

    public int getSelectedRecordId()
    {
        return this.selectedRecordId;
    }

    public void setSelectedRecordId(final int selectedRecordId)
    {
        this.selectedRecordId = selectedRecordId;
    }

}
