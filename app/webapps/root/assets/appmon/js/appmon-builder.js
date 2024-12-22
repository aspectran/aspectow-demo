function AppMonBuilder() {

    const endpoints = [];

    this.build = function (basePath, token, currentEndpoint) {
        $.ajax({
            url: basePath + "appmon/monitoring/endpoints/" + token,
            type: 'get',
            dataType: "json",
            success: function (data) {
                if (data) {
                    endpoints.length = 0;
                    let index = 0;
                    for (let key in data) {
                        let endpoint = data[key];
                        endpoint['index'] = index++;
                        endpoint['basePath'] = basePath;
                        if (!currentEndpoint || currentEndpoint === endpoint.name) {
                            endpoints.push(endpoint);
                        }
                    }
                    if (endpoints.length) {
                        establishEndpoint(0);
                    }
                }
            }
        });
    };

    const establishEndpoint = function (endpointIndex) {
        console.log('endpointIndex', endpointIndex);
        function onEndpointJoined(endpoint, payload) {
            console.log('endpoint', endpoint);
            buildEndpoint(endpoint, payload);
        }
        function onEstablishCompleted(endpoint, payload) {
            if (endpointIndex < endpoints.length - 1) {
                establishEndpoint(endpointIndex + 1);
            } else if (endpointIndex === endpoints.length - 1) {
                buildGroups();
                for (let key in payload.messages) {
                    let msg = payload.messages[key];
                    endpoint.viewer.processMessage(endpoint, msg);
                }
                if (endpoints.length) {
                    changeEndpoint(0);
                    if (location.hash) {
                        let name = location.hash.substring(1);
                        let endpointBox = $(".endpoint-box.available").eq(0);
                        endpointBox.find(".tabs-title.available").each(function () {
                            if ($(this).data("name") === name) {
                                $(this).addClass("is-active");
                            } else {
                                $(this).removeClass("is-active");
                            }
                        });
                        changeGroup(endpointBox, name);
                    }
                }
            }
        }
        function onErrorObserved(endpoint) {
            setTimeout(function () {
                if (endpoint.index === 1) {
                    clearScreen();
                }
                let client = new AppmonPollingClient(endpoint, onEndpointJoined, onEstablishCompleted);
                endpoint['client'] = client;
                client.start();
            }, (endpoint.index - 1) * 1000);
        }

        if (endpointIndex === 0) {
            clearScreen();
        }

        let endpoint = endpoints[endpointIndex];
        endpoint['viewer'] = new AppmonViewer();
        let client = new AppmonWebsocketClient(endpoint, onEndpointJoined, onEstablishCompleted, onErrorObserved);
        endpoint['client'] = client;
        client.start();
    };

    const clearScreen = function () {
        $(".endpoint.tabs .tabs-title.available").remove();
        $(".endpoint.tabs .tabs-title").show();
        $(".endpoint-box.available").remove();
        $(".endpoint-box").show();
    };

    const changeEndpoint = function (endpointIndex) {
        for (let key in endpoints) {
            endpoints[key].viewer.setVisible(false);
        }
        $(".endpoint-box.available").hide().eq(endpointIndex).show();
        endpoints[endpointIndex].viewer.setVisible(true);
        endpoints[endpointIndex].viewer.refresh();
    };

    const changeGroup = function (endpointBox, groupName) {
        let endpointIndex = endpointBox.data("index");
        endpointBox.find(".group-box").hide();
        let groupBox = endpointBox.find(".group-box[data-name=" + groupName + "]").show();
        groupBox.find(".log-box.available .log").each(function () {
            let log = $(this);
            if (!log.data("pause")) {
                endpoints[endpointIndex].viewer.refresh(log);
            }
        });
    };

    const buildEndpoint = function (endpoint, payload) {
        let endpointBox = addEndpoint(endpoint);
        let indicatorEndpoint = $(".endpoint.tabs .tabs-title.available .indicator").eq(endpoint.index);
        endpoint.viewer.putIndicator("endpoint", "event", endpoint.index, indicatorEndpoint);
        for (let key in payload.groups) {
            let groupInfo = payload.groups[key];
            addGroup(endpointBox, groupInfo);
            let indicatorGroup = endpointBox.find(".group.tabs .tabs-title.available[data-name=" + groupInfo.name + "]").find(".indicator");
            endpoint.viewer.putIndicator("group", "event", groupInfo.name, indicatorGroup);
            for (let key in groupInfo.events) {
                let eventInfo = groupInfo.events[key];
                let trackBox = addTrackBox(endpointBox, eventInfo);
                let reqNum = trackBox.find(".req-num");
                endpoint.viewer.putTrack(groupInfo.name, eventInfo.name, trackBox);
                endpoint.viewer.putIndicator(groupInfo.name, "event", eventInfo.name, reqNum);
            }
            for (let key in groupInfo.logs) {
                let logInfo = groupInfo.logs[key];
                let logBox = addLogBox(endpointBox, logInfo);
                let log = logBox.find(".log");
                log.data("tailing", true);
                logBox.find(".tailing-status").addClass("on");
                endpoint.viewer.putLogBox(groupInfo.name, logInfo.name, log);
                let indicatorLog = logBox.find(".status-bar");
                endpoint.viewer.putIndicator(groupInfo.name, "log", logInfo.name, indicatorLog);
            }
            for (let key in groupInfo.states) {
                let stateInfo = groupInfo.states[key];
                let stateBox = addStateBox(endpointBox, stateInfo);
                endpoint.viewer.putStateBox(groupInfo.name, stateInfo.name, stateBox);
            }
        }
        if (endpoint.mode === "polling") {
            $("ul.speed-options").show();
        }
    };

    const buildGroups = function () {
        $(".endpoint.tabs .tabs-title.available").eq(0).addClass("is-active");
        $(".endpoint.tabs .tabs-title.available a").click(function() {
            $(".endpoint.tabs .tabs-title").removeClass("is-active");
            let tab = $(this).closest(".tabs-title");
            tab.addClass("is-active");
            let endpointIndex = tab.data("index");
            changeEndpoint(endpointIndex);
        });
        $(".endpoint-box.available .tabs .tabs-title.available").each(function() {
            let endpointIndex = $(this).data("index");
            let endpointBox = $(".endpoint-box.available").eq(endpointIndex);
            let groupTab = endpointBox.find(".group.tabs .tabs-title.available").eq(0);
            let groupName = groupTab.addClass("is-active").data("name");
            changeGroup(endpointBox, groupName);
        });
        $(".group.tabs .tabs-title.available a").click(function() {
            let groupTab = $(this).closest(".tabs-title");
            let groupName = groupTab.data("name");
            $(".endpoint-box.available").each(function () {
                let endpointBox2 = $(this);
                $(endpointBox2).find(".group.tabs .tabs-title.available").each(function () {
                    let groupTab = $(this);
                    if (groupTab.data("name") === groupName) {
                        if (!groupTab.hasClass("is-active")) {
                            groupTab.addClass("is-active");
                            changeGroup(endpointBox2, groupName);
                        }
                    } else {
                        groupTab.removeClass("is-active");
                    }
                });
            });
        });
        $(".log-box .tailing-switch").click(function() {
            let log = $(this).closest(".log-box").find(".log");
            let endpointIndex = log.data("endpoint-index");
            let endpoint = endpoints[endpointIndex];
            if (log.data("tailing")) {
                log.data("tailing", false);
                $(this).find(".tailing-status").removeClass("on");
            } else {
                log.data("tailing", true);
                $(this).find(".tailing-status").addClass("on");
                endpoint.viewer.refresh(log);
            }
        });
        $(".log-box .pause-switch").click(function() {
            let log = $(this).closest(".log-box").find(".log");
            if (log.data("pause")) {
                log.data("pause", false);
                $(this).removeClass("on");
            } else {
                log.data("pause", true);
                $(this).addClass("on");
            }
        });
        $(".log-box .clear-screen").click(function() {
            let log = $(this).closest(".log-box").find(".log");
            let endpointIndex = log.data("endpoint-index");
            let endpoint = endpoints[endpointIndex];
            endpoint.viewer.clear(log);
        });
        $(".layout-options li a").click(function() {
            let endpointBox = $(this).closest(".endpoint-box");
            let logBox = endpointBox.find(".log-box.available");
            let liStacked = $(".layout-options li.stacked");
            let liTabbed = $(".layout-options li.tabbed");
            let li = $(this).parent();
            if (!li.hasClass("on")) {
                if (li.hasClass("tabbed")) {
                    liTabbed.addClass("on");
                    liStacked.removeClass("on");
                    endpointBox.removeClass("stacked");
                } else if (li.hasClass("stacked")) {
                    liTabbed.removeClass("on");
                    liStacked.addClass("on");
                    endpointBox.addClass("stacked");
                } else if (li.hasClass("compact")) {
                    li.addClass("on");
                    endpointBox.addClass("compact");
                    logBox.addClass("large-6");
                }
            } else {
                if (li.hasClass("compact")) {
                    li.removeClass("on");
                    endpointBox.removeClass("compact");
                    logBox.removeClass("large-6");
                }
            }
            let endpointIndex = endpointBox.data("index");
            endpointBox.find(".log-box.available").each(function () {
                if ($(this).find(".tailing-status").hasClass("on")) {
                    endpoints[endpointIndex].viewer.refresh();
                }
            });
        });
        $(".speed-options li").click(function() {
            let endpointBox = $(this).closest(".endpoint-box");
            let endpointIndex = endpointBox.data("index");
            let liFast = $(".speed-options li.fast");
            if (liFast.hasClass("on")) {
                liFast.removeClass("on");
                endpoints[endpointIndex].client.speed(0);
            } else {
                liFast.addClass("on");
                endpoints[endpointIndex].client.speed(1);
            }
        });
    };

    const addEndpoint = function (endpointInfo) {
        let tabs = $(".endpoint.tabs");
        let tab0 = tabs.find(".tabs-title").eq(0);
        let tab = tab0.hide().clone();
        tab.addClass("available");
        tab.attr("data-index", endpointInfo.index);
        tab.attr("data-name", endpointInfo.name);
        tab.attr("data-title", endpointInfo.title);
        tab.attr("data-endpoint", endpointInfo.url);
        let a = tab.find("a");
        a.find(".title").text(" " + endpointInfo.title + " ");
        tab.show().appendTo(tabs);
        let endpointBox = $(".endpoint-box");
        return endpointBox.eq(0).hide().clone()
            .addClass("available")
            .attr("data-index", endpointInfo.index)
            .attr("data-name", endpointInfo.name)
            .attr("data-title", endpointInfo.title)
            .insertAfter(endpointBox.last()).show();
    };

    const addGroup = function (endpointBox, groupInfo) {
        let endpointTitle = endpointBox.data("title");
        let tabs = endpointBox.find(".group.tabs");
        let tab0 = tabs.find(".tabs-title").eq(0);
        let index = tabs.find(".tabs-title").length - 1;
        let tab = tab0.hide().clone()
            .addClass("available")
            .attr("data-index", index)
            .attr("data-name", groupInfo.name)
            .attr("title", endpointTitle + " ›› " + groupInfo.title);
        tab.find("a .title").text(" " + groupInfo.title + " ");
        tab.show().appendTo(tabs);
        let groupBox = endpointBox.find(".group-box").eq(0).hide().clone();
        groupBox.addClass("available")
            .attr("data-name", groupInfo.name)
            .attr("data-title", groupInfo.title);
        return groupBox.appendTo(endpointBox);
    };

    const addTrackBox = function (endpointBox, eventInfo) {
        let groupBox = endpointBox.find(".group-box[data-name=" + eventInfo.group + "]");
        let trackBox = groupBox.find(".track-box").eq(0).hide().clone();
        trackBox.addClass("available")
            .attr("data-group", eventInfo.group)
            .attr("data-name", eventInfo.name);
        return trackBox.appendTo(groupBox.find("> .grid-x")).show();
    };

    const addLogBox = function (endpointBox, logInfo) {
        let endpointIndex = endpointBox.data("index");
        let endpointTitle = endpointBox.data("title");
        let groupBox = endpointBox.find(".group-box[data-name=" + logInfo.group + "]");
        let logBox = groupBox.find(".log-box").eq(0).hide().clone();
        logBox.addClass("large-6 available");
        logBox.attr("data-group", logInfo.group);
        logBox.attr("data-name", logInfo.name);
        logBox.find(".status-bar h4")
            .text(endpointTitle + " ›› " + logInfo.file);
        logBox.find(".log")
            .attr("data-endpoint-index", endpointIndex)
            .attr("data-endpoint-name", endpointTitle)
            .attr("data-log-name", logInfo.name);
        return logBox.appendTo(groupBox.find("> .grid-x")).show();
    };

    const addStateBox = function (endpointBox, stateInfo) {
        let groupBox = endpointBox.find(".group-box[data-name=" + stateInfo.group + "]");
        let stateBox = groupBox.find(".state-box").eq(0).hide().clone();
        stateBox.addClass("available")
            .attr("data-group", stateInfo.group)
            .attr("data-name", stateInfo.name);
        return stateBox.appendTo(groupBox.find("> .grid-x")).show();
    };
}
