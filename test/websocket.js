
const token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhbmRyZXc1IiwiaWQiOjIzNDk4MjUxMzc5MTk5NTkwNCwiZXhwIjoxNjMzOTAyMjAyLCJpc3MiOiJDb25jb3JkIn0.SLJhwmL8MhubT4MzvaRasbY2Fo2yYTViL5GEbjXWtdfG1tISseGwSjp6Jbuk3aAj4_k_wzxyXLjg_P6CJu9qzmYqKtzthirTXxzH6rNGT_sr8akVXLwionRI1ntQ7O81Rk6x8IuI3Z5CjmC_w520GdXJKFpWWW1KZ1U1NlPU58NF53xtA3HMMx_VSFc-gUHCVMmWylZV3JfguVfxA_kfcsDiykwzoG1zcSTbU5ORlwgaXXLy2CKrrv9QN0a4oKRctvBIaYiLWcnIpSBPHfZuMIvaQXcd9Tk_pM-Gdl7fp-3gEdrIW86QuHsdsPBzLV5YRaiBubpqz4fJVrvgz4U89Q";

initSocket("ws://localhost:8080/client?token=" + token)

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
