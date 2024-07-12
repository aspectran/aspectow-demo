function AppmonPollingClient(endpoint, onEndpointJoined, onEstablishCompleted) {

    this.join = function () {
        $.ajax({
            url: endpoint.basePath + "appmon/endpoint/join",
            type: 'post',
            dataType: "json",
            success: function (data) {
                if (data) {
                    if (onEndpointJoined) {
                        onEndpointJoined(endpoint, data);
                    }
                    if (onEstablishCompleted) {
                        onEstablishCompleted();
                    }
                    polling();
                }
            }
        });

    }

    const polling = function () {
        $.ajax({
            url: endpoint.basePath + "appmon/endpoint/pull",
            type: 'get',
            dataType: "json",
            success: function (data) {
                if (data) {
                    for (let key in data) {
                        endpoint.viewer.printMessage(data[key]);
                    }
                    //setTimeout(polling, endpoint.pollingInterval);
                }
            }
        });
    }

}
