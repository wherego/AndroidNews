package com.zhjohow.androidnews.ui.news.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.zhjohow.androidnews.R;
import com.zhjohow.androidnews.app.AppConstant;
import com.zhjohow.androidnews.bean.NewsChannelTable;
import com.zhjohow.androidnews.ui.news.adapter.ChannelAdapter;
import com.zhjohow.androidnews.ui.news.contract.NewsChannelContract;
import com.zhjohow.androidnews.ui.news.event.ChannelItemMoveEvent;
import com.zhjohow.androidnews.ui.news.model.NewsChannelModel;
import com.zhjohow.androidnews.ui.news.presenter.NewsChanelPresenter;
import com.zhjohow.androidnews.widget.ItemDragHelperCallback;
import com.zhjohow.common.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.functions.Action1;

/**
 * des:选择关注频道
 * Created by xsf
 * on 2016.09.11:51
 */
public class NewsChannelActivity extends BaseActivity<NewsChanelPresenter, NewsChannelModel>implements NewsChannelContract.View{
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.news_channel_mine_rv)
    RecyclerView newsChannelMineRv;
    @Bind(R.id.news_channel_more_rv)
    RecyclerView newsChannelMoreRv;

    private ChannelAdapter channelAdapterMine;
    private ChannelAdapter channelAdapterMore;

    @Override
    public int getLayoutId() {
        return R.layout.act_news_channel;
    }

    /**
     * 入口
     * @param context
     */
    public static void startAction(Context context){
        Intent intent = new Intent(context, NewsChannelActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRxManager.on(AppConstant.CHANNEL_SWAP, new Action1<ChannelItemMoveEvent>() {
            @Override
            public void call(ChannelItemMoveEvent channelItemMoveEvent) {
                if (channelItemMoveEvent!=null) {
                    mPresenter.onItemSwap((ArrayList<NewsChannelTable>) channelAdapterMine.getAll(),channelItemMoveEvent.getFromPosition(),channelItemMoveEvent.getToPosition());
                }
            }
        });
    }

    @Override
    public void initPresenter() {
            mPresenter.setVM(this, mModel);
    }

    @Override
    public void initView() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }
        });
        mPresenter.lodeChannelsRequest();
    }

    @Override
    public void returnMineNewsChannels(List<NewsChannelTable> newsChannelsMine) {
        channelAdapterMine = new ChannelAdapter(mContext,R.layout.item_news_channel);
        newsChannelMineRv.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        newsChannelMineRv.setItemAnimator(new DefaultItemAnimator());
        newsChannelMineRv.setAdapter(channelAdapterMine);
        channelAdapterMine.replaceAll(newsChannelsMine);
        channelAdapterMine.setOnItemClickListener(new ChannelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NewsChannelTable newsChannel = channelAdapterMine.get(position);
                    channelAdapterMore.add(newsChannel);
                    channelAdapterMine.removeAt(position);
                    mPresenter.onItemAddOrRemove((ArrayList<NewsChannelTable>) channelAdapterMine.getAll(), (ArrayList<NewsChannelTable>)channelAdapterMore.getAll());

            }
        });


        ItemDragHelperCallback itemDragHelperCallback = new ItemDragHelperCallback(channelAdapterMine);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragHelperCallback);
        itemTouchHelper.attachToRecyclerView(newsChannelMineRv);
        channelAdapterMine.setItemDragHelperCallback(itemDragHelperCallback);
    }

    @Override
    public void returnMoreNewsChannels(List<NewsChannelTable> newsChannelsMore) {
        channelAdapterMore = new ChannelAdapter(mContext,R.layout.item_news_channel);
        newsChannelMoreRv.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        newsChannelMoreRv.setItemAnimator(new DefaultItemAnimator());
        newsChannelMoreRv.setAdapter(channelAdapterMore);
        channelAdapterMore.replaceAll(newsChannelsMore);
        channelAdapterMore.setOnItemClickListener(new ChannelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NewsChannelTable newsChannel = channelAdapterMore.get(position);
                    channelAdapterMine.add(newsChannel);
                    channelAdapterMore.removeAt(position);
                    mPresenter.onItemAddOrRemove((ArrayList<NewsChannelTable>) channelAdapterMine.getAll(), (ArrayList<NewsChannelTable>)channelAdapterMore.getAll());
            }
        });
    }

    @Override
    public void showLoading(String title) {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void showErrorTip(String msg) {

    }
}