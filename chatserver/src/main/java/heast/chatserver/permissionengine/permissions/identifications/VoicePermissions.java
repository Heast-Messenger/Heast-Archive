package heast.chatserver.permissionengine.permissions.identifications;

public enum VoicePermissions {
    CREATECHANNEL(5), DELETECHANNEL(6), EDITCHANNEL(7),
    MUTEUSER(8), DEAFENUSER(9), KICKUSER(10);

    private int id;

    public int getID()
    {
        return this.id;
    }

    private VoicePermissions(int id)
    {
        this.id = id;
    }
}
