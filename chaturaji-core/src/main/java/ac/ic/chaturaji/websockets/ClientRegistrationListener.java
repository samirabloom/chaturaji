package ac.ic.chaturaji.websockets;

import io.netty.channel.Channel;

/**
 * @author samirarabbanian
 */
public interface ClientRegistrationListener {

    public void onRegister(Channel channel);

}
