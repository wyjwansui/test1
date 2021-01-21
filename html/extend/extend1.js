let plug = function (_option) {
    let thiz = this;
    let arg = [];
    let config = {
        type: null,
        data: null
    };
    $.extend(config,_option);

    let init = function () {
        console.log(" extend init ")
        let _obj = $(thiz);
        console.log(_obj.parent().html())
        arg.push(_obj.attr("id"));
        console.log("args:",arg)
    }
    init();
};

(function ($) {
    $.fn.extend({
        test3:plug
    });
})(jQuery);
