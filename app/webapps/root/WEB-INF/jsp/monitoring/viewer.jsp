<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="/assets/css/logtail-viewer.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.30.1/moment-with-locales.min.js"></script>
<script src="/assets/js/logtail-viewer.js"></script>
<div class="grid-x endpoint-box">
    <div class="cell options t10 b5">
        <ul class="layout-options">
            <li class="fi-layout tabbed on" data-columns="0"><a> Tabbed layout</a></li>
            <li class="fi-layout tiled hide-for-small-only" data-columns="2"><a> 2-column layout</a></li>
            <li class="fi-layout tiled hide-for-small-only" data-columns="3"><a> 3-column layout</a></li>
            <li class="fi-layout stacked show-for-small-only" data-columns="1"><a> Stacked layout</a></li>
        </ul>
    </div>
    <dl class="cell logtails tabs b0">
        <dd class="tabs-title"><a><span class="bullet fi-list-bullet"></span> <span class="title"> </span> <span class="indicator fi-loop"></span></a></dd>
    </dl>
    <div class="cell logtail-box">
        <div class="status-bar">
            <h4 class="ellipses"></h4>
            <a href="#" class="tailing-switch" title="Scroll to End of Log">
                <span class="tailing-status"></span>
            </a>
            <a href="#" class="clear-screen" title="Clear screen">
                <span class="icon fi-x"></span>
            </a>
            <a href="#" class="pause-switch" title="Pause log output">
                <span class="icon fi-pause"></span>
            </a>
        </div>
        <div class="missile-track" style="display: none">
            <div class="stack"></div>
        </div>
        <pre class="logtail"></pre>
        <div class="grid-x measurement-box" style="display: none">
            <div class="cell small-12 large-4 stats-cell">
                <div class="panel stats-box">
                    <dl class="stats">
                        <dt>Current Active Sessions</dt>
                        <dd><span class="number activeSessionCount">0</span></dd>
                        <dt>Current Inactive Sessions</dt>
                        <dd><span class="number evictedSessionCount">0</span></dd>
                        <dt>Highest Active Sessions</dt>
                        <dd><span class="number highestActiveSessionCount">0</span></dd>
                        <dt title="Number of sessions created since system bootup">Created Sessions</dt>
                        <dd><span class="number createdSessionCount">0</span></dd>
                        <dt>Expired Sessions</dt>
                        <dd><span class="number expiredSessionCount">0</span></dd>
                        <dt>Rejected Sessions</dt>
                        <dd><span class="number rejectedSessionCount">0</span></dd>
                    </dl>
                    <p class="text-right"><i>Elapsed <span class="elapsed"></span></i></p>
                </div>
            </div>
            <div class="cell small-12 large-8 sessions-cell">
                <div class="panel sessions-box">
                    <ul class="sessions">
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    const endpoints = [];

    $(function() {
        const endpoint = "${page.endpoint}";
        $.ajax({
            url: "/monitoring/endpoints/${page.token}",
            type: 'get',
            dataType: "json",
            success: function(data) {
                if (data) {
                    for (let key in data) {
                        if (!endpoint || endpoint === data[key].name) {
                            endpoints.push(data[key]);
                        }
                    }
                    for (let index = 0; index < endpoints.length; index++) {
                        establishEndpoint(index);
                    }
                }
            }
        });
    });

    function establishEndpoint(endpointIndex) {
        console.log('endpointIndex', endpointIndex);
        function onEndpointEstablished(endpoint, tailers) {
            console.log('endpoint', endpoint);
            console.log('tailers', tailers);
            let endpointBox = drawLogtailsTabs(endpoint, tailers);
            endpointBox.find(".logtail-box.available").each(function() {
                let logtail = $(this).find(".logtail");
                let logtailIndex = logtail.data("logtail-index");
                let logtailName = logtail.data("logtail-name");
                let logtailBox = logtail.closest(".logtail-box");

                endpoint.viewer.setLogtail(logtailName, logtail);

                let missileTrack = logtailBox.find(".missile-track.available");
                if (missileTrack.length > 0) {
                    endpoint.viewer.setMissileTrack(logtailName, missileTrack);
                }

                let indicator1 = $(".endpoints.tabs .tabs-title.available .indicator").eq(endpointIndex);
                let indicator2 = $(".logtails.tabs .tabs-title.available .indicator").eq(logtailIndex);
                let indicator3 = logtailBox.find(".status-bar");
                endpoint.viewer.setIndicators(logtailName, [indicator1, indicator2, indicator3]);

                let measurement = $(this).find(".measurement-box");
                endpoint.viewer.setMeasurement(logtailName, measurement);

                logtail.data("tailing", true);
                logtailBox.find(".tailing-status").addClass("on");
            });
        }
        function onEstablishCompleted() {
            if (endpointIndex < endpoints.length - 1) {
                establishEndpoint(++endpointIndex);
            } else {
                initializeTabs();
            }
        }
        let logViewer = new LogtailViewer(endpoints[endpointIndex], onEndpointEstablished, onEstablishCompleted);
        try {
            logViewer.openSocket();
        } catch (e) {
            logViewer.printErrorMessage("Socket connection failed");
        }
    }

    function initializeTabs() {
        $(".endpoints.tabs .tabs-title.available").removeClass("is-active").eq(0).addClass("is-active");
        $(".endpoint-box.available").hide().eq(0).show();
        $(".endpoints.tabs .tabs-title.available").each(function() {
            let endpointIndex = $(this).data("index");
            let endpointBox = $(".endpoint-box.available").eq(endpointIndex);
            endpointBox.find(".logtails.tabs .tabs-title.available").removeClass("is-active").eq(0).addClass("is-active");
            endpointBox.find(".logtail-box.available").hide().eq(0).show();
        });
        $(".endpoints.tabs .tabs-title.available a").click(function() {
            $(".endpoints.tabs .tabs-title").removeClass("is-active");
            let tab = $(this).closest(".tabs-title");
            console.log($(this).closest(".endpoints").html());
            let endpointIndex = tab.data("index");
            tab.addClass("is-active");
            $(".endpoint-box.available").hide().eq(endpointIndex).show();
            let logtails = endpoints[endpointIndex].viewer.getLogtails();
            for (let key in logtails) {
                let logtail = logtails[key];
                if (!logtail.data("pause")) {
                    endpoints[endpointIndex].viewer.refresh(logtail);
                }
            }
        });
        $(".logtails.tabs .tabs-title.available a").click(function() {
            let endpointBox = $(this).closest(".endpoint-box");
            let endpointIndex = endpointBox.data("index");
            let logtailTab = $(this).closest(".tabs-title");
            let logtailIndex = logtailTab.data("index");
            if (!logtailTab.hasClass("is-active")) {
                endpointBox.find(".logtails.tabs .tabs-title").removeClass("is-active");
                logtailTab.addClass("is-active");
                let logtailBox = endpointBox.find(".logtail-box.available").hide().eq(logtailIndex).show();
                let logtail = logtailBox.find(".logtail");
                if (!logtail.data("pause")) {
                    endpoints[endpointIndex].viewer.refresh(logtail);
                }
            }
        });
        $(".logtails.tabs .tabs-title.available a").dblclick(function(event) {
            let endpointBox = $(this).closest(".endpoint-box");
            endpointBox.find(".logtails.tabs .tabs-title").removeClass("is-active");
            $(this).click();
            event.preventDefault();
        });
        $(".logtail-box .tailing-switch").click(function() {
            let logtail = $(this).closest(".logtail-box").find(".logtail");
            let endpointIndex = logtail.data("endpoint-index");
            let endpoint = endpoints[endpointIndex];
            if (logtail.data("tailing")) {
                logtail.data("tailing", false);
                $(this).find(".tailing-status").removeClass("on");
            } else {
                logtail.data("tailing", true);
                $(this).find(".tailing-status").addClass("on");
                endpoint.viewer.refresh(logtail);
            }
        });
        $(".logtail-box .pause-switch").click(function() {
            let logtail = $(this).closest(".logtail-box").find(".logtail");
            if (logtail.data("pause")) {
                logtail.data("pause", false);
                $(this).removeClass("on");
            } else {
                logtail.data("pause", true);
                $(this).addClass("on");
            }
        });
        $(".logtail-box .clear-screen").click(function() {
            let logtail = $(this).closest(".logtail-box").find(".logtail");
            let endpointIndex = logtail.data("endpoint-index");
            let endpoint = endpoints[endpointIndex];
            endpoint.viewer.clear(logtail);
        });

        $(".layout-options li a").click(function() {
            $(".layout-options li").removeClass("on");
            $(this).parent().addClass("on");
            let endpointBox = $(this).closest(".endpoint-box");
            let logtailBox = endpointBox.find(".logtail-box");
            let measurementBox = endpointBox.find(".measurement-box");
            let columns = $(this).parent().data("columns");
            switch (columns) {
                case 1:
                    endpointBox.addClass("tiled");
                    logtailBox.removeClass("large-3 large-4 large-6");
                    break;
                case 2:
                    endpointBox.addClass("tiled");
                    logtailBox.removeClass("large-3 large-4 large-6").addClass("large-6");
                    break;
                case 3:
                    endpointBox.addClass("tiled");
                    logtailBox.removeClass("large-3 large-4 large-6").addClass("large-4");
                    break;
                default:
                    endpointBox.removeClass("tiled");
                    logtailBox.removeClass("large-3 large-4 large-6");
                    break;
            }
            if (columns > 1) {
                measurementBox.find(".stats-cell").removeClass("large-4 large-8");
                measurementBox.find(".sessions-cell").removeClass("large-4 large-8");
            } else {
                measurementBox.find(".stats-cell").removeClass("large-8 large-8").addClass("large-4");
                measurementBox.find(".sessions-cell").removeClass("large-8 large-8").addClass("large-8");
            }
        });
    }

    function drawLogtailsTabs(endpoint, tailers) {
        let endpointBox = addEndpointsTab(endpoint);
        for (let key in tailers) {
            let tailer = tailers[key];
            addLogtailsTab(endpointBox, tailer);
        }
        return endpointBox;
    }

    function addEndpointsTab(endpoint) {
        let tabs = $(".endpoints.tabs");
        let tab0 = tabs.find(".tabs-title").eq(0);
        let index = tabs.find(".tabs-title").length - 1;
        let tab = tab0.hide().clone();
        tab.addClass("available");
        tab.attr("data-index", index);
        tab.attr("data-name", endpoint.name);
        tab.attr("data-title", endpoint.title);
        tab.attr("data-endpoint", endpoint.url);
        let a = tab.find("a");
        a.find(".title").text(" " + endpoint.title + " ");
        tab.show().appendTo(tabs);
        let content = $(".endpoint-box").eq(0).hide().clone();
        content.addClass("available");
        content.attr("data-index", index).attr("data-name", endpoint.name).attr("data-title", endpoint.title);
        content.insertAfter($(".endpoint-box").last());
        return content;
    }

    function addLogtailsTab(endpointBox, logtail) {
        let endpointIndex = endpointBox.data("index");
        let endpointTitle = endpointBox.data("title");
        let tabs = endpointBox.find(".logtails.tabs");
        let tab0 = tabs.find(".tabs-title").eq(0);
        let index = tabs.find(".tabs-title").length - 1;
        let tab = tab0.hide().clone()
            .addClass("available")
            .attr("data-index", index)
            .attr("title", endpointTitle + " ›› " + logtail.title);
        tab.find("a .title").text(" " + logtail.title + " ");
        tab.show().appendTo(tabs);
        let logtailBox = endpointBox.find(".logtail-box").eq(0).hide().clone();
        logtailBox.addClass("available");
        logtailBox.attr("data-index", index).attr("data-name", logtail.name);
        logtailBox.find(".status-bar h4").text(endpointTitle + " ›› " + logtail.file);
        logtailBox.find(".logtail")
            .attr("data-endpoint-index", endpointIndex).attr("data-endpoint-name", endpointTitle)
            .attr("data-logtail-index", index).attr("data-logtail-name", logtail.name);
        logtailBox.insertAfter($(".logtail-box").last());
        if (logtail.visualizing) {
            logtailBox.addClass("with-track");
            logtailBox.find(".missile-track")
                .addClass("available")
                .attr("data-visualizing", logtail.visualizing)
                .show();
        }
        if (logtail.measurement && logtail.measuring !== false) {
            logtailBox.find(".measurement-box").show();
        }
        return logtailBox.show();
    }
</script>
