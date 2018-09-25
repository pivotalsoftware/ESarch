export const HostURLsMapping = {
    "localhost": "http://localhost:8080",
    "esrefarch-demo-trader-ui.cfapps.io": "https://esrefarch-demo-trader-app.cfapps.io",
    "axontrader.cfapps.io": "https://esrefarch-demo-trader-app.cfapps.io",
}
export const ApiConfig = () => {
    let hostname = window.location.hostname;
    let apiURL = HostURLsMapping[hostname];
    return apiURL ? apiURL : '';
}