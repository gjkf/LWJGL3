/**
 * Copyright (c) 21/06/15 Davide Cossu.
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

package com.gjkf.lwjgl3.test;

import com.gjkf.lwjgl3.render.Texture;
import com.gjkf.lwjgl3.util.Point2I;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.MemoryUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Test{

    private long windowID, lastFPS;
    private int mouseX = 0, mouseY = 0, fps;
    private boolean inputEnabled = true;
    private ArrayList<Point2I> arrayList = new ArrayList<>();

    double	x = 0.1F, y = 0.1F,		// starting point
            a = -0.966918F,			// coefficients for "The King's Dream"
            b = 2.879879F,
            c = 0.765145F,
            d = 0.744728F;
    int	initialIterations = 100,	// initial number of iterations
    // to allow the attractor to settle
    iterations = 100000;		// number of times to iterate through
    // the functions and draw a point

    private static Texture logo;

    public Test(){
        setup();
        loop();
        cleanUp();
    }

    public void setup(){
        int glfwResult = glfwInit();
        if(glfwResult == GL_FALSE){ //In case the initialization fails
            throw new IllegalStateException("GLFW Initialization failed");
        }

        int width = 800;
        int height = 800;

        glfwDefaultWindowHints(); // Loads GLFW's default window settings
        glfwWindowHint(GLFW_VISIBLE, GL_TRUE); // Sets window to be visible

        windowID = glfwCreateWindow(width, height, "Display thing", MemoryUtil.NULL, MemoryUtil.NULL);

        if(windowID == MemoryUtil.NULL){ //In case the initialization fails
            throw new IllegalStateException("Window creation failed");
        }

        glfwMakeContextCurrent(windowID); //Needs to be here
        glfwSwapInterval(1); //Caps the fps to 60 with VSync
        glfwShowWindow(windowID);

        GLContext.createFromCurrent(); //Binds the OpenGL context to the current thread, the GLFW one

        setTextures();
        setInputs();

//                glClearColor(0F, 0.4F, 0.4F, 1F);

        glClearColor(0F, 0F, 0F, 0F);

        //Enter the state that is required for modify the projection. Note that, in contrary to Java2D, the vertex
        //coordinate system does not have to be equal to the window coordinate space. The invocation to glOrtho creates
        //a 2D vertex coordinate system like this:
        //Upper-Left:  (0,0)   Upper-Right:  (640,0)
        //Bottom-Left: (0,480) Bottom-Right: (640,480)
        //If you skip the glOrtho method invocation, the default 2D projection coordinate space will be like this:
        //Upper-Left:  (-1,+1) Upper-Right:  (+1,+1)
        //Bottom-Left: (-1,-1) Bottom-Right: (+1,-1)

//        {//For normal use
//            glMatrixMode(GL_PROJECTION);
//            glOrtho(0, width, height, 0, 1, -1);
//            glMatrixMode(GL_MODELVIEW);
//        }



        {//For the attractor
            glViewport(0, 0, width, height);
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            // set up the modelview matrix (the objects)
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
        }

        lastFPS = getTime(); //Sets for the first time the FPS
    }

    public void loop(){
        while(glfwWindowShouldClose(windowID) == GL_FALSE){ //As long as the window should not close, keep updating it
            draw();
            input();
            updateFPS();
            glfwPollEvents(); //Catches all the mouse/keyboard events, makes the application actually responsive
        }
    }

    public void draw(){
        glClear(GL_COLOR_BUFFER_BIT); //Clear the contents of the window

//        logo.drawTexture(new int[]{150,0}, new int[]{600, 0}, new int[]{600, 600}, new int[]{150, 600});

        drawStrangeAttractor();

//        if(arrayList.size() > 1){ //This draws a line to any point where the mouse is pressed
//            for(int i = 1; i < arrayList.size(); i++){
//                glLineWidth(10F);
//                glBegin(GL_LINES);
//                {
//                    glColor4f(1F, 1F, 1F, 1F);
//                    glVertex2i(arrayList.get(i).getX(), arrayList.get(i).getY());
//                    glVertex2i(arrayList.get(i-1).getX(), arrayList.get(i-1).getY());
//                }
//                glEnd();
//            }
//        }

        glfwSwapBuffers(windowID); //Swaps the front and back frame-buffers. See this method as updating the window contents.
    }

    public void input(){
        // This is an example of polled input: we check whether a key is being pressed
        inputEnabled = glfwGetKey(windowID, GLFW_KEY_SPACE) != GLFW_PRESS;
    }

    public void cleanUp(){
        logo.cleanUp();
        glfwDestroyWindow(windowID); //It's important to release the resources when the program has finished to prevent dreadful memory leaks
        glfwTerminate(); //Destroys all remaining windows and cursors
    }

    public void setTextures(){
        try{
            logo = new Texture(new FileInputStream("resources/FlatAvatar.png"));
            logo.bindTexture();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public void setInputs(){
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
                if(action == 1){
                    arrayList.add(new Point2I(mouseX, mouseY));
                }
            }
        });
    }

    /**
     * Get the time in milliseconds
     *
     * @return The system time in milliseconds
     */

    public long getTime() {
        return System.nanoTime() / 1000000;
    }

    /**
     * Calculate the FPS
     */

    public void updateFPS() {
        if(getTime() - lastFPS > 1000){
            System.out.println(fps);
            fps = 0; //reset the FPS counter
            lastFPS += 1000; //add one second
        }
        fps++;
    }

    public void drawStrangeAttractor(){
        // compute some initial iterations to settle into the orbit of the attractor
        for(int i = initialIterations; i > 0; i--){

            // compute a new point using the strange attractor equations
            double xnew = Math.sin(y * b) + c*Math.sin(x * b);
            double ynew = Math.sin(x * a) + d*Math.sin(y * a);

            // save the new point
            x = xnew;
            y = ynew;
        }

        // draw some points
        glBegin(GL_POINTS);

        // set the foreground (pen) color
        glColor4f(1F, 1F, 1F, 0.02F);

        // enable blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // enable point smoothing
        glEnable(GL_POINT_SMOOTH);
        glPointSize(1F);

        // go through the equations many times, drawing a point for each iteration
        for(int i = iterations; i > 0; i--){

            // compute a new point using the strange attractor equations
            double xnew = Math.sin(y * b) + c*Math.sin(x * b);
            double ynew = Math.sin(x * a) + d*Math.sin(y * a);

            // save the new point
            x = xnew;
            y = ynew;

            glColor4f(1F, 1F, 1F, 1F);
            // draw the new point
            glVertex2d(x, y);
        }

        glEnd();

    }

}