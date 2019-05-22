<?php
require_once "jssdk.php";
$jssdk = new JSSDK("wxcabf8740173ea5ed", "bb25850afaa766521e1d9b1bd51affdc");
$signPackage = $jssdk->GetSignPackage();
?>


<!-- <script src="http://res.wx.qq.com/open/js/jweixin-1.4.0.js"></script> -->
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>

<script>
wx.config({
    debug: false,
    appId: '<?php echo $signPackage["appId"];?>',
    timestamp: <?php echo $signPackage["timestamp"];?>,
    nonceStr: '<?php echo $signPackage["nonceStr"];?>',
    signature: '<?php echo $signPackage["signature"];?>',
    jsApiList: [
        // 所有要调用的 API 都要加到这个列表中
        // 'updateAppMessageShareData', for v1.4
        // 'updateTimelineShareData', for v1.4
        'onMenuShareTimeline',  //for v1.0
        'onMenuShareAppMessage'     //for v1.0
      ]
});

wx.ready(function () {
    wx.onMenuShareTimeline({
        title: "",
        link: window.location.href,
        //absolute path
        imgUrl: "",
        success: function () {
        },
        cancel: function () {
        }
    });

    wx.onMenuShareAppMessage({
        title: "",
        desc: "",
        link: window.location.href,
        //absolute path
        imgUrl: "",
        success: function () {
        },
        cancel: function () {
        }
    });
});
</script>