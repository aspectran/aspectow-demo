function AppmonWebsocketClient(endpoint, onEndpointJoined, onEstablishCompleted, onErrorObserved) {
    let socket = null;
    let heartbeatTimer = null;
    let pendingMessages = [];
    let established = false;

    this.start = function () {
        openSocket();
    }

    this.stop = function () {
        closeSocket();
    }

    const openSocket = function () {
        if (socket) {
            socket.close();
        }
        let url = new URL(endpoint.url, location.href);
        url.protocol = url.protocol.replace('https:', 'wss:');
        url.protocol = url.protocol.replace('http:', 'ws:');
        socket = new WebSocket(url.href);
        let self = this;
        socket.onopen = function (event) {
            pendingMessages.push("Socket connection successful");
            socket.send("join:");
            heartbeatPing();
        };
        socket.onmessage = function (event) {
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
                        if (text.startsWith("logtail:")) {
                            text = text.substring(8);
                            if (text.startsWith("last:")) {
                                text = text.substring(5);
                                endpoint.viewer.printMessage(name, text, false);
                            } else {
                                endpoint.viewer.printMessage(name, text, true);
                            }
                        } else if (text.startsWith("status:")) {
                            text = text.substring(7);
                            idx = text.indexOf(":");
                            if (idx !== -1) {
                                let label = text.substring(0, idx);
                                let data = JSON.parse(text.substring(idx + 1));
                                console.log(name, label, data);
                                endpoint.viewer.printStatus(name, label, data);
                            }
                        }
                    } else {
                        let command = msg.substring(0, idx);
                        if (command === "joined") {
                            console.log(msg);
                            let payload = JSON.parse(msg.substring(idx + 1));
                            establish(payload);
                        }
                    }
                }
            }
        };
        socket.onclose = function (event) {
            endpoint.viewer.printEventMessage('Socket connection closed. Please refresh this page to try again!');
            closeSocket();
        };
        socket.onerror = function (event) {
            console.error("WebSocket error observed:", event);
            if (onErrorObserved) {
                onErrorObserved();
            } else {
                endpoint.viewer.printErrorMessage('Could not connect to WebSocket server');
                setTimeout(function () {
                    self.openSocket();
                }, 60000);
            }
        };
    };

    const closeSocket = function () {
        if (socket) {
            socket.close();
            socket = null;
        }
    };

    const establish = function (payload) {
        if (onEndpointJoined) {
            endpoint['mode'] = "websocket";
            onEndpointJoined(endpoint, payload);
        }
        if (onEstablishCompleted) {
            onEstablishCompleted();
        }
        if (pendingMessages && pendingMessages.length > 0) {
            for (let key in pendingMessages) {
                endpoint.viewer.printEventMessage(pendingMessages[key]);
            }
            pendingMessages = null;
        }
        established = true;
        socket.send("established:");
    };

    const heartbeatPing = function () {
        if (heartbeatTimer) {
            clearTimeout(heartbeatTimer);
        }
        let self = this;
        heartbeatTimer = setTimeout(function () {
            if (socket) {
                socket.send("--ping--");
                heartbeatTimer = null;
                heartbeatPing();
            }
        }, 57000);
    };
}
