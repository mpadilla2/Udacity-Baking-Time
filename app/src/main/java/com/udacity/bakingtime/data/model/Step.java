package com.udacity.bakingtime.data.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Step{


    private int id;
    private String shortDescription;
    private String description;
    private String videoURL;
    private String thumbnailURL;


    public Step(int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        super();
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Step withId(int id) {
        this.id = id;
        return this;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Step withShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormattedDescription(){
        // Reference: https://stackoverflow.com/a/10272822
        return "Step " + String.valueOf(getId()) + ". " + getDescription().substring(getDescription().indexOf(' ') + 1);
    }

    public Step withDescription(String description) {
        this.description = description;
        return this;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public Step withVideoURL(String videoURL) {
        this.videoURL = videoURL;
        return this;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public Step withThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("shortDescription", shortDescription).append("description", description).append("videoURL", videoURL).append("thumbnailURL", thumbnailURL).toString();
    }
}
