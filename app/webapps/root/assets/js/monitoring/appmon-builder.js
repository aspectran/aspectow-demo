function AppMonBuilder(endpoints) {

    this.establish = function () {
        for (let index = 0; index < endpoints.length; index++) {
            establishEndpoint(index);
        }
    }

    const establishEndpoint = function (endpointIndex) {
        console.log('endpointIndex', endpointIndex);
        function onEndpointJoined(endpoint, payload) {
            console.log('endpoint', endpoint);
            console.log('payload', payload);
            let endpointBox = addEndpointTab(endpoint);
            for (let key in payload.groups) {
                let group = payload.groups[key];
                addGroupTab(endpointBox, group);
            }
            for (let key in payload.logtails) {
                let logtail = payload.logtails[key];
                let logtailBox = addLogtail(endpointBox, logtail);
                endpoint.client.setLogtail(logtail.name, logtailBox.find(".logtail"));
            }
            for (let key in payload.measurements) {
                let measurement = payload.measurements[key];
                let measurementBox = addMeasurement(endpointBox, measurement);
                endpoint.client.setMeasurement(measurement.name, measurementBox);
            }
            endpointBox.find(".logtail-box.available").each(function() {
                let logtail = $(this).find(".logtail");
                let logtailIndex = logtail.data("logtail-index");
                let logtailName = logtail.data("logtail-name");
                let logtailBox = logtail.closest(".logtail-box");

                // endpoint.client.setLogtail(logtailName, logtail);

                let missileTrack = logtailBox.find(".missile-track.available");
                if (missileTrack.length > 0) {
                    endpoint.client.setMissileTrack(logtailName, missileTrack);
                }

                let indicator1 = $(".endpoint.tabs .tabs-title.available .indicator").eq(endpointIndex);
                let indicator2 = $(".group.tabs .tabs-title.available .indicator").eq(logtailIndex);
                let indicator3 = logtailBox.find(".status-bar");
                endpoint.client.setIndicators(logtailName, [indicator1, indicator2, indicator3]);

                // let measurement = $(this).find(".measurement-box");
                // endpoint.client.setMeasurement(logtailName, measurement);

                logtail.data("tailing", true);
                logtailBox.find(".tailing-status").addClass("on");
            });
        }
        function onEstablishCompleted() {
            if (endpointIndex < endpoints.length - 1) {
                establishEndpoint(++endpointIndex);
            } else if (endpointIndex === endpoints.length - 1) {
                initializeTabs();
                if (location.hash) {
                    let name = location.hash.substring(1);
                    $(".endpoint-box[data-index=" + endpointIndex + "] .tabs-title.available").each(function (index) {
                        if ($(this).data("name") === name) {
                            $(this).addClass("is-active");
                            $(".endpoint-box[data-index=" + endpointIndex + "] .logtail-box.available").eq(index).show();
                        } else {
                            $(this).removeClass("is-active");
                            $(".endpoint-box[data-index=" + endpointIndex + "] .logtail-box.available").eq(index).hide();
                        }
                    });
                }
            }
        }
        let client = new AppmonClient(endpoints[endpointIndex], onEndpointJoined, onEstablishCompleted);
        try {
            client.openSocket();
        } catch (e) {
            client.printErrorMessage("Socket connection failed");
        }
    }

    const initializeTabs = function () {
        $(".endpoint-box.available").hide().eq(0).show();
        $(".endpoint.tabs .tabs-title.available").each(function() {
            let endpointIndex = $(this).data("index");
            let endpointBox = $(".endpoint-box.available").eq(endpointIndex);
            let groupTab = endpointBox.find(".group.tabs .tabs-title.available").eq(0);
            let groupName = groupTab.addClass("is-active").data("name");
            changeGroup(endpointBox, groupName);
        }).eq(0).addClass("is-active");
        $(".endpoint.tabs .tabs-title.available a").click(function() {
            $(".endpoint.tabs .tabs-title").removeClass("is-active");
            let tab = $(this).closest(".tabs-title");
            let endpointIndex = tab.data("index");
            tab.addClass("is-active");
            $(".endpoint-box.available").hide().eq(endpointIndex).show();
            let logtails = endpoints[endpointIndex].client.getLogtails();
            for (let key in logtails) {
                let logtail = logtails[key];
                if (!logtail.data("pause")) {
                    endpoints[endpointIndex].client.refresh(logtail);
                }
            }
        });
        $(".group.tabs .tabs-title.available a").click(function() {
            let endpointBox = $(this).closest(".endpoint-box");
            let groupTab = $(this).closest(".tabs-title");
            let groupName = groupTab.data("name");
            if (!groupTab.hasClass("is-active")) {
                endpointBox.find(".group.tabs .tabs-title").removeClass("is-active");
                groupTab.addClass("is-active");
                changeGroup(endpointBox, groupName);
            }
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
                endpoint.client.refresh(logtail);
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
            endpoint.client.clear(logtail);
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
            let endpointIndex = endpointBox.data("index");
            endpointBox.find(".logtail-box.available").each(function (index) {
                if ($(this).find(".tailing-status").hasClass("on")) {
                    endpoints[endpointIndex].client.refresh();
                }
            });
        });
    }

    const changeGroup = function (endpointBox, groupName) {
        console.log('groupName', groupName);
        let endpointIndex = endpointBox.data("index");
        endpointBox.find(".logtail-box.available,.measurement-box.available").hide().each(function () {
            if ($(this).data("group") === groupName) {
                $(this).show();
            }
            let logtail = $(this).find(".logtail");
            if (!logtail.data("pause")) {
                endpoints[endpointIndex].client.refresh(logtail);
            }
        });
    }

    const addEndpointTab = function (endpoint) {
        let tabs = $(".endpoint.tabs");
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
        let endpointBox = $(".endpoint-box").eq(0).hide().clone();
        endpointBox.addClass("available")
            .attr("data-index", index)
            .attr("data-name", endpoint.name)
            .attr("data-title", endpoint.title)
            .insertAfter($(".endpoint-box").last());
        return endpointBox.show();
    }

    const addGroupTab = function (endpointBox, group) {
        let endpointTitle = endpointBox.data("title");
        let tabs = endpointBox.find(".group.tabs");
        let tab0 = tabs.find(".tabs-title").eq(0);
        let index = tabs.find(".tabs-title").length - 1;
        let tab = tab0.hide().clone()
            .addClass("available")
            .attr("data-index", index)
            .attr("data-name", group.name)
            .attr("title", endpointTitle + " ›› " + group.title);
        tab.find("a .title").text(" " + group.title + " ");
        tab.show().appendTo(tabs);
    }

    const addLogtail = function (endpointBox, logtail) {
        let endpointIndex = endpointBox.data("index");
        let endpointTitle = endpointBox.data("title");
        let logtailBox = endpointBox.find(".logtail-box").eq(0).hide().clone();
        logtailBox.addClass("available");
        logtailBox.attr("data-group", logtail.group);
        logtailBox.attr("data-name", logtail.name);
        logtailBox.find(".status-bar h4").text(endpointTitle + " ›› " + logtail.file);
        logtailBox.find(".logtail")
            .attr("data-endpoint-index", endpointIndex).attr("data-endpoint-name", endpointTitle)
            .attr("data-logtail-name", logtail.name);
        logtailBox.insertAfter($(".logtail-box").last());
        if (logtail.visualizing) {
            logtailBox.addClass("with-track");
            logtailBox.find(".missile-track")
                .addClass("available")
                .attr("data-visualizing", logtail.visualizing)
                .show();
        }
        return logtailBox;
    }

    const addMeasurement = function (endpointBox, measurement) {
        let measurementBox = endpointBox.find(".measurement-box").eq(0).hide().clone();
        measurementBox.addClass("available")
            .attr("data-group", measurement.group)
            .attr("data-name", measurement.name)
            .insertAfter($(".measurement-box").last());
        return measurementBox;
    }
}
