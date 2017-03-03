package valandur.webapi.misc;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.util.Tristate;
import valandur.webapi.WebAPI;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebAPICommandSource implements CommandSource {
    private String name = WebAPI.NAME;
    private int waitLines = 0;
    private Queue<String> lines = new ConcurrentLinkedQueue<>();

    public WebAPICommandSource() { }
    public WebAPICommandSource(String name, int waitLines) {
        this.name = name;
        this.waitLines = waitLines;
    }

    public List<String> getLines() {
        return new ArrayList(this.lines);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return null;
    }

    @Override
    public SubjectCollection getContainingCollection() {
        return null;
    }

    @Override
    public SubjectData getSubjectData() {
        return null;
    }

    @Override
    public SubjectData getTransientSubjectData() {
        return null;
    }

    @Override
    public boolean hasPermission(Set<Context> contexts, String permission) {
        return true;
    }

    @Override
    public Tristate getPermissionValue(Set<Context> contexts, String permission) {
        return Tristate.TRUE;
    }

    @Override
    public boolean isChildOf(Set<Context> contexts, Subject parent) {
        return false;
    }

    @Override
    public List<Subject> getParents(Set<Context> contexts) {
        return null;
    }

    @Override
    public Optional<String> getOption(Set<Context> contexts, String key) {
        return null;
    }

    @Override
    public String getIdentifier() {
        return "webapi";
    }

    @Override
    public Set<Context> getActiveContexts() {
        return null;
    }

    @Override
    public void sendMessage(Text message) {
        lines.add(message.toPlain());
        if (waitLines > 0 && lines.size() >= waitLines) {
            waitLines = 0;
            synchronized (this) {
                notify();
            }
        }
    }

    @Override
    public MessageChannel getMessageChannel() {
        return MessageChannel.TO_CONSOLE;
    }

    @Override
    public void setMessageChannel(MessageChannel channel) { }
}
