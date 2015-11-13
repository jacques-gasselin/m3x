/*
 * Copyright (c) 2009-2010, Jacques Gasselin de Richebourg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package javax.microedition.m3g;

import m3x.Require;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author jgasseli
 */
public class Object3D
{
    private final Map<AnimationTrack, int[]> channelsByTrack =
            new HashMap<AnimationTrack, int[]>();
    //defaults to true
    private boolean animationEnabled = true;
    private int userID;
    private Object userObject;
    
    public void addAnimationTrack(AnimationTrack animationTrack)
    {
        addAnimationTrack(animationTrack, 0);
    }

    public void addAnimationTrack(AnimationTrack animationTrack, int channel)
    {
        Require.notNull(animationTrack, "animationTrack");
        Require.indexInRange(channel, "channel",
                animationTrack.getKeyframeSequence().getChannelCount());
        
        int[] channels = channelsByTrack.get(animationTrack);
        if (channels == null)
        {
            channels = new int[1];
            channels[0] = channel;
        }
        else
        {
            //verify the channel is not already bound
            if (Arrays.binarySearch(channels, channel) >= 0)
            {
                throw new IllegalArgumentException("channel" + channel + " of" +
                        " animationTrack is already attached to this object");
            }
            
            final int[] newChannels = new int[channels.length + 1];
            System.arraycopy(channels, 0, newChannels, 0, channels.length);
            channels[channels.length] = channel;
            Arrays.sort(channels);
        }

        channelsByTrack.put(animationTrack, channels);
    }

    public int animate(int time)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Duplicates the object, returning a shallow copy of the object.
     *
     * <p><b>Implementation Note:</b> and class that wishes to
     * support this method should implement the version of this method
     * that takes an argument and a default constructor that is accessible
     * from here.
     *
     * @return a shallow copied duplicate
     */
    public final Object3D duplicate()
    {
        try
        {
            final Object3D ret = getClass().newInstance();
            duplicate(ret);
            return ret;
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        throw new UnsupportedOperationException("Class " + getClass() + " does" +
                " not allow duplication");
    }

    /**
     * Copies all the properties and references from this object to the
     * target object.
     * @param target the object to duplicate to.
     * @see #duplicate()
     */
    void duplicate(Object3D target)
    {
        for (AnimationTrack track : channelsByTrack.keySet())
        {
            for (int channel : channelsByTrack.get(track))
            {
                target.addAnimationTrack(track, channel);
            }
        }

        target.setAnimationEnabled(isAnimationEnabled());
        target.setUserID(getUserID());
        target.setUserObject(getUserObject());
    }

    /**
     * Finds an object in the hierarchy that has a matching userID.
     *
     * <p>The first object found that matches the userID is returned.
     * If no object has the matching id then null is returned.</p>
     *
     * <p>Note: The order of traversal is not specified, intentionally, so
     * a user should not assume depth first or breadth first search.</p>
     *
     * @param userID the user id to match with.
     * @return the first matching object, or null if none is found.
     * @see #getUserID()
     */
    public Object3D find(int userID)
    {
        final HashSet<Object3D> closedList = new HashSet<Object3D>();
        final ArrayList<Object3D> openList = new ArrayList<Object3D>();

        //since all operations only touch the end of the list;
        //this is a depth first search. Breath first search would
        //require the front to be removed for each iteration
        openList.add(this);
        //this is an exhaustive search
        while (openList.size() > 0)
        {
            final Object3D candidate = openList.remove(openList.size() - 1);
            //skip objects already visited
            if (!closedList.contains(candidate))
            {
                //count it as visited now
                closedList.add(candidate);
                //does it have the desired UserID?
                if (candidate.getUserID() == userID)
                {
                    return candidate;
                }
                //add the references from the candidate to the open list
                candidate.getReferences(openList);
            }
        }

        //none found
        return null;
    }

    /**
     *
     * @param type
     * @return
     */
    public Object3D[] findAll(Class<?> type)
    {
        final HashSet<Object3D> closedList = new HashSet<Object3D>();
        final ArrayList<Object3D> openList = new ArrayList<Object3D>();
        final ArrayList<Object3D> resultList = new ArrayList<Object3D>();

        //since all operations only touch the end of the list;
        //this is a depth first search. Breath first search would
        //require the front to be removed for each iteration
        openList.add(this);
        //this is an exhaustive search
        while (openList.size() > 0)
        {
            final Object3D candidate = openList.remove(openList.size() - 1);
            //skip objects already visited
            if (!closedList.contains(candidate))
            {
                //count it as visited now
                closedList.add(candidate);
                //of the required type?
                if (type.isInstance(candidate))
                {
                    resultList.add(candidate);
                }
                //add the references from the candidate to the open list
                candidate.getReferences(openList);
            }
        }

        Object3D[] results = new Object3D[resultList.size()];
        return resultList.toArray(results);
    }

    /*public int findAnimationEvents(int startTime, int endTime,
            int eventMask, AnimationEvent[] events)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }*/

    public AnimationTrack getAnimationTrack(int trackIndex)
    {
        Require.indexInRange(trackIndex, "trackIndex", getAnimationTrackCount());
        
        int index = 0;
        for (AnimationTrack track : channelsByTrack.keySet())
        {
            final int[] channels = channelsByTrack.get(track);
            index += channels.length;
            if (trackIndex < index)
            {
                return track;
            }
        }

        throw new IllegalStateException("the index calculation has failed");
    }

    public int getAnimationTrackChannel(int trackIndex)
    {
        Require.indexInRange(trackIndex, "trackIndex", getAnimationTrackCount());

        int index = 0;
        for (AnimationTrack track : channelsByTrack.keySet())
        {
            final int[] channels = channelsByTrack.get(track);
            final int nextIndex = index + channels.length;
            if (trackIndex < nextIndex)
            {
                final int offset = nextIndex - trackIndex;
                return channels[offset];
            }
            index = nextIndex;
        }

        throw new IllegalStateException("the index calculation has failed");
    }

    public int getAnimationTrackCount()
    {
        int count = 0;
        for (AnimationTrack track : channelsByTrack.keySet())
        {
            final int[] channels = channelsByTrack.get(track);
            count += channels.length;
        }
        return count;
    }

    public Object3D[] getReferences()
    {
        final int referenceCount = getReferences((Object3D[]) null);
        final Object3D[] references = new Object3D[referenceCount];
        getReferences(references);
        return references;
    }

    void getReferences(List<Object3D> references)
    {
        references.addAll(channelsByTrack.keySet());
    }

    public int getReferences(Object3D[] references)
    {
        final List<Object3D> list = new ArrayList<Object3D>();
        getReferences(list);
        if (references != null)
        {
            list.toArray(references);
        }
        return list.size();
    }

    public int getUserID()
    {
        return this.userID;
    }

    public Object getUserObject()
    {
        return this.userObject;
    }

    public boolean isAnimationEnabled()
    {
        return this.animationEnabled;
    }

    public void removeAnimationTrack(AnimationTrack animationTrack)
    {
        throw new UnsupportedOperationException();
    }

    public boolean replaceMatching(Object3D[] objects)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the animationEnabled flag on or off. This is the legacy
     * setter for the animationEnabled property, it does not conform to the
     * bean pattern.
     * 
     * @param enable
     * @deprecated
     */
    @Deprecated
    public void setAnimationEnable(boolean enable)
    {
        setAnimationEnabled(enable);
    }

    /**
     * Sets the animationEnabled flag on or off. This is the proper
     * bean pattern setter for the animationEnabled property.
     * 
     * @param enabled
     * @see #isAnimationEnabled()
     */
    public void setAnimationEnabled(boolean enabled)
    {
        this.animationEnabled = enabled;
    }

    public void setUserID(int userID)
    {
        this.userID = userID;
    }

    public void setUserObject(Object userObject)
    {
        this.userObject = userObject;
    }
}
