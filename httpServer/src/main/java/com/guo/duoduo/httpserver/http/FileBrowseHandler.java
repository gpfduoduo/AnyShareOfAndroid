package com.guo.duoduo.httpserver.http;


import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import android.text.TextUtils;
import android.util.Log;

import com.guo.duoduo.httpserver.utils.Constant;


/**
 * Created by Guo.Duo duo on 2015/9/5.
 */
public class FileBrowseHandler implements HttpRequestHandler
{
    private static final String tag = FileBrowseHandler.class.getSimpleName();

    private String webRoot;

    public FileBrowseHandler(String webRoot)
    {
        this.webRoot = webRoot;
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse,
            HttpContext httpContext) throws HttpException, IOException
    {

        String target = URLDecoder.decode(httpRequest.getRequestLine().getUri(),
            Constant.ENCODING);

        Log.d(tag, "http request target = " + target);

        final File file = new File(target);

        HttpEntity entity = new StringEntity("", Constant.ENCODING);

        String contentType = "text/html;charset=" + Constant.ENCODING;

        if (!file.exists())
        {
            Log.d(tag, " file is not exist");
            httpResponse.setStatusCode(HttpStatus.SC_NOT_FOUND);
        }
        else if (file.canRead())
        {
            httpResponse.setStatusCode(HttpStatus.SC_OK);
            if (file.isDirectory()) //实现文件夹浏览
            {
                Log.d(tag, " file is directory");
                String msg = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; "
                    + "charset=utf-8\"><title>文件服务</title></head><body><h1>Directory "
                    + target + "</h1><br/>";

                String[] files = file.list(new FilenameFilter()
                {
                    @Override
                    public boolean accept(File file, String s)
                    {
                        return !file.isHidden() && !file.getName().startsWith(".");
                    }
                });
                if (files != null)
                {
                    for (int i = 0; i < files.length; i++)
                    {
                        File curFile = new File(file, files[i]);
                        boolean isDir = curFile.isDirectory();
                        if (isDir)
                        {
                            msg += "<b>";
                            files[i] += "/";
                        }
                        msg += "<a href=\"" + encodeUri(target + files[i]) + "\">"
                            + files[i] + "</a>";
                        // Show file size
                        if (curFile.isFile())
                        {
                            long len = curFile.length();
                            msg += " &nbsp;<font size=2>(";
                            if (len < 1024)
                                msg += len + " bytes";
                            else if (len < 1024 * 1024)
                                msg += len / 1024 + "." + (len % 1024 / 10 % 100) + " KB";
                            else
                                msg += len / (1024 * 1024) + "." + len % (1024 * 1024)
                                    / 10 % 100 + " MB";

                            msg += ")</font>";
                        }
                        msg += "<br/>";
                        if (isDir)
                            msg += "</b>";
                    }
                }
                msg += "</body></html>";
                entity = new StringEntity(msg, Constant.ENCODING);
                httpResponse.setHeader("Content-Type", contentType);
            }
            else
            //实现文件下载
            {
                Log.d(tag, " file is real file");
                String mime = null;
                int dot = file.getCanonicalPath().lastIndexOf(".");
                if (dot >= 0)
                {
                    mime = (String) Constant.theMimeTypes.get(file.getCanonicalPath()
                            .substring(dot + 1).toLowerCase(Locale.ENGLISH));
                    if (TextUtils.isEmpty(mime))
                        mime = Constant.MIME_DEFAULT_BINARY;

                    long fileLength = file.length();
                    httpRequest.addHeader("Content-Length", "" + fileLength);
                    httpResponse.setHeader("Content-Type", mime);
                    httpResponse.addHeader("Content-Description", "File Transfer");
                    httpResponse.addHeader("Content-Disposition", "attachment;filename="
                        + encodeFilename(file));
                    httpResponse.setHeader("Content-Transfer-Encoding", "binary");

                    entity = new EntityTemplate(new ContentProducer()
                    {
                        @Override
                        public void writeTo(OutputStream outStream) throws IOException
                        {
                            write(file, outStream);
                        }
                    });
                }
            }
        }
        else
        {
            Log.d(tag, " file is forbidden");
            httpResponse.setStatusCode(HttpStatus.SC_FORBIDDEN);
        }

        httpResponse.setEntity(entity);
    }

    private String encodeUri(String uri)
    {
        String newUri = "";
        StringTokenizer st = new StringTokenizer(uri, "/ ", true);
        while (st.hasMoreTokens())
        {
            String tok = st.nextToken();
            if (tok.equals("/"))
                newUri += "/";
            else if (tok.equals(" "))
                newUri += "%20";
            else
            {
                newUri += URLEncoder.encode(tok);
            }
        }
        return newUri;
    }

    private void write(File inputFile, OutputStream outStream) throws IOException
    {
        FileInputStream fis = new FileInputStream(inputFile);
        try
        {
            int count;
            byte[] buffer = new byte[Constant.BUFFER_LENGTH];
            while ((count = fis.read(buffer)) != -1)
            {
                outStream.write(buffer, 0, count);
            }
            outStream.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw e;
        }
        finally
        {
            fis.close();
            outStream.close();
        }
    }

    private String encodeFilename(File file) throws IOException
    {
        String filename = URLEncoder.encode(getFilename(file), Constant.ENCODING);
        return filename.replace("+", "%20");
    }

    private String getFilename(File file)
    {
        return file.isFile() ? file.getName() : file.getName() + ".zip";
    }
}
