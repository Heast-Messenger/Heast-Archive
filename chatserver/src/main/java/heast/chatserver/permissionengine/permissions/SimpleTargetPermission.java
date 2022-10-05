package heast.chatserver.permissionengine.permissions;

public class SimpleTargetPermission implements TargetPermission {
    private String name;
    private String readableName;
    private int id;
    private PermissionTarget target;

    public SimpleTargetPermission(String name, String readableName, int id, PermissionTarget target) {
        this.name = name;
        this.readableName = readableName;
        this.id = id;
        this.target = target;
    }

    @Override
    public String getReadableName() {
        return this.readableName;
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

