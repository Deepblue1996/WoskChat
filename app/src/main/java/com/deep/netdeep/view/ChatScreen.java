package com.deep.netdeep.view;

import android.annotation.SuppressLint;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.deep.dpwork.annotation.DpLayout;
import com.deep.dpwork.annotation.DpStatus;
import com.deep.dpwork.util.SoftListenerUtil;
import com.deep.netdeep.R;
import com.deep.netdeep.base.TBaseScreen;
import com.deep.netdeep.net.bean.UserChatBean;
import com.deep.netdeep.util.TouchExt;
import com.prohua.roundlayout.RoundAngleFrameLayout;

import java.util.Objects;

import butterknife.BindView;

@DpStatus(blackFont = true)
@DpLayout(R.layout.chat_screen)
public class ChatScreen extends TBaseScreen {

    @BindView(R.id.backLin)
    ConstraintLayout backLin;
    @BindView(R.id.backTouch)
    ImageView backTouch;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.contentEdit)
    EditText contentEdit;
    @BindView(R.id.sendBt)
    RoundAngleFrameLayout sendBt;
    @BindView(R.id.centerBottom)
    LinearLayout centerBottom;

    private UserChatBean userChatBean;

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    public void setUserChatBean(UserChatBean userChatBean) {
        this.userChatBean = userChatBean;
    }

    /**
     * 懒加载
     */
    public static ChatScreen newInstance() {
        return new ChatScreen();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void init() {
        userName.setText(userChatBean.userTable.getUsername());
        backTouch.setOnTouchListener((v, event) -> TouchExt.alpTouch(v, event, this::closeEx));
        sendBt.setOnTouchListener((v, event) -> TouchExt.alpTouch(v, event, () -> {

        }));

        onGlobalLayoutListener = SoftListenerUtil.listener(Objects.requireNonNull(getActivity()), backLin, i -> {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) centerBottom.getLayoutParams();
            layoutParams.bottomMargin = i;
            centerBottom.setLayoutParams(layoutParams);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        backLin.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
    }

}
