package heast.chatserver.permissionengine.permissions;

import java.io.Serializable;

public interface Permission extends Serializable {
    String getReadableName();
    String getName();
    int getID();
}
