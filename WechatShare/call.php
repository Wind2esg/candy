<!-- @wind2esg -->
<!-- mp.wexin.qq.com/wiki -->

<?php
require_once "jssdk.php";
$jssdk = new JSSDK("appId", "appSecret");
$signPackage = $jssdk->GetSignPackage();
?>

<!-- import js sdk  -->

<!-- <script src="http://res.wx.qq.com/open/js/jweixin-1.4.0.js"></script> -->
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>

<script>
//set wx.config
wx.config({
    debug: false,
    appId: '<?php echo $signPackage["appId"];?>',
    timestamp: <?php echo $signPackage["timestamp"];?>,
    nonceStr: '<?php echo $signPackage["nonceStr"];?>',
    signature: '<?php echo $signPackage["signature"];?>',
    jsApiList: [
        // 所有要调用的 API 都要加到这个列表中
        // official recommand but they didnt work. WEIRD!
        // 'updateAppMessageShareData', for v1.4
        // 'updateTimelineShareData', for v1.4
        'onMenuShareTimeline',  //for v1.0
        'onMenuShareAppMessage'     //for v1.0
      ]
});

//put share function in ready
wx.ready(function () {
    wx.onMenuShareTimeline({
        title: "title",
        link: window.location.href,
        //absolute path
        imgUrl: "http://www.xxx.com/a.jpg",
        success: function () {
        },
        cancel: function () {
        }
    });

    wx.onMenuShareAppMessage({
        title: "title",
        desc: "description",
        link: window.location.href,
        //absolute path
        imgUrl: "http://www.xxx.com/a.jpg",
        success: function () {
        },
        cancel: function () {
        }
    });
});
</script>