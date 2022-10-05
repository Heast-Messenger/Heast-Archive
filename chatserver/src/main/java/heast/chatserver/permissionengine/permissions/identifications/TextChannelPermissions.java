package heast.chatserver.permissionengine.permissions.identifications;

public enum TextChannelPermissions {
    CREATE(2), DELETE(3), EDIT(4);

    private int id;

    public int getID()
    {
        return this.id;
    }

    private TextChannelPermissions(int id)
    {
        this.id = id;
    }
}
