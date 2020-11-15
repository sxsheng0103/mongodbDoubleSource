layui.config({
    base: $config.resUrl+'js/gjs/'//定义基目录
}).extend({
    ajaxExtention:'ajaxExtention', //加载自定义扩展
    $tool:'tool',
    $api:'api',
    $sha1:'sha1'
}).use(['form', 'layer','ajaxExtention','$tool','$sha1','$api','jquery'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : parent.layer,
        $ = layui.jquery,
        // 操作对象
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
    form.on('submit(login)',function (data) {
        debugger
        $.ajax({
            url:'loginCheckDefaultUser/',
            data:data.field,
            dataType:'json',
            type:'post',
            success:function (data) {
                if (data.code!=null &&  data.code== 'fail'){
                    layer.msg(data.message);
                }else if(data.code!=null &&  data.code== 'success'){
                    var date = new Date();
                    date.setTime(date.getTime()+data.age);
                    document.cookie=data.cookie+'='+data.Svalue+';expires='+date;
                    location.href = "/main";
                }
            }
        })
        return false;
    });
    debugger
    $.ajax({
        url:"/getVCode",
        data:{},
        type:"Post",
        dataType:"json",
        async: false,
        success:function(data){
            $('.code').find('img').prop('src',data.img);
        },
        error:function(data){
        }
    });
    $('#imgcode').click(function () {
        var suffix = Date.now();
        debugger
        $.ajax({
            url:"/getVCode",
            data:{},
            type:"Post",
            dataType:"json",
            async: false,
            success:function(data){
                $('.code').find('img').prop('src',data.img);
            },
            error:function(data){
            }
        });
        // $api.image({version:suffix},setimage,null);
        // $('.code').find('img').prop('src',$tool.getContext() + 'getVCode?version=' + suffix);
    });
    /**
     * 更换验证码
     * */
    /*$('.code').click(function () {
        debugger
        var suffix = Date.now();
        $('.code').find('img').prop('src',$tool.getContext() + 'getVCode?version=' + suffix);
    });*/

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


/*
$(document).ready(function f() {
    //$('.code').find('img').prop('src',$tool.getContext() + 'getVCode');


});*/
