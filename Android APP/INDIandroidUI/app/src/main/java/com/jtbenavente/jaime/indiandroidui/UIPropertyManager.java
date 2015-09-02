package com.jtbenavente.jaime.indiandroidui;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.indilib.i4j.client.INDIProperty;

/**
 * Created by Jaime on 5/8/15.
 */

/**
 *  Interface to handle views and indi propertys
 */
public interface UIPropertyManager {
    /**
     *  Check if this class can represent p
     *
     * @param p Indi property
     * @return True/false if class can represent p
     */
    boolean handlesProperty (INDIProperty p);

    /**
     *  Create and return a view sets with p elements
     *
     * @param p Indi property
     * @param inflater Layout inflater to inflate view
     * @param parent ViewGroup to inflate view
     * @param context Context to allow use Activity methods
     * @return View
     */
    View getPropertyView (INDIProperty p, LayoutInflater inflater, ViewGroup parent, Context context);

    /**
     *  update view v with Indi property p elements
     *
     * @param p Indi property
     * @param v View
     */
    void updateView (INDIProperty p, View v);

    /**
     *  Create a new view dialog to allow set Indi property p elements
     *
     * @param p Indi property
     * @param inflater Layout inflater to inflate view
     * @return View
     */
    View getUpdateView(INDIProperty p, LayoutInflater inflater, DialogFragment fragment);

    /**
     *  Update property with change saved at view v
     *
     * @param p Indi property
     * @param v View
     */
    void updateProperty(INDIProperty p, View v);

    /**
     *  Get priority
     *
     * @return priority
     */
    int getPriority();

    /**
     *  Get update button reference
     *
     *  @return update button
     */

    Button getUpdateButton();
}
