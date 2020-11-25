package com.xiangxue.news.homefragment.headlinenews;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.xiangxue.base.BaseApplication;
import com.xiangxue.base.mvvm.model.BaseMvvmModel;
import com.xiangxue.base.mvvm.model.IBaseModelListener;
import com.xiangxue.base.mvvm.model.PagingResult;
import com.xiangxue.news.homefragment.api.NewsChannelsBean;

import java.util.List;

public class HeadlineNewsViewModel implements IBaseModelListener<List<NewsChannelsBean.ChannelList>> {
    protected NewsChannelModel model;
    public MutableLiveData<List<NewsChannelsBean.ChannelList>> dataList = new MutableLiveData<>();

    public HeadlineNewsViewModel(){
        model = new NewsChannelModel();
        model.register(this);
        model.getCachedDataAndLoad();
    }

    @Override
    public void onLoadSuccess(BaseMvvmModel model, List<NewsChannelsBean.ChannelList> channelLists, PagingResult... results) {
        if(model instanceof NewsChannelModel) {
            dataList.postValue(channelLists);
        }
    }

    @Override
    public void onLoadFail(BaseMvvmModel model, String message, PagingResult... results) {
        Toast.makeText(BaseApplication.sApplication, message, Toast.LENGTH_LONG).show();
    }
}
