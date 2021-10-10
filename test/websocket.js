
initSocket("ws://localhost:8080/client")

function initSocket(address) {
    let ws = new WebSocket(address);
    ws.onopen = () => {
        console.log("Opened.")
    };
    ws.onmessage = (msg) => {
        console.log("Message received");
        console.log(msg.data);
    };
    ws.onclose = () => {
        console.log("Closed.");
    };
}
