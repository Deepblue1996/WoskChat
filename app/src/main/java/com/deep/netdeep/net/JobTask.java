package com.deep.netdeep.net;

import com.deep.netdeep.net.bean.BaseEn;
import com.deep.netdeep.net.bean.TokenBean;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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

}
