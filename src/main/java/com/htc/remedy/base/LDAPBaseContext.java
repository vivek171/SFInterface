package com.htc.remedy.base;

import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

/**
 * Created by kvivek on 4/11/2018.
 */
public class LDAPBaseContext extends InitialDirContext implements AutoCloseable {
    public LDAPBaseContext() throws NamingException {
    }

}
