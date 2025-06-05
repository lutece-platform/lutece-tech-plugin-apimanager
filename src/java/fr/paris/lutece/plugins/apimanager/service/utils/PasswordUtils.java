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
package fr.paris.lutece.plugins.apimanager.service.utils;

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils
{

    private static final Logger logger = LoggerFactory.getLogger( PasswordUtils.class );

    private static final int SALT_LENGTH = 16; // bytes
    private static final int HASH_LENGTH = 256; // bits
    private static final int ITERATIONS = 100_000; // security vs. performance tradeoff

    private static final int SECRET_SIZE = AppPropertiesService.getPropertyInt( "apimanager.client.secret.size", 64 );

    /**
     * Generates and returns a newly generated secret, Base64 encoded.
     * 
     * @return Base64 encoded secret
     */
    public static String generateSecurePassword( )
    {
        final SecureRandom secureRandom = new SecureRandom( );
        final byte [ ] keyBytes = new byte [ SECRET_SIZE];
        secureRandom.nextBytes( keyBytes );
        return Base64.getEncoder( ).encodeToString( keyBytes );
    }

    public static String hashPassword( String password ) throws Exception
    {
        byte [ ] salt = generateSalt( );
        byte [ ] hash = pbkdf2( password.toCharArray( ), salt, ITERATIONS, HASH_LENGTH );

        // Combine salt + hash, Base64 encode
        String encodedSalt = Base64.getEncoder( ).encodeToString( salt );
        String encodedHash = Base64.getEncoder( ).encodeToString( hash );

        return String.format( "%d:%s:%s", ITERATIONS, encodedSalt, encodedHash );
    }

    public static boolean verifyPassword( String password, String storedHash )
    {

        String [ ] parts = storedHash.split( ":" );
        if ( parts.length != 3 )
        {
            // we don't give the information about false format
            logger.error( "Invalid stored hash: {}", storedHash );
            return false;
        }

        int iterations = Integer.parseInt( parts [0] );
        byte [ ] salt = Base64.getDecoder( ).decode( parts [1] );
        byte [ ] expectedHash = Base64.getDecoder( ).decode( parts [2] );

        byte [ ] computedHash = null;
        try
        {
            computedHash = pbkdf2( password.toCharArray( ), salt, iterations, expectedHash.length * 8 );
        }
        catch( Exception exception )
        {
            logger.error( "Failed to hash password", exception );
            return false;
        }

        return constantTimeEquals( expectedHash, computedHash );
    }

    private static byte [ ] pbkdf2( char [ ] password, byte [ ] salt, int iterations, int keyLength ) throws Exception
    {
        PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength );
        SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA256" );
        return skf.generateSecret( spec ).getEncoded( );
    }

    private static byte [ ] generateSalt( )
    {
        byte [ ] salt = new byte [ SALT_LENGTH];
        new SecureRandom( ).nextBytes( salt );
        return salt;
    }

    private static boolean constantTimeEquals( byte [ ] a, byte [ ] b )
    {
        if ( a.length != b.length )
        {
            return false;
        }
        int result = 0;
        for ( int i = 0; i < a.length; i++ )
        {
            result |= a [i] ^ b [i];
        }
        return result == 0;
    }
}
