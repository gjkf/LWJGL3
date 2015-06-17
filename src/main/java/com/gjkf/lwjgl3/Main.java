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

package main.java.com.gjkf.lwjgl3;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main{

    private static long windowID, lastFPS;
    private static int mouseX = 0, mouseY = 0, fps;
    private static boolean inputEnabled = true;

    public static void main(String[] args){
        GLFWErrorCallback errorCallback = Callbacks.errorCallbackPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        setup();
        loop();
        cleanUp();
    }

    private static void setup(){
        int glfwResult = glfwInit();
        if(glfwResult == GL_FALSE){ //In case the initialization fails
            throw new IllegalStateException("GLFW Initialization failed");
        }

        int width = 666;
        int height = 666;

        windowID = glfwCreateWindow(width, height, "Display thing", MemoryUtil.NULL, MemoryUtil.NULL);

        if(windowID == MemoryUtil.NULL){ //In case the initialization fails
            throw new IllegalStateException("Window creation failed");
        }

        glfwMakeContextCurrent(windowID); //Needs to be here
        glfwSwapInterval(1); //Caps the fps to 60 with VSync
        glfwShowWindow(windowID);

        GLContext.createFromCurrent(); //Binds the OpenGL context to the current thread, the GLFW one

        //This is an example of callback input: we set up code that will run if the callback is triggered
        glfwSetCursorPosCallback(windowID, new GLFWCursorPosCallback(){
            @Override
            public void invoke(long window, double xpos, double ypos){
                mouseX = (int) xpos;
                mouseY = (int) ypos;
            }
        });

        glfwSetMouseButtonCallback(windowID, new GLFWMouseButtonCallback(){
            //Button: 0 = left 1 = right 2 = middle
            //Action: 1 = press 0 = release
            //Mods = Unknown
            @Override
            public void invoke(long window, int button, int action, int mods){
                System.out.println("BTN: " + button + " Action: " + action + " Mods: " + mods);
            }
        });

        glClearColor(0F, 0.4F, 0.4F, 1F);

        //Enter the state that is required for modify the projection. Note that, in contrary to Java2D, the vertex
        //coordinate system does not have to be equal to the window coordinate space. The invocation to glOrtho creates
        //a 2D vertex coordinate system like this:
        //Upper-Left:  (0,0)   Upper-Right:  (640,0)
        //Bottom-Left: (0,480) Bottom-Right: (640,480)
        //If you skip the glOrtho method invocation, the default 2D projection coordinate space will be like this:
        //Upper-Left:  (-1,+1) Upper-Right:  (+1,+1)
        //Bottom-Left: (-1,-1) Bottom-Right: (+1,-1)
        glMatrixMode(GL_PROJECTION);
        glOrtho(0, width, height, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);

        lastFPS = getTime(); //Sets for the first time the FPS
    }

    private static void loop(){
        while(glfwWindowShouldClose(windowID) == GL_FALSE){ //As long as the window should not close, keep updating it
            draw();
            input();
            updateFPS();
            glfwPollEvents(); //Catches all the mouse/keyboard events, makes the application actually responsive
        }
    }

    private static void draw(){
        glClear(GL_COLOR_BUFFER_BIT); //Clear the contents of the window

        if(inputEnabled){
            glBegin(GL_QUADS);
            { //The order in which the points are made is really important!
                glColor4f(0.0F, 1F, 0.4F, 0.5F);
                glVertex2i(30, 50);
                glColor4f(1F, 1F, 1F, 1F);
                glVertex2i(30, 170);
                glColor4f(1F, 1F, 0F, 1F);
                glVertex2i(100, 170);
                glColor4f(0F, 1F, 0F, 0.2F);
                glVertex2i(100, 50);
            }
            glEnd();
        }else{
            glBegin(GL_TRIANGLES);
            {
                glColor4f(1F, 1F, 1F, 0.2F);
                glVertex2i(130, 140);
                glColor4f(0F, 0F, 1F, 0.5F);
                glVertex2i(170, 180);
                glColor4f(0F, 0F, 0F, 0.3F);
                glVertex2i(200, 150);
            }
            glEnd();
        }

        glfwSwapBuffers(windowID); //Swaps the front and back frame-buffers. See this method as updating the window// contents.
    }

    private static void input(){
        // This is an example of polled input: we check whether a key is being pressed
        inputEnabled = glfwGetKey(windowID, GLFW_KEY_SPACE) != GLFW_PRESS;
//        System.out.println(mouseX + ", " + mouseY);
    }

    private static void cleanUp() {
        glfwDestroyWindow(windowID); //It's important to release the resources when the program has finished to prevent dreadful memory leaks
        glfwTerminate(); //Destroys all remaining windows and cursors
    }

    /**
     * Get the time in milliseconds
     *
     * @return The system time in milliseconds
     */

    public static long getTime() {
        return System.nanoTime() / 1000000;
    }

    /**
     * Calculate the FPS and set it in the title bar
     */

    public static void updateFPS() {
        if(getTime() - lastFPS > 1000){
            System.out.println(fps);
            fps = 0; //reset the FPS counter
            lastFPS += 1000; //add one second
        }
        fps++;
    }

}