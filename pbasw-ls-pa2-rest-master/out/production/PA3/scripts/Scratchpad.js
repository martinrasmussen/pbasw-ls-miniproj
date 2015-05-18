/* Basic XML request */
var url = 'http://localhost:8080/proxy/xml/http://api.wolframalpha.com/v2/query?input=Denmark&appid=THPTEV-KH849EY8UT&latlong=55.6763,12.5681';
var podContainer = $("#pod-container");

//var xmlRequest = new XMLHttpRequest();
//xmlRequest.open("GET", url, true); // GET, url, async?
//xmlRequest.onload = function onXmlResponse(err){    // On response(error?)
//    if(err){
//        console.log(err);
//        return;
//    }
//
//    console.log(xmlRequest.response);
//};
//xmlRequest.send(null);  // Send with no form info
//
//
///* Parsing with jQuery */
//var jQueryXml;
//function parseWithJquery(xmlString){
//    var xmlDoc = $.parseXml(xmlString);
//    jQueryXml = $(xmlDoc);
//}
//
///* Querying with jQuery */
//var pods = jQueryXml.find("pod");
//for(var i = 0; i < 0; i++) {
//    var pod = $(pods[i]);
//    var podName = pod.attr("title");
//    var imgTag = pod.find("subpod").find("img")[0];
//    console.log([podName, ": ", imgTag].join(""));
//}

/* Example solution */
var request = new XMLHttpRequest();
request.open("GET", url, true);
request.onload = function onload(err) {
    if(err) {
        console.log(err);
        return;
    }

    var responseXml = request.response;
    var $xml = $($.parseXml(responseXml));

    var pods = $xml.find("pod");
    for(var i = 0; i < pods.length; i++) {
        var pod = pods[i];
        var podTitle = pod.attr("title");
        var podImg = pod.find("subpod").find("img")[0];
        writePodInfo(podContainer, podTitle, podImg);
    }
};

function writePodInfo($container, podTitle, podImg) {
    var titleTag = "<h3>"+podTitle+"</h3>";
    var newDiv = ["<div>", titleTag, podImg, "</div>"].join("");
    $container.append(newDiv);
}