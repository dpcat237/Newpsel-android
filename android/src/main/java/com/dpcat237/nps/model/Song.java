package com.dpcat237.nps.model;

import java.util.ArrayList;

public class Song extends Generic {
    protected Integer list_id;
    protected Integer item_id;
    protected String list_title;
    protected String title;
    protected String content;
    protected String language;
    protected String file;
    protected String part_file;
    protected Boolean is_grabbed = false;
    protected Boolean is_played = false;
    protected String type;
    private ArrayList<SongPart> parts;


    public Integer getListId() {
        return list_id;
    }

    public void setListId(Integer listId) {
        this.list_id = listId;
    }

    public Integer getItemApiId() {
        return item_id;
    }

    public void setItemApiId(Integer itemId) {
        this.item_id = itemId;
    }

    public String getListTitle() {
        return list_title;
    }

    public void setListTitle(String listTitle) {
        this.list_title = listTitle;
    }

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFilename() {
        return file;
    }

    public void setFilename(String file) {
        this.file = file;
    }

    public String getPartFilename() {
        return part_file;
    }

    public void setPartFilename(String partFile) {
        this.part_file = partFile;
    }

    public Boolean isGrabbed() {
        return is_grabbed;
    }

    public void setGrabbed(Boolean is_grabbed) {
        this.is_grabbed = is_grabbed;
    }

    public Boolean isPlayed() {
        return is_played;
    }

    public void setPlayed(Boolean is_played) {
        this.is_played = is_played;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addPart(SongPart part) {
        if (parts == null) {
            parts = new ArrayList<SongPart>();
        }
        parts.add(part);
    }
    public ArrayList<SongPart> getParts() {
        return parts;
    }
}
