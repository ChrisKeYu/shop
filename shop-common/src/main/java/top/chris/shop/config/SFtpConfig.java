package top.chris.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * SFtp服务器可配置实体类
 *
 */
@Component
public class SFtpConfig {
    /**
     * IP
     */
    @Value("${sftp.host}")
    private String host;

    /**
     * 账号
     */
    @Value("${sftp.userName}")
    private String userName;

    /**
     * 密码
     */
    @Value("${sftp.password}")
    private String password;

    /**
     * 基础路径
     */
    @Value("${sftp.basePath}")
    private String basePath;

    /**
     * 协议
     */
    @Value("${sftp.protocol}")
    private String protocol;

    /**
     * 端口
     */
    @Value("${sftp.port}")
    private Integer port;

    /**
     * session连接超时时间
     */
    @Value("${sftp.sessionConnectTimeout}")
    private Integer sessionConnectTimeout;

    /**
     * channel连接超时时间
     */
    @Value("${sftp.channelConnectedTimeout}")
    private Integer channelConnectedTimeout;

    /**
     * 下载地址的基础url，这个是配置的图片服务器的地址,最后访问图片时候，需要用该基础地址：例如：http://192.168.2.2:8089/foodie/category/zs3.jpg
     */
    @Value("${sftp.ImageBaseUrl}")
    private String ImageBaseUrl = "url";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getSessionConnectTimeout() {
        return sessionConnectTimeout;
    }

    public void setSessionConnectTimeout(Integer sessionConnectTimeout) {
        this.sessionConnectTimeout = sessionConnectTimeout;
    }

    public Integer getChannelConnectedTimeout() {
        return channelConnectedTimeout;
    }

    public void setChannelConnectedTimeout(Integer channelConnectedTimeout) {
        this.channelConnectedTimeout = channelConnectedTimeout;
    }

    public String getImageBaseUrl() {
        return ImageBaseUrl;
    }

    public void setImageBaseUrl(String imageBaseUrl) {
        ImageBaseUrl = imageBaseUrl;
    }
}