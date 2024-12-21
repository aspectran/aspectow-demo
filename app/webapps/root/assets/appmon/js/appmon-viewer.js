function AppmonViewer() {
    let logs = {};
    let states = {};
    let tracks = {};
    let indicators = {};
    let visible = false;
    let prevPosition = 0;

    this.putLogBox = function (group, name, logBox) {
        logs[group + ":log:" + name] = logBox;
    };

    this.putStateBox = function (group, name, stateBox) {
        states[group + ":state:" + name] = stateBox;
    };

    this.putTrack = function (group, name, trackBox) {
        tracks[group + ":event:" + name] = trackBox;
    };

    this.putIndicators = function (group, name, indicatorArr) {
        indicators[group + ":event:" + name] = indicatorArr;
    };

    const getLogBox = function (name) {
        if (logs && name) {
            return logs[name];
        } else {
            return $(".log");
        }
    };

    const getStateBox = function (name) {
        if (states && name) {
            return states[name];
        } else {
            return $(".state-box");
        }
    };

    const getTrack = function (name) {
        if (tracks && name) {
            return tracks[name];
        } else {
            return $(".track-box");
        }
    };

    this.refresh = function (logBox) {
        if (logBox) {
            scrollToBottom(logBox);
        } else {
            for (let key in logs) {
                if (!logs[key].data("pause")) {
                    scrollToBottom(logs[key]);
                }
            }
        }
    };

    this.clear = function (logBox) {
        if (logBox) {
            logBox.empty();
        }
    };

    const scrollToBottom = function (logBox) {
        if (logBox.data("tailing")) {
            let timer = logBox.data("timer");
            if (timer) {
                clearTimeout(timer);
            }
            timer = setTimeout(function () {
                logBox.scrollTop(logBox.prop("scrollHeight"));
                if (logBox.find("p").length > 11000) {
                    logBox.find("p:gt(10000)").remove();
                }
            }, 300);
            logBox.data("timer", timer);
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

    this.printEventMessage = function (text, name) {
        if (name) {
            let log = getLogBox(name);
            $("<p/>").addClass("event ellipses").html(text).appendTo(log);
            scrollToBottom(log);
        } else {
            for (let key in logs) {
                this.printEventMessage(text, key);
            }
        }
    };

    this.printErrorMessage = function (text, name) {
        if (name || !Object.keys(logs).length) {
            let log = getLogBox(name);
            $("<p/>").addClass("event error").html(text).appendTo(log);
            scrollToBottom(log);
        } else {
            for (let key in logs) {
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
        switch (type) {
            case "event":
                processEvent(name, label, JSON.parse(text));
                break;
            case "log":
                printLog(name, text);
                break;
            case "state":
                processState(name, label, JSON.parse(text));
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
        let logBox = getLogBox(name);
        if (!logBox.data("pause")) {
            $("<p/>").text(text).appendTo(logBox);
            scrollToBottom(logBox);
        }
    };

    const processState = function (name, label, data) {
        switch (label) {
            case "session":
                printSessionState(name, data);
        }
    };

    const printSessionState = function (name, data) {
        let stateBox = getStateBox(name);
        stateBox.find(".activeSessionCount").text(data.activeSessionCount);
        stateBox.find(".highestActiveSessionCount").text(data.highestActiveSessionCount);
        stateBox.find(".evictedSessionCount").text(data.evictedSessionCount);
        stateBox.find(".createdSessionCount").text(data.createdSessionCount);
        stateBox.find(".expiredSessionCount").text(data.expiredSessionCount);
        stateBox.find(".rejectedSessionCount").text(data.rejectedSessionCount);
        stateBox.find(".elapsed").text(data.elapsedTime);
        let ul = stateBox.find("ul.sessions");
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
