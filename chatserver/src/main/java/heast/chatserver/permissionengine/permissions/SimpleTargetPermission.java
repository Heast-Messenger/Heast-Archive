package heast.chatserver.permissionengine.permissions;

public class SimpleTargetPermission implements TargetPermission {
    private String name;
    private String description;
    private int id;
    private PermissionTarget target;

    public SimpleTargetPermission(String name, String desription, int id, PermissionTarget target) {
        this.name = name;
        this.description = desription;
        this.id = id;
        this.target = target;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public PermissionTarget getTarget() {
        return null;
    }
}

