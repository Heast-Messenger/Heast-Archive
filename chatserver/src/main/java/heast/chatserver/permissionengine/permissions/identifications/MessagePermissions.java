package heast.chatserver.permissionengine.permissions.identifications;

public enum MessagePermissions {
    DELETE(0), EDIT(1);

    private int id;

    public int getID()
    {
        return this.id;
    }

    private MessagePermissions(int id)
    {
        this.id = id;
    }
}
