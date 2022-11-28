package heast.chatserver.permissionengine.permissions;

import java.io.Serializable;

public interface Permission extends Serializable {
    String getDescription();
    String getName();
    int getID();
}
