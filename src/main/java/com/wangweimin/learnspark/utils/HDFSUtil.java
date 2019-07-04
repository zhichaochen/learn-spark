package com.wangweimin.learnspark.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author weimin.wang
 * @Date 2019/7/3 10:02
 **/
public class HDFSUtil {

    private static Logger log = LoggerFactory.getLogger(HDFSUtil.class);

    public static final int BUFFER_SIZE = 8 * 1024 * 1024;

    private static FileSystem fileSystem;

    @Value("${hdp.hosts}")
    public void init(String host) {
        log.debug("HdfsUtil init... host:{}",host);
        System.setProperty("HADOOP_USER_NAME", "hadoop");
        try {
            fileSystem = FileSystem.get(URI.create(host), new Configuration());
        } catch (IOException e) {
            log.error("FileSystem instance init failed!" + e.getMessage(), e);
        }
    }

    /**
     * 下载文件到本地
     *
     * @param filePath
     * @param localPath
     * @return
     * @throws Exception
     */
    public static void download(String filePath, String localPath) throws Exception {
        Path path = new Path(filePath);
        FileStatus fileStatus = fileSystem.getFileStatus(path);
        if (fileStatus.isFile()) {
            FSDataInputStream in = fileSystem.open(path);
            OutputStream out = new FileOutputStream(localPath);
            IOUtils.copyBytes(in, out, BUFFER_SIZE);
            in.close();
            out.close();
        }
    }

    /**
     * 删除HDFS文件或者空目录
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static boolean delete(String path) throws Exception {
        Path hdfsPath = new Path(path);
        FileStatus fileStatus = fileSystem.getFileStatus(hdfsPath);
        if (!fileStatus.isDirectory() || fileSystem.listStatus(hdfsPath).length == 0) {
            return fileSystem.delete(hdfsPath, true);
        }

        return false;
    }

    /**
     * 判断路径是否存在
     *
     * @param path hdfs路径
     * @return
     * @throws Exception
     */
    public static boolean exists(String path) throws Exception {
        return fileSystem.exists(new Path(path));
    }

    /**
     * 将文件保存至HDFS
     *
     * @param path    路径
     * @param content 文件内容
     * @return
     */
    public static void upload(String path, byte[] content) throws Exception {
        FSDataOutputStream fsOut = fileSystem.create(new Path(path));
        fsOut.write(content);
        fsOut.close();
    }

    /**
     * @param dst
     * @param localFilePath
     * @throws Exception
     */
    public static void upload(String localFilePath, String dst) throws Exception {
        fileSystem.copyFromLocalFile(new Path(localFilePath), new Path(dst));
    }

    /**
     * 重命名 hdfs文件
     *
     * @param oldPath 老地址
     * @param newPath 新地址
     * @throws Exception
     */
    public static void rename(String oldPath, String newPath) throws Exception {
        if (isDir(oldPath)) {
            throw new RuntimeException("此地址是目录，不可重命名");
        }

        fileSystem.rename(new Path(oldPath), new Path(newPath));
    }

    /**
     * 获取指定HDFS目录的子目录
     *
     * @param rootPath 父目录
     * @return
     * @throws Exception
     */
    public static List<String> getChildren(String rootPath) throws Exception {
        List<String> children = new ArrayList<String>();
        FileStatus[] statusArray = fileSystem.listStatus(new Path(rootPath));
        for (FileStatus status : statusArray) {
            children.add(status.getPath().toString());
        }
        return children;
    }

    /**
     * 检查路径是否为目录
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static boolean isDir(String path) throws Exception {
        Path hdfsPath = new Path(path);
        FileStatus fileStatus = fileSystem.getFileStatus(hdfsPath);
        return fileStatus.isDirectory();
    }

    /**
     * copy文件到hdfs
     *
     * @param localPath
     * @param hdfsPath
     * @throws Exception
     */
    public static void copyFromLocalFile(String localPath, String hdfsPath) throws Exception {
        // 要上传的源文件所在路径
        Path src = new Path(localPath);
        // hadoop文件系统的跟目录
        Path dst = new Path(hdfsPath);
        // 将源文件copy到hadoop文件系统
        fileSystem.copyFromLocalFile(src, dst);
    }

    public static void saveObject(Object obj, String hdfsPath) {
        ObjectOutputStream oos = null;
        try {
            Path path = new Path(hdfsPath);
            oos = new ObjectOutputStream(new FSDataOutputStream(fileSystem.create(path)));
            oos.writeObject(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
