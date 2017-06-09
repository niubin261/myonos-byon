package org.onos.byon.cli;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onos.byon.NetworkService;
import org.onosproject.cli.AbstractShellCommand;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onos.byon.NetworkService;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.HostId;
/**
 * Created by niubin on 17-6-9.
 */
@Command(scope = "byon", name = "remove-Network", description = "remove host form a network")
public class RemoveNetworkCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "network", description = "Network name",
            required = true, multiValued = false)
    String network = null;

    @Override
    protected void execute() {
        NetworkService networkService = get(NetworkService.class);
        networkService.deleteNetwork(network);
        print("Remove network %s ",  network);
    }

}
