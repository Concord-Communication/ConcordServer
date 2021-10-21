
const token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlkIjozNDExMDM5Nzc3NDYwMjI0LCJleHAiOjE2Mzc0MDMxMTUsImlzcyI6IkNvbmNvcmQifQ.Le2V2YYAiXeilRvnhJpgL3CqYaGCBGP7Vn6RJUqhzn5vufodHR39dZGvpmyBmHVmdUOQMYIiXRuHe_dLw_FtIQEbs04GVEW5eW57niagEbnSFX1q3gaxy1L5LF26aq8yq1ot5pG--QYUGLETdbcAcqn0MdmDGccBuzNKmWj6411OdPGH8x6eVUyzcNMwv3mIvyDcIZsYsBR3kdpF34l-PiaRyoVV1QGGPbY4WG89HFzFKxaLi73DgV3oJwqbOE1-5Sbimiu8XEcd2WyXBUD5gYbtZDNfndNCsKBBLEx7CbrQkpLiihsO3pyl9w6Sy8def-OxIu0Q8lcflBP1SiGcxg";
let ws = new WebSocket("ws://localhost:8080/client?token=" + token);
ws.onopen = () => {
    console.log("Opened websocket connection.");
    // window.setInterval(() => {
    //     if (ws.readyState === 3) return;
    //     console.log("Sending automated chat message...");
    //     ws.send(JSON.stringify({
    //         "type": "chat_written",
    //         "channelId": 235009133105909760,
    //         "threadId": null,
    //         "sentAt": Date.now(),
    //         "content": "This is an automated message."
    //     }));
    // }, 5000);
};
ws.onmessage = (msg) => {
    console.log(msg.data);
    let dataJson = JSON.parse(msg.data);
    if (dataJson.type === "heartbeat") {
        console.log("Sending reply to heartbeat.")
        ws.send(msg.data);
    }
};
ws.onclose = () => {
    console.log("Closed.");
};
