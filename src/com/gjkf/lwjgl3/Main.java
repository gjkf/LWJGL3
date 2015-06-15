/**
 * Copyright (c) 15/06/15 Davide Cossu.
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

package com.gjkf.lwjgl3;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main{

    private static GLFWErrorCallback errorCallback;
    private static long windowID;

    public static void main(String[] args){
        //START OF INIT//

        errorCallback = Callbacks.errorCallbackPrint(System.err); //Error callbacks to print out any error that occurs
        glfwSetErrorCallback(errorCallback);

        int glfwResult = glfwInit();
        if(glfwResult == GL_FALSE){ //In case the initialization fails
            throw new IllegalStateException("GLFW Initialization failed");
        }

        windowID = glfwCreateWindow(640, 380, "Display thing", MemoryUtil.NULL, MemoryUtil.NULL);

        if(windowID == MemoryUtil.NULL){ //In case the initialization fails
            throw new IllegalStateException("Window creation failed");
        }

        glfwMakeContextCurrent(windowID); //Needs to be here
        glfwSwapInterval(1); //Caps the fps to 60
        glfwShowWindow(windowID);

        GLContext.createFromCurrent(); //Binds the OpenGL context to the current thread, the GLFW one

        //END OF INIT//

        //START OF CUSTOMIZATION//

        glClearColor(0F, 0.4F, 0.4F, 1F);

        //END OF CUSTOMIZATION//

        //START OF MAIN LOOP//

        while(glfwWindowShouldClose(windowID) == GL_FALSE){ //As long as the window should not close, keep updating it
            glClear(GL_COLOR_BUFFER_BIT); //Clears everything from the display
            glfwSwapBuffers(windowID); //Swaps the background and the foreground, updating the display
            glfwPollEvents(); //Catches all the mouse/keyboard events
        }

        //END OF MAIN LOOP/

        //START OF SHUTDOWN//

        glfwDestroyWindow(windowID);
        glfwTerminate();

        //END OF SHUTDOWN//
    }

}