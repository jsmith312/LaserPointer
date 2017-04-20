package edu.arizona.group5;

/**
 * Simple connection object for helping with listing previous connections.
 * 
 * @authorWilliam Snider
 * 
 */
public class Connection {

    private String host;
    private int port;

    public Connection(String host, int port) {
	setHost(host);
	setPort(port);
    }

    public String getHost() {
	return host;
    }

    public int getPort() {
	return port;
    }

    public void setHost(String host) {
	this.host = host;
    }

    public void setPort(int port) {
	this.port = port;
    }

    public String toString() {
	return "Host: " + host + " Port: " + port;
    }
}
