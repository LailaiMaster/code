package com.xiangxue.news.homefragment.newslist;

import androidx.lifecycle.MutableLiveData;

import com.xiangxue.base.customview.BaseCustomViewModel;
import com.xiangxue.base.mvvm.model.BaseMvvmModel;
import com.xiangxue.base.mvvm.model.IBaseModelListener;
import com.xiangxue.base.mvvm.model.PagingResult;
import com.xiangxue.base.mvvm.viewmodel.ViewStatus;

import java.util.ArrayList;
import java.util.List;

public class NewsListViewModel implements IBaseModelListener<List<BaseCustomViewModel>> {
    public MutableLiveData<List<BaseCustomViewModel>> dataList = new MutableLiveData<>();
    private NewsListModel mModel;
    public MutableLiveData<ViewStatus> viewStatusLiveData = new MutableLiveData();
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public NewsListViewModel(String channelId, String channelName){
        dataList.setValue(new ArrayList<>());
        mModel = new NewsListModel(channelId, channelName);
        mModel.register(this);
        mModel.getCachedDataAndLoad();
    }

    public void refresh(){
        mModel.refresh();
    }

    public void loadNextPage(){
        mModel.loadNextPage();
    }

    @Override
    public void onLoadSuccess(BaseMvvmModel model, List<BaseCustomViewModel> baseCustomViewModels, PagingResult... result) {
        if(model instanceof NewsListModel){
            if (result[0].isFirstPage) {
                dataList.getValue().clear();
            }
            if (result[0].isEmpty) {
                if (result[0].isFirstPage) {
                    viewStatusLiveData.postValue(ViewStatus.EMPTY);
                } else {
                    viewStatusLiveData.postValue(ViewStatus.NO_MORE_DATA);
                }
            } else {
                dataList.getValue().addAll(baseCustomViewModels);
                dataList.postValue(dataList.getValue());
                viewStatusLiveData.postValue(ViewStatus.SHOW_CONTENT);
            }
        }
    }

    @Override
    public void onLoadFail(BaseMvvmModel model, String message, PagingResult... result) {
        errorMessage.postValue(message);
        if(result[0].isFirstPage){
            viewStatusLiveData.postValue(ViewStatus.REFRESH_ERROR);
        } else {
            viewStatusLiveData.postValue(ViewStatus.LOAD_MORE_FAILED);
        }
    }
}
