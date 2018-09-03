package com.udacity.bakingtime.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

public class VideoPlayerViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> mCurrentWindow = new MutableLiveData<>();
    private final MutableLiveData<Long> mPlayBackPosition = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mPlayWhenReady = new MutableLiveData<>();
    private final MutableLiveData<String> mMediaUrl = new MutableLiveData<>();

    public VideoPlayerViewModel(@NonNull Application application) {
        super(application);
    }

    public void setCurrentWindow(Integer currentWindow){
        mCurrentWindow.setValue(currentWindow);
    }

    public LiveData<Integer> getCurrentWindow(){
        return mCurrentWindow;
    }

    public void setPlayBackPosition(Long playBackPosition){
        mPlayBackPosition.setValue(playBackPosition);
    }

    public LiveData<Long> getPlayBackPosition(){
        return mPlayBackPosition;
    }

    public void setPlayWhenReady(Boolean playWhenReady){
        mPlayWhenReady.setValue(playWhenReady);
    }

    public LiveData<Boolean> getPlayWhenReady(){
        return mPlayWhenReady;
    }

    public void setMediaUrl(String mediaUrl){
        mMediaUrl.setValue(mediaUrl);
    }

    public LiveData<String> getMediaUrl(){
        return mMediaUrl;
    }
}
