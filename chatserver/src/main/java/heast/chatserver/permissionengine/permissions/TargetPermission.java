package heast.chatserver.permissionengine.permissions;

/**
 * Represents a permission with a specific target.
 * @author LambdaSpg
 * @see Permission
 * @see PermissionTarget
 */
public interface TargetPermission extends Permission {
    String getReadableName();
    String getName();
    int getID();
    PermissionTarget getTarget();
}
