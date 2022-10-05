package heast.chatserver.permissionengine.permissions.identifications;

public enum VoicePermissions {
    MUTE(8), DEAFEN(9), KICK(10);

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
