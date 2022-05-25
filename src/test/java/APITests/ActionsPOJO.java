package APITests;

public class ActionsPOJO {
    private String caption;
    private String coverUrl;
    private String postStatus;
    private String Action;
    private String Content;

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }



    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

}
