package com.kailoslab.ai4x.commons.exception;

import com.kailoslab.ai4x.commons.code.Length;

public class Ai4xExceptionMessage {

    public static final String E000001 = "Already existed the %s-%s";
    public static final String E000002 = "Already deleted the %s-%s";
    public static final String E000003 = "Cannot save a empty id";
    public static final String E000004 = "A length of id must be less than " + Length.id;
    public static final String E000005 = "Cannot save a empty name";
    public static final String E000006 = "A length of name must be less than " + Length.name;
    public static final String E000007 = "Cannot save a empty title";
    public static final String E000008 = "A length of title must be less than " + Length.title;
    public static final String E000009 = "Cannot save a empty value";
    public static final String E000010 = "Cannot find the %s-%s";
}
