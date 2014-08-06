package com.dpcat237.nps.model;

public class DictateItem extends LaterItem {
    private Boolean has_tts_error = false;


    public Boolean hasTtsError() {
        return has_tts_error;
    }

    public void setTtsError(Boolean has_tts_error) {
        this.has_tts_error = has_tts_error;
    }

	@Override
	public String toString() {
		return title;
	}
}
