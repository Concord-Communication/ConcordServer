
const token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhbmRyZXc1IiwiaWQiOjIzNDk4MjUxMzc5MTk5NTkwNCwiZXhwIjoxNjM0MDQwNjc5LCJpc3MiOiJDb25jb3JkIn0.Z7fC-pgkApFwqCCxDpfy4GStO6lDLNKlEMGBuzoJBcJJaPf9YeVHgAsv0D4y6B6yDhI-rCBNAmHjWYOf2FsbSfKjr38a2o66RuMzH-nrBPClXohwdUHv7UJjEHW7ZgWAHoabF4bfancnXHgyr5eRoCvDP_Z_J-DcycN1oYeDZMAZOoksVgxE_fnOATBfcXqCgpY-YSnUlK-pK9XWDDamZ9Wf_KfqmqdjbN0c7YJUR-IJzUALaQXG5Hq2Ax-dxFNckadIUqu2fF8ionpW8heEbErIXgn7rluSrr1ADx4yBIXztOwJlTCZ6GPSBNhOhcmdwnJal0xroFxwR6jnX0AEmA";
let ws = new WebSocket("ws://localhost:8080/client?token=" + token);
ws.onopen = () => {
    console.log("Opened websocket connection.");
    window.setInterval(() => {
        ws.send(JSON.stringify({
            "type": "heartbeat"
        }));
    }, 10000);
    window.setInterval(() => {
        console.log("Sending automated chat message...");
        ws.send(JSON.stringify({
            "type": "chat",
            "channelId": 235009133105909760,
            "content": "This is an automated message."
        }));
    }, 5000);
};
ws.onmessage = (msg) => {
    console.log(msg.data);
};
ws.onclose = () => {
    console.log("Closed.");
};
