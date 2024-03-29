<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="/assets/css/monitoring/monitor.css?20240315">
<script src="/assets/js/monitoring/logtail-viewer.js?20240315"></script>
<script src="/assets/js/monitoring/monitor.js?20240315"></script>
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
        <dd class="tabs-title"><a><span class="bullet fi-list-bullet"></span>
            <span class="title"> </span> <span class="indicator fi-loop"></span></a></dd>
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
    $(function() {
        const endpoint = "${page.endpoint}";
        $.ajax({
            url: "/monitoring/endpoints/${page.token}",
            type: 'get',
            dataType: "json",
            success: function(data) {
                if (data) {
                    const endpoints = [];
                    for (let key in data) {
                        if (!endpoint || endpoint === data[key].name) {
                            endpoints.push(data[key]);
                        }
                    }
                    new Monitor(endpoints).establish();
                }
            }
        });
    });
</script>
