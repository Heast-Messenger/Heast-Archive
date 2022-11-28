package heast.chatserver.permissionengine;

import javax.management.relation.Role;
import java.util.List;

public class PermissionClient {
    //TODO: implement
    private String name;
    private int id;
    private List<Role> roles;

    public PermissionClient(int id, String name, List<Role> roles) {
        this.id = id;
        this.name = name;
        this.roles = roles;
    }

    /**
     * Gets all the needed information from the database to create a PermissionClient
     * @param id
     * @return
     */
    public static PermissionClient getClient(int id){
        //TODO implement
        return null;
    }
}
