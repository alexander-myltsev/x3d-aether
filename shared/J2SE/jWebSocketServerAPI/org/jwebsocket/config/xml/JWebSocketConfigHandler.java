//	---------------------------------------------------------------------------
//	jWebSocket - Copyright (c) 2010 jwebsocket.org
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package org.jwebsocket.config.xml;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javolution.util.FastList;

import javolution.util.FastMap;

import org.jwebsocket.config.ConfigHandler;
import org.jwebsocket.config.JWebSocketConfig;
import org.jwebsocket.config.LoggingConfig;
import org.jwebsocket.config.LoggingConfigHandler;
import org.jwebsocket.kit.WebSocketRuntimeException;

/**
 * Handler class that handles the <tt>jWebSocket.xml</tt> configuration. This
 * class starts from the root and delegates the handler to specific config
 * handler, to read the whole config file.
 * 
 * @author puran
 * @version $Id: JWebSocketConfigHandler.java 596 2010-06-22 17:09:54Z fivefeetfurther $
 */
public class JWebSocketConfigHandler implements ConfigHandler {

    // We cannot use the logging subsystem here because its config needs to be loaded first!

    private static final String ELEMENT_INSTALLATION = "installation";
    private static final String ELEMENT_PROTOCOL = "protocol";
    private static final String ELEMENT_INITIALIZER_CLASS = "initializerClass";
    private static final String ELEMENT_JWEBSOCKET_HOME = "jWebSocketHome";
    private static final String ELEMENT_LIBRARY_FOLDER = "libraryFolder";
    private static final String ELEMENT_ENGINES = "engines";
    private static final String ELEMENT_ENGINE = "engine";
    private static final String ELEMENT_SERVERS = "servers";
    private static final String ELEMENT_SERVER = "server";
    private static final String ELEMENT_PLUGINS = "plugins";
    private static final String ELEMENT_PLUGIN = "plugin";
    private static final String ELEMENT_FILTERS = "filters";
    private static final String ELEMENT_FILTER = "filter";
    private static final String ELEMENT_LOGGING = "logging";
    private static final String ELEMENT_LOG4J = "log4j";
    private static final String ELEMENT_RIGHTS = "rights";
    private static final String ELEMENT_RIGHT = "right";
    private static final String ELEMENT_ROLES = "roles";
    private static final String ELEMENT_ROLE = "role";
    private static final String ELEMENT_USERS = "users";
    private static final String ELEMENT_USER = "user";
    private static final String JWEBSOCKET = "jWebSocket";

    private static FastMap<String, ConfigHandler> handlerContext = new FastMap<String, ConfigHandler>();

    // initialize the different config handler implementations
    static {
        handlerContext.put("engine", new EngineConfigHandler());
        handlerContext.put("plugin", new PluginConfigHandler());
        handlerContext.put("server", new ServerConfigHandler());
        handlerContext.put("user", new UserConfigHandler());
        handlerContext.put("role", new RoleConfigHandler());
        handlerContext.put("right", new RightConfigHandler());
        handlerContext.put("filter", new FilterConfigHandler());
        handlerContext.put("log4j", new LoggingConfigHandler());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JWebSocketConfig processConfig(XMLStreamReader streamReader) {
        JWebSocketConfig.Builder configBuilder = new JWebSocketConfig.Builder();

        try {
            while (streamReader.hasNext()) {
                streamReader.next();
                if (streamReader.isStartElement()) {
                    String elementName = streamReader.getLocalName();

                    if (elementName.equals(ELEMENT_INSTALLATION)) {
                        streamReader.next();
                        configBuilder.addInstallation(streamReader.getText());
                    } else if (elementName.equals(ELEMENT_INITIALIZER_CLASS)) {
                        streamReader.next();
                        configBuilder.addInitializer(streamReader.getText());
                    } else if (elementName.equals(ELEMENT_PROTOCOL)) {
                        streamReader.next();
                        configBuilder.addProtocol(streamReader.getText());
                    } else if (elementName.equals(ELEMENT_JWEBSOCKET_HOME)) {
                        streamReader.next();
                        configBuilder.addJWebSocketHome(streamReader.getText());
                    } else if (elementName.equals(ELEMENT_LIBRARY_FOLDER)) {
                        streamReader.next();
                        configBuilder.addLibraryFolder(streamReader.getText());
                    } else if (elementName.equals(ELEMENT_ENGINES)) {
                        List<EngineConfig> engines = handleEngines(streamReader);
                        configBuilder = configBuilder.addEngines(engines);
                    } else if (elementName.equals(ELEMENT_SERVERS)) {
                        List<ServerConfig> servers = handleServers(streamReader);
                        configBuilder = configBuilder.addServers(servers);
                    } else if (elementName.equals(ELEMENT_PLUGINS)) {
                        List<PluginConfig> plugins = handlePlugins(streamReader);
                        configBuilder = configBuilder.addPlugins(plugins);
                    } else if (elementName.equals(ELEMENT_FILTERS)) {
                        List<FilterConfig> filters = handleFilters(streamReader);
                        configBuilder = configBuilder.addFilters(filters);
                    } else if (elementName.equals(ELEMENT_LOGGING)) {
                        List<LoggingConfig> loggingConfigs = handleLoggingConfigs(streamReader);
                        configBuilder = configBuilder.addLoggingConfig(loggingConfigs);
                    } else if (elementName.equals(ELEMENT_RIGHTS)) {
                        List<RightConfig> globalRights = handleRights(streamReader);
                        configBuilder = configBuilder.addGlobalRights(globalRights);
                    } else if (elementName.equals(ELEMENT_ROLES)) {
                        List<RoleConfig> roles = handleRoles(streamReader);
                        configBuilder = configBuilder.addGlobalRoles(roles);
                    } else if (elementName.equals(ELEMENT_USERS)) {
                        List<UserConfig> users = handleUsers(streamReader);
                        configBuilder = configBuilder.addUsers(users);
                    } else {
                        // ignore
                    }
                }
                if (streamReader.isEndElement()) {
                    String elementName = streamReader.getLocalName();
                    if (elementName.equals(JWEBSOCKET)) {
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new WebSocketRuntimeException("Error parsing jWebSocket.xml configuration file", e);
        }
        // now return the config object, this is the only one config object that
        // should exists
        // in the system
        return configBuilder.buildConfig();
    }

    /**
     * private method to handle the user config.
     * 
     * @param streamReader
     *            the stream reader object
     * @return the list of user config
     * @throws XMLStreamException
     *             if there's any exception reading configuration
     */
    private List<UserConfig> handleUsers(XMLStreamReader streamReader) throws XMLStreamException {
        List<UserConfig> users = new FastList<UserConfig>();
        while (streamReader.hasNext()) {
            streamReader.next();
            if (streamReader.isStartElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_USER)) {
                    UserConfig user = (UserConfig) handlerContext.get(elementName).processConfig(streamReader);
                    users.add(user);
                }
            }
            if (streamReader.isEndElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_USERS)) {
                    break;
                }
            }
        }
        return users;
    }

    /**
     * method that reads the roles configuration
     * 
     * @param streamReader
     *            the stream reader object
     * @return the list of roles config
     * @throws XMLStreamException
     *             if there's any exception reading configuration
     */
    private List<RoleConfig> handleRoles(XMLStreamReader streamReader) throws XMLStreamException {
        List<RoleConfig> roles = new FastList<RoleConfig>();
        while (streamReader.hasNext()) {
            streamReader.next();
            if (streamReader.isStartElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_ROLE)) {
                    RoleConfig role = (RoleConfig) handlerContext.get(elementName).processConfig(streamReader);
                    roles.add(role);
                }
            }
            if (streamReader.isEndElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_ROLES)) {
                    break;
                }
            }
        }
        return roles;
    }

    /**
     * private method to read the list of rights configuration
     * 
     * @param streamReader
     *            the stream reader object
     * @return the list of rights configuration
     * @throws XMLStreamException
     *             if there's any exception reading configuration
     */
    private List<RightConfig> handleRights(XMLStreamReader streamReader) throws XMLStreamException {
        List<RightConfig> rights = new FastList<RightConfig>();
        while (streamReader.hasNext()) {
            streamReader.next();
            if (streamReader.isStartElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_RIGHT)) {
                    RightConfig right = (RightConfig) handlerContext.get(elementName).processConfig(streamReader);
                    rights.add(right);
                }
            }
            if (streamReader.isEndElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_RIGHTS)) {
                    break;
                }
            }
        }
        return rights;
    }

    /**
     * private method that reads the config for plugins
     * 
     * @param streamReader
     *            the stream reader object
     * @return the list of plugin configs
     * @throws XMLStreamException
     *             if exception occurs while reading
     */
    private List<PluginConfig> handlePlugins(XMLStreamReader streamReader) throws XMLStreamException {
        List<PluginConfig> plugins = new FastList<PluginConfig>();
        while (streamReader.hasNext()) {
            streamReader.next();
            if (streamReader.isStartElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_PLUGIN)) {
                    PluginConfig plugin = (PluginConfig) handlerContext.get(elementName).processConfig(streamReader);
                    plugins.add(plugin);
                }
            }
            if (streamReader.isEndElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_PLUGINS)) {
                    break;
                }
            }
        }
        return plugins;
    }

    /**
     * private method that reads the config for filters
     * 
     * @param streamReader
     *            the stream reader object
     * @return the list of filter configs
     * @throws XMLStreamException
     *             if exception occurs while reading
     */
    private List<FilterConfig> handleFilters(XMLStreamReader streamReader) throws XMLStreamException {
        List<FilterConfig> filters = new FastList<FilterConfig>();
        while (streamReader.hasNext()) {
            streamReader.next();
            if (streamReader.isStartElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_FILTER)) {
                    FilterConfig filter = (FilterConfig) handlerContext.get(elementName).processConfig(streamReader);
                    filters.add(filter);
                }
            }
            if (streamReader.isEndElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_FILTERS)) {
                    break;
                }
            }
        }
        return filters;
    }

    /**
     * private method that reads the config for logging
     * 
     * @param streamReader
     *            the stream reader object
     * @return the list of logging configs
     * @throws XMLStreamException
     *             if exception occurs while reading
     */
    private List<LoggingConfig> handleLoggingConfigs(XMLStreamReader streamReader) throws XMLStreamException {
        List<LoggingConfig> loggingConfigs = new FastList<LoggingConfig>();
        while (streamReader.hasNext()) {
            streamReader.next();
            if (streamReader.isStartElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_LOG4J)) {
                    LoggingConfig loggingConfig = (LoggingConfig) handlerContext.get(elementName).processConfig(streamReader);
                    loggingConfigs.add(loggingConfig);
                }
            }
            if (streamReader.isEndElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_LOGGING)) {
                    break;
                }
            }
        }
        return loggingConfigs;
    }

    /**
     * private method that reads the list of server configs
     * 
     * @param streamReader
     *            the stream reader object
     * @return the list of server configs
     * @throws XMLStreamException
     *             if exception occurs reading xml
     */
    private List<ServerConfig> handleServers(XMLStreamReader streamReader) throws XMLStreamException {
        List<ServerConfig> servers = new FastList<ServerConfig>();
        while (streamReader.hasNext()) {
            streamReader.next();
            if (streamReader.isStartElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_SERVER)) {
                    ServerConfig server = (ServerConfig) handlerContext.get(elementName).processConfig(streamReader);
                    servers.add(server);
                }
            }
            if (streamReader.isEndElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_SERVERS)) {
                    break;
                }
            }
        }
        return servers;
    }

    /**
     * private method that reads the list of engines config from the xml file
     * 
     * @param streamReader
     *            the stream reader object
     * @return the list of engine configs
     * @throws XMLStreamException
     *             if exception occurs while reading
     */
    private List<EngineConfig> handleEngines(XMLStreamReader streamReader) throws XMLStreamException {
        List<EngineConfig> engines = new FastList<EngineConfig>();
        while (streamReader.hasNext()) {
            streamReader.next();
            if (streamReader.isStartElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_ENGINE)) {
                    EngineConfig engine = (EngineConfig) handlerContext.get(elementName).processConfig(streamReader);
                    engines.add(engine);
                }
            }
            if (streamReader.isEndElement()) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(ELEMENT_ENGINES)) {
                    break;
                }
            }
        }
        return engines;
    }
}
