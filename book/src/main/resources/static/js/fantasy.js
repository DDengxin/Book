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

    var nav = selectall('.nav_');
    var be = selectall('.be');

    nav[0].onmouseover = be[0].onmouseover = () => {

        be[0].style.display = 'block'
    }

    nav[0].onmouseout = be[0].onmouseout = () => {
        be[0].style.display = 'none';
    }