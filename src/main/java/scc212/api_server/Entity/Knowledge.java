/**
 * Used to save JSON data
 *
 * @author Chongyang Zhoao
 *
 * @date 2020/04/29
 */

package scc212.api_server.Entity;

public class Knowledge {
    private String title;
    private String contents[] = new String[10];

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String[] getContents()
    {
        return contents;
    }

    public void setContents(String contents, int i)
    {
        this.contents[i] = contents;
    }

}
