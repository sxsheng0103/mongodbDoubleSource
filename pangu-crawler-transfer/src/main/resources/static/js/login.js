layui.config({
    base: $config.resUrl+'js/gjs/'//定义基目录
}).extend({
    ajaxExtention:'ajaxExtention', //加载自定义扩展
    $tool:'tool',
    $api:'api',
    $sha1:'./sha/sha'
}).use(['form', 'layer','ajaxExtention','$tool','$sha','$api'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : parent.layer,
        $ = layui.jquery,
        $sha1 = layui.$sha1,
        $tool=layui.$tool,
        $api = layui.$api;

    //video背景
    /*$(window).resize(function () {
        if ($(".video-player").width() > $(window).width()) {
            $(".video-player").css({
                "height": $(window).height(),
                "width": "auto",
                "left": -($(".video-player").width() - $(window).width()) / 2
            });
        } else {
            $(".video-player").css({
                "width": $(window).width(),
                "height": "auto",
                "left": -($(".video-player").width() - $(window).width()) / 2
            });
        }
    }).resize();*/

    //登录按钮事件
    form.on("submit(login)", function (data) {
        debugger
        console.log(data.field);
        var req = {
            name: data.field.username,
            pwd: $sha1.hex_sha1(data.field.password),
            code: data.field.code
        };

       $api.Login(req,function (data) {
           //保存用户信息到session中
           window.sessionStorage.setItem("sysUser",data.data.loginName);
           window.sessionStorage.setItem("userId",data.data.userId);
           //登录成功跳转到首页,code !== '0000'的已经在ajaxExtention中统一处理了
           window.location.href = $tool.getResUrl()+"layuicms/index.html";
       });

        return false;
    });

    /**
     * 更换验证码
     * */
    $('.code').click(function () {
        debugger
        var suffix = Date.now();
        $('.code').find('img').prop('src',$tool.getContext() + 'getVCode?version=' + suffix);
    });

    function init() {
        if(isLogin()){ // 已经登录过直接跳转到首页
            window.location.href = $tool.getResUrl()+"layuicms/index.html";
        }else{
            $('.code').click(); //获取验证码
        }
    }
    init();

    /**
     * 是否已经登录过
     */
    function isLogin() {
        var userId = window.sessionStorage.getItem("userId");
        return !$tool.isBlank(userId);
    }

});
