function Monitor(endpoints) {

    this.establish = function () {
        for (let index = 0; index < endpoints.length; index++) {
            establishEndpoint(index);
        }
    }

    const establishEndpoint = function (endpointIndex) {
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
        let logViewer = new LogtailViewer(endpoints[endpointIndex], onEndpointEstablished, onEstablishCompleted);
        try {
            logViewer.openSocket();
        } catch (e) {
            logViewer.printErrorMessage("Socket connection failed");
        }
    }

    const initializeTabs = function () {
        $(".endpoint-box.available").hide().eq(0).show();
        $(".endpoints.tabs .tabs-title.available").each(function() {
            let endpointIndex = $(this).data("index");
            let endpointBox = $(".endpoint-box.available").eq(endpointIndex);
            endpointBox.find(".logtails.tabs .tabs-title.available").removeClass("is-active").eq(0).addClass("is-active");
            endpointBox.find(".logtail-box.available").hide().eq(0).show();
        }).eq(0).addClass("is-active");
        $(".endpoints.tabs .tabs-title.available a").click(function() {
            $(".endpoints.tabs .tabs-title").removeClass("is-active");
            let tab = $(this).closest(".tabs-title");
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
        }).dblclick(function(event) {
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
            let endpointIndex = endpointBox.data("index");
            endpointBox.find(".logtail-box.available").each(function (index) {
                if ($(this).find(".tailing-status").hasClass("on")) {
                    endpoints[endpointIndex].viewer.refresh();
                }
            });
        });
    }

    const drawLogtailsTabs = function (endpoint, tailers) {
        let endpointBox = addEndpointsTab(endpoint);
        for (let key in tailers) {
            let tailer = tailers[key];
            addLogtailsTab(endpointBox, tailer);
        }
        return endpointBox;
    }

    const addEndpointsTab = function (endpoint) {
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
        content.addClass("available")
            .attr("data-index", index)
            .attr("data-name", endpoint.name)
            .attr("data-title", endpoint.title)
            .insertAfter($(".endpoint-box").last());
        return content;
    }

    const addLogtailsTab = function (endpointBox, logtail) {
        let endpointIndex = endpointBox.data("index");
        let endpointTitle = endpointBox.data("title");
        let tabs = endpointBox.find(".logtails.tabs");
        let tab0 = tabs.find(".tabs-title").eq(0);
        let index = tabs.find(".tabs-title").length - 1;
        let tab = tab0.hide().clone()
            .addClass("available")
            .attr("data-index", index)
            .attr("data-name", logtail.name)
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

}
