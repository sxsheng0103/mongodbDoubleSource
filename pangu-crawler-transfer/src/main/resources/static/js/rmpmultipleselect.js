/**
 * 多选下拉框
 * Name:rmpmultipleselect.js
 * Author:dingsheng
 */
layui.define(['jquery', 'rmputils'], function(exports) {
    var $ = layui.jquery;
    var rmputils = layui.rmputils;

    /*
     data:[
     {
     value:"",
     text:"",
     selected:true
     }
     ]
     */
    var Class = function(options) {
        this.config = {
            ele:$('select[multiple="multiple"]'),
            width:200,
            render:false,
            options:{
                'data':[],
                'tip':''
            },
            optionChange:null,  //选项改变时   参数isSelected value,
            dropdownClose:null  //关闭selected下拉面板时 参数selected selectedText
        }

        typeof options == 'object' && $.extend(true, this.config, options);

        this.selected = {
            selectedValue:[],
            selectedText:[]
        }
    }

    Class.prototype.init = function() {
        var defaultOption = this.config;
        var selected = this.selected;


        var _element = defaultOption.ele;
        typeof _element != 'object' && (_element = $(_element));

        //非自定义数据
        if (!defaultOption.render) {
            //获取option
            $.each(_element.children('option'), function(i, n){
                if (i == 0) {
                    defaultOption.options.tip = $(n).text();
                    return true;
                }
                var d = {
                    value:$(n).val(),
                    text:$(n).text()
                };
                if ($(n).attr('selected')) {
                    d['selected'] = true;
                }
                defaultOption.options.data.push(d);
            });
            _element.hide();
            _element.before('<div id="multiple-select-view"></div>');
            _element = $('#multiple-select-view')
        }

        _element.append('<div class="layui-form-select" style="width: ' + defaultOption.width + 'px;"><div class="layui-select-title"><input type="text" placeholder="' + defaultOption.options['tip']  + '" value="" readonly="" class="layui-input"><i class="layui-edge"></i></div></div>');

        //初始化渲染
        _element.children('.layui-form-select').append('<dl class="layui-anim layui-anim-upbit"></dl>');

        var _dl = _element.find('dl');
        var _formselect = _element.find('.layui-form-select');
        var _selectinput = _element.find('.layui-select-title > input');

        _dl.append('<dd lay-value="" class="layui-select-tips">' + defaultOption.options.tip + '</dd>');

        $.each(defaultOption.options.data, function(i, n) {
            _dl.append('<dd style="margin-top:6px;" lay-value="' + n.value + '" lay-text="' + n.text + '"><i style="display:inline-block;width:16px;height:16px;border:1px solid #e6e6e6;vertical-align:middle;margin-right:5px;line-height:19px;text-align:center;">&radic;</i>' + n.text + '</dd>');
            if (n.selected) {
                _dl.find('dd').eq(i + 1).addClass('layui-this');
                selected.selectedText.push(n.text);
                selected.selectedValue.push(n.value);
            }
        });
        _selectinput.val(selected.selectedText.join(","));


        _dl.find('dd:gt(0)').on('mousedown', function(e){
            var that = this;
            if ($(that).hasClass('layui-this')) {
                /*selected.selectedText.removeByValue($(that).attr('lay-text'));
                 selected.selected.removeByValue($(that).attr('lay-value'))*/;
                rmputils.arrayUtils.removeValue(selected.selectedText, $(that).attr('lay-text'));
                rmputils.arrayUtils.removeValue(selected.selectedValue, $(that).attr('lay-value'));
            } else {
                selected.selectedText.push($(that).attr('lay-text'));
                selected.selectedValue.push($(that).attr('lay-value'));
            }

            typeof defaultOption.optionChange == 'function' && (defaultOption.optionChange)(!$(that).hasClass('layui-this'), $(that).attr('lay-value'));
            _selectinput.val(selected.selectedText.join(","));
            $(this).toggleClass('layui-this');
            e.stopPropagation();
        });

        _element.on('mousedown', function(){
            _formselect.toggleClass('layui-form-selected');
            if (!_formselect.hasClass('layui-form-selected')) {
                typeof defaultOption.dropdownClose == 'function' &&
                defaultOption.dropdownClose(selected.selectedValue, selected.selectedText);
            }
        });
    }

    var multipleselect = function(options) {
        var ins = new Class(options);
        ins.init();
        return ins;
    };

    exports('rmpmultipleselect', multipleselect);
});