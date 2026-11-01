"use strict";

function _instanceof(left, right) { if (right != null && typeof Symbol !== "undefined" && right[Symbol.hasInstance]) { return !!right[Symbol.hasInstance](left); } else { return left instanceof right; } }

function _classCallCheck(instance, Constructor) { if (!_instanceof(instance, Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

function _classPrivateFieldGet(receiver, privateMap) { var descriptor = privateMap.get(receiver); if (!descriptor) { throw new TypeError("attempted to get private field on non-instance"); } if (descriptor.get) { return descriptor.get.call(receiver); } return descriptor.value; }

var _loading = new WeakMap();

var _notify = new WeakMap();

var _statusAnimate = new WeakMap();

var _confirm = new WeakMap();

var _dialog = new WeakMap();

/**
 * Utils
 * @class
 */
var Utils = /*#__PURE__*/function () {
    function Utils() {
        var _this = this;

        _classCallCheck(this, Utils);

        _defineProperty(this, "appendDom", function (el) {
            var dom = parent.document.createElement(el.name);
            document.getElementsByTagName(el.position)[0].appendChild(dom);
            dom.innerHTML = el.content;
        });

        _loading.set(this, {
            writable: true,
            value: function value() {
                var that = _this;
                init();

                _this.loading = function () {
                    var message = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : '';
                    var autoClose = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
                    var closeTime = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : 3000;
                    showLoading(message, autoClose, closeTime);
                };

                _this.loadingEnd = function () {
                    $('.portal-loading').removeClass('active');
                };

                function init() {
                    var dom = "\n                <div class=\"portal-loading\">\n                    <div class=\"loading-content\">\n                        <div class=\"loader\">\n                            <svg class=\"circular\" viewBox=\"25 25 50 50\">\n                                <circle class=\"path\" cx=\"50\" cy=\"50\" r=\"20\" fill=\"none\" stroke-width=\"2\" stroke-miterlimit=\"10\"/>\n                            </svg>\n                        </div>\n                        <p class=\"loading-text\"></p>\n                    </div>\n                </div>\n            ";
                    that.appendDom({
                        position: 'body',
                        name: 'div',
                        content: dom
                    });
                }

                function showLoading(message, autoClose, closeTime) {
                    $('.portal-loading .loading-text').html(message);
                    $('.portal-loading').addClass('active');
                    if (autoClose) autoClose(closeTime);
                }

                function autoClose(closeTime) {
                    setTimeout(function () {
                        $('.portal-loading').removeClass('active');
                    }, closeTime);
                }
            }
        });

        _notify.set(this, {
            writable: true,
            value: function value(conf) {
                var that = _this;
                _this.notify = {
                    success: function success(message) {
                        return create('success', 'checkmark', message);
                    },
                    warning: function warning(message) {
                        return create('warning', 'warning', message);
                    },
                    error: function error(message) {
                        return create('error', 'close', message);
                    },
                    info: function info(message) {
                        return create('info', 'information', message);
                    }
                };

                function create(type, icon, message) {
                    var element = parent.document.createElement('div');
                    $(element).addClass('portal-notify').addClass(type);
                    $('body').append(element);
                    element.innerHTML = "\n                <i class=\"icon ion-md-".concat(icon, "\"></i>\n                <span class=\"content\">").concat(message, "</span>\n            ");
                    append(element);
                }

                function append(element) {
                    $('body').append(element);
                    setTimeout(function () {
                        $(element).addClass('active');
                    }, 10);
                    setTimeout(function () {
                        remove(element);
                    }, 3000);
                }

                function remove(element) {
                    $(element).removeClass('active');
                    setTimeout(function () {
                        $(element).remove();
                    }, 1000);
                }
            }
        });

        _statusAnimate.set(this, {
            writable: true,
            value: function value(conf) {
                var that = _this;

                _this.statusAnimate = function (type, message, size, color, time) {
                    if (type === 'success') createSuccess(message, size, color, time);
                    if (type === 'error') createError(message, size, color, time);
                };

                function createSuccess() {
                    var message = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : '';
                    var size = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 80;
                    var color = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : '#67C23A';
                    var time = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : 3000;
                    var dom = "\n                <div class=\"portal-animate-dialog\">\n                    <div class=\"portal-animate portal-animate-success\">\n                        <svg width=\"".concat(size, "\" height=\"").concat(size, "\">\n                            <circle class=\"circle\"\n                                    fill=\"none\"\n                                    transform=\"rotate(-90 ").concat(size / 2, " ").concat(size / 2, ")\"\n                                    stroke=\"").concat(color, "\"\n                                    stroke-width=\"").concat(size / 20, "\"\n                                    stroke-linecap=\"round\"\n                                    cx=\"").concat(size / 2, "\"\n                                    cy=\"").concat(size / 2, "\"\n                                    r=\"").concat((size - size / 20) / 2, "\"/>\n                            <polyline class=\"tick\"\n                                      fill=\"none\"\n                                      stroke=\"").concat(color, "\"\n                                      stroke-width=\"").concat(size / 20 + size / 40, "\"\n                                      points=\"").concat(0.22 * size, ",").concat(0.535 * size, "\n                                              ").concat(0.4325 * size, ",").concat(0.71 * size, "\n                                              ").concat(0.76 * size, ",").concat(0.345 * size, "\"\n                                      stroke-linecap=\"round\"\n                                      stroke-linejoin=\"round\"/>\n                        </svg>\n                        <h3 class=\"title\">").concat(message, "</h3>\n                    </div>\n                </div>\n            ");
                    appendDom(dom, time);
                }

                function createError() {
                    var message = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : '';
                    var size = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 80;
                    var color = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : '#F56C6C';
                    var time = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : 3000;
                    var dom = "\n                <div class=\"portal-animate-dialog\">\n                    <div class=\"portal-animate portal-animate-error\">\n                        <svg width=\"".concat(size, "\" height=\"").concat(size, "\">\n                            <circle class=\"circle\"\n                                    fill=\"none\"\n                                    transform=\"rotate(-90 ").concat(size / 2, " ").concat(size / 2, ")\"\n                                    stroke=\"").concat(color, "\"\n                                    stroke-width=\"").concat(size / 20, "\"\n                                    stroke-linecap=\"round\"\n                                    cx=\"").concat(size / 2, "\"\n                                    cy=\"").concat(size / 2, "\"\n                                    r=\"").concat((size - size / 20) / 2, "\"/>\n                            <polyline class=\"cross left\"\n                                      fill=\"none\"\n                                      stroke=\"").concat(color, "\"\n                                      stroke-width=\"").concat(size / 20 + size / 40, "\"\n                                      points=\"").concat(size * 0.35, ",").concat(size * 0.35, "\n                                              ").concat(size * 0.65, ",").concat(size * 0.65, "\"\n                                      stroke-linecap=\"round\"\n                                      stroke-linejoin=\"round\"/>\n                            <polyline class=\"cross right\"\n                                      fill=\"none\"\n                                      stroke=\"").concat(color, "\"\n                                      stroke-width=\"").concat(size / 20 + size / 40, "\"\n                                      points=\"").concat(size * 0.65, ",").concat(size * 0.35, "\n                                              ").concat(size * 0.35, ",").concat(size * 0.65, "\"\n                                      stroke-linecap=\"round\"\n                                      stroke-linejoin=\"round\"/>\n                        </svg>\n                        <h3 class=\"title\">").concat(message, "</h3>\n                    </div>\n                </div>\n            ");
                    appendDom(dom, time);
                }

                function appendDom(element, time) {
                    that.appendDom({
                        position: 'body',
                        name: 'div',
                        content: element
                    });
                    setTimeout(function () {
                        $('.portal-animate-dialog').addClass('active');
                    }, 10);
                    setTimeout(function () {
                        remove();
                    }, time);
                }

                function remove() {
                    $('.portal-animate-dialog').removeClass('active');
                    setTimeout(function () {
                        $('.portal-animate-dialog').remove();
                    }, 1000);
                }
            }
        });

        _confirm.set(this, {
            writable: true,
            value: function value() {
                // 绑定 this
                var that = _this; // 创建 DOM

                create(); // 绑定显示确认框方法

                _this.confirm = function (obj) {
                    if (typeof obj === 'string') obj = {
                        message: obj
                    };
                    open(obj);
                }; // 绑定键盘按下事件，监听 ESC 与 Enter


                $(window).keydown(function (event) {
                    var dialog = $('.component.confirm'); // 若 confirm 未显示则停止之后判断

                    if (!dialog.hasClass('active')) return; // Enter

                    if (event.keyCode === 13) return dialog.find('.btn-confirm').click(); // ESC

                    if (event.keyCode === 27) return close();
                }); // 显示确认框

                function open(obj) {
                    $('.component.confirm .header').html(obj.title || that.defField.notify);
                    $('.component.confirm .section').html(obj.message);
                    bindConfirm(obj);
                    bindCancel(obj);
                    $('.component.confirm').addClass('active');
                } // 关闭确认框


                function close() {
                    $('.component.confirm .btn-confirm').unbind('click');
                    $('.component.confirm .btn-cancel').unbind('click');
                    $('.component.confirm').removeClass('active');
                } // 绑定确认回调


                function bindConfirm(obj) {
                    $('.component.confirm .btn-confirm').html(obj.confirmText || that.defField.confirm).show().click(function () {
                        // 必存在确认按钮
                        if (obj.confirm) obj.confirm();
                        close();
                    });
                } // 绑定取消回调


                function bindCancel(obj) {
                    if (!obj.cancel) return $('.component.confirm .btn-cancel').hide();
                    $('.component.confirm .btn-cancel').html(obj.cancelText || that.defField.cancel).show().click(function () {
                        obj.cancel();
                        close();
                    });
                } // 创建 DOM 函数


                function create() {
                    var dom = document.createElement('div');
                    dom.innerHTML = "\n                <div class=\"component dialog confirm\">\n                    <div class=\"mask\"></div>\n                    <div class=\"content\">\n                        <div class=\"header\"></div>\n                        <div class=\"section\"></div>\n                        <div class=\"control\">\n                            <button type=\"button\" class=\"btn-confirm\"></button>\n                            <button type=\"button\" class=\"btn-cancel\"></button>\n                        </div>\n                    </div>\n                </div>\n            ";
                    document.body.appendChild(dom);
                }
            }
        });

        _dialog.set(this, {
            writable: true,
            value: function value() {
                var that = _this;
                _this.dialog = {
                    create: create,
                    open: open,
                    close: close
                };

                function create(obj) {
                    var name = obj.name,
                        title = obj.title,
                        dom = obj.dom,
                        event = obj.event,
                        confirm = obj.confirm,
                        cancel = obj.cancel,
                        confirmText = obj.confirmText,
                        cancelText = obj.cancelText;
                    var component = document.createElement('div');
                    component.innerHTML = "\n                <div class=\"component dialog promt\" data-name=\"".concat(name, "\">\n                    <div class=\"mask\"></div>\n                    <div class=\"content\">\n                        <div class=\"header\">").concat(title, "</div>\n                        <div class=\"section\">").concat(dom, "</div>\n                        <div class=\"control\">\n                            ").concat(confirm ? "<button type=\"button\" class=\"btn-confirm\">".concat(confirmText || that.defField.confirm, "</button>") : '', "\n                            ").concat(cancel ? "<button type=\"button\" class=\"btn-cancel\">".concat(cancelText || that.defField.cancel, "</button>") : '', "\n                        </div>\n                    </div>\n                </div>\n            ");
                    document.body.appendChild(component);
                    if (event) event();
                    $(component).find('.btn-confirm').click(function () {
                        if (confirm) confirm();
                    });
                    $(component).find('.btn-cancel').click(function () {
                        if (cancel) cancel();
                        close(name);
                    }); // 绑定键盘按下事件，监听 ESC 与 Enter

                    $(window).keydown(function (event) {
                        // ESC
                        if (event.keyCode === 27) {
                            if (cancel) cancel();
                            close(name);
                        }

                        ;
                    });
                }

                function open(name, callback) {
                    $(".component.promt[data-name=".concat(name, "]")).addClass('active');
                    if (callback) callback();
                }

                function close(name, callback) {
                    $(".component.promt[data-name=".concat(name, "]")).removeClass('active');
                    if (callback) callback();
                }
            }
        });

        this.defField = {
            notify: '通知',
            confirm: '确认',
            cancel: '取消',
            year: '年',
            month: '月',
            day: '日',
            hour: '小时',
            minute: '分',
            second: '秒'
        };
    }

    _createClass(Utils, [{
        key: "use",

        /**
         * 启用组件
         */
        value: function use(name) {
            if (name === 'confirm' && !this[name]) _classPrivateFieldGet(this, _confirm).call(this);
            if (name === 'loading' && !this[name]) _classPrivateFieldGet(this, _loading).call(this);
            if (name === 'dialog' && !this[name]) _classPrivateFieldGet(this, _dialog).call(this); // if (name === 'notify' && !this[name]) this.#notify();

            if (name === 'statusAnimate' && !this[name]) _classPrivateFieldGet(this, _statusAnimate).call(this);
            return this;
        }
        /**
         * 启用组件
         */

    }, {
        key: "setToolsLang",
        value: function setToolsLang(lang) {
            var _this2 = this;

            Object.keys(lang).forEach(function (key) {
                _this2.defField[key] = lang[key];
            });
        }
        /**
         * 克隆对象并解除关联
         * @method
         * @param       {Object}    obj     传入对象
         * @return    {Object}            新对象
         */

    }, {
        key: "clone",
        value: function clone(obj) {
            return this.extend(true, {}, obj);
        }
        /**
         * 获取 URL 参数值
         * @method
         * @param       {String}    name    参数名称
         * @return      {String}            参数值
         */

    }, {
        key: "getUrlParams",
        value: function getUrlParams(name) {
            var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
            var r = location.search.substr(1).match(reg);
            return r === null ? '' : unescape(r[2]);
        }
        /**
         * 流量格式化
         * @author ll@srun.com
         * @method
         * @param       {Number}    flow        传入流量数值
         * @param       {Number}    mode        传入进位方式 (默认 1024)
         * @param       {String}    unit        传入流量单位 (默认 B)
         * @param       {Number}    promotion   传入流量转换后的对比数  (默认 1024)
         * @return      {String}                格式化结果
         */

    }, {
        key: "formatFlow",
        value: function formatFlow(flow) {
            var mode = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 1024;
            var unit = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : 'B';
            var promotion = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : 1024;
            flow = Number(flow);
            if (!flow) return "0 ".concat(unit);
            var unitList = ['B', 'KB', 'MB', 'GB', 'TB'];
            var unitIndex = unitList.indexOf(unit.toUpperCase());
            mode = Number(mode);
            promotion = Number(promotion);
            flow = flow * Math.pow(mode, unitIndex);

            for (var i = 0; i < unitList.length; i += 1) {
                var min = Math.pow(mode, i);
                var max = Math.pow(mode, i + 1);

                if (min <= flow && flow < max) {
                    if (flow / min >= promotion) return (flow / min / mode).toFixed(2) + ' ' + unitList[i + 1];
                    if (flow / min < promotion) return (flow / min).toFixed(2) + ' ' + unitList[i];
                }
            }
        }
        /**
         * 日期格式化
         * @author xr@srun.com
         * @method
         * @param       {Number}    timeStamp   传入时间戳
         * @param       {String}    format      传入格式
         * @return      {String}                格式化结果
         */

    }, {
        key: "formatDate",
        value: function formatDate(timeStamp, format) {
            timeStamp = Number(timeStamp);
            if (String(timeStamp).length === 10) timeStamp = timeStamp * 1000;
            var time = new Date(timeStamp);
            var yyyy = time.getFullYear();
            var M = time.getMonth() + 1;
            var d = time.getDate();
            var H = time.getHours();
            var m = time.getMinutes();
            var s = time.getSeconds();
            var rule = [{
                key: 'yyyy',
                val: yyyy
            }, {
                key: 'yy',
                val: String(yyyy).substring(2)
            }, {
                key: 'MM',
                val: this.add0(M)
            }, {
                key: 'M',
                val: M
            }, {
                key: 'dd',
                val: this.add0(d)
            }, {
                key: 'd',
                val: d
            }, {
                key: 'HH',
                val: this.add0(H)
            }, {
                key: 'H',
                val: H
            }, {
                key: 'mm',
                val: this.add0(m)
            }, {
                key: 'm',
                val: m
            }, {
                key: 'ss',
                val: this.add0(s)
            }, {
                key: 's',
                val: s
            }];
            rule.forEach(function (item) {
                format = format.replace(new RegExp(item.key, 'g'), item.val);
            });
            return format;
        }
        /**
         * 时间长度格式化
         * @method
         * @param       {Number}    time    传入时长数值
         * @param       {String}    format  时长转化格式
         * @return      {String}            格式化结果
         */

    }, {
        key: "formatTime",
        value: function formatTime(time) {
            var format = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 'Hms';
            // 若传入时间为 0 则直接返回 0s
            if (!time) return "0 ".concat(this.defField.second); // 单位数组

            var unit = []; // 处理结果字符串

            var result = ''; // 传入时间规定为 Number 类型

            time = Number(time);
            if (format.includes('y')) unit.push({
                label: this.defField.year,
                value: 3600 * 24 * 365
            });
            if (format.includes('M')) unit.push({
                label: this.defField.month,
                value: 3600 * 24 * 30
            });
            if (format.includes('d')) unit.push({
                label: this.defField.day,
                value: 3600 * 24
            });
            if (format.includes('H')) unit.push({
                label: this.defField.hour,
                value: 3600
            });
            if (format.includes('m')) unit.push({
                label: this.defField.minute,
                value: 60
            });
            if (format.includes('s')) unit.push({
                label: this.defField.second,
                value: 1
            }); // 遍历传入的单位

            for (var i = 0; i < unit.length; i += 1) {
                var _unit$i = unit[i],
                    label = _unit$i.label,
                    value = _unit$i.value; // 若剩余时间不足单位进位值，则判断进行下一位单位

                if (time < value) continue;
                result += parseInt(String(time / value), 0) + label;
                time %= value;
            }

            return result;
        }
        /**
         * 数字字符串前拼接 0
         * @method
         * @param       {Number}    str     传入数字或字符串
         * @return      {String}            拼接结果
         */

    }, {
        key: "add0",
        value: function add0(str) {
            return Number(str) < 10 ? "0".concat(str) : str;
        }
        /**
         * 字段校验
         * @method
         * @param       {String}    value   字段 Dom
         * @param       {String}    rule    校验规则
         */

    }, {
        key: "fieldCheck",
        value: function fieldCheck(value, rule) {
            // 非空规则
            if (rule === 'blank') return Boolean(value); // >0

            if (rule === '>0') return Number(value) > 0; // >= 0

            if (rule === '>=0') return Number(value) >= 0; // 手机号 CN

            if (rule === 'phone') return /^1[3456789]\d{9}$/.test(value); // 正整数

            if (rule === 'zzs') return /^[1-9]\d*$/.test(value); // 自然数（非负整数）

            if (rule === 'zrs') return /^\d+$/.test(value);
        }
        /**
         * 设置 Cookie
         * @method
         * @param       {String}    name   Cookie 名称
         * @param       {String}    value  Cookie 值
         * @param       {Number}    day    有效期 (默认 7 天)
         */

    }, {
        key: "setCookie",
        value: function setCookie(name, value) {
            var day = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : 7;
            var date = new Date();
            date.setDate(date.getDate() + day);
            document.cookie = name + '=' + value + ';expires=' + date;
            document.cookie = "".concat(name, "=").concat(value, ";expires=").concat(date.toGMTString());
        }
    }, {
        key: "getCookie",

        /**
         * 读取 Cookie
         * @method
         * @param       {String}    name    Cookie 名称
         * @return      {String}            Cookie 值
         */
        value: function getCookie(name) {
            var reg = RegExp("".concat(name, "=([^;]+)"));
            var arr = document.cookie.match(reg);
            return arr ? arr[1] : false;
        }
    }, {
        key: "delCookie",

        /**
         * 删除 Cookie
         * @method
         * @param       {String}    name    Cookie 名称
         */
        value: function delCookie(name) {
            this.setCookie(name, '', -1);
        }
    }, {
        key: "randomStr",

        /**
         * 生成随机字符串
         * @method
         * @param       {Number}    length  字符串长度
         * @param       {Array}     list    出现范围
         * @return      {String}            生成的随机字符串
         */
        value: function randomStr(length, list) {
            if (!list) list = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
            var str = '';

            for (var i = 0; i < length; i += 1) {
                str += list[this.randomNum(0, list.length - 1)];
            }

            return str;
        }
        /**
         * 生成随机数字
         * @method
         * @param       {Number}    min     随机数字最大限制
         * @param       {Number}    max     随机数字最小限制
         * @return      {Number}            生成的随机数字
         */

    }, {
        key: "randomNum",
        value: function randomNum() {
            var min = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;
            var max = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 100;
            var r = Math.floor(Math.random() * Math.pow(10, String(max).length));
            if (r > min && r < max) return r;
            return this.randomNum(min, max);
        }
        /**
         * 生成二维码
         * @method
         * @param       {Number}    min     随机数字最大限制
         * @param       {Number}    max     随机数字最小限制
         * @return      {Number}            生成的随机数字
         */

    }, {
        key: "qrCode",
        value: function qrCode(obj) {
            obj.size = obj.size || 180;
            obj.background = obj.background || "#FFFFFF";
            obj.foreground = obj.foreground || "#4086CE";
            $(obj.el).qrcode({
                text: obj.text,
                height: obj.size,
                width: obj.size,
                // 为了支持 IE8 及以下
                render: document.createElement('canvas').getContext ? 'canvas' : 'table',
                // 背景颜色
                background: obj.background,
                // 前景颜色
                foreground: obj.foreground
            });
        }
        /**
         * Copy From Quasar Utils
         * FIXME: 内部 extend() 可能需要加上 this
         * @method
         */

    }, {
        key: "extend",
        value: function (_extend) {
            function extend() {
                return _extend.apply(this, arguments);
            }

            extend.toString = function () {
                return _extend.toString();
            };

            return extend;
        }(function () {
            var toString = Object.prototype.toString,
                hasOwn = Object.prototype.hasOwnProperty,
                class2type = {};
            'Boolean Number String Function Array Date RegExp Object'.split(' ').forEach(function (name) {
                class2type['[object ' + name + ']'] = name.toLowerCase();
            });

            function type(obj) {
                return obj === null ? String(obj) : class2type[toString.call(obj)] || 'object';
            }

            function isPlainObject(obj) {
                if (!obj || type(obj) !== 'object') {
                    return false;
                }

                if (obj.constructor && !hasOwn.call(obj, 'constructor') && !hasOwn.call(obj.constructor.prototype, 'isPrototypeOf')) {
                    return false;
                }

                var key;

                for (key in obj) {}

                return key === undefined || hasOwn.call(obj, key);
            }

            var options,
                name,
                src,
                copy,
                copyIsArray,
                clone,
                target = arguments[0] || {},
                i = 1,
                length = arguments.length,
                deep = false;

            if (typeof target === 'boolean') {
                deep = target;
                target = arguments[1] || {};
                i = 2;
            }

            if (Object(target) !== target && type(target) !== 'function') {
                target = {};
            }

            if (length === i) {
                target = this;
                i--;
            }

            for (; i < length; i++) {
                if ((options = arguments[i]) !== null) {
                    for (name in options) {
                        src = target[name];
                        copy = options[name];

                        if (target === copy) {
                            continue;
                        }

                        if (deep && copy && (isPlainObject(copy) || (copyIsArray = type(copy) === 'array'))) {
                            if (copyIsArray) {
                                copyIsArray = false;
                                clone = src && type(src) === 'array' ? src : [];
                            } else {
                                clone = src && isPlainObject(src) ? src : {};
                            }

                            target[name] = extend(deep, clone, copy);
                        } else if (copy !== undefined) {
                            target[name] = copy;
                        }
                    }
                }
            }

            return target;
        })
        /**
         * appendDom 插入 Dom
         * @el.position             {String}    插入位置    head | body
         * @el.name                 {String}    元素名称
         * @el.content              {String}    元素内容
         */

    }]);

    return Utils;
}();

_defineProperty(Utils, "version", 'v1.0.0');
