package com.uoscybercaddy.dabajo.activity;

import com.uoscybercaddy.dabajo.view.WriteInfo;

import java.util.ArrayList;

public class ImageDirectoryHelper
{
    static ArrayList<String> GetImageDirecotry(WriteInfo info)
    {
        String string = new String();
        string += "posts/";
        string += info.getWriter();
        string += "/";
        string += info.createdAt.toString();
        string += "/";

        ArrayList<String> imgPathList = new ArrayList<String>();

        for(int i = 0 ; i < info.imageCount ; i++)
        {
            String imgPathStr = string;
            imgPathStr += Integer.toString(i);
            imgPathStr += ".jpg";
            imgPathList.add(imgPathStr);
        }
        return imgPathList;
    }

    static ArrayList<String> GetVideoDirecotry(WriteInfo info)
    {
        String string = new String();
        string += "posts/";
        string += info.getWriter();
        string += "/";
        string += info.createdAt.toString();
        string += "/";

        ArrayList<String> imgPathList = new ArrayList<String>();

        for(int i = 0 ; i < info.videoCount ; i++)
        {
            String imgPathStr = string;
            imgPathStr += Integer.toString(i);
            imgPathStr += ".";
            imgPathStr += info.videoExtensions.get(i);
            imgPathList.add(imgPathStr);
        }
        return imgPathList;
    }
}
