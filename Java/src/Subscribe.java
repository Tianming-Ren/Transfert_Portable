import redis.clients.jedis.JedisPubSub;

public class Subscribe extends JedisPubSub {
	IHM ihm;

	public void setIHM(IHM ihm) {
		this.ihm = ihm;
	}

	@Override
	/*
	 * When a message is received in channel "TransfertPortable", verify the state
	 * and change it if necessary. If a message is received in channel "Message",
	 * just show the message.
	 */
	public void onMessage(String channel, String message) {
		System.out.println("Message received, channel:" + channel + ", message:" + message);
		if (channel.contentEquals("TransfertPortable")) {
			if (message.contentEquals("TActived") || message.contentEquals("BActived")
					|| message.contentEquals("Desactived")) {
				ihm.checkState();
			}
		} else if (channel.contentEquals("Message")) {
			ihm.textAreaReceive.append(message + "\n");
		}
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {
		System.out.println(pattern + "," + channel + "," + message);
	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		ihm.onConnected();
		System.out.println("onSubscribe: channel[" + channel + "]," + "subscribedChannels[" + subscribedChannels + "]");
	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		System.out.println(
				"onUnsubscribe: channel[" + channel + "], " + "subscribedChannels[" + subscribedChannels + "]");
	}

	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) {
		System.out.println(
				"onPUnsubscribe: pattern[" + pattern + "]," + "subscribedChannels[" + subscribedChannels + "]");
	}

	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {
		System.out
				.println("onPSubscribe: pattern[" + pattern + "], " + "subscribedChannels[" + subscribedChannels + "]");

	}

}
