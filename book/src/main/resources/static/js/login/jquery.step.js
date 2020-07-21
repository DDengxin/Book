$(function() {
		var step= $("#myStep").step({
			animate:true,
			initStep:1,
			speed:1000
		});		
		$("#preBtn").click(function(event) {
			var yes=step.preStep();

		});
		$("#applyBtn").click(function(event) {		
			
		    var code = $.trim($("#Verification").val());
			var phone =/[1][3-9][0-9]{9,9}/;
		  var phones = $.trim($("#phone").val());
		if ($.trim(phones) == "") {
			Tip('请填写手机号码！');
			$("#phone").focus();
			return;
		}
		if(!phone.exec(phones)){

				Tip('手机输入格式不正确,请从新输入');
				$("#phones").focus();
			return;
			}
		if ($.trim(code) == "") {
			Tip('请填写验证码！');
			$("#Verification").focus();
			return;
		}
            document.getElementById("applyBtn").disabled=true;
            var phone=document.getElementById("phone").value;
            var code=document.getElementById("Verification").value;
            $.ajax({
                // async: false,
                url:'/book/phone/verification',//地址
                dataType:'text',//数据类型
                type:'POST',//类型
                data:{
                    phone:phone,
                    inputCode:code
                },
                success:function(data){
                    if (data == "success"){
                        var yes=step.nextStep();
                        return;
                    } else if(data=="exist"){
                        Tip('该手机已被注册');
                        $("#phones").focus();
                        document.getElementById("applyBtn").disabled=false;
                        return;

                    }else if (data=="false") {
                        Tip('验证码不正确');
                        $("#Verification").focus();
                        document.getElementById("applyBtn").disabled=false;
                        return;

                    }else {
                        alert("请求失败，请重新再试")
                        document.getElementById("applyBtn").disabled=false;
                        return;
                    }


                },
                //失败/超时
                error:function(XMLHttpRequest,textStatus,errorThrown){
                    if(textStatus==='timeout'){
                        alert('请求超时');
                        setTimeout(function(){
                            alert('重新请求');
                        },2000);
                    }
                }
            });



		});
		$("#submitBtn").click(function(event) {
            var txtconfirm = $.trim($("#confirmpwd").val());
            var txtPwd = $("#password").val();
            var patrn = /^(\w){6,20}$/;
            var userpatrn = /^[\u4e00-\u9fa5]+$/;
            var username = $("#username").val();

            if ($.trim(username) == "") {

                Tips('请输入你要设置的用户名！');
                $("#txtPwd").focus();
                return;

            }
            
            if (!userpatrn.exec(username)) {

                Tips('用户名必须输入中文');
                $("#username").focus();
                return;

            }

            if ($.trim(txtPwd) == "") {

                Tips('请输入你要设置的密码！');
                $("#txtPwd").focus();
                return;

            }

            if (!patrn.exec(txtPwd)) {
                Tips('密码格式不正确(6-20个字母、数字、下划线)');
                $("#txtPwd").focus();
                return;
                ;
            }

            if ($.trim(txtconfirm) == "") {

                Tips('请再次输入密码！');
                $("#txtconfirm").focus();
                return;

            }

            if ($.trim(txtconfirm) != $.trim(txtPwd)) {

                Tips('你输入的密码不匹配，请从新输入！');
                $("#txtconfirm").focus();
                return;
            }
            var username = document.getElementById("username").value;
            var phone=document.getElementById("phone").value;
            var password=document.getElementById("password").value;
            document.getElementById("submitBtn").disabled=true;
            $.ajax({
                url:'/book/user/add',//地址
                dataType:'text',//数据类型
                type:'POST',//类型
                data:{
                    phone:phone,
                    password:password,
                    username:username
                },
                success:function(data){
                    if (data == "success"){
                        var yes=step.nextStep();
                        return;
                    }else if ("exist"){
                        alert("该用户已注册，不能重复注册");
                        document.getElementById("submitBtn").disabled=false;
                    }
                    else {
                        alert("请求失败，请重新再试");
                        document.getElementById("submitBtn").disabled=false;
                        return;
                    }
                },
                //失败/超时
                error:function(XMLHttpRequest,textStatus,errorThrown){
                    if(textStatus==='timeout'){
                        alert('请求超时');
                        setTimeout(function(){
                            alert('重新请求');
                        },2000);
                    }
                }
            });
            // var yes = step.nextStep();

				$(function () {  setTimeout("lazyGo();", 1000); });
                function lazyGo() {
		         var sec = $("#sec").text();
		            $("#sec").text(--sec);
		            if (sec > 0)
		         	setTimeout("lazyGo();", 1000);
		            else
			window.location.href = "article_home.html";
	}
	
			
			
		});
		$("#goBtn").click(function(event) {
			var yes=step.goStep(3);
		});	
	});


(function (factory) {
    "use strict";
    if (typeof define === 'function') {
        // using CMD; register as anon module
      define.cmd&&define('jquery-step', ['jquery'], function (require, exports, moudles) {
            var $=require("jquery");
            factory($);
            return $;
        });
      define.amd&&define(['jquery'], factory);
    } else {
        // no CMD; invoke directly
        factory( (typeof(jQuery) != 'undefined') ? jQuery : window.Zepto );
    }
}

(function($){
  $.fn.step = function(options) { 
      var opts = $.extend({}, $.fn.step.defaults, options);
      var size=this.find(".step-header li").length;
      var barWidth=opts.initStep<size?100/(2*size)+100*(opts.initStep-1)/size : 100;
      var curPage=opts.initStep;

      this.find(".step-header").prepend("<div class=\"step-bar\"><div class=\"step-bar-active\"></div></div>");
      this.find(".step-list").eq(opts.initStep-1).show();
      if (size<opts.initStep) {
        opts.initStep=size;
      }
      if (opts.animate==false) {
        opts.speed=0;
      }
      this.find(".step-header li").each(function (i, li) {
        if (i<opts.initStep){
          $(li).addClass("step-active");
        }
        //$(li).prepend("<span>"+(i+1)+"</span>");
        $(li).append("<span>"+(i+1)+"</span>");
    });
    this.find(".step-header li").css({
      "width": 100/size+"%"
    });
    this.find(".step-header").show();
    this.find(".step-bar-active").animate({
        "width": barWidth+"%"},
        opts.speed, function() {
        
    });

      this.nextStep=function() {
        if (curPage>=size) {
          return false;
        }
        return this.goStep(curPage+1);
      }

      this.preStep=function() {
        if (curPage<=1) {
          return false;
        }
        return this.goStep(curPage-1);
      }

      this.goStep=function(page) {
        if (page ==undefined || isNaN(page) || page<0) {
          if(window.console&&window.console.error){
            console.error('the method goStep has a error,page:'+page);
          }
        return false;
        }
        curPage=page;
        this.find(".step-list").hide();
        this.find(".step-list").eq(curPage-1).show();
        this.find(".step-header li").each(function (i, li) {
          $li=$(li);
          $li.removeClass('step-active');
          if (i<page){
            $li.addClass('step-active');
            if(opts.scrollTop){
             $('html,body').animate({scrollTop:0}, 'slow');
            }
        }
      });
      barWidth=page<size?100/(2*size)+100*(page-1)/size : 100;
        this.find(".step-bar-active").animate({
          "width": barWidth+"%"},
          opts.speed, function() {
            
        });
        return true;
      }
      return this;
  };
   
  $.fn.step.defaults = {
      animate:true,
      speed:500,
    initStep:1,
    scrollTop:true
  };
})
 );  