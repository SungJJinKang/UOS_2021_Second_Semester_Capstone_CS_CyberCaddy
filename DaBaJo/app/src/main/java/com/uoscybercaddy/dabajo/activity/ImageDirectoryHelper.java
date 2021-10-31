package com.uoscybercaddy.dabajo.activity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uoscybercaddy.dabajo.view.WriteInfo;

import java.util.ArrayList;

public class ImageDirectoryHelper
{
    static ArrayList<String> GetImageDirecotry(WriteInfo info)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String string = new String();
        string += "posts/";
        string += user.getUid();
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String string = new String();
        string += "posts/";
        string += user.getUid();
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
