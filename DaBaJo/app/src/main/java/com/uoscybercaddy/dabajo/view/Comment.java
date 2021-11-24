package com.uoscybercaddy.dabajo.view;

import java.io.Serializable;

public class Comment implements Serializable
{

    public String CommentContent123;
    public String WriterUID123;

    public String getCommentContent() {
        return this.CommentContent123;
    }
    public void setCommentText(String commentContent1) {
        this.CommentContent123 = commentContent1;
    }

    public String getWriterUID() {
        return this.WriterUID123;
    }

    public void setWriterUID(String writerUID1) {
        this.WriterUID123 = writerUID1;
    }

}

