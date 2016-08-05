package com.smily.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SftpClientUtil {

	/**
	 * 初始化日志引擎
	 */
	private final Logger logger = LoggerFactory.getLogger(SftpClientUtil.class);

	/** Sftp */
	ChannelSftp sftp = null;
	/** 主机 */
	private String host = "";
	/** 端口 */
	private int port = 0;
	/** 用户名 */
	private String username = "";
	/** 密码 */
	private String password = "";

	/**
	 * 构造函数
	 * 
	 * @param host 主机
	 * @param port 端口
	 * @param username 用户名
	 * @param password 密码
	 */
	public SftpClientUtil(String host, int port, String username, String password) {

		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	/**
	 * 连接sftp服务器
	 * 
	 * @throws Exception
	 */
	public void connect() throws Exception {

		JSch jsch = new JSch();
		Session sshSession = jsch.getSession(username, host, port);
		logger.debug(SftpClientUtil.class + "Session created.");

		sshSession.setPassword(password);
		Properties sshConfig = new Properties();
		sshConfig.put("StrictHostKeyChecking", "no");
		sshSession.setConfig(sshConfig);
		sshSession.connect(20000);
		logger.debug(SftpClientUtil.class + " Session connected.");

		logger.debug(SftpClientUtil.class + " Opening Channel.");
		Channel channel = sshSession.openChannel("sftp");
		channel.connect();
		sftp = (ChannelSftp) channel;
		logger.debug(SftpClientUtil.class + " Connected to " + host + ".");
	}

	/**
	 * Disconnect with server
	 * 
	 * @throws Exception
	 */
	public void disconnect() throws Exception {
		if (sftp != null) {
			if (sftp.isConnected()) {
				sftp.disconnect();
			} else if (sftp.isClosed()) {
				logger.debug(SftpClientUtil.class + " sftp is closed already");
			}
		}
	}

	/**
	 * 上传单个文件
	 * 
	 * @param directory 上传的目录
	 * @param uploadFile 要上传的文件
	 * @throws Exception
	 */
	public void upload(String directory, String uploadFile) throws Exception {
		sftp.cd(directory);
		File file = new File(uploadFile);
		sftp.put(new FileInputStream(file), file.getName());
	}

	/**
	 * 上传目录下全部文件
	 * 
	 * @param directory 上传的目录
	 * @throws Exception
	 */
	public void uploadByDirectory(String directory) throws Exception {

		String uploadFile = "";
		List<String> uploadFileList = listFiles(directory);
		Iterator<String> it = uploadFileList.iterator();

		while (it.hasNext()) {
			uploadFile = it.next().toString();
			upload(directory, uploadFile);
		}
	}

	/**
	 * 下载单个文件
	 * 
	 * @param directory 下载目录
	 * @param downloadFile 下载的文件
	 * @param saveDirectory 存在本地的路径
	 * @throws Exception
	 */
	public void download(String directory, String downloadFile, String saveDirectory) throws Exception {
		String saveFile = saveDirectory + "//" + downloadFile;

		sftp.cd(directory);
		File file = new File(saveFile);
		sftp.get(downloadFile, new FileOutputStream(file));
	}

	/**
	 * 下载目录下全部文件
	 * 
	 * @param directory 下载目录
	 * @param saveDirectory 存在本地的路径
	 * @throws Exception
	 */
	public void downloadByDirectory(String directory, String saveDirectory) throws Exception {
		String downloadFile = "";
		List<String> downloadFileList = listFiles(directory);
		Iterator<String> it = downloadFileList.iterator();

		while (it.hasNext()) {
			downloadFile = it.next().toString();
			if (downloadFile.toString().indexOf(".") < 0) {
				continue;
			}
			download(directory, downloadFile, saveDirectory);
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param directory 要删除文件所在目录
	 * @param deleteFile 要删除的文件
	 * @throws Exception
	 */
	public void delete(String directory, String deleteFile) throws Exception {
		sftp.cd(directory);
		sftp.rm(deleteFile);
	}

	/**
	 * 列出目录下的文件
	 * 
	 * @param directory 要列出的目录
	 * @return list 文件名列表
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<String> listFiles(String directory) throws Exception {

		Vector fileList;
		List<String> fileNameList = new ArrayList<String>();

		fileList = sftp.ls(directory);
		Iterator it = fileList.iterator();

		while (it.hasNext()) {
			String fileName = ((LsEntry) it.next()).getFilename();
			if (".".equals(fileName) || "..".equals(fileName)) {
				continue;
			}
			fileNameList.add(fileName);

		}

		return fileNameList;
	}

	/**
	 * 更改文件名
	 * 
	 * @param directory 文件所在目录
	 * @param oldFileNm 原文件名
	 * @param newFileNm 新文件名
	 * @throws Exception
	 */
	public void rename(String directory, String oldFileNm, String newFileNm) throws Exception {
		sftp.cd(directory);
		sftp.rename(oldFileNm, newFileNm);
	}

	public void cd(String directory) throws Exception {
		sftp.cd(directory);
	}

	public InputStream get(String directory) throws Exception {
		InputStream streatm = sftp.get(directory);
		return streatm;
	}

	public static void main(String[] args) {
		String host = "10.6.208.16";
		String username = "pisas";
		String password = "pisas@123";
		int port = 22;
		ChannelSftp sftp = null;
		String localPath = "/Users/Smily/Documents/TestingFolder/Sftp/local";
		String remotePath = "/Users/Smily/Documents/TestingFolder/Sftp/remote";
		String fileListPath = "/Users/Smily/Documents/TestingFolder/Sftp/local/test.txt";
		final String seperator = "/";

		SftpClientUtil sftpClientUtil = new SftpClientUtil(host, port, username, password);
		try {
			sftpClientUtil.connect();
			sftpClientUtil.downloadByDirectory("/MBCL/INTERFACE_FILES/PISAS/OUTPUT/wip", localPath);
			sftpClientUtil.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}