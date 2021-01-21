
;(function () {
    var defalult = {
        data:[],
        type:'',
        index:0
    };
    var container = {
        current:null,
        last:null
    }
    var getAbsolute = function(el){
        let el2 = el;
        let curtop = 0;
        let curleft = 0;
        if (document.getElementById || document.all) {
            do  {
                curleft += el.offsetLeft-el.scrollLeft;
                curtop += el.offsetTop-el.scrollTop;
                el = el.offsetParent;
                el2 = el2.parentNode;
                while (el2 != el) {
                    curleft -= el2.scrollLeft;
                    curtop -= el2.scrollTop;
                    el2 = el2.parentNode;
                }
            } while (el.offsetParent);

        } else if (document.layers) {
            curtop += el.y;
            curleft += el.x;
        }
        return {"top":curtop, "left":curleft};
    };
    var buildSelectItem = function (_opts) {

        let _list_div = $("<div class='item-list'></div>");
        let _contain = $("<div class='input-div'>").attr("id","contain"+parseInt(100000*Math.random())).css("width",_opts.width).append(_list_div);
        $("body").append(_contain)
        $.each(_opts.data,function (i,v) {
            let _div = $("<div class='item-div'>").html(v);
            _list_div.append(_div);
        });
        return _contain;
    }
    $(document).click(function(e) {
        if( container.last == null || (container.current != null && container.last.attr("id") == container.current.attr("id"))
            || $(e.target).hasClass("item-list") || $(e.target).hasClass("item-div")){
            return
        }
        container.last.hide();
    })
    $.fn.extend({
        inputSelect : function (options) {
            let $this = this;
            let opts = $.extend({},defalult,options);
            opts.width = $this.outerWidth()
            $this.contain = buildSelectItem(opts);
            $this.on("blur",function (e) {
                container.last = container.current
                container.current = null
            })
            $this.on("keyup",function (e) {
                console.log("keyup")
                console.log($(e.target).val())
            })
            return $this.on("focus",function () {
                let _input = $(this);
                console.log("focus:")

                container.current = $this.contain

                let _top = getAbsolute(this);
                $this.contain.css("top",_top.top+$(this).outerHeight()).css("left",_top.left)
                $this.contain.show()
                $this.contain.find(".item-div").unbind("click").on("click",function () {
                    _input.val($(this).html());
                    $this.contain.hide();
                })
            });
        }
    })
})(jQuery);
