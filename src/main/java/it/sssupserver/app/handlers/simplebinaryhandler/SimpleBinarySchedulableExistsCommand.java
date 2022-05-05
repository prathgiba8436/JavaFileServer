package it.sssupserver.app.handlers.simplebinaryhandler;

import it.sssupserver.app.base.Path;
import it.sssupserver.app.commands.*;
import it.sssupserver.app.commands.schedulables.*;
import it.sssupserver.app.executors.Executor;
import it.sssupserver.app.users.Identity;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SimpleBinarySchedulableExistsCommand extends SchedulableExistsCommand {
    private Identity user;
    @Override
    public void setUser(Identity user) {
        this.user = user;
    }

    @Override
    public Identity getUser() {
        return this.user;
    }

    private SocketChannel out;
    public SimpleBinarySchedulableExistsCommand(ExistsCommand cmd, SocketChannel out) {
        super(cmd);
        this.out = out;
    }

    @Override
    public void reply(boolean exists) throws Exception {
        // 12 bytes header, no payload
        var bytes = new ByteArrayOutputStream(12);
        var bs = new DataOutputStream(bytes);
        // write data to buffer
        bs.writeInt(this.isAuthenticated() ? 2 : 1);    // version
        bs.writeShort(3);  // command: EXISTS
        bs.writeShort(1);  // category: answer
        bs.writeBoolean(exists);    // data bytes
        bs.write(new byte[3]);  // padding
        bs.flush();
        // now data can be sent
        this.out.write(ByteBuffer.wrap(bytes.toByteArray()));
    }

    public static void handle(Executor executor, SocketChannel sc, int version, Identity user, int marker) throws Exception {
        SimpleBinaryHandler.checkCategory(sc);
        String path = SimpleBinaryHelper.readString(sc);
        var cmd = new ExistsCommand(new Path(path));
        var schedulable = new SimpleBinarySchedulableExistsCommand(cmd, sc);
        schedulable.setUser(user);
        executor.scheduleExecution(schedulable);
    }
}
