package nl.gamedata.admin.column;

public abstract class AbstractColumn
{

    private final String width;

    private final String defaultHeader;

    private String header;

    public AbstractColumn(String width, String defaultHeader)
    {
        this.width = width;
        this.defaultHeader = defaultHeader;
        this.header = defaultHeader;
    }

    public String getWidth()
    {
        return this.width;
    }

    public String getHeader()
    {
        return this.header;
    }

    public void setHeader(String header)
    {
        this.header = header;
    }

    public String getDefaultHeader()
    {
        return defaultHeader;
    }

    public abstract String getContent();

}
