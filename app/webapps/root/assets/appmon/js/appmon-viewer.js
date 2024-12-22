function AppmonViewer() {
    let logs = {};
    let states = {};
    let tracks = {};
    let indicators = {};
    let visible = false;
    let prevPosition = 0;

    this.putLogBox = function (group, label, logBox) {
        logs[group + ":log:" + label] = logBox;
    };

    this.putStateBox = function (group, label, stateBox) {
        states[group + ":state:" + label] = stateBox;
    };

    this.putTrack = function (group, label, trackBox) {
        tracks[group + ":event:" + label] = trackBox;
    };

    this.putIndicator = function (group, type, label, indicator) {
        indicators[group + ":" + type + ":" + label] = indicator;
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
                tracks[key].find(".bullet").remove();
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

    this.processMessage = function (endpoint, msg) {
        let idx1 = msg.indexOf(":");
        let idx2 = msg.indexOf(":", idx1 + 1);
        let idx3 = msg.indexOf(":", idx2 + 1);
        let group = msg.substring(0, idx1);
        let type = msg.substring(idx1 + 1, idx2);
        let label = msg.substring(idx2 + 1, idx3);
        let name = msg.substring(0, idx3);
        let text = msg.substring(idx3 + 1);
        switch (type) {
            case "event":
                indicate(endpoint, group, type, label);
                processEvent(label, name, JSON.parse(text));
                break;
            case "log":
                indicate(endpoint, group, type, label);
                printLog(name, text);
                break;
            case "state":
                processState(label, name, JSON.parse(text));
                break;
        }
    };

    const processEvent = function (label, name, data) {
        switch (label) {
            case "request":
                let reqNum = indicators[name];
                if (reqNum) {
                    reqNum.text(data.number);
                }
                if (visible) {
                    let track = getTrack(name);
                    if (track) {
                        launchBullet(track, data);
                    }
                }
        }
    }

    const launchBullet = function (track, data) {
        if (data.elapsedTime) {
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
            let bullet = $("<div class='bullet'/>")
                .attr("sessionId", data.sessionId)
                .css("top", position + "px")
                .appendTo(track);
            setTimeout(function () {
                bullet.remove();
            }, lifespan);
        }
    };

    const generateRandom = function (min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    };

    const indicate = function (endpoint, group, type, label) {
        let indicator1 = indicators["endpoint:event:" + endpoint.index];
        blink(indicator1);
        if (visible) {
            if (type === "log") {
                let indicator3 = indicators[group + ":log:" + label];
                blink(indicator3);
            } else {
                let indicator2 = indicators["group:event:" + group];
                blink(indicator2);
            }
        }
    };

    const blink = function (indicator) {
        if (indicator && !indicator.hasClass("on")) {
            indicator.addClass("blink on");
            setTimeout(function () {
                indicator.removeClass("blink on");
            }, 500);
        }
    }

    const printLog = function (name, text) {
        let logBox = getLogBox(name);
        if (!logBox.data("pause")) {
            $("<p/>").text(text).appendTo(logBox);
            scrollToBottom(logBox);
        }
    };

    const processState = function (label, name, data) {
        switch (label) {
            case "session":
                printSessionState(name, data);
        }
    };

    const printSessionState = function (name, data) {
        let stateBox = getStateBox(name);
        if (stateBox) {
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
        }
    };
}
