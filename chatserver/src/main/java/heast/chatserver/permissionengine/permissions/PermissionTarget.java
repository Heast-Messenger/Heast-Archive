package heast.chatserver.permissionengine.permissions;

/**
 * Represents a target of a permission.
 * @author LambdaSpg
 * @see Permission
 * @see TargetPermission
 */
public enum PermissionTarget {
    SELF(0),OTHER(1), ALL(2);

    private int id;

    public int getID()
    {
        return this.id;
    }

    /**
     * Creates a new PermissionTarget.
     * @param id The kind of target.
     */
    private PermissionTarget(int id)
    {
        this.id = id;
    }

    public static PermissionTarget getTarget(int id){
        switch (id){
            case 0: return PermissionTarget.SELF;
            case 1: return PermissionTarget.OTHER;
            case 2: return PermissionTarget.ALL;
            default: return PermissionTarget.SELF;
        }
    }
}
