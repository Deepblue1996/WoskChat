package com.deep.netdeep.view;

import android.annotation.SuppressLint;
import android.widget.ImageView;
import android.widget.TextView;

import com.deep.dpwork.annotation.DpLayout;
import com.deep.dpwork.annotation.DpStatus;
import com.deep.netdeep.R;
import com.deep.netdeep.base.TBaseScreen;
import com.deep.netdeep.core.CoreApp;
import com.deep.netdeep.net.bean.UserTable;
import com.deep.netdeep.util.ImgPhotoUtil;
import com.deep.netdeep.util.TouchExt;

import butterknife.BindView;

@DpStatus(blackFont = true)
@DpLayout(R.layout.user_chat_info_screen)
public class UserChatInfoScreen extends TBaseScreen {

    @BindView(R.id.backTouch)
    ImageView backTouch;
    @BindView(R.id.headImg)
    ImageView headImg;
    @BindView(R.id.nickTv)
    TextView nickTv;
    @BindView(R.id.userId)
    TextView userId;
    @BindView(R.id.contentTv)
    TextView contentTv;

    private UserTable userTable;

    public void setUserTable(UserTable userTable) {
        this.userTable = userTable;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void init() {

        try {
            upInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        backTouch.setOnTouchListener((v, event) -> TouchExt.alpTouch(v, event, this::closeEx));
    }

    @SuppressLint("SetTextI18n")
    public void upInfo() {
        userId.setText("USER ID:" + userTable.getId() + "(" + userTable.getUsername() + ")");
        nickTv.setText(userTable.getNickname());
        contentTv.setText(userTable.getContent());
        ImgPhotoUtil.getPhoto(_dpActivity, userTable.getHeaderPath(), headImg);
    }

    @Override
    public SCREEN screenOpenAnim() {
        return SCREEN.Horizontal;
    }
}
