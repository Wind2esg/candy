/*
* exif.js 获取图片方向
*/
function getOrientation(img){
	var orient;
	EXIF.getData(img,function(){
		orient = EXIF.getTag(this, 'Orientation');
	});
	return orient;
}

//获取旋转角度
function rotateDeg(orient){
	if(orient == 8){
		return 270;
	}
	if(orient == 3){
		return 180;
	}
	if(orient == 6){
		return 90;
	}
};

//css to rotate the pic
$($d).css("transform","rotate(" + rotateDeg(orient) + "deg)");

//uri params
function getQueryString(name){
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);//search,查询？后面的参数，并匹配正则
    if(r!=null)return  unescape(r[2]); return null;
}