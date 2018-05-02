
# coding: utf-8

# In[1]:


import numpy as np


# In[2]:


def segments_by_xth(x, xth):
    #return segs as indices
    count = len(x)
            
    y = (x<=xth).astype(int)    
    y = y[:-1] - y[1:]
    
    inside = 1 + np.argwhere(y==-1)
    outside = np.argwhere(y==1)
    #print(inside.shape, outside.shape)
    
    if inside[0]>outside[0]:
        outside = outside[1:]
    
    if inside[-1]>outside[-1]:
        inside = inside[:-1]
        
    assert len(inside)==len(outside)   
    res = np.concatenate((inside, outside), axis=1).astype(int)    
    return res


# In[3]:


def filter_segment_by_length(segments, min_len):
    s = segments
    cond = (s[:,1]-s[:,2]+1)>=min_len
    s = s[cond, :]
    return s    

