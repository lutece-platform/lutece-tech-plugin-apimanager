/*
 * Copyright (c) 2002-2025, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.apimanager.service;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.business.meecrogate.Meecrogate;
import fr.paris.lutece.plugins.apimanager.business.meecrogate.MeecrogateHome;
import fr.paris.lutece.plugins.apimanager.service.utils.PasswordUtils;
import fr.paris.lutece.plugins.apimanager.web.rest.GatewayRest;
import fr.paris.lutece.plugins.apimanager.web.rest.dto.GitResponse;
import fr.paris.lutece.plugins.apimanager.web.rest.dto.MeecrogateAckResponse;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.json.ErrorJsonResponse;
import fr.paris.lutece.util.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;

public class MeecrogateGatewayService
{
    private static final Logger logger = LoggerFactory.getLogger( PasswordUtils.class );

    private static MeecrogateGatewayService _instance = new MeecrogateGatewayService( );

    String gitlabAckUrl = AppPropertiesService.getProperty( "apimanager.meecrogate.gitlabAckUrl", null );
    String instanceName = AppPropertiesService.getProperty( "apimanager.meecrogate.instanceName", null );
    String gitlabAckToken = AppPropertiesService.getProperty( "apimanager.meecrogate.gitlabAckToken", null );
    DateTimeFormatter formatter;
    UncheckedObjectMapper uncheckedObjectMapper;
    UncheckedAckObjectMapper uncheckedAckObjectMapper;

    private MeecrogateGatewayService( )
    {
        uncheckedObjectMapper = new UncheckedObjectMapper();
        uncheckedAckObjectMapper = new UncheckedAckObjectMapper();
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    }


    public static MeecrogateGatewayService getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new MeecrogateGatewayService( );
        }
        return _instance;
    }


    public MeecrogateAckResponse getStatus(String env)
    {
        MeecrogateAckResponse meecrogateAckResponse = new MeecrogateAckResponse( );
        URI uri = URI.create(gitlabAckUrl + "%2F" + instanceName + "%2Ejson?ref="+env);
        HttpRequest requestBuilder = HttpRequest.newBuilder()
                .headers(
                        "Content-Type", "application/json",
                        "PRIVATE-TOKEN", gitlabAckToken
                )
                .GET()
                .uri(uri)
                .build();
        try {
            GitResponse response = HttpClient.newHttpClient()
                    .sendAsync(requestBuilder, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(uncheckedObjectMapper::readValue)
                    .get();

            byte[] decoded = Base64.getDecoder().decode(response.getContent());
            String decodedStr = new String(decoded, StandardCharsets.UTF_8);
            meecrogateAckResponse = this.uncheckedAckObjectMapper.readValue(decodedStr);
        } catch (Exception e) {
            logger.error(e.getMessage());
          return null;
        }
        return meecrogateAckResponse;
    }


    class UncheckedObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {

        GitResponse readValue(String content) {
            try {
                return this.readValue(content, new TypeReference<GitResponse>() {
                });
            } catch (IOException ioe) {
                throw new CompletionException(ioe);
            }
        }

    }

    class UncheckedAckObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {

        MeecrogateAckResponse readValue(String content) {
            try {
                return this.readValue(content, new TypeReference<MeecrogateAckResponse>() {
                });
            } catch (IOException ioe) {
                throw new CompletionException(ioe);
            }
        }

    }

}
