/**@wind2esg
 * pc or mobile, portrait or landscape
 * if pc or landscape, then add mask.
 * zero depandence.
 */


function mask(){
    const _LoadingHtmlStart = '<div id="maskDiv">';
    const _LoadingHtmlTitle = '<p id="maskTitle">COMPANY</p>';
    const _LoadingHtmlDesc = '<p id="maskDesc">请您使用移动设备竖屏模式体验本H5</p>';
    const _LoadingHtmlEnd = '</div>';
                
    document.write(_LoadingHtmlStart+_LoadingHtmlTitle+_LoadingHtmlDesc+_LoadingHtmlEnd);
    
    //have to add css here instead of in css file.

    const maskDiv = document.getElementById('maskDiv');
    maskDiv.style.width = "100%";
    maskDiv.style.height = "100%";
    maskDiv.style.zIndex = "1000";
    maskDiv.style.backgroundColor = "black";
    maskDiv.style.textAlign = "center";
    maskDiv.style.paddingTop = "25%";

    const maskTitle = document.getElementById('maskTitle');
    maskTitle.style.fontSize = "xx-large";
    maskTitle.style.color = "white";

    const maskDesc = document.getElementById('maskDesc');
    maskDesc.style.fontSize = "xx-large";
    maskDesc.style.color = "white";

}

function unmask(){
    let loadingMask = document.getElementById('loadingDiv');
    loadingMask.parentNode.removeChild(loadingMask);
}

// 0: portrait, 2:pc, landscape:1
let orien = 0;
function rotate() {
    if(window.orientation == undefined){
        // alert('pc');
        mask();
        orien = 2
    }
    if (window.orientation == 180 || window.orientation == 0) {
        // alert('portrait');
        //if 0, good.
        //if not 0, means orientation changed from landscape to portrait, we set orien to 0 then unmask.
        if(orien != 0){
            orien = 0;
            unmask();
        }
    }
    if (window.orientation == 90 || window.orientation == -90) {
        // alert('landscape');
        mask();
        orien = 1;
    }
}

//check when page loaded
window.addEventListener("load", rotate, false);

//check again when orientation changed
window.addEventListener("onorientationchange" in window ? "orientationchange" : "resize", rotate, false);
