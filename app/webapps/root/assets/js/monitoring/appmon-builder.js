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
            let endpointBox = addEndpoint(endpoint);
            for (let key in payload.groups) {
                let group = payload.groups[key];
                addGroup(endpointBox, group);
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
                let missileTrack = logtailBox.find(".missile-track.available");
                if (missileTrack.length > 0) {
                    endpoint.client.setMissileTrack(logtailName, missileTrack);
                }
                let indicator1 = $(".endpoint.tabs .tabs-title.available .indicator").eq(endpointIndex);
                let indicator2 = $(".group.tabs .tabs-title.available .indicator").eq(logtailIndex);
                let indicator3 = logtailBox.find(".status-bar");
                endpoint.client.setIndicators(logtailName, [indicator1, indicator2, indicator3]);
                logtail.data("tailing", true);
                logtailBox.find(".tailing-status").addClass("on");
            });
        }
        function onEstablishCompleted() {
            if (endpointIndex < endpoints.length - 1) {
                establishEndpoint(++endpointIndex);
            } else if (endpointIndex === endpoints.length - 1) {
                build();
                if (location.hash) {
                    let name = location.hash.substring(1);
                    $(".endpoint-box[data-index=" + endpointIndex + "] .tabs-title.available").each(function () {
                        if ($(this).data("name") === name) {
                            $(this).addClass("is-active");
                        } else {
                            $(this).removeClass("is-active");
                        }
                    });
                    let endpointBox = $(".endpoint-box.available").eq(endpointIndex);
                    changeGroup(endpointBox, name);
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

    const changeGroup = function (endpointBox, groupName) {
        let endpointIndex = endpointBox.data("index");
        endpointBox.find(".group-box").hide();
        let groupBox = endpointBox.find(".group-box[data-name=" + groupName + "]").show();
        groupBox.find(".logtail-box.available .logtail").each(function () {
            let logtail = $(this);
            if (!logtail.data("pause")) {
                endpoints[endpointIndex].client.refresh(logtail);
            }
        });
    }

    const build = function () {
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
            let endpointBox = $(this).closest(".endpoint-box");
            let logtailBox = endpointBox.find(".logtail-box.available");
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
                    li.addClass("on vertical");
                    endpointBox.addClass("compact vertical");
                }
            } else {
                if (li.hasClass("compact")) {
                    if (li.hasClass("vertical")) {
                        li.removeClass("vertical").addClass("horizontal");
                        endpointBox.removeClass("vertical").addClass("horizontal");
                        logtailBox.addClass("large-6");
                    } else if (li.hasClass("horizontal")) {
                        li.removeClass("on horizontal");
                        endpointBox.removeClass("compact horizontal");
                        logtailBox.removeClass("large-6");
                    }
                }
            }
            let endpointIndex = endpointBox.data("index");
            endpointBox.find(".logtail-box.available").each(function () {
                if ($(this).find(".tailing-status").hasClass("on")) {
                    endpoints[endpointIndex].client.refresh();
                }
            });
        });
    }

    const addEndpoint = function (endpoint) {
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

    const addGroup = function (endpointBox, group) {
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
        let groupBox = endpointBox.find(".group-box").eq(0).hide().clone();
        groupBox.addClass("available")
            .attr("data-name", group.name)
            .attr("data-title", group.title);
        return groupBox.appendTo(endpointBox);
    }

    const addLogtail = function (endpointBox, logtail) {
        let endpointIndex = endpointBox.data("index");
        let endpointTitle = endpointBox.data("title");
        let groupBox = endpointBox.find(".group-box[data-name=" + logtail.group + "]");
        let logtailBox = groupBox.find(".logtail-box").eq(0).hide().clone();
        logtailBox.addClass("available");
        logtailBox.attr("data-group", logtail.group);
        logtailBox.attr("data-name", logtail.name);
        logtailBox.find(".status-bar h4").text(endpointTitle + " ›› " + logtail.file);
        logtailBox.find(".logtail")
            .attr("data-endpoint-index", endpointIndex).attr("data-endpoint-name", endpointTitle)
            .attr("data-logtail-name", logtail.name);
        if (logtail.visualizing) {
            logtailBox.addClass("with-track");
            logtailBox.find(".missile-track")
                .addClass("available")
                .attr("data-visualizing", logtail.visualizing)
                .show();
        } else {
            logtailBox.addClass("no-track");
        }
        return logtailBox.appendTo(groupBox.find(".logtail-box-wrap")).show();
    }

    const addMeasurement = function (endpointBox, measurement) {
        let groupBox = endpointBox.find(".group-box[data-name=" + measurement.group + "]");
        let measurementBox = groupBox.find(".measurement-box").eq(0).hide().clone();
        measurementBox.addClass("available")
            .attr("data-group", measurement.group)
            .attr("data-name", measurement.name);
        return measurementBox.appendTo(groupBox).show();
    }
}
