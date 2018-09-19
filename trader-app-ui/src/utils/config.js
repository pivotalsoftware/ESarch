export const ApiConfig = () => {
    let hostname = window.location.hostname;
    console.log(hostname);
    if (hostname === "localhost") {
        return "http://localhost:8080"
    } else if (hostname === "esrefarch-demo-trader-ui.cfapps.io") {
        return "https://esrefarch-demo-trader-app.cfapps.io"
    } else {
        return "https://esrefarch-demo-trader-app.cfapps.io"
    }
}