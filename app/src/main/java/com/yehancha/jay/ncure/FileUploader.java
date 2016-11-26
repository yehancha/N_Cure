package com.yehancha.jay.ncure;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUploader {
    private static final int BUFFER_SIZE = 1024 * 1024;
    private static final String TWO_HYPHENS = "--";
    private static final String LINE_END = "\r\n";
    private static final String BOUNDARY = "apiclient-" + System.currentTimeMillis();
    private static final String MIME_TYPE = "multipart/form-data;boundary=" + BOUNDARY;

    private OnFileUploadResultListener onFileUploadResultListener;

    private byte[] multipartBody;
    private String url;
    private String relativeFilePathOnServer;

    private Context context;

    public FileUploader(Context context, byte[] fileData, String url, String relativeFilePathOnServer, OnFileUploadResultListener onFileUploadResultListener) throws Exception {
        this.context = context;
        this.url = url;
        this.relativeFilePathOnServer = relativeFilePathOnServer;
        this.onFileUploadResultListener = onFileUploadResultListener;

        buildMultipartBody(fileData);
    }

    public void upload() {
        MultipartRequest multipartRequest = new MultipartRequest(url, null, MIME_TYPE, multipartBody, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if (onFileUploadResultListener != null) {
                    onFileUploadResultListener.onFileUploadResult(new String(response.data), null /* no errors */);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (onFileUploadResultListener != null) {
                    String stringError = error.getMessage();
                    if (stringError == null) {
                        stringError = "error";
                    }
                    onFileUploadResultListener.onFileUploadResult(null /* no results */, stringError);
                }
            }
        });
        Volley.newRequestQueue(context).add(multipartRequest);
    }

    private void buildMultipartBody(byte[] fileData) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        buildPart(dos, fileData, relativeFilePathOnServer);
        // send multipart form data necesssary after file data
        dos.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END);
        // pass to multipart body
        multipartBody = bos.toByteArray();
    }

    private void buildPart(DataOutputStream dataOutputStream, byte[] fileData, String fileName) throws IOException {
        dataOutputStream.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""
                + fileName + "\"" + LINE_END);
        dataOutputStream.writeBytes(LINE_END);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = BUFFER_SIZE;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        // read file and write it into form...
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(LINE_END);
    }

    public static byte[] readFile(File file) throws Exception {
        FileInputStream inputStream = new FileInputStream(file);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];

        while (true) {
            int bytesRead = inputStream.read( buffer, 0, BUFFER_SIZE );
            if (bytesRead == -1) {
                break;
            }
            byteArrayOutputStream.write( buffer, 0, bytesRead );
        }

        byteArrayOutputStream.flush();
        byte[] result = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        inputStream.close();

        return result;
    }

    public interface OnFileUploadResultListener {
        void onFileUploadResult(String result, String error);
    }
}
