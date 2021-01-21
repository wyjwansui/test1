(function ($) {
        $.extend({
            test1:function () {
                console.log("extend test1")
            }
        });

        $.fn.extend({
            test2:function () {
                console.log("extend test2")
                var _obj = $(this);
                console.log(_obj.parent().html())
            }
        });


    })(jQuery);
