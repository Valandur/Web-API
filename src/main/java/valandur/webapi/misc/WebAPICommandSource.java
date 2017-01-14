package valandur.webapi.misc;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.util.Tristate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class WebAPICommandSource implements CommandSource {
    List<String> lines = new ArrayList<String>();

    public List<String> getLines() {
        return this.lines;
    }

    @Override
    public String getName() {
        return "Web-API";
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
        this.lines.add(message.toPlain());
    }

    @Override
    public MessageChannel getMessageChannel() {
        return MessageChannel.TO_NONE;
    }

    @Override
    public void setMessageChannel(MessageChannel channel) {
    }
}
