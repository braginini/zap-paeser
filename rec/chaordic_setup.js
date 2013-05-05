(function () {
    var c = function (e) {
        (typeof console != "undefined") && console.debug(e)
    }, d = function (e, f) {
        return Object.prototype.hasOwnProperty.call(e, f)
    }, b = function (g, f) {
        for (var e in f) {
            if (d(f, e) && !d(g, e)) {
                g[e] = f[e]
            }
        }
        return g
    }, a;
    if (window.chaordic) {
        c("[WARN] Chaordic loader already present.");
        return
    }
    window.chaordic = { setup: function (f) {
        f = b({}, f);
        if (!f.API_KEY) {
            c('[ERROR] No apiKey present. Please define the "API_KEY" property');
            return
        }
        if (!f.ENVIRONMENT) {
            c('[ERROR] No environment present. Please define the "ENVIRONMENT" property')
        }
        chaordic.Config = f;
        var j = { PROTOCOL: ("https:" == location.protocol) ? "https://" : "http://", ENVIRONMENT: "HOMOLOGATION",
                DYNAMIC_CONTEXT: "/raas/", STATIC_CONTEXT: "/static/" + f.API_KEY + "/",
                VERSION: "0-0-0R0" }, i = { PRODUCTION: { DYNAMIC_HOST: f.API_KEY + "-raas.chaordicsystems.com",
                STATIC_HOST: "static.chaordicsystems.com" }, HOMOLOGATION: { DYNAMIC_HOST: "mirror-raas.chaordicsystems.com",
                STATIC_HOST: "mirror-raas.chaordicsystems.com", LOG: true }, DEVELOPMENT: { DYNAMIC_HOST: location.host,
                STATIC_HOST: location.hostname, LOG: true }, RESEARCH: { DYNAMIC_HOST: location.host, STATIC_HOST: location.hostname,
                SHOW_INSPECTION_DATA: true, LOG: true} }, g = document.getElementsByTagName("body")[0] || document.documentElement,
            h = new Date().getTime(), e = document.createElement("script");
        if (a) {
            c("[WARN] chaordic.setup() already called.");
            return
        }
        a = true;
        b(f, j);
        if (!d(i, f.ENVIRONMENT)) {
            c('[WARN] Unknown environment "' + f.ENVIROMNENT + '", using default: ' + j.ENVIRONMENT);
            f.ENVIRONMENT = j.ENVIRONMENT
        }
        b(f, i[f.ENVIRONMENT]);
        e.setAttribute("charset", "UTF-8");
        e.src = f.PROTOCOL + f.STATIC_HOST + f.STATIC_CONTEXT + "current.js?t=" + h;
        g.insertBefore(e, g.firstChild)
    } }
})();

var config = {
    API_KEY: "extra",
    ENVIRONMENT: "PRODUCTION"
};
chaordic.setup(config);


if (!chaordic.Config.CURRENT_LOADED) {
    (function (b, g) {
        var f, h = function (p) {
            p += "=";
            var o = document.cookie.split(";");
            for (var q = 0; q < o.length; q++) {
                var r = o[q];
                while (r.charAt(0) == " ") {
                    r = r.substring(1, r.length)
                }
                if (r.indexOf(p) == 0) {
                    return r.substring(p.length, r.length)
                }
            }
            return null
        }, d = function (o, i) {
            return o.getElementsByTagName(i)[0] || o.documentElement
        }, m = "cs", c = (function (i) {
            if ((function (o) {
                return o && o[1]
            })(i.match(/MSIE\s([^;]*)/))) {
                return function (p, q, o) {
                    o = function (r) {
                        if ("loaded" == r || "complete" == r) {
                            q()
                        }
                    };
                    o(p.readyState);
                    p.onreadystatechange = function () {
                        o(this.readyState)
                    }
                }
            }
            if (/KHTML/.test(i)) {
                return function (o, p) {
                    o.addEventListener("load", p, false)
                }
            }
            return function (o, p) {
                o.onload = p
            }
        })(navigator.userAgent), l = function (i, r) {
            var q = document, o = d(q, "head"), p = q.createElement("script");
            c(p, function () {
                p.parentNode.removeChild(p);
                r()
            });
            p.setAttribute("charset", "UTF-8");
            p.src = i;
            o.insertBefore(p, o.firstChild);
            return p
        }, e = function (s) {
            if (h(m)) {
                return s()
            }
            var r = g.PROTOCOL + g.DYNAMIC_HOST + g.DYNAMIC_CONTEXT + "/pages/cookie?apiKey=" + g.API_KEY;
            if (navigator.vendor && navigator.vendor.indexOf("Apple") !== -1) {
                var p = document.createElement("div"), o = d(document, "body"), i = "chaordic.session", q = document.createElement("iframe");
                q.id = q.name = i;
                p.style.display = "none";
                o.insertBefore(p, o.firstChild);
                p.appendChild(q);
                q.onload = function () {
                    document.cookie = m + "=1; path=/";
                    p.parentNode.removeChild(p);
                    s()
                };
                setTimeout(function () {
                    var u = document.createElement("form");
                    u.target = i;
                    p.appendChild(u);
                    u.method = "POST";
                    u.target = i;
                    u.action = r;
                    var t = document.createElement("input");
                    t.type = "text";
                    t.name = "apiKey";
                    t.value = g.API_KEY;
                    u.appendChild(t);
                    u.submit()
                }, 0);
                return
            }
            l(r, s)
        }, a = function () {
            var i = [], p = [], o = false, q = function (u, t, r, s) {
                t = i.length;
                r = 0;
                s = function () {
                    l(i[r++], function () {
                        if (r < t) {
                            s()
                        } else {
                            u()
                        }
                    })
                };
                s()
            };
            this.load = function (r) {
                i.push(r);
                return this
            };
            this.run = function (r) {
                if (o) {
                    k(r)
                } else {
                    p.push(r)
                }
                return this
            };
            this.go = function () {
                if (o) {
                    return
                }
                var t, s = false, r = false, u = function () {
                    if (!s || !r) {
                        return
                    }
                    for (t = 0; p[t]; t++) {
                        k(p[t])
                    }
                    o = true
                };
                q(function () {
                    s = true;
                    u()
                });
                e(function () {
                    r = true;
                    u()
                })
            }
        }, k = function (i) {
            chaordic.Base.requires("chaordic.listener.Listener").requires("chaordic.widget.Widget").requires("chaordic.widget.BestSellers").requires("chaordic.widget.Featured").requires("chaordic.widget.WeddingList").requires("chaordic.widget.FrequentlyBought").requires("chaordic.widget.Personalized").requires("chaordic.widget.PurchasePersonalized").requires("chaordic.widget.Rec4You").requires("chaordic.widget.ShoppingCart").requires("chaordic.widget.SimilarItems").requires("chaordic.widget.UltimateBuy").requires("chaordic.widget.AlternateBuy").requires("chaordic.widget.ViewPersonalized").run(i)
        }, j = function (i) {
            n.run(i)
        }, n = new a;
        if (window._csLoader) {
            for (f = 0; _csLoader[f]; f++) {
                j(_csLoader[f])
            }
        }
        _csLoader = {push: function () {
            for (f = 0; arguments[f]; f++) {
                j(arguments[f])
            }
        }};
        a.getLoader = function () {
            return n
        };
        b.Loader = a
    })(chaordic, chaordic.Config);
    (function (c, b, a, d) {
        if (typeof bridgehead == "string") {
            c.STATIC_HOST = bridgehead
        }
        c.STATIC_API = "novapontocom_17_8089/";
        while (a[d]) {
            b.load(c.PROTOCOL + c.STATIC_HOST + c.STATIC_CONTEXT + c.STATIC_API + a[d++])
        }
        b.run(function () {
            var f = chaordic.commons.Location.getParams(location);
            if (f.cs_mail) {
                var e = String(location.hostname).replace(/([^\.]+.)?([^\.]+\.com\.br)/, "$2");
                document.cookie = "cs_mail=" + f.cs_mail + "; path=/; domain=" + e
            }
        });
        b.go()
    })(chaordic.Config, chaordic.Loader.getLoader(), ["CS.js"], 0);
    chaordic.Config.CURRENT_LOADED = true
}
;


if (!chaordic.Config.CURRENT_LOADED) {
    (function (b, g) {
        var f, h = function (p) {
            p += "=";
            var o = document.cookie.split(";");
            for (var q = 0; q < o.length; q++) {
                var r = o[q];
                while (r.charAt(0) == " ") {
                    r = r.substring(1, r.length)
                }
                if (r.indexOf(p) == 0) {
                    return r.substring(p.length, r.length)
                }
            }
            return null
        }, d = function (o, i) {
            return o.getElementsByTagName(i)[0] || o.documentElement
        }, m = "cs", c = (function (i) {
            if ((function (o) {
                return o && o[1]
            })(i.match(/MSIE\s([^;]*)/))) {
                return function (p, q, o) {
                    o = function (r) {
                        if ("loaded" == r || "complete" == r) {
                            q()
                        }
                    };
                    o(p.readyState);
                    p.onreadystatechange = function () {
                        o(this.readyState)
                    }
                }
            }
            if (/KHTML/.test(i)) {
                return function (o, p) {
                    o.addEventListener("load", p, false)
                }
            }
            return function (o, p) {
                o.onload = p
            }
        })(navigator.userAgent), l = function (i, r) {
            var q = document, o = d(q, "head"), p = q.createElement("script");
            c(p, function () {
                p.parentNode.removeChild(p);
                r()
            });
            p.setAttribute("charset", "UTF-8");
            p.src = i;
            o.insertBefore(p, o.firstChild);
            return p
        }, e = function (s) {
            if (h(m)) {
                return s()
            }
            var r = g.PROTOCOL + g.DYNAMIC_HOST + g.DYNAMIC_CONTEXT + "/pages/cookie?apiKey=" + g.API_KEY;
            if (navigator.vendor && navigator.vendor.indexOf("Apple") !== -1) {
                var p = document.createElement("div"), o = d(document, "body"), i = "chaordic.session", q = document.createElement("iframe");
                q.id = q.name = i;
                p.style.display = "none";
                o.insertBefore(p, o.firstChild);
                p.appendChild(q);
                q.onload = function () {
                    document.cookie = m + "=1; path=/";
                    p.parentNode.removeChild(p);
                    s()
                };
                setTimeout(function () {
                    var u = document.createElement("form");
                    u.target = i;
                    p.appendChild(u);
                    u.method = "POST";
                    u.target = i;
                    u.action = r;
                    var t = document.createElement("input");
                    t.type = "text";
                    t.name = "apiKey";
                    t.value = g.API_KEY;
                    u.appendChild(t);
                    u.submit()
                }, 0);
                return
            }
            l(r, s)
        }, a = function () {
            var i = [], p = [], o = false, q = function (u, t, r, s) {
                t = i.length;
                r = 0;
                s = function () {
                    l(i[r++], function () {
                        if (r < t) {
                            s()
                        } else {
                            u()
                        }
                    })
                };
                s()
            };
            this.load = function (r) {
                i.push(r);
                return this
            };
            this.run = function (r) {
                if (o) {
                    k(r)
                } else {
                    p.push(r)
                }
                return this
            };
            this.go = function () {
                if (o) {
                    return
                }
                var t, s = false, r = false, u = function () {
                    if (!s || !r) {
                        return
                    }
                    for (t = 0; p[t]; t++) {
                        k(p[t])
                    }
                    o = true
                };
                q(function () {
                    s = true;
                    u()
                });
                e(function () {
                    r = true;
                    u()
                })
            }
        }, k = function (i) {
            chaordic.Base.requires("chaordic.listener.Listener").requires("chaordic.widget.Widget").requires("chaordic.widget.BestSellers").requires("chaordic.widget.Featured").requires("chaordic.widget.WeddingList").requires("chaordic.widget.FrequentlyBought").requires("chaordic.widget.Personalized").requires("chaordic.widget.PurchasePersonalized").requires("chaordic.widget.Rec4You").requires("chaordic.widget.ShoppingCart").requires("chaordic.widget.SimilarItems").requires("chaordic.widget.UltimateBuy").requires("chaordic.widget.AlternateBuy").requires("chaordic.widget.ViewPersonalized").run(i)
        }, j = function (i) {
            n.run(i)
        }, n = new a;
        if (window._csLoader) {
            for (f = 0; _csLoader[f]; f++) {
                j(_csLoader[f])
            }
        }
        _csLoader = {push: function () {
            for (f = 0; arguments[f]; f++) {
                j(arguments[f])
            }
        }};
        a.getLoader = function () {
            return n
        };
        b.Loader = a
    })(chaordic, chaordic.Config);
    (function (c, b, a, d) {
        if (typeof bridgehead == "string") {
            c.STATIC_HOST = bridgehead
        }
        c.STATIC_API = "novapontocom_17_8089/";
        while (a[d]) {
            b.load(c.PROTOCOL + c.STATIC_HOST + c.STATIC_CONTEXT + c.STATIC_API + a[d++])
        }
        b.run(function () {
            var f = chaordic.commons.Location.getParams(location);
            if (f.cs_mail) {
                var e = String(location.hostname).replace(/([^\.]+.)?([^\.]+\.com\.br)/, "$2");
                document.cookie = "cs_mail=" + f.cs_mail + "; path=/; domain=" + e
            }
        });
        b.go()
    })(chaordic.Config, chaordic.Loader.getLoader(), ["CS.js"], 0);
    chaordic.Config.CURRENT_LOADED = true
}
;
