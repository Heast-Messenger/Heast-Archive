package heast.chatserver.permissionengine.permissions.identifications;

public enum MessagePermissions {
    DELETEMESSAGE(0), EDITMESSAGE(1), CREATECHANNEL(2),
    DELETECHANNEL(3), EDITCHANNEL(4);

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
