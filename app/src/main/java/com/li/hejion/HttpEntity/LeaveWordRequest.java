package com.li.hejion.HttpEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by li on 2014/8/2.
 */
public class LeaveWordRequest implements Serializable{
    private static final long serialVersionUID =1l;
    private String title;
    private String content;
    private List<String> files;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
