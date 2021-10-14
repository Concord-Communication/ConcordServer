
const token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhbmRyZXc1IiwiaWQiOjIzNDk4MjUxMzc5MTk5NTkwNCwiZXhwIjoxNjM0MjAxODEyLCJpc3MiOiJDb25jb3JkIn0.J18bPE5w0DPYwzI1Ig3WXCRHOf6sD6_khnoj4xM9C1Xk-DUw_sqTJ2vCYf_3aOLnTp5XXBZ597LrkjKJ0hsS7vO-usANi052F5CVRl27t-t0At4wgyD0lbmrzTOVtU7EtkcBzqXWFfc5ZMNGcE-7mAhyWUw7hSkrAGDNVaPi_17WzmKlz54tGmkhU5I9_cnRTOEggGNzdniqR_e_tEoI87hyN8ETgvQ2e9IAKJrRr15AnABQIdqH8my71fEIIDWfDmHsxNPDiJyONJ2UkhcplgF0skErjk_wT6QeKN_7At6EOfAE-vBDpONV0EhXujxRdHQ4cI9uwyiM7bErBnQHXw";
let ws = new WebSocket("ws://localhost:8080/client?token=" + token);
ws.onopen = () => {
    console.log("Opened websocket connection.");
    window.setInterval(() => {
        ws.send(JSON.stringify({
            "type": "heartbeat",
            "timestamp": Date.now()
        }));
    }, 10000);
    window.setInterval(() => {
        console.log("Sending automated chat message...");
        ws.send(JSON.stringify({
            "type": "chat_written",
            "channelId": 235009133105909760,
            "threadId": null,
            "sentAt": Date.now(),
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
