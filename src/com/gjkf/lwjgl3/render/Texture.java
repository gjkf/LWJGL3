/**
 * Copyright (c) 18/06/15 Davide Cossu.
 * <p/>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 */

package com.gjkf.lwjgl3.render;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.PNGDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Util class that handles the use of Textures
 */

public class Texture{

    private int textureID;

    private InputStream inputStream;
    private ByteBuffer textureData;

    private PNGDecoder textureDecoder;

    /**
     * <b>NOTE: Currently only supports {@code *.png}</b>
     * @param stream The location of the file.
     */

    public Texture(InputStream stream){
        this.inputStream = stream;
    }

    /**
     * Binds the texture to the OpenGL context
     */

    public void bindTexture(){
        try{
            textureDecoder = new PNGDecoder(inputStream);
            textureData = BufferUtils.createByteBuffer(4 * textureDecoder.getWidth() * textureDecoder.getHeight());
            textureDecoder.decode(textureData, textureDecoder.getWidth() * 4, PNGDecoder.RGBA);
            textureData.flip();
        }catch(IOException e){
            e.printStackTrace();
        }

        GLContext.createFromCurrent();

        // Enable texture drawing
        glEnable(GL_TEXTURE_2D);
        // Create a texture ID
        textureID = glGenTextures();
        // Bind the texture to the TEXTURE_2D slot (there can only be one bound texture at a time)
        glBindTexture(GL_TEXTURE_2D, textureID);
        // Magnification and minification filters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // Hand the texture data from Java to OpenGL:
        glTexImage2D(GL_TEXTURE_2D, // Texture type (1D, 2D, 3D)
                0, // Level, always set this to zero
                GL_RGBA, // Internal format, RGBA works best
                textureDecoder.getWidth(), // Width of the texture in pixels
                textureDecoder.getHeight(), // Width of the texture in pixels
                0, // Border, always set this to zero
                GL_RGBA, // Texture format, in our case this is RGBA (you can dynamically find the texture type with PNGDecoder)
                GL_UNSIGNED_BYTE, // Type of the texture data, this is always unsigned byte (this should ring a bell with C/C++ programmers)
                textureData);
        // Unbind the texture, in our program this isn't strictly necessary because we have only one texture
        // But it's a good practice for when you have multiple textures
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Draws the texture
     *
     * @param topLeft The topLeft point of the texture
     * @param topRight The topRight point of the texture
     * @param bottomRight The bottomRight point of the texture
     * @param bottomLeft The bottomLeft point of the texture
     */

    public void drawTexture(int[] topLeft, int[] topRight, int[] bottomRight, int[] bottomLeft){
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glBindTexture(GL_TEXTURE_2D, textureID);  // Bind the texture

        // We assign texture coordinates to vertex coordinates, which maps the texture to an OpenGL surface
        // (0, 0) is the upper-left corner of the texture
        // (1, 0) is the upper-right corner
        // (0, 1) is the bottom-left
        // (1, 1) is the bottom-right
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2i(topLeft[0], topLeft[1]); // Upper-left
        glTexCoord2f(1, 0);
        glVertex2i(topRight[0], topRight[1]); // Upper-right
        glTexCoord2f(1, 1);
        glVertex2i(bottomRight[0], bottomRight[1]); // Bottom-right
        glTexCoord2f(0, 1);
        glVertex2i(bottomLeft[0], bottomLeft[1]); // Bottom-left
        glEnd();

        glDisable(GL_BLEND);

        glBindTexture(GL_TEXTURE_2D, 0); // Unbind the texture
    }

    /**
     * Deletes the texture to prevent any memory leaks
     */

    public void cleanUp(){
        glDeleteTextures(textureID);
    }

}