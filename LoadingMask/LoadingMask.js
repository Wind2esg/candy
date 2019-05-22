/**
 * Loading mask. 
 * PLZ add css code.
 */

const _LoadingHtmlStart = '<div id="loadingDiv">';
const _LoadingHtmlTitle = '<p id="loadingtxt1">COMPANY</p>';
const _LoadingHtmlDesc = '<p id="loadingtxt2">LOADING</p>';
const _LoadingHtmlEnd = '</div>';
                
document.write(_LoadingHtmlStart+_LoadingHtmlTitle+_LoadingHtmlDesc+_LoadingHtmlEnd);

document.onreadystatechange = function() {
    if (document.readyState == "complete") {
        let loadingMask = document.getElementById('loadingDiv');
        loadingMask.parentNode.removeChild(loadingMask);
    }
};