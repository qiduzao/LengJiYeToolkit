package com.lengjiye.toolkit.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lengjiye.toolkit.R;
import com.lengjiye.toolkit.activity.BaseActivity;
import com.lengjiye.toolkit.utils.OkHttpUtils;
import com.lengjiye.tools.AppTool;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 试用okhttp
 */
public class OKHttpFragment extends BaseFragment {

    @ViewInject(R.id.text)
    private TextView text;

    public OKHttpFragment() {
    }

    /**
     * 创建一个fragment
     *
     * @return
     */
    public static OKHttpFragment newInstance() {
        OKHttpFragment fragment = new OKHttpFragment();
        return fragment;
    }

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_okhttp, container, false);
    }


    /**
     * 请求网页数据   异步请求
     */
    private void loadWebPageDataGitAsync() {
        AppTool.generateClassAndMethodTag();
        String path = "https://www.baidu.com/";
        OkHttpUtils.getInstance().getRequest(path, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.obj = e;
                message.what = 1;
//                ttsHandler.sendMessage(message);
                testRxJavaCommunication(e.toString(), false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String htmlStr = response.body().string();
                Message message = new Message();
                message.obj = htmlStr;
                message.what = 0;
//                ttsHandler.sendMessage(message);
                testRxJavaCommunication(htmlStr, false);
            }
        });
    }

    /**
     * 请求网页数据  同步请求
     */
    private void loadWebPageDataGit() {
        Observable.create(
                new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> sub) throws Exception {
                        try {
                            String path = "https://www.baidu.com/";
                            Response response = OkHttpUtils.getInstance().getRequest(path, null);
                            if (response.code() == 200) {
                                sub.onNext(response.toString());
                            } else {
                                sub.onError(new Exception("code" + response.code()));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            sub.onError(e);
                        }
                    }

                }
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(mContext, "请求成功", Toast.LENGTH_SHORT).show();
                        text.setText(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mContext, "请求失败", Toast.LENGTH_SHORT).show();
                        text.setText("请求失败:" + e);
                    }

                    @Override
                    public void onComplete() {

                    }

                });

    }

    /**
     * post 表单提交数据
     */
    private void loadWebPageDataPost() {
        //创建FormBody对象
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("search", "Jurassic Park");
        RequestBody requestBody = builder.build();
        OkHttpUtils.getInstance().postRequest("https://en.wikipedia.org/w/index.php", requestBody, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.obj = e;
                message.what = 1;
                ttsHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String htmlStr = response.body().string();
                Message message = new Message();
                message.obj = htmlStr;
                message.what = 0;
                ttsHandler.sendMessage(message);
            }
        });
    }


    /**
     * post string
     */
    private void loadWebPageDataString() {
        String postBody = ""
                + "Releases\n"
                + "--------\n"
                + "\n"
                + " * _1.0_ May 6, 2013\n"
                + " * _1.1_ June 15, 2013\n"
                + " * _1.2_ August 11, 2013\n";
        OkHttpUtils.getInstance().postRequest("https://api.github.com/markdown/raw", "text/x-markdown; charset=utf-8", postBody, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.obj = e;
                message.what = 1;
                ttsHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String htmlStr = response.body().string();
                Message message = new Message();
                message.obj = htmlStr;
                message.what = 0;
                ttsHandler.sendMessage(message);
            }
        });
    }

    /**
     * 上传图片
     */
    private void sendImageView() {
        //创建RequestBody对象
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "Square Logo")
                .addFormDataPart("image", "logo-square.png",
                        RequestBody.create(MediaType.parse("image/png"), new File(Environment.getExternalStorageDirectory()
                                .getAbsoluteFile() + "/" + "icon.png")))
                .build();
        OkHttpUtils.getInstance().postRequest("https://api.imgur.com/3/image", requestBody, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.obj = e;
                message.what = 1;
                ttsHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String htmlStr = response.body().string();
                Message message = new Message();
                message.obj = htmlStr;
                message.what = 0;
                ttsHandler.sendMessage(message);
            }
        });
    }

    /**
     * 测试使用rxJava通讯
     *
     * @param message 消息
     * @param b       成功还是失败
     */
    private void testRxJavaCommunication(String message, final boolean b) {
        Observable.just(message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (b) {
                            Toast.makeText(mContext, "请求成功", Toast.LENGTH_SHORT).show();
                            text.setText(s);
                        } else {
                            Toast.makeText(mContext, "请求失败", Toast.LENGTH_SHORT).show();
                            text.setText("请求失败：" + s);
                        }
                    }
                });
    }

    MyHandler ttsHandler = new MyHandler((BaseActivity) mActivity);

    @Event({R.id.bt_wangye_post, R.id.bt_wangye_get_async, R.id.bt_wangye_get, R.id.bt_wangye_tring, R.id.bt_shanghcuantupian})
    private void Click(View view) {
        switch (view.getId()) {
            case R.id.bt_wangye_get_async:
                loadWebPageDataGitAsync();
                break;
            case R.id.bt_wangye_get:
                loadWebPageDataGit();
                break;
            case R.id.bt_wangye_post:
                loadWebPageDataPost();
                break;
            case R.id.bt_wangye_tring:
                loadWebPageDataString();
                break;
            case R.id.bt_shanghcuantupian:
                sendImageView();
                break;
        }
    }


    /**
     * 防止内存溢出
     */
    class MyHandler extends Handler {
        WeakReference<BaseActivity> mActivity;

        public MyHandler(BaseActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity theActivity = mActivity.get();
            if (theActivity == null) {
                return;
            }
            switch (msg.what) {
                case 0:
                    Toast.makeText(mContext, "请求成功", Toast.LENGTH_SHORT).show();
                    text.setText((CharSequence) msg.obj);
                    Log.e("lz", "请求成功:" + msg.obj);
                    break;
                case 1:
                    Toast.makeText(mContext, "请求失败", Toast.LENGTH_SHORT).show();
                    text.setText("请求失败：" + msg.obj);
                    Log.e("lz", "请求失败:" + msg.obj);
                    break;
            }
        }
    }
}
