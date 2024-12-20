function AppmonViewer() {
    let logtails = {};
    let statuses = {};
    let tracks = {};
    let indicators = {};
    let visible = false;
    let prevPosition = 0;

    this.setIndicators = function (group, name, indicatorArr) {
        indicators[group + ":event:" + name] = indicatorArr;
    };

    this.setTrack = function (group, name, trackBox) {
        tracks[group + ":event:" + name] = trackBox;
    };

    this.setStatus = function (group, name, statusBox) {
        statuses[group + ":status:" + name] = statusBox;
    };

    this.setLogtail = function (group, name, logtailBox) {
        logtails[group + ":logtail:" + name] = logtailBox;
    };

    this.refresh = function (logtail) {
        if (logtail) {
            scrollToBottom(logtail);
        } else {
            for (let key in logtails) {
                if (!logtails[key].data("pause")) {
                    scrollToBottom(logtails[key]);
                }
            }
        }
    };

    this.clear = function (logtail) {
        if (logtail) {
            logtail.empty();
        }
    };

    this.setVisible = function (flag) {
        visible = !!flag;
        if (!visible) {
            for (let key in tracks) {
                tracks[key].find(".missile").remove();
            }
        }
    };

    const getTrack = function (name) {
        if (tracks && name) {
            return tracks[name];
        } else {
            return $(".track-box");
        }
    };

    const getStatus = function (name) {
        if (statuses && name) {
            return statuses[name];
        } else {
            return $(".status-box");
        }
    };

    const getLogtail = function (name) {
        if (logtails && name) {
            return logtails[name];
        } else {
            return $(".logtail");
        }
    };

    const scrollToBottom = function (logtail) {
        if (logtail.data("tailing")) {
            let timer = logtail.data("timer");
            if (timer) {
                clearTimeout(timer);
            }
            timer = setTimeout(function () {
                logtail.scrollTop(logtail.prop("scrollHeight"));
                if (logtail.find("p").length > 11000) {
                    logtail.find("p:gt(10000)").remove();
                }
            }, 300);
            logtail.data("timer", timer);
        }
    };

    this.printEventMessage = function (text, name) {
        if (name) {
            let logtail = getLogtail(name);
            $("<p/>").addClass("event ellipses").html(text).appendTo(logtail);
            scrollToBottom(logtail);
        } else {
            for (let key in logtails) {
                this.printEventMessage(text, key);
            }
        }
    };

    this.printErrorMessage = function (text, name) {
        if (name || !Object.keys(logtails).length) {
            let logtail = getLogtail(name);
            $("<p/>").addClass("event error").html(text).appendTo(logtail);
            scrollToBottom(logtail);
        } else {
            for (let key in logtails) {
                this.printErrorMessage(text, key);
            }
        }
    };

    this.processMessage = function (msg) {
        let idx1 = msg.indexOf(":");
        let idx2 = msg.indexOf(":", idx1 + 1);
        let idx3 = msg.indexOf(":", idx2 + 1);
        let type = msg.substring(idx1 + 1, idx2);
        let label = msg.substring(idx2 + 1, idx3);
        let name = msg.substring(0, idx3);
        let text = msg.substring(idx3 + 1);
        console.log(type, text);
        switch (type) {
            case "event":
                processEvent(name, label, JSON.parse(text));
                break;
            case "logtail":
                printLog(name, text);
                break;
            case "status":
                processStatus(name, label, JSON.parse(text));
                break;

        }
    };

    const processEvent = function (name, label, data) {
        indicate(name);
        if (visible) {
            switch (label) {
                case "request":
                    let track = getTrack(name);
                    if (track) {
                        launchBullet(track, data);
                    }
            }
        }
    }

    const launchBullet = function (track, data) {
        let sessionId = data.sessionId;
        let elapsedMillis = data.elapsedTime; //millis
        let lifespan = elapsedMillis + 1100;
        let position = generateRandom(3, 107);
        if (prevPosition) {
            if (Math.abs(position - prevPosition) <= 20) {
                position = generateRandom(3, 111);
                if (Math.abs(position - prevPosition) <= 20) {
                    position = generateRandom(3, 111);
                }
            }
        }
        prevPosition = position;
        let bullet = $("<div/>")
            .addClass("bullet")
            .attr("sessionId", sessionId)
            .css("top", position + "px")
            .appendTo(track);
        setTimeout(function () {
            bullet.remove();
        }, lifespan);
    };

    const generateRandom = function (min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    };

    const indicate = function (name) {
        let indicatorArr = indicators[name];
        if (indicatorArr) {
            let first = true;
            for (let key in indicatorArr) {
                if (first || visible) {
                    let indicator = indicatorArr[key];
                    if (!indicator.hasClass("on")) {
                        indicator.addClass("blink on");
                        setTimeout(function () {
                            indicator.removeClass("blink on");
                        }, 500);
                    }
                }
                first = false;
            }
        }
    };

    const printLog = function (name, text) {
        let logtail = getLogtail(name);
        if (!logtail.data("pause")) {
            $("<p/>").text(text).appendTo(logtail);
            scrollToBottom(logtail);
        }
    };

    const processStatus = function (name, label, data) {
        switch (label) {
            case "session":
                printSessionStatus(name, data);
        }
    };

    const printSessionStatus = function (name, data) {
        let status = getStatus(name);
        status.find(".activeSessionCount").text(data.activeSessionCount);
        status.find(".highestActiveSessionCount").text(data.highestActiveSessionCount);
        status.find(".evictedSessionCount").text(data.evictedSessionCount);
        status.find(".createdSessionCount").text(data.createdSessionCount);
        status.find(".expiredSessionCount").text(data.expiredSessionCount);
        status.find(".rejectedSessionCount").text(data.rejectedSessionCount);
        status.find(".elapsed").text(data.elapsedTime);
        let ul = status.find("ul.sessions");
        if (data.createdSessions) {
            data.createdSessions.forEach(function (info) {
                ul.find("li[data-sid='" + info.sessionId + "']").remove();
                let indicator = $("<div/>").addClass("indicator");
                if (!info.username) {
                    indicator.addClass("logged-out")
                }
                let li = $("<li/>").attr("data-sid", info.sessionId).append(indicator).appendTo(ul);
                if (info.country) {
                    $("<img class='flag'/>")
                        .attr("src", "/assets/countries/flags/" + info.country.toLowerCase() + ".png")
                        .attr("alt", info.country)
                        .attr("title", countries[info.country].name)
                        .appendTo(li);
                }
                let str = "Session <strong>" + info.sessionId + "</strong> created at <strong>" + info.createAt + "</strong>";
                if (info.username) {
                    str = "(<strong>" + info.username + "</strong>) " + str;
                }
                $("<span/>").html(str).appendTo(li);
            });
        }
        if (data.destroyedSessions) {
            data.destroyedSessions.forEach(function (sessionId) {
                ul.find("li[data-sid='" + sessionId + "']").remove();
            });
        }
    };
}
