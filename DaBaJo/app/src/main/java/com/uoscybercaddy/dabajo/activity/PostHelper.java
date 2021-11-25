package com.uoscybercaddy.dabajo.activity;

public class PostHelper
{
    private static String SpecificUserSearchIntentExtraName = "특정유저";
    public static String GetSpecificUserSearchIntentExtraName()
    {
        return SpecificUserSearchIntentExtraName;
    }

    private static String CategoryIntentExtraName = "category";
    public static String GetCategoryIntentExtraName()
    {
        return CategoryIntentExtraName;
    }

    public static String GetDBPath(String category)
    {
        return "posts_" + category;
    }
}
