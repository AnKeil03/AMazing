/* simulates entering a username and password from frontend

 */

package com.seProject.groupProject7.login;

import com.seProject.groupProject7.DatabaseLoader;

public class FrontendSim {

    public static void enterLoginDetails(String user, String pass) {
        System.out.println("[Front End] entered ("+user+","+pass+")");
        String encPass = Cryptography.cipher(pass);
        System.out.println("[Front End] encrypted password to "+encPass);
        System.out.println("[Front End] sending encrypted request to server");
        DatabaseLoader.serverRequests.addLoginRequest(user,encPass);
    }
}
