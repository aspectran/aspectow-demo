function PollingClient(endpoint, viewer, onJoined, onEstablished) {

    const MODE = "polling";

    this.start = function (joinGroups) {
        join(joinGroups);
    };

    this.speed = function (speed) {
        changePollingInterval(speed);
    };

    const join = function (joinInstances) {
        $.ajax({
            url: endpoint.basePath + "backend/polling/" + endpoint.token + "/join",
            type: 'post',
            dataType: "json",
            data: {
                joinInstances: joinInstances
            },
            success: function (data) {
                if (data) {
                    endpoint['mode'] = MODE;
                    endpoint['token'] = data.token;
                    endpoint['pollingInterval'] = data.pollingInterval;
                    if (onJoined) {
                        onJoined(endpoint, data);
                    }
                    if (onEstablished) {
                        onEstablished(endpoint);
                    }
                    viewer.printMessage("Polling every " + data.pollingInterval + " milliseconds.");
                    polling();
                }
            }
        });
    };

    const polling = function () {
        $.ajax({
            url: endpoint.basePath + "backend/polling/" + endpoint.token + "/pull",
            type: 'get',
            success: function (data) {
                if (data && data.token && data.messages) {
                    endpoint['token'] = data.token;
                    for (let key in data.messages) {
                        viewer.processMessage(data.messages[key]);
                    }
                    setTimeout(polling, endpoint.pollingInterval);
                } else {
                    console.error(endpoint.name, "connection lost");
                    viewer.printErrorMessage("Connection lost. Please refresh this page to try again!");
                }
            }
        });
    };

    const changePollingInterval = function (speed) {
        $.ajax({
            url: endpoint.basePath + "backend/polling/" + endpoint.token + "/pollingInterval",
            type: 'post',
            dataType: "json",
            data: {
                speed: speed
            },
            success: function (data) {
                console.log(endpoint.name, "pollingInterval", data);
                if (data) {
                    endpoint.pollingInterval = data;
                    viewer.printMessage("Polling every " + data + " milliseconds.");
                }
            }
        });
    };
}
