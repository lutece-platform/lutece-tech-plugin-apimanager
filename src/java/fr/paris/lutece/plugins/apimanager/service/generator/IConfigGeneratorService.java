package fr.paris.lutece.plugins.apimanager.service.generator;

import fr.paris.lutece.plugins.apimanager.business.client.Client;

public interface IConfigGeneratorService {

    void generateOauth2Client(final Client client, final String environment, final String comment, final String user);
}
