//获取ID
function $$(id) {
    // body...
    return typeof id === "string" ? document.getElementById(id) : null;
}
//缓动动画函数
function bufferAnimation(obj, json, fn) {
    // 先清除定时器
    clearInterval(obj.timer);
    //设置定时器
    var begin = 0,
        target = 0,
        speed = 0;
    obj.timer = setInterval(function() {
        //标识
        var flag = true;
        for (var key in json) {
            //初始值
            if ('opacity' === key) {
                begin = parseInt(parseFloat(getCssAttrValue(obj, key)) * 100);
                target = parseInt(parseFloat(json[key]) * 100);
            } else if ('scrollTop' === key) {
                begin = Math.ceil(obj.scrollTop);
                target = parseInt(json[key]);
            } else {
                begin = parseInt(getCssAttrValue(obj, key)) || 0;
                target = parseInt(json[key]);
            }
            //求出步长
            speed = (target - begin) * 0.2;
            //判断是否向上取整
            speed = (target >= begin) ? Math.ceil(speed) : Math.floor(speed);
            //动起来
            if ('opacity' === key) {
                obj.style.opacity = (begin + speed) / 100;
                obj.style.filter = 'alpha(Opacity:' + (begin + speed) + ')';
            } else if ('scrollTop' === key) {
                obj.scrollTop = begin + speed;
            } else if ('zIndex' === key) {
                obj.style[key] = json[key];
            } else {
                obj.style[key] = begin + speed + 'px';
            }
            //到达目标值后停止定时器
            if (begin !== target) {
                flag = false;
            }
        }
        //当所有值都满足后清除定时器
        if (flag) {
            clearInterval(obj.timer);
            //判断有没有回调函数
            if (fn) {
                fn();
            }
        }
    }, 50)
}

//改变CSS样式
function changeCssStyle(obj, attr, value) {
    if (attr === 'opacity') {
        obj.style.filter = '(opacity(' + parseFloat(value / 100) + '))';
        obj.style.opacity = value;
        return
    }
    obj.style[attr] = value;
}


//JS获取CSS属性值
function getCssAttrValue(obj, attr) {
    if (obj.currentStyle) { //IE和opear
        return obj.currentStyle[attr];
    } else {
        return window.getComputedStyle(obj, null)[attr];
    }
}

var box29Main1 = $$('box29Main1');
var box29Ul = $$('box29Main1Ul');
var allLis = box29Ul.children;
var box29_clt = $$('box29_clt');
var cltChild = box29_clt.children;
var brief = document.querySelectorAll('.brief');

box29Main1.onmouseover = function() {
    // body...
    bufferAnimation(box29_clt, {
        'opacity': 1
    })
}
box29Main1.onmouseout = function() {
    // body...
    bufferAnimation(box29_clt, {
        'opacity': 0
    })
}
var json = [{
    width: 250,
    height: 200,
    top: 20,
    left: 0,
    opacity: 0.9,
    zIndex: 3
}, {
    width: 350,
    height: 250,
    top: 40,
    left: 100,
    opacity: 1,
    zIndex: 4
}, {
    width: 250,
    height: 200,
    top: 20,
    left: 250,
    opacity: 0.9,
    zIndex: 3
}]
for (var i = 0; i < json.length; i++) {
    bufferAnimation(allLis[i], json[i]);
}
for (var j = 0; j < cltChild.length; j++) {
    cltChild[j].onclick = function() {

        if (this.className === 'box29-clt1-prev') {

            json.push(json.shift());

        } else if (this.className === 'box29-clt1-next') {

            json.unshift(json.pop());

        }
        for (var i = 0; i < json.length; i++) {

            bufferAnimation(allLis[i], json[i]);
        }

    }
}





function a() {

    var box29Main = $$('box29Main');
    var box29Ul = $$('box29MainUl');
    var allLis = box29Ul.children;
    var box29_clt = $$('box29_clt');
    var cltChild = box29_clt.children;
    box29Main.onmouseover = function() {
        // body...
        bufferAnimation(box29_clt, {
            'opacity': 1
        })
    }
    box29Main.onmouseout = function() {
        // body...
        bufferAnimation(box29_clt, {
            'opacity': 0
        })
    }
    for (var i = 0; i < json.length; i++) {
        bufferAnimation(allLis[i], json[i]);
    }
    for (var j = 0; j < cltChild.length; j++) {
        cltChild[j].onclick = function() {
            if (this.className === 'box29-clt-prev') {

                $$('shuming').innerHTML = '啦啦啦'
                json.push(json.shift());
            } else if (this.className === 'box29-clt-next') {
                json.unshift(json.pop());
                $$('shuming').innerHTML = 'xixii'
            }
            for (var i = 0; i < json.length; i++) {
                bufferAnimation(allLis[i], json[i]);
            }
            // console.log(json);
        }
    }
    box29Main.onmouseover = function() {
        // body...
        bufferAnimation(box29_clt, {
            'opacity': 1
        })
    }
    box29Main.onmouseout = function() {
        // body...
        bufferAnimation(box29_clt, {
            'opacity': 0
        })
    }
}


//box2


var box2Main1 = $$('box2Main1');
var box29Ul = $$('box2Main1Ul');
var allLis2 = box29Ul.children;
var box2_clt = $$('box2_clt');
var cltChild2 = box2_clt.children;
var brief2 = document.querySelectorAll('.brief2');

box2Main1.onmouseover = function() {
    // body...
    bufferAnimation(box2_clt, {
        'opacity': 1
    })
}
box2Main1.onmouseout = function() {
    // body...
    bufferAnimation(box2_clt, {
        'opacity': 0
    })
}

for (var i = 0; i < json.length; i++) {
    bufferAnimation(allLis2[i], json[i]);
}
for (var j = 0; j < cltChild2.length; j++) {
    cltChild2[j].onclick = function() {

        if (this.className === 'box2-clt1-prev') {

            json.push(json.shift());

        } else if (this.className === 'box2-clt1-next') {

            json.unshift(json.pop());

        }
        for (var i = 0; i < json.length; i++) {

            bufferAnimation(allLis2[i], json[i]);
        }

    }
}





function b() {

    var box29Main = $$('box29Main');
    var box29Ul = $$('box29MainUl');
    var allLis2 = box29Ul.children;
    var box2_clt = $$('box2_clt');
    var cltChild2 = box2_clt.children;
    box29Main.onmouseover = function() {
        // body...
        bufferAnimation(box2_clt, {
            'opacity': 1
        })
    }
    box29Main.onmouseout = function() {
        // body...
        bufferAnimation(box2_clt, {
            'opacity': 0
        })
    }
    for (var i = 0; i < json.length; i++) {
        bufferAnimation(allLis2[i], json[i]);
    }
    for (var j = 0; j < cltChild2.length; j++) {
        cltChild2[j].onclick = function() {
            if (this.className === 'box29-clt-prev') {
                json.push(json.shift());
            } else if (this.className === 'box29-clt-next') {
                json.unshift(json.pop());
            }
            for (var i = 0; i < json.length; i++) {
                bufferAnimation(allLis2[i], json[i]);
            }
            // console.log(json);
        }
    }
    box29Main.onmouseover = function() {
        // body...
        bufferAnimation(box2_clt, {
            'opacity': 1
        })
    }
    box29Main.onmouseout = function() {
        // body...
        bufferAnimation(box2_clt, {
            'opacity': 0
        })
    }
}


//rote3

var box3Main1 = $$('box3Main1');
var box3Ul = $$('box3Main1Ul');
var allLis3 = box3Ul.children;
var box3_clt = $$('box3_clt');
var cltChild3 = box3_clt.children;
var brief3 = document.querySelectorAll('.brief3');

box3Main1.onmouseover = function() {
    // body...
    bufferAnimation(box3_clt, {
        'opacity': 1
    })
}
box3Main1.onmouseout = function() {
    // body...
    bufferAnimation(box3_clt, {
        'opacity': 0
    })
}
for (var i = 0; i < json.length; i++) {
    bufferAnimation(allLis3[i], json[i]);
}
for (var j = 0; j < cltChild3.length; j++) {
    cltChild3[j].onclick = function() {

        if (this.className === 'box3-clt1-prev') {

            json.push(json.shift());

        } else if (this.className === 'box3-clt1-next') {

            json.unshift(json.pop());

        }
        for (var i = 0; i < json.length; i++) {

            bufferAnimation(allLis3[i], json[i]);
        }

    }
}





function c() {

    var box29Main = $$('box29Main');
    var box3Ul = $$('box29MainUl');
    var allLis3 = box3Ul.children;
    var box3_clt = $$('box3_clt');
    var cltChild3 = box3_clt.children;
    box29Main.onmouseover = function() {
        // body...
        bufferAnimation(box3_clt, {
            'opacity': 1
        })
    }
    box29Main.onmouseout = function() {
        // body...
        bufferAnimation(box3_clt, {
            'opacity': 0
        })
    }
    for (var i = 0; i < json.length; i++) {
        bufferAnimation(allLis3[i], json[i]);
    }
    for (var j = 0; j < cltChild3.length; j++) {
        cltChild3[j].onclick = function() {
            if (this.className === 'box29-clt-prev') {
                json.push(json.shift());
            } else if (this.className === 'box29-clt-next') {
                json.unshift(json.pop());
            }
            for (var i = 0; i < json.length; i++) {
                bufferAnimation(allLis3[i], json[i]);
            }
            // console.log(json);
        }
    }
    box29Main.onmouseover = function() {
        // body...
        bufferAnimation(box3_clt, {
            'opacity': 1
        })
    }
    box29Main.onmouseout = function() {
        // body...
        bufferAnimation(box3_clt, {
            'opacity': 0
        })
    }
}



var box4Main1 = $$('box4Main1');
var box4Ul = $$('box4Main1Ul');
var allLis4 = box4Ul.children;
var box4_clt = $$('box4_clt');
var cltChild4 = box4_clt.children;
var brief4 = document.querySelectorAll('.brief4');

box4Main1.onmouseover = function() {
    // body...
    bufferAnimation(box4_clt, {
        'opacity': 1
    })
}
box4Main1.onmouseout = function() {
    // body...
    bufferAnimation(box4_clt, {
        'opacity': 0
    })
}
var json = [{
    width: 250,
    height: 200,
    top: 20,
    left: 0,
    opacity: 0.9,
    zIndex: 3
}, {
    width: 350,
    height: 250,
    top: 40,
    left: 100,
    opacity: 1,
    zIndex: 4
}, {
    width: 250,
    height: 200,
    top: 20,
    left: 250,
    opacity: 0.9,
    zIndex: 3
}]
for (var i = 0; i < json.length; i++) {
    bufferAnimation(allLis4[i], json[i]);
}
for (var j = 0; j < cltChild4.length; j++) {
    cltChild4[j].onclick = function() {

        if (this.className === 'box4-clt1-prev') {

            json.push(json.shift());

        } else if (this.className === 'box4-clt1-next') {

            json.unshift(json.pop());

        }
        for (var i = 0; i < json.length; i++) {

            bufferAnimation(allLis4[i], json[i]);
        }

    }
}





function c() {

    var box29Main = $$('box29Main');
    var box4Ul = $$('box29MainUl');
    var allLis4 = box4Ul.children;
    var box4_clt = $$('box4_clt');
    var cltChild4 = box4_clt.children;
    box29Main.onmouseover = function() {
        // body...
        bufferAnimation(box4_clt, {
            'opacity': 1
        })
    }
    box29Main.onmouseout = function() {
        // body...
        bufferAnimation(box4_clt, {
            'opacity': 0
        })
    }
    for (var i = 0; i < json.length; i++) {
        bufferAnimation(allLis4[i], json[i]);
    }
    for (var j = 0; j < cltChild4.length; j++) {
        cltChild4[j].onclick = function() {
            if (this.className === 'box29-clt-prev') {
                json.push(json.shift());
            } else if (this.className === 'box29-clt-next') {
                json.unshift(json.pop());
            }
            for (var i = 0; i < json.length; i++) {
                bufferAnimation(allLis4[i], json[i]);
            }
            // console.log(json);
        }
    }
    box29Main.onmouseover = function() {
        // body...
        bufferAnimation(box4_clt, {
            'opacity': 1
        })
    }
    box29Main.onmouseout = function() {
        // body...
        bufferAnimation(box4_clt, {
            'opacity': 0
        })
    }
}


//rote5

var box3Main5 = $$('box3Main5');
var box5Ul = $$('box3Main5Ul');
var allLis5 = box5Ul.children;
var box5_clt = $$('box5_clt');
var cltChild5 = box5_clt.children;
var brief5 = document.querySelectorAll('.brief5');

box3Main5.onmouseover = function() {
    // body...
    bufferAnimation(box5_clt, {
        'opacity': 1
    })
}
box3Main5.onmouseout = function() {
    // body...
    bufferAnimation(box5_clt, {
        'opacity': 0
    })
}
var json = [{
    width: 250,
    height: 200,
    top: 20,
    left: 0,
    opacity: 0.9,
    zIndex: 3
}, {
    width: 350,
    height: 250,
    top: 40,
    left: 100,
    opacity: 1,
    zIndex: 4
}, {
    width: 250,
    height: 200,
    top: 20,
    left: 250,
    opacity: 0.9,
    zIndex: 3
}]
for (var i = 0; i < json.length; i++) {
    bufferAnimation(allLis5[i], json[i]);
}
for (var j = 0; j < cltChild5.length; j++) {
    cltChild5[j].onclick = function() {

        if (this.className === 'box5-clt1-prev') {

            json.push(json.shift());

        } else if (this.className === 'box5-clt1-next') {

            json.unshift(json.pop());

        }
        for (var i = 0; i < json.length; i++) {

            bufferAnimation(allLis5[i], json[i]);
        }

    }
}





function c() {

    var box29Main = $$('box29Main');
    var box5Ul = $$('box29MainUl');
    var allLis5 = box5Ul.children;
    var box5_clt = $$('box5_clt');
    var cltChild5 = box5_clt.children;
    box29Main.onmouseover = function() {
        // body...
        bufferAnimation(box5_clt, {
            'opacity': 1
        })
    }
    box29Main.onmouseout = function() {
        // body...
        bufferAnimation(box5_clt, {
            'opacity': 0
        })
    }
    for (var i = 0; i < json.length; i++) {
        bufferAnimation(allLis5[i], json[i]);
    }
    for (var j = 0; j < cltChild5.length; j++) {
        cltChild5[j].onclick = function() {
            if (this.className === 'box29-clt-prev') {
                json.push(json.shift());
            } else if (this.className === 'box29-clt-next') {
                json.unshift(json.pop());
            }
            for (var i = 0; i < json.length; i++) {
                bufferAnimation(allLis5[i], json[i]);
            }
            // console.log(json);
        }
    }
    box29Main.onmouseover = function() {
        // body...
        bufferAnimation(box5_clt, {
            'opacity': 1
        })
    }
    box29Main.onmouseout = function() {
        // body...
        bufferAnimation(box5_clt, {
            'opacity': 0
        })
    }
}


var box3Main6 = $$('box3Main6');
var box6Ul = $$('box3Main6Ul');
var allLis6 = box6Ul.children;
var box6_clt = $$('box6_clt');
var cltChild6 = box6_clt.children;
var brief6 = document.querySelectorAll('.brief6');

box3Main6.onmouseover = function() {
    // body...
    bufferAnimation(box6_clt, {
        'opacity': 1
    })
}
box3Main6.onmouseout = function() {
    // body...
    bufferAnimation(box6_clt, {
        'opacity': 0
    })
}
var json = [{
    width: 250,
    height: 200,
    top: 20,
    left: 0,
    opacity: 0.9,
    zIndex: 3
}, {
    width: 350,
    height: 250,
    top: 40,
    left: 100,
    opacity: 1,
    zIndex: 4
}, {
    width: 250,
    height: 200,
    top: 20,
    left: 250,
    opacity: 0.9,
    zIndex: 3
}]
for (var i = 0; i < json.length; i++) {
    bufferAnimation(allLis6[i], json[i]);
}
for (var j = 0; j < cltChild6.length; j++) {
    cltChild6[j].onclick = function() {

        if (this.className === 'box6-clt1-prev') {

            json.push(json.shift());

        } else if (this.className === 'box6-clt1-next') {

            json.unshift(json.pop());

        }
        for (var i = 0; i < json.length; i++) {

            bufferAnimation(allLis6[i], json[i]);
        }

    }
}





function c() {

    var box29Main = $$('box29Main');
    var box6Ul = $$('box29MainUl');
    var allLis6 = box6Ul.children;
    var box6_clt = $$('box6_clt');
    var cltChild6 = box6_clt.children;
    box29Main.onmouseover = function() {
        // body...
        bufferAnimation(box6_clt, {
            'opacity': 1
        })
    }
    box29Main.onmouseout = function() {
        // body...
        bufferAnimation(box6_clt, {
            'opacity': 0
        })
    }
    for (var i = 0; i < json.length; i++) {
        bufferAnimation(allLis6[i], json[i]);
    }
    for (var j = 0; j < cltChild6.length; j++) {
        cltChild6[j].onclick = function() {
            if (this.className === 'box29-clt-prev') {
                json.push(json.shift());
            } else if (this.className === 'box29-clt-next') {
                json.unshift(json.pop());
            }
            for (var i = 0; i < json.length; i++) {
                bufferAnimation(allLis6[i], json[i]);
            }
            // console.log(json);
        }
    }
    box29Main.onmouseover = function() {
        // body...
        bufferAnimation(box6_clt, {
            'opacity': 1
        })
    }
    box29Main.onmouseout = function() {
        // body...
        bufferAnimation(box6_clt, {
            'opacity': 0
        })
    }
}