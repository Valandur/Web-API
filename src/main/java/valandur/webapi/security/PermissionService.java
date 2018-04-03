package valandur.webapi.security;

import ninja.leaping.configurate.ConfigurationNode;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.util.TreeNode;

import java.util.*;

public class PermissionService implements IPermissionService {

    @Override
    public TreeNode<String, Boolean> permissionTreeFromConfig(ConfigurationNode config) {
        if (config == null || config.getValue() == null) {
            return new TreeNode<>(false);
        }

        if (!config.hasMapChildren()) {
            if (config.getValue().getClass() == Boolean.class)
                return new TreeNode<>(config.getKey().toString(), config.getBoolean());
            else {
                TreeNode<String, Boolean> node = new TreeNode<>(config.getKey().toString(), true);
                node.addChild(new TreeNode<>("*", true));
                return node;
            }
        }

        TreeNode<String, Boolean> root = new TreeNode<>(config.getKey().toString(), true);
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : config.getChildrenMap().entrySet()) {
            if (entry.getKey().toString().equalsIgnoreCase(".")) {
                root.setValue(entry.getValue().getBoolean());
                continue;
            }

            root.addChild(permissionTreeFromConfig(entry.getValue()));
        }
        return root;
    }
    @Override
    public void permissionTreeToConfig(ConfigurationNode config, TreeNode<String, Boolean> perms) {
        if (perms == null) {
            return;
        }

        Collection<TreeNode<String, Boolean>> children = perms.getChildren();

        if (children.size() == 0) {
            config.setValue(perms.getValue());
            return;
        }

        TreeNode<String, Boolean> first = children.iterator().next();

        if (children.size() == 1 && perms.getValue() && first.getKey().equalsIgnoreCase("*") && first.getValue()) {
            config.setValue("*");
            return;
        }

        if (!perms.getValue()) {
            config.getNode(".").setValue(false);
        }

        for (TreeNode<String, Boolean> child : children) {
            permissionTreeToConfig(config.getNode(child.getKey()), child);
        }
    }

    @Override
    public boolean permits(TreeNode<String, Boolean> perms, String[] reqPerms) {
        return permits(perms, Arrays.asList(reqPerms));
    }
    @Override
    public boolean permits(TreeNode<String, Boolean> perms, List<String> reqPerms) {
        return  subPermissions(perms, reqPerms).getValue();
    }

    @Override
    public TreeNode<String, Boolean> subPermissions(TreeNode<String, Boolean> perms, String[] path) {
        return subPermissions(perms, Arrays.asList(path));
    }
    @Override
    public TreeNode<String, Boolean> subPermissions(TreeNode<String, Boolean> perms, List<String> path) {
        // Check if we ourselves already are a permit-all permission
        if (perms.getKey() != null && perms.getKey().equalsIgnoreCase("*") && perms.getValue()) {
            return perms;
        }

        // Go through all levels of the required permission
        for (String perm : path) {
            // Get the specific permission node for this level, if we have one
            Optional<TreeNode<String, Boolean>> subPerm = perms.getChild(perm);
            if (subPerm.isPresent()) {
                perms = subPerm.get();
                continue;
            }

            // If we don't have a specific permission for this level, check if there is a "*" permission
            Optional<TreeNode<String, Boolean>> allChild = perms.getChild("*");
            return allChild.orElseGet(IPermissionService::emptyNode);
        }

        // If we get here then that means we have an exact permission for this path
        // or the path had no elements
        return perms;
    }
}
