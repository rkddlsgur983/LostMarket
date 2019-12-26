package com.sejong.unknown.view.login.data;

import com.sejong.unknown.service.retrofit.RetrofitHelper;
import com.sejong.unknown.service.retrofit.RetrofitService;
import com.sejong.unknown.view.login.domain.LoginRepository;

import java.util.HashMap;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class LoginRepositoryImpl implements LoginRepository {
    @Override
    public Single<Response<ResponseBody>> login() {
        RetrofitService service = RetrofitHelper.getRetrofitService(RetrofitService.class);
        HashMap<String, String> input = new HashMap<>();
        input.put("userid", "admin");
        input.put("userpasswd", "sejong");
        return service
                .login(input)
                .subscribeOn(Schedulers.io());
    }
}
