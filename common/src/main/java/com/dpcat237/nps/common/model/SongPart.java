package com.dpcat237.nps.common.model;

import com.dpcat237.nps.common.model.Generic;

public class SongPart extends Generic {
    protected Integer song_id;
    protected String content;
    protected String file;


    public Integer getSongId() {
        return song_id;
    }

    public void setSongId(Integer song_id) {
        this.song_id = song_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilename() {
        return file;
    }

    public void setFilename(String file) {
        this.file = file;
    }
}
