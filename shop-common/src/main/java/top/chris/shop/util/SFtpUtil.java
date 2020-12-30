package top.chris.shop.util;


import com.jcraft.jsch.*;

import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.chris.shop.config.SFtpConfig;

import java.io.InputStream;
import java.util.Arrays;
/**
 * 正在使用FTP工具
 */
public class SFtpUtil{

    /**
     * 初始化日志对象
     */
    private static Logger log = LoggerFactory.getLogger(SFtpUtil.class);

    /**
     * 功能描述： 实现文件上传
     *
     * @param fileDir     文件所在的路径
     * @param fileName    文件的名称
     * @param inputStream 文件流
     * @param sFtpConfig  文件相关的配置信息
     * @return 返回上传结果
     * @throws Exception
     */
    public static boolean uploadFile(String fileDir, String fileName, InputStream inputStream, SFtpConfig sFtpConfig) throws Exception {
        ChannelSftp sftp = getSftp(sFtpConfig);
        try {
            fileDir = sFtpConfig.getBasePath() + fileDir;
            boolean dirs = createDirs(fileDir, sftp);
            if (!dirs) {
                log.info("创建文件目录失败！");
                return false;
            }
            sftp.put(inputStream, fileName);
            return true;
        } catch (Exception e) {
            log.info("文件上传失败：{}", e.getMessage());
            return false;
        } finally {
            disconnect(sftp);
        }
    }

    /**
     * 功能描述： 创建文件夹
     *
     * @param dirPath 需要创建文件夹的路径
     * @param sftp    sftp对象
     * @return 返回创建的结果
     */
    private static boolean createDirs(String dirPath, ChannelSftp sftp) {
        if (dirPath != null && !dirPath.isEmpty() && sftp != null) {
            String[] dirs = Arrays.stream(dirPath.split("/"))
                    .filter(a -> a != null && !a.equals(""))
                    .toArray(String[]::new);
            for (String dir : dirs) {
                try {
                    sftp.cd(dir);
                    log.info("进入的目录是 {}", dir);
                } catch (Exception e) {
                    try {
                        sftp.mkdir(dir);
                        log.info("创建的目录是 {}", dir);
                    } catch (SftpException e1) {
                        log.error("创建失败的目录是:{}", dir, e1);
                        e1.printStackTrace();
                    }
                    try {
                        sftp.cd(dir);
                        log.info("进入的目录是 {}", dir);
                    } catch (SftpException e1) {
                        log.error("进入失败的目录是:{}", dir, e1);
                        e1.printStackTrace();
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 删除文件
     *  (无法删除，原因未知)
     * @param directory 要删除文件所在目录
     * @param deleteFile 要删除的文件
     * @param sFtpConfig sftp配置文件
     */
    public static void delete(String directory, String deleteFile,SFtpConfig sFtpConfig) throws JSchException {
        ChannelSftp sftp = getSftp(sFtpConfig);
        String fileDir = sFtpConfig.getBasePath() + directory;
        try {
            log.info("访问的文件目录为："+fileDir);
            sftp.cd(fileDir);
            log.info("删除的文件名称为："+deleteFile);
            sftp.rm("/root/"+fileDir+deleteFile);
        } catch (Exception e) {
            log.info("delete:"+e);
        }
    }

    /**
     * 功能描述： 创建sftp连接
     *
     * @param sFtpConfig sftp连接对象
     * @return 返回 sftp通道对象
     * @throws Exception
     */
    private static ChannelSftp getSftp(SFtpConfig sFtpConfig) throws JSchException {
        JSch jsch = new JSch();
        Session session = getSession(jsch, sFtpConfig.getHost(), sFtpConfig.getUserName(), sFtpConfig.getPort());
        session.setPassword(sFtpConfig.getPassword());
        session.connect(sFtpConfig.getSessionConnectTimeout());
        Channel channel = session.openChannel(sFtpConfig.getProtocol());
        channel.connect(sFtpConfig.getChannelConnectedTimeout());
        return (ChannelSftp) channel;
    }

    /**
     * 创建session
     *
     * @param jsch     jsch对象
     * @param host     sftpIP地址
     * @param username sftp账号
     * @param port     sftp端口
     * @return 返回 session对象
     * @throws Exception
     */
    private static Session getSession(JSch jsch, String host, String username, Integer port) throws JSchException {
        Session session;
        if (port <= 0) {
            session = jsch.getSession(username, host);
        } else {
            session = jsch.getSession(username, host, port);
        }
        if (session == null) {
            return null;
        }
        session.setConfig("StrictHostKeyChecking", "no");
        return session;
    }

    /**
     * 功能描述： 关闭连接
     *
     * @param sftp sftp对象
     */
    private static void disconnect(ChannelSftp sftp) {
        try {
            if (sftp != null) {
                if (sftp.isConnected()) {
                    sftp.disconnect();
                } else if (sftp.isClosed()) {
                    log.info("sftp已经关闭");
                }
                if (null != sftp.getSession()) {
                    sftp.getSession().disconnect();
                }
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

}
