window.onload = function() {

    //封装获取id的方法
    function $(id) {
        return document.getElementById(id);
    }
    //封装获取class的方法
    function select(className) {

        return document.querySelector(className);

    }

    //封装获取同名class的方法

    function selectall(className) {

        return document.querySelectorAll(className);

    }



    //封装登录注册悬浮窗函数
    //re_button为登陆注册按钮，re_solid为登录注册悬浮窗，close为悬浮窗关闭按钮
    function register(re_button, re_solid, close) {

        re_button.onclick = function() {
            re_solid.style.display = 'block';
            //mask为阴影层
            $('mask').style.display = 'block';

        }
        close.onclick = function() {

            re_solid.style.display = 'none';
            //mask为阴影层
            $('mask').style.display = 'none';

        }

    }


    //调用register登录悬浮窗函数
    register($('login'), $('login_rise'), $('login_close'))
    register($('register'), $('register_rise'), $('register_close'))

    //搜索框下的div

    var put = document.querySelector(".put");
    var putsolid = put.children[1];


    // 第一步：点击任何地方都触发事件
    document.onclick = function() {

        // putsolid.style.display = 'none'

    }

    //将元素的位置设为不可点击；(此部分点击不会触发点击事件)
    $('putdiv').onclick = function() {

        putsoli.style.display = 'block';
        event.stopPropagation();

    }

    //li的悬浮层
    //a传li    b传li悬浮层   c传悬浮层里的div   d幻灯片 

    //li悬浮层里的切换

    cut('.cut1', '.cutimg1');
    cut('.cut2', '.cutimg2');
    cut('.cut3', '.cutimg3');
    cut('.cut4', '.cutimg4');
    cut('.cut5', '.cutimg5');
    cut('.cut6', '.cutimg6');
    cut('.cut7', '.cutimg7');
    cut('.cut8', '.cutimg8');
    cut('.cut9', '.cutimg9');

    //li悬浮层里的切换函数
    function cut(Cut, cutimg) {
        var suspend2 = select(Cut).children;
        var solidimg = select(cutimg).children;

        solidimg[0].onmouseover = () => {
            suspend2[0].children[0].innerHTML = '三体';
            suspend2[0].children[1].innerHTML = '<span>[点评]:</span>穿越平行世界的苏楠，幸运得到吃鸡之无限升级系统，在游戏中淘汰玩家可获取经验值升级，升级后系统会给予奖励，枪械精通、神级天赋、黑科技物品等应有尽有。'
        }
        solidimg[1].onmouseover = () => {

            suspend2[0].children[0].innerHTML = '魔道祖师';
            suspend2[0].children[1].innerHTML = '<span>[点评]:</span>某日，陈陌遇到了一只天禁级BOSS，可能它自己都不知道，他会死在自己的大招下……面对千万成员级公会，系统提示响了起来：“叮…检测到你有无数个大招，请问要怎么轰！”';
        }
        solidimg[2].onmouseover = () => {

            suspend2[0].children[0].innerHTML = '烦人修仙';
            suspend2[0].children[1].innerHTML = '<span>[点评]:</span>穿越平行世界的苏楠，幸运得到吃鸡之无限升级系统，在游戏中淘汰玩家可获取经验值升级，升级后系统会给予奖励，枪械精通、神级天赋、黑科技物品等应有尽有';
        }

    }

    //轮播图
    // 下标要从1开始,以为播放的第一张图前面有一张最后那张图，下标要注意对应
    var box = document.getElementsByClassName("box")[0];
    var btn = document.getElementsByClassName("btn");
    var index = 1;
    var moveTimer;


    // 点击下一张
    function next() {
        index++;
        if (index == 7) {
            index = 2;
            box.style.left = "-808px";
        }
        moveWidthIndex();
        btnCol();
    }


    // 点击上一张
    function pre() {
        index--;
        if (index == -1) {
            index = 4;
            box.style.left = "-4040px";
        }
        moveWidthIndex();
    }

    function moveWidthIndex() {
        var l = index * -808 - box.offsetLeft;
        var count = 0;
        clearInterval(moveTimer);
        moveTimer = setInterval(function() {
            count++;
            box.style.left = box.offsetLeft + l / 10 + "px";
            if (count >= 10) {
                clearInterval(moveTimer);
                box.style.left = index * -808 + "px";
            }
        }, 100);
    }


    for (let i = 0; i < btn.length; i++) {
        btn[i].index = i;
        btn[i].onmouseover = function() {
            index = this.index + 1;
            console.log(index);
            moveWidthIndex();
            btnCol();
            clearInterval(timer);
            timer = setInterval(function() {
                next();
            }, 5000);
            // next();
        }
    }
    // 点击书名按钮的函数
    function btnCol() {
        for (let i = 0; i < btn.length; i++) {
            btn[i].style.backgroundColor = "black";
        }
        if (index == 6) {
            index = 1;
        }
        btn[index - 1].style.backgroundColor = "#ed4259";
    }


    //轮播图结束

    //右边轮播图

    var slip = document.querySelector(".slip");
    var div1 = slip.children[0]; //获取slip里面的第一个元素 
    var op = 20;


    function go() {

        if (op <= -1000) {

            op = 0;

        }
        op -= 1;
        div1.style.top = op + 'px';

    }
    var time = setInterval(go, 50);

    div1.onmouseover = function() { //当鼠标指针移动到时

        clearInterval(time); //停止执行定时器
    }

    div1.onmouseout = function() { //当鼠标离开时

            time = setInterval(go, 50) //执行定时器
        }
        //右边轮播图结束

    //编辑推荐
    var redact = select('.redact');
    var reh4 = selectall('.reh4');

    for (let i = 0; i < reh4.length; i++) {
        redact.children[i].onmouseover = function() {
            reh4[i].style.display = 'block';
        }

        redact.children[i].onmouseout = function() {
            reh4[i].style.display = 'none';
        }
    }

    //榜单  更多
    var unf = 1;

    $('unfold').onclick = () => {

        if (unf == 1) {

            $('annouc').style.height = '560' + 'px';
            $('unfold').innerHTML = '收起更多';
            return unf = 2;

        } else {
            $('annouc').style.height = '410' + 'px';
            $('unfold').innerHTML = '展开更多';
            return unf = 1;
        }

    }


    //置顶按钮
    function doctop() {

        var documenttop = document.documentElement.scrollTop;

        if (documenttop > 600) {

            selectall('.stick')[0].style.display = 'block';
        } else {

            selectall('.stick')[0].style.display = 'none';
        }
    }

    selectall('.stick')[0].onclick = function() {

        document.documentElement.scrollTop = 0;

    }
    setInterval(doctop, 2000)

    //倒计时
    var maxtime = 60 * 60 * 5; //一个小时，按秒计算，自己调整!   
    function CountDown() {
        if (maxtime >= 0) {

            //floor() 方法执行的是向下取整计算，它返回的是小于或等于函数参数，并且与之最接近的整数
            days = Math.floor(maxtime / 360);
            minutes = Math.floor(maxtime / 60);
            seconds = Math.floor(maxtime % 60);
            msg = +days+"天"+minutes + "分" + seconds + "秒";
            selectall('.day')[0].innerHTML = msg;

            if (maxtime == 5 * 60) alert("还剩5分钟");
            --maxtime;
        } else {

            clearInterval(timer);

        }
    }

    timer = setInterval(CountDown, 1000);


    //倒计时结束








}