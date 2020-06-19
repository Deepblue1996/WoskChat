package com.deep.netdeep.net;

import com.deep.netdeep.net.bean.BaseEn;
import com.deep.netdeep.net.bean.TokenBean;
import com.deep.netdeep.net.bean.UserChatBean;
import com.deep.netdeep.net.bean.UserTable;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Class -
 * <p>
 * Created by Deepblue on 2018/12/4 0004.
 */

public interface JobTask {

    /**
     * 登陆 请求
     *
     * @return
     */
    @FormUrlEncoded
    @POST("/tcpservice_war/login")
    Observable<BaseEn<TokenBean>> login(@Field("username") String username, @Field("password") String password);

    /**
     * 注册 请求
     *
     * @return
     */
    @FormUrlEncoded
    @POST("/tcpservice_war/register")
    Observable<BaseEn<String>> register(@Field("username") String username, @Field("password") String password);

    /**
     * 判断是否过期
     *
     * @return
     */
    @FormUrlEncoded
    @POST("/tcpservice_war/loginEffective")
    Observable<BaseEn<String>> loginEffective(@Field("token") String token);

    /**
     * 获取所有在线用户的信息
     *
     * @return
     */
    @FormUrlEncoded
    @POST("/tcpservice_war/userList")
    Observable<BaseEn<List<UserChatBean>>> userList(@Field("token") String token);

    /**
     * 上传头像
     *
     * @param token token
     * @param files 文件
     * @return
     */
    @Multipart
    @POST("/tcpservice_war/fileUploadHeadPortrait")
    Observable<BaseEn<String>> fileUploadHeadPortrait(@Header("token") String token, @Part MultipartBody.Part files);

    /**
     * 获取图片
     *
     * @return
     */
    @FormUrlEncoded
    @POST("/tcpservice_war/rePhoto")
    Observable<BaseEn<String>> rePhoto(@Field("name") String name);

    /**
     * 修改昵称签名
     *
     * @return
     */
    @FormUrlEncoded
    @POST("/tcpservice_war/editInfo")
    Observable<BaseEn<UserTable>> editInfo(@Field("info") String info);

}
