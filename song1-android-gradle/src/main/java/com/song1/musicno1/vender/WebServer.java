package com.song1.musicno1.vender;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.MediaColumns.DATA;

public class WebServer extends NanoHTTPD {
  /**
   * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
   */
  private static final Map<String, String> MIME_TYPES = new HashMap<String, String>() {{
    put("css", "text/css");
    put("htm", "text/html");
    put("html", "text/html");
    put("xml", "text/xml");
    put("txt", "text/plain");
    put("asc", "text/plain");
    put("gif", "image/gif");
    put("jpg", "image/jpeg");
    put("jpeg", "image/jpeg");
    put("png", "image/png");
    put("mp3", "audio/mpeg");
    put("m3u", "audio/mpeg-url");
    put("wav", "audio/wav");
    put("mp4", "video/mp4");
    put("ogv", "video/ogg");
    put("flv", "video/x-flv");
    put("mov", "video/quicktime");
    put("swf", "application/x-shockwave-flash");
    put("js", "application/javascript");
    put("pdf", "application/pdf");
    put("doc", "application/msword");
    put("ogg", "application/x-ogg");
    put("zip", "application/octet-stream");
    put("exe", "application/octet-stream");
    put("class", "application/octet-stream");
  }};

  /**
   * The distribution licence
   */
  private static final String LICENCE =
      "Copyright (c) 2012-2013 by Paul S. Hawke, 2001,2005-2013 by Jarno Elonen, 2010 by Konstantinos Togias\n"
          + "\n"
          + "Redistribution and use in source and binary forms, with or without\n"
          + "modification, are permitted provided that the following conditions\n"
          + "are met:\n"
          + "\n"
          + "Redistributions of source code must retain the above copyright notice,\n"
          + "this list of conditions and the following disclaimer. Redistributions in\n"
          + "binary form must reproduce the above copyright notice, this list of\n"
          + "conditions and the following disclaimer in the documentation and/or other\n"
          + "materials provided with the distribution. The name of the author may not\n"
          + "be used to endorse or promote products derived from this software without\n"
          + "specific prior written permission. \n"
          + " \n"
          + "THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR\n"
          + "IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES\n"
          + "OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.\n"
          + "IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,\n"
          + "INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT\n"
          + "NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,\n"
          + "DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY\n"
          + "THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n"
          + "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\n"
          + "OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";
  private final Context context;

  public WebServer(String host, int port, Context context) {
    super(host, port);
    this.context = context;
  }

  public WebServer(int port, Context context) {
    super(port);
    this.context = context;
  }

  /**
   * URL-encodes everything between "/"-characters. Encodes spaces as '%20' instead of '+'.
   */
  private String encodeUri(String uri) {
    String newUri = "";
    StringTokenizer st = new StringTokenizer(uri, "/ ", true);
    while (st.hasMoreTokens()) {
      String tok = st.nextToken();
      if (tok.equals("/"))
        newUri += "/";
      else if (tok.equals(" "))
        newUri += "%20";
      else {
        try {
          newUri += URLEncoder.encode(tok, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
      }
    }
    return newUri;
  }

  /**
   * Serves file from homeDir and its' subdirectories (only). Uses only URI, ignores all headers and HTTP parameters.
   */
  public Response serveFile(Map<String, String> header, File file_path) {
    Response res = null;
    if (!file_path.exists())
      res = new Response(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");

    try {
      if (res == null) {
        // Get MIME type from file name extension, if possible
        String mime = null;
        int dot = file_path.getCanonicalPath().lastIndexOf('.');
        if (dot >= 0)
          mime = MIME_TYPES.get(file_path.getCanonicalPath().substring(dot + 1).toLowerCase());
        if (mime == null)
          mime = NanoHTTPD.MIME_DEFAULT_BINARY;

        // Calculate etag
        String etag = Integer.toHexString((file_path.getAbsolutePath() + file_path.lastModified() + "" + file_path.length()).hashCode());

        // Support (simple) skipping:
        long startFrom = 0;
        long endAt = -1;
        String range = header.get("range");
        if (range != null) {
          if (range.startsWith("bytes=")) {
            range = range.substring("bytes=".length());
            int minus = range.indexOf('-');
            try {
              if (minus > 0) {
                startFrom = Long.parseLong(range.substring(0, minus));
                endAt = Long.parseLong(range.substring(minus + 1));
              }
            } catch (NumberFormatException ignored) {
            }
          }
        }

        // Change return code and add Content-Range header when skipping is requested
        long fileLen = file_path.length();
        if (range != null && startFrom >= 0) {
          if (startFrom >= fileLen) {
            res = new Response(Response.Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
            res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
            res.addHeader("ETag", etag);
          } else {
            if (endAt < 0)
              endAt = fileLen - 1;
            long newLen = endAt - startFrom + 1;
            if (newLen < 0)
              newLen = 0;

            final long dataLen = newLen;
            FileInputStream fis = new FileInputStream(file_path) {
              @Override
              public int available() throws IOException {
                return (int) dataLen;
              }
            };
            fis.skip(startFrom);

            res = new Response(Response.Status.PARTIAL_CONTENT, mime, fis);
            res.addHeader("Content-Length", "" + dataLen);
            res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
            res.addHeader("ETag", etag);
          }
        } else {
          if (etag.equals(header.get("if-none-match")))
            res = new Response(Response.Status.NOT_MODIFIED, mime, "");
          else {
            res = new Response(Response.Status.OK, mime, new FileInputStream(file_path));
            res.addHeader("Content-Length", "" + fileLen);
            res.addHeader("ETag", etag);
          }
        }
      }
    } catch (IOException ioe) {
      res = new Response(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
    }

    res.addHeader("Accept-Ranges", "bytes"); // Announce that the file server accepts partial content requestes
    return res;
  }

  @Override
  public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parms, Map<String, String> files) {
    Log.d("Song1", "Http server serve: " + parms.get("file"));
    if (!method.equals(Method.GET)) {
      return new Response(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Method not supported.");
    }

    if (uri.isEmpty()) {
      return new Response(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, File not found.");
    }

    if (!uri.equals("/shared")) {
      return new Response(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, File not found.");
    }

    String file_path = parms.get("file");
    if (file_path == null) {
      return new Response(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, File not found.");
    }

    File file = new File(file_path);
    if (file.isDirectory() || !file.exists()) {
      return new Response(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, File not found.");
    }

    return serveFile(header, file);
  }

  private String getFilePathById(String id) {
    Cursor cursor = context.getContentResolver().query(
        EXTERNAL_CONTENT_URI,
        new String[]{ DATA },
        "_id=?",
        new String[]{ id },
        null
    );
    if (cursor == null) return null;
    if (!cursor.moveToFirst()) return null;
    return cursor.getString(0);
  }
}
