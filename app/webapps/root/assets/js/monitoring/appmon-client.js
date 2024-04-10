function AppmonClient(endpoint, onEndpointJoined, onEstablishCompleted) {
    let socket = null;
    let heartbeatTimer = null;
    let pendingMessages = [];
    let logtails = {};
    let missileTracks = {};
    let indicators = {};
    let statuses = {};
    let established = false;
    let prevLogTime = null;
    let prevSentTime = new Date().getTime();
    let prevPosition = 0;

    this.openSocket = function() {
        if (socket) {
            socket.close();
        }
        let url = new URL(endpoint.url, location.href);
        url.protocol = url.protocol.replace('https:', 'wss:');
        url.protocol = url.protocol.replace('http:', 'ws:');
        socket = new WebSocket(url.href);
        let self = this;
        socket.onopen = function(event) {
            pendingMessages.push("Socket connection successful");
            socket.send("join:");
            heartbeatPing();
        };
        socket.onmessage = function(event) {
            if (typeof event.data === "string") {
                if (event.data === "--pong--") {
                    heartbeatPing();
                    return;
                }
                let msg = event.data;
                let idx = msg.indexOf(":");
                if (idx !== -1) {
                    if (established) {
                        let name = msg.substring(0, idx);
                        let text = msg.substring(idx + 1);
                        if (text.startsWith("last:")) {
                            text = text.substring(5);
                            printMessage(name, text, false);
                        } else if (text.startsWith("status:")) {
                            text = text.substring(7);
                            idx = text.indexOf(":");
                            if (idx !== -1) {
                                let label = text.substring(0, idx);
                                let data = JSON.parse(text.substring(idx + 1));
                                console.log(name, label, data);
                                printStatus(name, label, data);
                            }
                        } else {
                            printMessage(name, text, true);
                        }
                    } else {
                        let command = msg.substring(0, idx);
                        if (command === "joined") {
                            console.log(msg);
                            let payload = JSON.parse(msg.substring(idx + 1));
                            establish(self, payload);
                        }
                    }
                }
            }
        };
        socket.onclose = function(event) {
            printEventMessage('Socket connection closed. Please refresh this page to try again!');
            self.closeSocket();
        };
        socket.onerror = function(event) {
            console.error("WebSocket error observed:", event);
            printErrorMessage('Could not connect to WebSocket server');
            setTimeout(function() {
                self.openSocket();
            }, 60000);
        };
    };

    this.closeSocket = function() {
        if (socket) {
            socket.close();
            socket = null;
        }
    };

    this.refresh = function(logtail) {
        if (logtail) {
            scrollToBottom(logtail);
        } else {
            for (let key in logtails) {
                scrollToBottom(logtails[key]);
            }
        }
    };

    this.clear = function(logtail) {
        if (logtail) {
            logtail.empty();
        }
    };

    this.setLogtail = function(name, logtail) {
        logtails[name] = logtail;
    };

    this.setMissileTrack = function(name, missileTrack) {
        missileTracks[name] = (missileTrack.length > 0 ? missileTrack : null);
    };

    this.setIndicators = function(name, indicatorArr) {
        indicators[name] = indicatorArr;
    };

    this.setStatus = function(name, status) {
        statuses[name] = status;
    };

    this.getLogtails = function() {
        return logtails;
    };

    const establish = function(client, payload) {
        endpoint['client'] = client;
        if (onEndpointJoined) {
            onEndpointJoined(endpoint, payload);
        }
        if (onEstablishCompleted) {
            onEstablishCompleted();
        }
        if (pendingMessages && pendingMessages.length > 0) {
            for (let key in pendingMessages) {
                printEventMessage(pendingMessages[key]);
            }
            pendingMessages = null;
        }
        established = true;
        socket.send("established:");
    };

    const heartbeatPing = function() {
        if (heartbeatTimer) {
            clearTimeout(heartbeatTimer);
        }
        let self = this;
        heartbeatTimer = setTimeout(function() {
            if (socket) {
                socket.send("--ping--");
                heartbeatTimer = null;
                heartbeatPing();
            }
        }, 57000);
    };

    const getLogtail = function(name) {
        if (logtails && name) {
            return logtails[name];
        } else {
            return $(".logtail");
        }
    };

    const getStatus = function(name) {
        if (statuses && name) {
            return statuses[name];
        } else {
            return $(".status-box");
        }
    };

    const scrollToBottom = function(logtail) {
        if (logtail.data("tailing")) {
            let timer = logtail.data("timer");
            if (timer) {
                clearTimeout(timer);
            }
            timer = setTimeout(function() {
                logtail.scrollTop(logtail.prop("scrollHeight"));
                if (logtail.find("p").length > 11000) {
                    logtail.find("p:gt(10000)").remove();
                }
            }, 300);
            logtail.data("timer", timer);
        }
    };

    const printMessage = function(name, text, visualizing) {
        indicate(name);
        let logtail = getLogtail(name);
        if (!logtail.data("pause")) {
            if (visualizing) {
                visualize(name, text);
            }
            $("<p/>").text(text).appendTo(logtail);
            scrollToBottom(logtail);
        }
    };

    const printEventMessage = function(text, name) {
        if (name) {
            let logtail = getLogtail(name);
            $("<p/>").addClass("event ellipses").html(text).appendTo(logtail);
            scrollToBottom(logtail);
        } else {
            for (let key in logtails) {
                printEventMessage(text, key);
            }
        }
    };

    const printErrorMessage = function(text, name) {
        if (name) {
            let logtail = getLogtail(name);
            $("<p/>").addClass("event error").html(text).appendTo(logtail);
            scrollToBottom(logtail);
        } else {
            for (let key in logtails) {
                printErrorMessage(text, key);
            }
        }
    };

    const indicate = function(name) {
        let indicatorArr = indicators[name];
        if (indicatorArr) {
            for (let key in indicatorArr) {
                let indicator = indicatorArr[key];
                if (!indicator.hasClass("on")) {
                    indicator.addClass("blink on");
                    setTimeout(function() {
                        indicator.removeClass("blink on");
                    }, 500);
                }
            }
        }
    };

    const visualize = function(name, text) {
        let missileTrack = missileTracks[name];
        if (missileTrack) {
            setTimeout(function() {
                let visualizing = missileTrack.data("visualizing");
                if (visualizing) {
                    launchMissile(missileTrack, text);
                }
            }, 1);
        }
    };

    const pattern1 = /^DEBUG (.+) \[(.+)] Create new session id=([^\s;]+)/;
    const pattern2 = /^DEBUG (.+) \[(.+)] Session ([^\s;]+) accessed, stopping timer, active requests=(\d+)/;
    const pattern3 = /^DEBUG (.+) \[(.+)] Session ([^\s;]+) complete, active requests=(\d+)/;
    const pattern4 = /^DEBUG (.+) \[(.+)] Invalidate session id=([^\s;]+)/;
    const pattern5 = /^DEBUG (.+) \[(.+)] Reject session id=([^\s;]+)/;

    const launchMissile = function(missileTrack, text) {
        let matches1 = pattern1.exec(text);
        let matches2 = pattern2.exec(text);
        let matches3 = pattern3.exec(text);
        let matches4 = pattern4.exec(text);
        let matches5 = pattern5.exec(text);

        // if (matches1 || matches2 || matches3 || matches4 || matches5) {
        //     console.log(text);
        //     console.log('matches1', matches1);
        //     console.log('matches2', matches2);
        //     console.log('matches3', matches3);
        //     console.log('matches4', matches4);
        //     console.log('matches5', matches5);
        // }

        let dateTime = "";
        let sessionId = "";
        let requests = 0;
        let delay = 0;
        if (matches3 || matches4 || matches5) {
            if (matches3) {
                sessionId = matches3[3];
                requests = parseInt(matches3[4]) + 1;
            } else if (matches4) {
                sessionId = matches4[3];
                requests = 1
            } else if (matches5) {
                sessionId = matches5[3];
                requests = 1
            }
            if (requests > 3) {
                requests = 3;
            }
            let mis = missileTrack.find(".missile[sessionId='" + sessionId + "_" + requests + "']");
            if (mis.length > 0) {
                let dur = 650 + mis.data("delay")||0;
                if (mis.hasClass("mis-2")) {
                    dur += 250;
                } else if (mis.hasClass("mis-3")) {
                    dur += 500;
                }
                setTimeout(function () {
                    mis.remove();
                }, dur + 800);
            }
            return;
        }
        if (matches1 || matches2) {
            if (matches1) {
                dateTime = matches1[1];
                sessionId = matches1[3];
                requests = 1;
            } else if (matches2) {
                dateTime = matches2[1];
                sessionId = matches2[3];
                requests = matches2[4];
            }
            if (requests > 3) {
                requests = 3;
            }
            let logTime = moment(dateTime);
            let currTime = new Date().getTime();
            let spentTime = currTime - prevSentTime;
            if (prevLogTime) {
                delay = logTime.diff(prevLogTime);
                if (delay >= 1000 || delay < 0 || spentTime >= delay + 1000) {
                    delay = 0;
                }
            }
            prevLogTime = logTime;
            prevSentTime = currTime;
        }
        if (requests > 0) {
            let position = generateRandom(3, 120 - 3 - (requests * 4 + 8));
            if (delay < 1000 && prevPosition) {
                if (Math.abs(position - prevPosition) <= 20) {
                    position = generateRandom(3, 120 - 3 - (requests * 4 + 8));
                    if (Math.abs(position - prevPosition) <= 20) {
                        position = generateRandom(3, 120 - 3 - (requests * 4 + 8));
                    }
                }
            }
            prevPosition = position;
            let mis = $("<div/>").attr("sessionId", sessionId + "_" + requests);
            mis.data("delay", 1000 + delay);
            setTimeout(function () {
                mis.addClass("mis-" + requests).removeClass("hidden");
            }, 1000 + delay);
            mis.css("top", position + "px");
            mis.appendTo(missileTrack).addClass("hidden missile");
        }
    };

    const generateRandom = function(min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    };

    const printStatus = function(name, label, data) {
        switch (label) {
            case "session":
                printSessionStatus(name, data);
        }
    }

    const printSessionStatus = function(name, data) {
        let status = getStatus(name);
        status.find(".activeSessionCount").text(data.activeSessionCount);
        status.find(".highestActiveSessionCount").text(data.highestActiveSessionCount);
        status.find(".evictedSessionCount").text(data.evictedSessionCount);
        status.find(".createdSessionCount").text(data.createdSessionCount);
        status.find(".expiredSessionCount").text(data.expiredSessionCount);
        status.find(".rejectedSessionCount").text(data.rejectedSessionCount);
        status.find(".elapsed").text(data.elapsedTime);
        if (data.currentSessions) {
            status.find("ul.sessions").empty();
            data.currentSessions.forEach(function(username) {
                let indicator = $("<div/>").addClass("indicator");
                if (username.indexOf("0:") === 0) {
                    indicator.addClass("logged-out")
                }
                username = username.substring(2);
                let name = $("<span/>").addClass("name").text(username);
                let li = $("<li/>").append(indicator).append(name);
                status.find("ul.sessions").append(li);
            });
        }
    };
}
