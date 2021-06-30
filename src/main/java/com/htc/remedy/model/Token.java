package com.htc.remedy.model;


public class Token
{
    private String access_token;

    private String refresh_token;

    private String id_token;

    private String token_type;

    private String expires_in;

    public String getAccess_token ()
    {
        return access_token;
    }

    public void setAccess_token (String access_token)
    {
        this.access_token = access_token;
    }

    public String getRefresh_token ()
    {
        return refresh_token;
    }

    public void setRefresh_token (String refresh_token)
    {
        this.refresh_token = refresh_token;
    }

    public String getId_token ()
    {
        return id_token;
    }

    public void setId_token (String id_token)
    {
        this.id_token = id_token;
    }

    public String getToken_type ()
    {
        return token_type;
    }

    public void setToken_type (String token_type)
    {
        this.token_type = token_type;
    }

    public String getExpires_in ()
    {
        return expires_in;
    }

    public void setExpires_in (String expires_in)
    {
        this.expires_in = expires_in;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [access_token = "+access_token+", refresh_token = "+refresh_token+", id_token = "+id_token+", token_type = "+token_type+", expires_in = "+expires_in+"]";
    }
}