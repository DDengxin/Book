window.onload = () => {

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
    function register(a, b, c) {

        a.onclick = function() {
            b.style.display = 'block';
            $('mask').style.display = 'block';

        }
        c.onclick = function() {

            b.style.display = 'none';
            $('mask').style.display = 'none';

        }

    }

    //搜索框下的div

    var put = document.querySelector(".put");
    var putsoli = put.children[1];


    // 第一步：点击任何地方都触发事件
    document.onclick = function() {

        putsoli.style.display = 'none'

    }

    //将元素的位置设为不可点击；(此部分点击不会触发点击事件)
    $('putdiv').onclick = function() {

        putsoli.style.display = 'block';
        event.stopPropagation();

    }

    //调用register登录悬浮窗函数
    register($('login'), $('login_rise'), $('login_close'))
    register($('register'), $('register_rise'), $('register_close'))

    //换页
    function slip(pre, next, showbook1, showbook2) {

        let a = 1;

        pre.onclick = () => {

            if (a == 1) {
                showbook1.style.display = 'none';
                showbook2.style.display = 'block';
                return a = 2;
            } else if (a == 2) {
                showbook1.style.display = 'block';
                showbook2.style.display = 'none';
                return a = 1;
            }
        }

        next.onclick = () => {



            if (a == 1) {
                showbook1.style.display = 'none';
                showbook2.style.display = 'block';
                return a = 2;
            } else if (a == 2) {
                showbook1.style.display = 'block';
                showbook2.style.display = 'none';
                return a = 1;
            }
        }

    }

    var number = select('.number').children;
    var letter = selectall('.letter');

    slip(number[1], number[7], letter[0], letter[1])

    function slipone(number, showbook1, showbook2) {

        let a = 1;

        number.onclick = () => {

            if (a = 1) {
                showbook1.style.display = 'block';
                showbook2.style.display = 'none'
            }

        }

    }

    function sliptwo(number, showbook1, showbook2) {

        let a = 1;

        number.onclick = () => {

            if (a = 1) {
                showbook1.style.display = 'none';
                showbook2.style.display = 'block';
            }

        }

    }


    slipone(number[2], letter[0], letter[1]);

    sliptwo(number[3], letter[0], letter[1]);

    slipone(number[4], letter[0], letter[1]);

    sliptwo(number[5], letter[0], letter[1]);

    slipone(number[6], letter[0], letter[1]);

    sliptwo(number[8], letter[0], letter[1]);













}