package top.chris.shop.common;

public class AlipayConfig {
    // 作为身份标识的应用ID
    public static String app_id = "2021000116670674";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key  = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCSUnAo66taz8Dv0nAxx3PUfKNKGEwgqEqVd+XhaVO94VTE7zsIJA3iCi3vIpqmGjWGv9PPrRBWCKxNCRXjDAF/TV1acII8Ijt7iVfB/1H8WA6v20z+7RBB8rvHldrGl3LL0Jxsev2X7yNJJUAbQ5dhBBlp3VFBDvq/7b0Q+1aDigmNxq58xQoOv2VtlNk5oPwQLibJoJ8IWeAs2A0iOopTgL6BD4p5iBGdWFpi9yck7z5dh8HqJNPAmxxfa1bBI0Sun/aW6ydiMcV5+JVojIfK0DCcjmu3+Q66tJPBq3+QvjweaNlFslmJAXDTPSpHUc+y+ODrLltsUYMstimAMJRLAgMBAAECggEAS3vt0p5pYsrEfLPyHywGzDqUPng0ZbCL8XnOBLBCfDVPdtz3diarZs3bjn8qisAAlwhE6+EbCVcmFAO4381kxFiT7UEXXpBGYV+j/zeJAwYmZYBbwzNm7K6dQsRC/apJlW2EFM6KHqRGbsihvq7q3CiyN5PuBAMHRpYNsEIJCogT5GezjB2tEnSYWazxPi+WUa6t+GCgrTj2XLuJAUphL53ktOKMuaOoqPPsrqDVPW6qR7J1v/oYTIgI6RIYxVeIgs6OYDjQ+VmjyriPsXGFT5e0qiV1u9llV/+w4FsAbu2X8StwD6HTeIKP9Bi9SAyEohDM5e/mLyvgxDR7ZJnzyQKBgQDl+zgMcE5e+Pm7IYzO1gYPdMV5wkkvNP1nR1l5EHDdnZ1bDEVDIN/1JLVj41zvu6Hsc89VHvIMordPXVgV07rDNzGEQHLXWnVkSIbroDTEyHS+sM1Kaw29YMwwqiO9axQkKTTUSs14oFOAPTgnewTzYfSH2aiR+2cxKy1RT5o8lwKBgQCi4EGJKTw6ASvfczTaG/+gmQn0FVpKgS+DXutCs+Co03yDeNq2eIYr5fLIFAYaQeAoSYe91T9cO+Ubf4UF+ffAwgO9yU3/xCyeGWKfR78uaKtTvYEG4S1OS/XApJqsugOKwBc2ya1gGZYfXEsaAfbSrjeBGINXGLd/dbOVZ5J4bQKBgBiLENYy/gogo/YWhIJmUMtBePmfhFPnYcutz5yd5o2gDnTzAElDruLogfsg/oAPFB651TyOSuKSUuHDYm0h5WKD92LXxem5m6GSI+PtevQtOFhJYf85IYQ+WkwvPBUw4jthsmOdgAQJiB+9aMq91C9tGr2MuvKpU7/pjbKJvjoNAoGASASEtdNH0BYIgir8qZxLsYU5w3VVkTACBEKKH9pNchjhK3jarGgqW2q3qQRBloaeSPIKVUPfWjcZnMkL1pKqm86tvNMv6/GgEOAEnY0p2alqaAKY9KPIVqB7r4yY39r4CffZy93tDVsC6Cb1BjDsJEfoPurJ2E8ThAZVO7CqMRkCgYEA3pzxnJ3oLvc4WwkGNA/jsNrRjM+njVrG9i3gwoLOHjcgs1+Bt7XIZxLXkUGmGbHNRyPHvSNQgp3Fad2hV/1yZ1amT+D/j1Z/V5DWHQ/NDggSa1+a1C7TtMQGHZOzvudKd8VNkGD0rVHeqIxCEHfc8dLwFy055Wm8AB6/8wz/R1Q=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnCB7/1SEy8ojp8gxvwjKXq+a9OJsLfaQWXtTwSJYNZvqlkNT/131cmQbuvQnxz4tyQRATutgRwJNQOGerP094tup3YpvBbMuJgE+wbwjSSUa0ehS8rjMr6THJw/xdADFPLWviX0UVB8Zg9oSDJt20w2aN9QKHEJc647T4owpXCPKoIbOmcRomKYhSK2EA2m/jsk+aND1j/OY2k8/V/q1o4cRkPEi0ILtda9e5qqkL78uc5cbrEo+sdKT8okQH0V/EYoVsxrVM4o+ixcx0ADuk25OrZgrnyF6/tcnTXly0OsvmDnaXto3u24wTkc5tmmcUSNN1RiTUdMkb7utomDw9QIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问(暂时不需要)
    public static String notify_url = "http://www.baidu.com";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问(支付宝完成交易后，告知支付结果到本后台中的接口，该接口主要用来更新订单状态)
    public static String return_url = "http://localhost:8080/payment/callback?method=callback";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
}
