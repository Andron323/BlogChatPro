package betblogchat.pro;

class News_data {
    public String id;
    public String url;
    public String url_for_but;
    public String content;
    public String image;
    public String video;
    public String likes;
    public String views;
    public String date_time;
    public String language;

    public News_data(String id, String url,String url_for_but, String content, String image, String video, String likes, String views, String date_time, String language)
    {
        this.id = id;
        this.url = url;
        this.url_for_but = url_for_but;
        this.content = content;
        this.image = image;
        this.video = video;
        this.likes = likes;
        this.views = views;
        this.date_time = date_time;
        this.language = language;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getUrl_for_but()
    {
        return url_for_but;
    }

    public void setUrl_for_but(String url_for_but)
    {
        this.url_for_but = url_for_but;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getVideo()
    {
        return video;
    }

    public void setVideo(String video)
    {
        this.video = video;
    }

    public String getLikes()
    {
        return likes;
    }

    public void setLikes(String likes)
    {
        this.likes = likes;
    }

    public String getViews()
    {
        return views;
    }

    public void setViews(String views)
    {
        this.views = views;
    }

    public String getDate_time()
    {
        return date_time;
    }

    public void setDate_time(String date_time)
    {
        this.date_time = date_time;
    }

}
