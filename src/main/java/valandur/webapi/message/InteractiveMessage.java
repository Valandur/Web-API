package valandur.webapi.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.message.IInteractiveMessage;
import valandur.webapi.api.message.IInteractiveMessageOption;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InteractiveMessage extends CachedObject<IInteractiveMessage> implements IInteractiveMessage {

    private UUID uuid;
    @Override
    public UUID getUUID() {
        return uuid;
    }

    private String id;
    @Override
    public String getId() {
        return id;
    }

    private String target;
    @Override
    @JsonDetails
    public String getTarget() {
        return target;
    }

    private List<String> targets;
    @Override
    @JsonDetails
    public List<String> getTargets() { return targets; }

    private String message;
    @Override
    @JsonDetails
    public Text getMessage() {
        if (message == null) {
            return null;
        }
        return TextSerializers.FORMATTING_CODE.deserialize(message);
    }

    private Boolean once;
    @Override
    @JsonDetails
    public Boolean isOnce() {
        return once;
    }

    private List<InteractiveMessageOption> options;
    @Override
    @JsonDetails
    public List<IInteractiveMessageOption> getOptions() {
        return new ArrayList<>(options);
    }

    @Override
    @JsonIgnore
    public boolean hasOptions() {
        return options != null;
    }




    public InteractiveMessage() {
        super(null);

        this.uuid = UUID.randomUUID();
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/message/" + uuid;
    }

    @Override
    public Optional<IInteractiveMessage> getLive() {
        return super.getLive();
    }
}
