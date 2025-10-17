<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://aspectran.com/tags" prefix="aspectran" %>
<!DOCTYPE html>
<html class="no-js" lang="en">
<head>
    <meta charset="utf-8">
    <script>
        (() => {
            const getStoredTheme = () => localStorage.getItem('theme');
            const getPreferredTheme = () => {
                const storedTheme = getStoredTheme();
                if (storedTheme) {
                    return storedTheme;
                }
                return 'auto';
            };
            const setTheme = theme => {
                const newTheme = theme === 'auto'
                    ? (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light')
                    : theme;
                document.documentElement.setAttribute('data-bs-theme', newTheme);
            };
            setTheme(getPreferredTheme());
        })();
    </script>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="google" content="notranslate">
    <title>${empty page.title ? "JPetStore Demo" : page.title}</title>
    <meta name="description" content="${empty page.description ? "Welcome to the Aspectran Demo Site" : page.description}">
    <link rel="mask-icon" href="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/aspectran-logo.svg" color="#4B555A">
    <link rel="apple-touch-icon" sizes="57x57" href="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/apple-icon-57x57.png">
    <link rel="apple-touch-icon" sizes="60x60" href="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/apple-icon-60x60.png">
    <link rel="apple-touch-icon" sizes="72x72" href="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/apple-icon-72x72.png">
    <link rel="apple-touch-icon" sizes="76x76" href="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/apple-icon-76x76.png">
    <link rel="apple-touch-icon" sizes="114x114" href="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/apple-icon-114x114.png">
    <link rel="apple-touch-icon" sizes="120x120" href="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/apple-icon-120x120.png">
    <link rel="apple-touch-icon" sizes="144x144" href="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/apple-icon-144x144.png">
    <link rel="apple-touch-icon" sizes="152x152" href="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/apple-icon-152x152.png">
    <link rel="apple-touch-icon" sizes="180x180" href="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/apple-icon-180x180.png">
    <link rel="icon" type="image/png" sizes="192x192"  href="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/android-icon-192x192.png">
    <link rel="icon" type="image/png" sizes="16x16" href="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/favicon-16x16.png">
    <link rel="icon" type="image/png" sizes="32x32" href="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="96x96" href="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/favicon-96x96.png">
    <meta name="msapplication-TileImage" content="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/ms-icon-144x144.png">
    <meta name="msapplication-TileColor" content="#4B555A">
    <link rel="stylesheet" type="text/css" href="<aspectran:token type='bean' expression='cdnAssets^url'/>/bootstrap@5.3.8/css/aspectran.css?v=20251017"/>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,400;0,700;1,400&display=swap">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css" integrity="sha256-pdY4ejLKO67E0CM2tbPtq1DJ3VGDVVdqAR6j3ZwdiE4=" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js" integrity="sha384-FKyoEForCGlyvwx9Hj09JcYn3nv7wiPVlz7YYwJrWVcXK/BmnVDxM+D2scQbITxI" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/htmx.org@2.0.7/dist/htmx.min.js" integrity="sha256-YCMa5rqds4JesVomESLV9VkhxNU7Zr9jfcGLTuJ8efk=" crossorigin="anonymous"></script>
    <script src="<aspectran:token type='bean' expression='cdnAssets^url'/>/js/navigation.js?v=20250923"></script>
    <script src="<aspectran:token type='bean' expression='cdnAssets^url'/>/js/theme-toggler.js?v=20251005"></script>
</head>
<body id="top-of-page" class="plate solid console" itemscope itemtype="https://schema.org/WebPage">
<nav id="navigation" class="navbar navbar-expand-lg" data-bs-theme="dark">
    <div class="title-bar">
        <div class="title-bar-left">
            <aspectran:profile expression="prod">
                <a class="navbar-brand logo" href="<aspectran:url value="https://public.aspectran.com/"/>" title="Aspectran"><img src="https://assets.aspectran.com/img/aspectran-site-logo.png" alt="Aspectran"/></a>
            </aspectran:profile>
            <aspectran:profile expression="!prod">
                <a class="navbar-brand logo" href="<aspectran:url value="/../"/>" title="Aspectran"><img src="https://assets.aspectran.com/img/aspectran-site-logo.png" alt="Aspectran"/></a>
            </aspectran:profile>        </div>
        <div class="title-bar-center">
            <a href="#top-of-page">Aspectran</a>
        </div>
        <div class="title-bar-right">
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
        </div>
    </div>
    <div class="top-bar collapse navbar-collapse" id="navbarSupportedContent">
        <div class="container d-lg-flex g-0 g-lg-4">
            <div class="top-bar-logo">
                <div class="circle">
                    <aspectran:profile expression="prod">
                        <a class="navbar-brand logo" href="<aspectran:url value="https://public.aspectran.com/"/>" title="Aspectran"><img src="https://assets.aspectran.com/img/aspectran-site-logo.png" alt="Aspectran"/></a>
                    </aspectran:profile>
                    <aspectran:profile expression="!prod">
                        <a class="navbar-brand logo" href="<aspectran:url value="/../"/>" title="Aspectran"><img src="https://assets.aspectran.com/img/aspectran-site-logo.png" alt="Aspectran"/></a>
                    </aspectran:profile>
                </div>
            </div>
            <div class="top-bar-left me-auto">
                <ul class="navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" role="button" data-bs-toggle="dropdown" aria-expanded="false"
                           title="Sample applications built on Aspectran">Sample Apps</a>
                        <ul class="dropdown-menu">
                            <aspectran:profile expression="prod">
                                <li><a class="dropdown-item" href="https://jpetstore.aspectran.com">JPetStore Demo</a></li>
                                <li><a class="dropdown-item" href="https://petclinic.aspectran.com">PetClinic Demo</a></li>
                                <li><a class="dropdown-item" href="https://demo.aspectran.com/">Aspectran Demo</a></li>
                            </aspectran:profile>
                            <aspectran:profile expression="!prod">
                                <li><a class="dropdown-item" href="<aspectran:url value="/../jpetstore/"/>">JPetStore Demo</a></li>
                                <li><a class="dropdown-item" href="<aspectran:url value="/../petclinic/"/>">PetClinic Demo</a></li>
                                <li><a class="dropdown-item" href="<aspectran:url value="/../demo/"/>">Aspectran Demo</a></li>
                            </aspectran:profile>
                        </ul>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link">Get Involved</a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="https://github.com/aspectran/aspectran-jpetstore">GitHub</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
            <div class="top-bar-right d-lg-flex align-items-center gap-3">
                <ul class="navbar-nav">
                    <aspectran:profile expression="prod">
                        <li class="nav-item"><a class="nav-link" href="https://public.aspectran.com/monitoring/#jpetstore" target="_blank">Monitoring</a></li>
                    </aspectran:profile>
                    <aspectran:profile expression="!prod">
                        <li class="nav-item"><a class="nav-link" href="<aspectran:url value="/../monitoring/#jpetstore"/>" target="_blank">Monitoring</a></li>
                    </aspectran:profile>
                </ul>
                <div class="quick-search-box m-2 mx-md-3 m-lg-0">
                    <form name="google_quick_search" role="search">
                        <div class="input-group">
                            <input class="form-control" type="text" name="keyword" placeholder="Search" aria-label="Search" aria-describedby="top-bar-quick-search-btn">
                            <button class="btn btn-outline-primary text-white" type="button" id="top-bar-quick-search-btn"><i class="bi bi-search"></i></button>
                        </div>
                    </form>
                </div>
                <div class="settings d-flex align-items-center justify-content-end gap-2 m-2 mx-md-3 m-lg-0">
                    <div class="theme-toggler dropdown">
                        <button class="btn btn-primary dropdown-toggle" type="button" id="theme-toggler-btn" data-bs-toggle="dropdown" aria-expanded="false" aria-label="Toggle theme">
                            <i class="bi theme-icon-active"></i>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="theme-toggler-btn">
                            <li>
                                <button type="button" class="dropdown-item d-flex align-items-center" data-bs-theme-value="light">
                                    <i class="bi bi-sun-fill me-2 opacity-50"></i>
                                    Light
                                    <i class="bi bi-check2 ms-auto d-none"></i>
                                </button>
                            </li>
                            <li>
                                <button type="button" class="dropdown-item d-flex align-items-center" data-bs-theme-value="dark">
                                    <i class="bi bi-moon-stars-fill me-2 opacity-50"></i>
                                    Dark
                                    <i class="bi bi-check2 ms-auto d-none"></i>
                                </button>
                            </li>
                            <li>
                                <button type="button" class="dropdown-item d-flex align-items-center" data-bs-theme-value="auto">
                                    <i class="bi bi-circle-half me-2 opacity-50"></i>
                                    Auto
                                    <i class="bi bi-check2 ms-auto d-none"></i>
                                </button>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
</nav>
<section itemscope itemtype="https://schema.org/Article">
    <div id="masthead">
        <div class="container">
            <header>
                <p class="subheadline" itemprop="alternativeHeadline">
                    A full-stack sample web application built on top of Aspectran and MyBatis</p>
                <h1 itemprop="headline">JPetStore Demo</h1>
                <p class="teaser" itemprop="description">
                    The goal of the JPetStore Demo App is to provide
                    and demonstrate a sample web application that leverages Aspectran and MyBatis.
                </p>
            </header>
            <div class="hexagons">
                <div class="hexagon hex1"></div>
                <div class="hexagon hex2"></div>
                <div class="hexagon hex3"></div>
                <div class="hexagon hex5"></div>
                <div class="hexagon hex6"></div>
            </div>
        </div>
        <c:if test="${not empty page.headinclude}">
            <jsp:include page="/WEB-INF/jsp/${page.headinclude}.jsp"/>
        </c:if>
        <c:if test="${not empty page.headimageinclude}">
            <jsp:include page="/WEB-INF/jsp/${page.headimageinclude}.jsp"/>
        </c:if>
        <div class="container breadcrumb-bar">
            <nav role="navigation" aria-label="You are here:">
                <ol class="breadcrumb" itemprop="breadcrumb">
                    <aspectran:profile expression="prod">
                        <li class="breadcrumb-item"><a href="<aspectran:url value="https://public.aspectran.com/"/>">Sample Apps</a></li>
                    </aspectran:profile>
                    <aspectran:profile expression="!prod">
                        <li class="breadcrumb-item"><a href="<aspectran:url value="/../"/>">Sample Apps</a></li>
                    </aspectran:profile>
                    <li class="breadcrumb-item active"><a href="<aspectran:url value="/"/>">JPetStore Demo</a></li>
                </ol>
            </nav>
        </div>
    </div>
    <div class="container page-content" id="jpetstore-content">
        <c:if test="${not empty INCLUDE_PAGE}">
            <jsp:include page="/WEB-INF/jsp/${INCLUDE_PAGE}.jsp"/>
        </c:if>
    </div>
</section>
<div class="container">
    <div id="up-to-top" class="row">
        <div class="col text-end">
            <a class="btn" href="#top-of-page"><i class="bi bi-chevron-up"></i></a>
        </div>
    </div>
</div>
<footer id="footer-content">
    <div id="footer">
        <div class="container">
            <div class="row">
                <div class="col-md-2 col-lg-1 mt-1">
                    <h5><a class="logo" href="https://aspectran.com/aspectran/" title="Aspectran"><img src="https://assets.aspectran.com/img/aspectran-logo-grey-x100.png" width="100" height="100" alt="Aspectran" title="Aspectran"/></a></h5>
                </div>
                <div class="col-md-6 col-lg-4">
                    <a href="https://aspectran.com/aspectran/"><h5>About Aspectran</h5></a>
                    <p><a href="https://aspectran.com/aspectran/">Aspectran is a lightweight, high‑performance framework for building both simple shell applications and large enterprise web services on the JVM.</a></p>
                </div>
                <div class="col-md-4 col-lg-3 offset-lg-1">
                    <h5>Get Involved</h5>
                    <ul class="list-unstyled">
                        <li class="bi bi-github"> <a href="https://github.com/aspectran" target="_blank" title="" class="external">GitHub</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    <div id="subfooter">
        <div class="container">
            <nav class="row mb-1">
                <section id="subfooter-left" class="col-md-6 credits">
                    <p>Copyright © 2018-present The Aspectran Project</p>
                </section>
                <section id="subfooter-right" class="col-md-6 social-icons text-end">
                    <%= com.aspectran.core.AboutMe.POWERED_BY_LINK %>
                </section>
            </nav>
        </div>
    </div>
</footer>
<script>
    $(function () {
        /* google search */
        $("form[name=google_quick_search]").submit(function (event) {
            window.open("https://www.google.com/search?q=" + this.keyword.value + "+site:https%3A%2F%2Faspectran.com");
            event.preventDefault();
        });
    });
</script>
<script>
    /* Creating custom :external selector */
    $.expr[':'].external = function (obj) {
        return obj.href
            && !obj.href.match(/aspectran.com\//)
            && !obj.href.match(/^javascript:/)
            && !obj.href.match(/^mailto:/)
            && (obj.hostname !== location.hostname);
    };
    $(function () {
        /* Add 'external' CSS class to all external links */
        $('a:external').addClass('external');
        /* turn target into target=_blank for elements w external class */
        $('.external').attr('target','_blank');
    })
</script>
<script>
    $(function () {
        let links = $("#navbarSupportedContent .navbar-nav a[href='" + decodeURI(location.pathname) + "']").last();
        if (links.length > 0) {
            let arr = [];
            arr.push({'name': links.text(), 'href': location.pathname});
            links.parentsUntil(".navbar-nav").each(function () {
                let a2 = $(this).find(".nav-link");
                if (a2.is("a")) {
                    let href = a2.attr("href");
                    if (href !== location.pathname) {
                        arr.push({'name': a2.text(), 'href': href || ""});
                    }
                }
            });
            arr.reverse();
            for (let i in arr) {
                console.log(i);
                let item = arr[i];
                let li = $("<li class='breadcrumb-item'></li>");
                if (i < arr.length - 1) {
                    $("<a/>").attr("href", item.href).text(item.name).appendTo(li);
                } else {
                    li.addClass("active").text(item.name);
                }
                li.appendTo(".breadcrumb");
            }
        }
        if (!$(".breadcrumb li").length) {
            $(".breadcrumb-bar").addClass("invisible");
        }
    });
</script>
</body>
</html>
