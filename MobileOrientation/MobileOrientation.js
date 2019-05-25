/**@wind2esg
 * pc or mobile, portrait or landscape
 * if pc or landscape, then add mask.
 */

function mask(){
    document.getElementById('maskDiv').style.visibility = "visible";
}

function unmask(){
    document.getElementById('maskDiv').style.visibility="hidden";;
}

// 0: portrait, 2:pc, landscape:1
var orien = 0;
function rotate() {
    if(window.orientation == undefined){
        // alert('pc');
        orien = 2
        mask();
    }
    if (window.orientation%180 == 180 || window.orientation%180 == 0) {
        // alert('portrait');
        //if 0, good.
        //if not 0, means orientation changed from landscape to portrait, we set orien to 0 then unmask.
        if(orien != 0){
            orien = 0;
            unmask();
        }
    }
    if (window.orientation%180 == 90 || window.orientation%180 == -90) {
        // alert('landscape');
        orien = 1;
        mask();
        
    }
}

//check when page loaded
rotate();
//check again when orientation changed
window.addEventListener("orientationchange", rotate, false);