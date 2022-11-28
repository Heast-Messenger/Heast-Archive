package heast.chatserver.permissionengine;

import java.util.BitSet;

public class PermissionRole {
    //TODO: implement

    /**
     * Simple name for the role
     */
    private String name;
    /**
     * Role ID <br>
     * Used to identify the role, chosen by creation date (earliest created role has the lowest ID)
     */
    private int rid;

    /**
    * 0: Top (Owner) <br>
    * 1: Admin <br>
    * 2: Moderator <br>
    * 3: Member <br>
    * 4: etc. <br>
    * <p></p>
    * The lower the number, the more high ranking the role is, thus can override permissions of lower roles
     */
    private int hierarchy;

    //TODO: init permission with correct size, possibly with the use of the PermissionHandler getGlobalPermissionAmount() method
    /**
     * Saves the permissions of the role within a BitSet because every User Permission has a unique id
     */
    private BitSet permissions;

    public PermissionRole(String name, int rid, int hierarchy, BitSet permissions) {
        this.name = name;
        this.rid = rid;
        this.hierarchy = hierarchy;
        this.permissions = permissions;
    }

    /**
     * Gets all the necessary information from the database to create a PermissionClient
     * @param rid the role id
     * @return the PermissionRole
     */
    public static PermissionRole getRole(int rid){
        //TODO implement
        return null;
    }
}
